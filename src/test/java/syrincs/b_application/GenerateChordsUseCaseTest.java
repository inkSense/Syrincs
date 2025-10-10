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

}
