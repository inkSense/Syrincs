package syrincs.a_domain.hindemith;

import java.util.*;

public class ChordAnalysis {
    // Algorithmus zur Bestimmung eines Akkordes:
    // "Die Bestandaufnahme der Klänge scheidet darum das gesamte Akkordmaterial zunächst in zwei Hauptgruppen: In der Gruppe A sind alle tritonusfreien Klänge. Den Akkorden mit Tritonus wird die Gruppe B zugewiesen." S.119

    public enum Column { A_TRITONE_FREE, B_WITH_TRITONE }

    public static final class Result {
        public final Column column;
        public final int rootNote; // MIDI
        public final int group; // 1..14
        public final int frameInterval;
        public final List<Integer> notes; // sorted copy

        public Result(Column column,
                      int rootNote,
                      int group,
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

        HindemithChord hindemithChord = new HindemithChord(notes);
        Integer root = hindemithChord.getRootNote();
        if (root == null) {
            throw new IllegalStateException("Root note must not be null for analyzed chord");
        }

        int group = classifyChordGroup(hindemithChord, notes.get(0));

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

    private int classifyChordGroup(HindemithChord hindemithChord, int bassNote) {
        var specs = new ChordSpecificationRepository();
        Map<Integer, ChordSpecification> groupSpecs = specs.getGroupSpecifications(); // 1..14

        // Prioritize more specific rules:
        // 1) multiple tritones
        // 2) dim/dim7 (group 14)
        // 3) layering of M3 or P4 (group 13)
        // 4) all remaining in natural order
        List<Integer> multiTritones = new ArrayList<>();
        List<Integer> dimOrDim7 = new ArrayList<>();
        List<Integer> layering = new ArrayList<>();
        List<Integer> others = new ArrayList<>();

        for (int g = 1; g <= 14; g++) {
            ChordSpecification spec = groupSpecs.get(g);
            if (spec == null) continue;
            if (spec.getMehrereTritoni()) {
                multiTritones.add(g);
            } else if (spec.getDimOrDim7()) {
                dimOrDim7.add(g);
            } else if (spec.getLayersOfMajor3OrPerfect4()) {
                layering.add(g);
            } else {
                others.add(g);
            }
        }
        List<Integer> order = new ArrayList<>();
        order.addAll(multiTritones);
        order.addAll(dimOrDim7);
        order.addAll(layering);
        order.addAll(others);

        for (Integer g : order) {
            ChordSpecification spec = groupSpecs.get(g);
            if (spec == null) continue;
            if (ChordRules.matches(hindemithChord, bassNote, spec)) {
                return g;
            }
        }
        throw new IllegalStateException("Chord group has no matching groups");
    }

    public List<HindemithChord> analyzeList(List<List<Integer>> noteSets) {

        List<HindemithChord> hindemithChords = new ArrayList<>();
        for (List<Integer> notes : noteSets) {
            HindemithChord chord = new HindemithChord(notes);
            var result = analyze(notes);
            chord.setGroup(result.group);
            hindemithChords.add(chord);
        }
        return hindemithChords;
    }
}
