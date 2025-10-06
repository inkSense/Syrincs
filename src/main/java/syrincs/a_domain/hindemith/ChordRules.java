package syrincs.a_domain.hindemith;

import syrincs.a_domain.ChordCalculator.Chord;
import syrincs.a_domain.ChordCalculator.ChordSpecification;
import syrincs.a_domain.Interval;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ChordRules
 * Shared rule evaluation for Hindemith chord specifications.
 * This utility consolidates the interval-based checks that were previously
 * duplicated in ChordCalculator and ChordAnalysisHindemith.
 *
 * Important: This method intentionally does not check root==bass or column A/B;
 * callers keep those aspects separate where needed.
 */
public final class ChordRules {
    private ChordRules() {}

    /**
     * Full match: interval-related parts PLUS optional root/bass relation and column requirement.
     * This is the primary entry point for both generation and analysis.
     */
    public static boolean matches(Chord chord, int bassNote, ChordSpecification spec) {
        return matchesIntervalsOnly(chord, spec)
                && rootRelation(bassNote, chord.getRootNote(), spec.getRootNoteEqual())
                && columnRequirement(chord, spec.getColumnRequirement());
    }

    /**
     * Matches only the interval-related parts of a specification (no root/bass comparison).
     */
    public static boolean matchesIntervalsOnly(Chord chord, ChordSpecification spec) {
        List<Interval> allIntervals = chord.getAllIntervals();
        List<Interval> rootIntervals = chord.getRootIntervals();
        List<Interval> pcIntervals = chord.calculateAllIntervalsOfPitchClasses();

        return intervalsNotInSet(allIntervals, spec.getExcludeAll())
                && layersOfMajor3rdOrPerfect4th(rootIntervals, spec.getLayersOfMajor3OrPerfect4())
                && dimOrDim7(rootIntervals, spec.getDimOrDim7())
                && includesAtLeastOneOf(allIntervals, spec.getIncludeAtLeastOneOf())
                && includesAll(allIntervals, spec.getIncludeAll())
                && includesAllWithAlternatives(allIntervals, spec.getIncludeAllWithAlternatives())
                && hasMehrereTritoni(pcIntervals, spec.getMehrereTritoni());
    }

    // --- Helpers copied from existing logic, kept private here ---

    private static boolean intervalsNotInSet(List<Interval> intervals, Set<Integer> exclude) {
        if (exclude == null || exclude.isEmpty()) return true;
        Set<Integer> diffs = intervals.stream().map(i -> i.getDifferenceWithoutOctavations()).collect(Collectors.toSet());
        return Collections.disjoint(diffs, exclude);
    }

    // Note: preserves operator precedence from original code: (required == cond1) || cond2
    private static boolean layersOfMajor3rdOrPerfect4th(List<Interval> intervals, boolean required) {
        Set<Integer> mod4 = intervals.stream().map(n -> n.getDifferenceWithoutOctavations() % 4).collect(Collectors.toSet());
        Set<Integer> mod5 = intervals.stream().map(n -> n.getRealDifference() % 5).collect(Collectors.toSet());
        boolean cond1 = (mod4.size() == 1 && mod4.contains(0));
        boolean cond2 = (mod5.size() == 1 && mod5.contains(0));
        return (required == cond1) || cond2;
    }

    private static boolean dimOrDim7(List<Interval> intervals, boolean required) {
        Set<Integer> mod3 = intervals.stream().map(n -> n.getRealDifference() % 3).collect(Collectors.toSet());
        boolean isDimFamily = (mod3.size() == 1 && mod3.contains(0));
        return required == isDimFamily;
    }

    private static boolean includesAtLeastOneOf(List<Interval> intervals, Set<Integer> includeAny) {
        if (includeAny == null || includeAny.isEmpty()) return true;
        Set<Integer> diffs = intervals.stream().map(i -> i.getDifferenceWithoutOctavations()).collect(Collectors.toSet());
        return includeAny.stream().anyMatch(diffs::contains);
    }

    private static boolean includesAll(List<Interval> intervals, Set<Integer> includeAll) {
        if (includeAll == null || includeAll.isEmpty()) return true;
        List<Integer> diffs = intervals.stream().map(i -> i.getDifferenceWithoutOctavations()).toList();
        return diffs.containsAll(includeAll);
    }

    private static boolean includesAllWithAlternatives(List<Interval> intervals, List<Set<Integer>> groups) {
        if (groups == null || groups.isEmpty()) return true;
        Set<Integer> diffs = intervals.stream()
                .map(i -> i.getDifferenceWithoutOctavations())
                .collect(Collectors.toSet());
        for (Set<Integer> group : groups) {
            if (group == null || group.isEmpty()) continue; // ignore empty groups
            boolean any = group.stream().anyMatch(diffs::contains);
            if (!any) return false;
        }
        return true;
    }

    private static boolean hasMehrereTritoni(List<Interval> intervals, boolean mustHaveMultiple) {
        if (!mustHaveMultiple) return true;
        long tritones = intervals.stream().filter(i -> i.getDifferenceWithoutOctavations() == 6).count();
        return tritones >= 2;
    }

    private static boolean rootRelation(int bass, Integer root, String condition) {
        if (condition == null) return true;
        if (root == null) return false;
        return "==".equals(condition) ? (bass == root) : (bass != root);
    }

    private static boolean columnRequirement(Chord chord, ChordSpecification.ColumnRequirement req) {
        if (req == null || req == ChordSpecification.ColumnRequirement.ANY) return true;
        // detect tritone on pitch-class level
        List<Interval> pcIntervals = chord.calculateAllIntervalsOfPitchClasses();
        boolean hasTritone = pcIntervals.stream().anyMatch(i -> i.getDifferenceWithoutOctavations() == 6);
        return (req == ChordSpecification.ColumnRequirement.WITH_TRITONE) ? hasTritone : !hasTritone;
    }
}
