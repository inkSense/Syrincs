package syrincs.a_domain.hindemith;

import syrincs.a_domain.ChordCalculator.Chord;
import syrincs.a_domain.ChordCalculator.ChordCalculator;
import syrincs.a_domain.ChordCalculator.ChordSpecification;
import syrincs.a_domain.Interval;

import java.util.*;
import java.util.stream.Collectors;

public class ChordAnalysisHindemith {
    // Algorithmus zur Bestimmung eines Akkordes:
    // "Die Bestandaufnahme der Klänge scheidet darum das gesamte Akkordmaterial zunächst in zwei Hauptgruppen: In der Gruppe A sind alle tritonusfreien Klänge. Den Akkorden mit Tritonus wird die Gruppe B zugewiesen." S.119

    public enum Column { A_TRITONE_FREE, B_WITH_TRITONE }

    public static final class Result {
        public final Column column;
        public final Optional<Integer> rootNote; // MIDI
        public final Optional<Integer> group; // 0..13
        public final int frameInterval;
        public final List<Integer> notes; // sorted copy

        public Result(Column column,
                      Optional<Integer> rootNote,
                      Optional<Integer> group,
                      int frameInterval,
                      List<Integer> notes) {
            this.column = column;
            this.rootNote = rootNote;
            this.group = group;
            this.frameInterval = frameInterval;
            this.notes = notes;
        }
    }

    private final Series2 series2 = new Series2();

    /**
     * Analysiert einen Akkord (Liste von MIDI-Noten) nach Hindemith.
     * - Gruppiert nach Tritonus vorhanden (Spalten A/B)
     * - Bestimmt optionalen Grundton (über bestehende Chord-Logik)
     * - Ermittelt eine Akkordgruppe 0..13, indem die vorhandenen Constraints geprüft werden
     */
    public Result analyze(List<Integer> midiNotes) {
        if (midiNotes == null || midiNotes.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 notes to analyze a chord");
        }
        List<Integer> notes = midiNotes.stream().sorted().toList();
        int frame = Collections.max(notes) - Collections.min(notes);

        Column column = hasTritoneInPitchClasses(notes) ? Column.B_WITH_TRITONE : Column.A_TRITONE_FREE;

        Chord chord = new Chord(notes);
        Optional<Integer> root = Optional.ofNullable(chord.getRootNote());

        Optional<Integer> group = classifyChordGroup(chord, notes.get(0));

        return new Result(column, root, group, frame, notes);
    }

    private boolean hasTritoneInPitchClasses(List<Integer> notes) {
        List<Integer> pcs = notes.stream().map(n -> Math.floorMod(n, 12)).distinct().toList();
        for (int i = 0; i < pcs.size(); i++) {
            for (int j = i + 1; j < pcs.size(); j++) {
                int st = Math.floorMod(pcs.get(j) - pcs.get(i), 12);
                if (series2.isTritone(st)) return true;
            }
        }
        return false;
    }

    private Optional<Integer> classifyChordGroup(Chord chord, int bassNote) {
        ChordCalculator calc = new ChordCalculator();
        Map<Integer, ChordSpecification> constraints = calc.getDissDegreeConstraints();
        for (int dg = 0; dg <= 13; dg++) {
            ChordSpecification c = constraints.get(dg);
            if (c == null) continue;
            if (passesConstraint(chord, c, bassNote)) {
                return Optional.of(dg);
            }
        }
        return Optional.empty();
    }

    // Re-Implementierung der Prüf-Logik, angelehnt an ChordCalculator.checkIntervals(...)
    private boolean passesConstraint(Chord chord, ChordSpecification c, int bassNote) {
        List<Interval> allIntervals = chord.getAllIntervals();
        List<Interval> rootIntervals = chord.getRootIntervals();
        return intervalsNotInSet(allIntervals, c.getExcludeAll())
                && layersOfMajor3rdOrPerfect4th(rootIntervals, c.getLayersOfMajor3OrPerfect4())
                && dimOrDim7(rootIntervals, c.getDimOrDim7())
                && includesAtLeastOneOf(allIntervals, c.getIncludeAtLeastOneOf())
                && includesAll(allIntervals, c.getIncludeAll())
                && includesAllWithAlternatives(allIntervals, c.getIncludeAllWithAlternatives())
                && hasMehrereTritoniOnPitchClasses(chord, c.getMehrereTritoni())
                && rootNoteEqualsBassNote(bassNote, chord.getRootNote(), c.getRootNoteEqual());
    }

    private boolean intervalsNotInSet(List<Interval> intervals, Set<Integer> exclude) {
        List<Integer> differences = intervals.stream().map(i -> i.getDifferenceWithoutOctavations()).collect(Collectors.toList());
        return exclude == null || Collections.disjoint(differences, exclude);
    }

    // Achtung: Entspricht der Operator-Priorität des Originals (constraintValue == cond1) || cond2
    private boolean layersOfMajor3rdOrPerfect4th(List<Interval> intervals, boolean constraintValue) {
        Set<Integer> mod4 = intervals.stream().map(n -> n.getDifferenceWithoutOctavations() % 4).collect(Collectors.toSet());
        Set<Integer> mod5 = intervals.stream().map(n -> n.getRealDifference() % 5).collect(Collectors.toSet());
        boolean cond1 = (mod4.size() == 1 && mod4.contains(0));
        boolean cond2 = (mod5.size() == 1 && mod5.contains(0));
        return (constraintValue == cond1) || cond2;
    }

    private boolean dimOrDim7(List<Interval> intervals, boolean constraintValue) {
        Set<Integer> mod3 = intervals.stream().map(n -> n.getRealDifference() % 3).collect(Collectors.toSet());
        boolean isDimFamily = (mod3.size() == 1 && mod3.contains(0));
        return constraintValue == isDimFamily;
    }

    private boolean includesAtLeastOneOf(List<Interval> intervals, Set<Integer> includeAny) {
        Set<Integer> diffs = intervals.stream().map(i -> i.getDifferenceWithoutOctavations()).collect(Collectors.toSet());
        return includeAny == null || includeAny.stream().anyMatch(diffs::contains);
    }

    private boolean includesAll(List<Interval> intervals, Set<Integer> includeAll) {
        List<Integer> diffs = intervals.stream().map(i -> i.getDifferenceWithoutOctavations()).toList();
        return includeAll == null || diffs.containsAll(includeAll);
    }

    private boolean includesAllWithAlternatives(List<Interval> intervals, List<Set<Integer>> groups) {
        if (groups == null || groups.isEmpty()) return true;
        java.util.Set<Integer> diffs = intervals.stream()
                .map(i -> i.getDifferenceWithoutOctavations())
                .collect(java.util.stream.Collectors.toSet());
        for (java.util.Set<Integer> group : groups) {
            if (group == null || group.isEmpty()) continue; // leere Gruppen ignorieren
            boolean any = group.stream().anyMatch(diffs::contains);
            if (!any) return false;
        }
        return true;
    }

    private boolean hasMehrereTritoniOnPitchClasses(Chord chord, boolean mustHaveMultiple) {
        if (!mustHaveMultiple) return true;
        List<Interval> pitchClassIntervals = chord.calculateAllIntervalsOfPitchClasses();
        long tritones = pitchClassIntervals.stream().filter(i -> i.getDifferenceWithoutOctavations() == 6).count();
        return tritones >= 2;
    }

    private boolean rootNoteEqualsBassNote(int bass, Integer root, String condition) {
        if (condition == null) return true;
        if (root == null) return false;
        return "==".equals(condition) ? (bass == root) : (bass != root);
    }
}
