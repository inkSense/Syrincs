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
        public final Optional<Integer> group; // 1..14
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
        Map<Integer, ChordSpecification> groupSpecs = calc.getGroupSpecifications(); // 1..14

        // Prioritize more specific rules: those explicitly requiring multiple tritones
        List<Integer> withMultiTritonesFirst = new ArrayList<>();
        List<Integer> others = new ArrayList<>();
        for (int g = 1; g <= 14; g++) {
            ChordSpecification spec = groupSpecs.get(g);
            if (spec == null) continue;
            if (spec.getMehrereTritoni()) {
                withMultiTritonesFirst.add(g);
            } else {
                others.add(g);
            }
        }
        List<Integer> order = new ArrayList<>(withMultiTritonesFirst);
        order.addAll(others);

        for (Integer g : order) {
            ChordSpecification spec = groupSpecs.get(g);
            if (spec == null) continue;
            if (ChordRules.matches(chord, bassNote, spec)) {
                return Optional.of(g);
            }
        }
        return Optional.empty();
    }
}
