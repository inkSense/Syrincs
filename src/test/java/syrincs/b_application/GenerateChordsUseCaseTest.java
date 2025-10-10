package syrincs.b_application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syrincs.a_domain.hindemith.ChordAnalysis;
import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.a_domain.chord.NoteCombinator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenerateChordsUseCaseTest {

    @Test
    @DisplayName("generateChordsForThreeNotes: kleiner Bereich 60..62 erzeugt genau 1 Dreiklang")
    void generateThreeNotes_smallRange_singleChord() {
        GenerateChordsUseCase uc = new GenerateChordsUseCase(new NoteCombinator(), new ChordAnalysis(), 2);
        List<HindemithChord> chords = uc.generateChordsForThreeNotes(60, 62);
        assertNotNull(chords);
        assertEquals(1, chords.size(), "60..62 should yield exactly one 3-note combination");
        assertEquals(3, chords.getFirst().getNotes().size());
        assertTrue(chords.getFirst().getNotes().containsAll(List.of(60,61,62)));
    }

    @Test
    @DisplayName("generateAllChordsToFiveNotes: Bereich 60..64 ergibt analysierte 5-Noten-Akkorde")
    void generateUpToFiveNotes_returnsFiveNoteChordsAnalyzed() {
        GenerateChordsUseCase uc = new GenerateChordsUseCase(new NoteCombinator(), new ChordAnalysis(), 1);
        List<HindemithChord> chords = uc.generateAllChordsToFiveNotes(60, 64);
        assertNotNull(chords);
        assertFalse(chords.isEmpty(), "Expected non-empty result for 5 distinct notes in range");
        // As implemented, the UC returns analyzed chords (with group) for the last iteration (5-note sets)
        for (HindemithChord c : chords) {
            assertEquals(5, c.getNotes().size(), "All chords should have 5 notes in this configuration");
            assertNotNull(c.getGroup(), "Chords should be analyzed and have a group assigned");
        }
    }
}
