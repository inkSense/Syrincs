package syrincs.a_domain.hindemith;

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
    public static boolean matches(HindemithChord hindemithChord, int bassNote, ChordSpecification spec) {
        return matchesIntervalsOnly(hindemithChord, spec)
                && rootRelation(bassNote, hindemithChord.getRootNote(), spec.getRootNoteEqual())
                && columnRequirement(hindemithChord, spec.getColumnRequirement());
    }

    /**
     * Matches only the interval-related parts of a specification (no root/bass comparison).
     */
    public static boolean matchesIntervalsOnly(HindemithChord hindemithChord, ChordSpecification spec) {
        List<HindemithInterval> allHindemithIntervals = hindemithChord.getAllIntervals();
        List<HindemithInterval> rootHindemithIntervals = hindemithChord.getRootIntervals();
        List<HindemithInterval> pcHindemithIntervals = hindemithChord.calculateAllIntervalsOfPitchClasses();

        return intervalsNotInSet(allHindemithIntervals, spec.getExcludeAll())
                && layersOfMajor3rdOrPerfect4th(rootHindemithIntervals, spec.getLayersOfMajor3OrPerfect4())
                && dimOrDim7(rootHindemithIntervals, spec.getDimOrDim7())
                && includesAtLeastOneOf(allHindemithIntervals, spec.getIncludeAtLeastOneOf())
                && includesAll(allHindemithIntervals, spec.getIncludeAll())
                && includesAllWithAlternatives(allHindemithIntervals, spec.getIncludeAllWithAlternatives())
                && hasMehrereTritoni(pcHindemithIntervals, spec.getMehrereTritoni());
    }

    // --- Helpers copied from existing logic, kept private here ---

    private static boolean intervalsNotInSet(List<HindemithInterval> hindemithIntervals, Set<Integer> exclude) {
        if (exclude == null || exclude.isEmpty()) return true;
        Set<Integer> diffs = hindemithIntervals.stream().map(i -> i.getDifferenceWithoutOctavations()).collect(Collectors.toSet());
        return Collections.disjoint(diffs, exclude);
    }

    // Note: preserves operator precedence from original code: (required == cond1) || cond2
    private static boolean layersOfMajor3rdOrPerfect4th(List<HindemithInterval> hindemithIntervals, boolean required) {
        Set<Integer> mod4 = hindemithIntervals.stream().map(n -> n.getDifferenceWithoutOctavations() % 4).collect(Collectors.toSet());
        Set<Integer> mod5 = hindemithIntervals.stream().map(n -> n.getRealDifference() % 5).collect(Collectors.toSet());
        boolean cond1 = (mod4.size() == 1 && mod4.contains(0));
        boolean cond2 = (mod5.size() == 1 && mod5.contains(0));
        return (required == cond1) || cond2;
    }

    private static boolean dimOrDim7(List<HindemithInterval> hindemithIntervals, boolean required) {
        Set<Integer> mod3 = hindemithIntervals.stream().map(n -> n.getRealDifference() % 3).collect(Collectors.toSet());
        boolean isDimFamily = (mod3.size() == 1 && mod3.contains(0));
        return required == isDimFamily;
    }

    private static boolean includesAtLeastOneOf(List<HindemithInterval> hindemithIntervals, Set<Integer> includeAny) {
        if (includeAny == null || includeAny.isEmpty()) return true;
        Set<Integer> diffs = hindemithIntervals.stream().map(i -> i.getDifferenceWithoutOctavations()).collect(Collectors.toSet());
        return includeAny.stream().anyMatch(diffs::contains);
    }

    private static boolean includesAll(List<HindemithInterval> hindemithIntervals, Set<Integer> includeAll) {
        if (includeAll == null || includeAll.isEmpty()) return true;
        List<Integer> diffs = hindemithIntervals.stream().map(i -> i.getDifferenceWithoutOctavations()).toList();
        return diffs.containsAll(includeAll);
    }

    private static boolean includesAllWithAlternatives(List<HindemithInterval> hindemithIntervals, List<Set<Integer>> groups) {
        if (groups == null || groups.isEmpty()) return true;
        Set<Integer> diffs = hindemithIntervals.stream()
                .map(i -> i.getDifferenceWithoutOctavations())
                .collect(Collectors.toSet());
        for (Set<Integer> group : groups) {
            if (group == null || group.isEmpty()) continue; // ignore empty groups
            boolean any = group.stream().anyMatch(diffs::contains);
            if (!any) return false;
        }
        return true;
    }

    private static boolean hasMehrereTritoni(List<HindemithInterval> hindemithIntervals, boolean mustHaveMultiple) {
        if (!mustHaveMultiple) return true;
        long tritones = hindemithIntervals.stream().filter(i -> i.getDifferenceWithoutOctavations() == 6).count();
        return tritones >= 2;
    }

    private static boolean rootRelation(int bass, Integer root, String condition) {
        if (condition == null) return true;
        if (root == null) return false;
        return "==".equals(condition) ? (bass == root) : (bass != root);
    }

    private static boolean columnRequirement(HindemithChord hindemithChord, ChordSpecification.ColumnRequirement req) {
        if (req == null || req == ChordSpecification.ColumnRequirement.ANY) return true;
        // detect tritone on pitch-class level
        List<HindemithInterval> pcHindemithIntervals = hindemithChord.calculateAllIntervalsOfPitchClasses();
        boolean hasTritone = pcHindemithIntervals.stream().anyMatch(i -> i.getDifferenceWithoutOctavations() == 6);
        return (req == ChordSpecification.ColumnRequirement.WITH_TRITONE) ? hasTritone : !hasTritone;
    }
}
