package syrincs.a_domain.hindemith;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syrincs.a_domain.ChordCalculator.Chord;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadHindemithJsonContentTest {

    @Test
    @DisplayName("Lädt JSON und findet erwartete Akkorde in gIonic/3/0/INTERVALS_13_TO_16")
    void loadJsonAndVerifyExpectedChords() {
        Hindemith h = new Hindemith();
        // JSON-Datei, die bereits im Repo liegt
        h.loadAllChordsFromFile(52, 68);

        // Robust gegen unterschiedliche Map-Verschachtelung im gespeicherten JSON:
        // Wir gehen von Scale ("gIonic") aus und sammeln alle Akkorde in INTERVALS_13_TO_16 mit genau 3 Noten,
        // egal ob zuerst nach DissGrad oder nach numNotes gruppiert wurde.
        var all = h.getAllChords();
        assertNotNull(all, "All chords map should not be null");
        var scaleMap = all.get("gIonic");
        assertNotNull(scaleMap, "Scale 'gIonic' must be present in JSON");

        java.util.List<Chord> chords = new java.util.ArrayList<>();
        for (java.util.Map<Integer, java.util.Map<FrameIntervalRange, java.util.List<Chord>>> level1 : scaleMap.values()) {
            if (level1 == null) continue;
            // Versuch A: numNotes-Schlüssel = 3 direkt anfragen
            var maybeByNumNotes = level1.get(3);
            if (maybeByNumNotes != null) {
                var list = maybeByNumNotes.get(FrameIntervalRange.INTERVALS_13_TO_16);
                if (list != null) chords.addAll(list);
            }
            // Versuch B: alle Untereinträge ansehen und nach Range + Notenanzahl filtern
            for (var byRange : level1.values()) {
                if (byRange == null) continue;
                var list = byRange.get(FrameIntervalRange.INTERVALS_13_TO_16);
                if (list != null) {
                    for (Chord c : list) {
                        if (c != null && c.getNumNotes() == 3) chords.add(c);
                    }
                }
            }
        }

        assertFalse(chords.isEmpty(), "Chord list should not be empty for gIonic/numNotes=3/INTERVALS_13_TO_16");

        // Prüfe, ob ein bekannter Akkord aus der Datei enthalten ist
        // Auszug aus data/minLowerNote52_maxUpperNote68.json zeigt [52,59,67] unter gIonic/3/0/INTERVALS_13_TO_16
        assertTrue(
                chords.stream().anyMatch(c -> c.getNotes().equals(List.of(52, 59, 67))),
                "Expected to find chord [52,59,67] in gIonic/.../INTERVALS_13_TO_16"
        );

        // Optionale Zusatzprüfung: alle Akkorde liegen wirklich im Rahmenintervall 13..16
        assertTrue(
                chords.stream().allMatch(c -> {
                    int fi = c.getFrameInterval();
                    return fi >= 13 && fi <= 16;
                }),
                "All chords should have a frame interval between 13 and 16"
        );
    }
}
