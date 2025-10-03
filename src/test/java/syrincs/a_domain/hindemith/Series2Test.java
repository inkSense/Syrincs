package syrincs.a_domain.hindemith;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Series2Test {

    @Test
    @DisplayName("getIntervals returns Series 2 interval ordering")
    void testGetIntervals() {
        Series2 s = new Series2();
        assertEquals(List.of(12, 7, 5, 4, 8, 3, 9, 2, 10, 1, 11, 6), s.getIntervals());
    }

    @Test
    @DisplayName("getSeries2Of returns absolute MIDI notes from base")
    void testGetSeries2Of() {
        Series2 s = new Series2();
        int c4 = 60;
        List<Integer> expected = List.of(72, 67, 65, 64, 68, 63, 69, 62, 70, 61, 71, 66);
        assertEquals(expected, s.getSeries2Of(c4));
    }

    @Test
    @DisplayName("Harmonic and melodic power orders are exposed and complementary")
    void testPowerOrders() {
        Series2 s = new Series2();
        List<Integer> harmonic = s.getHarmonicPowerOrder();
        List<Integer> melodic = s.getMelodicPowerOrder();
        assertEquals(List.of(7, 5, 4, 8, 3, 9, 2, 10, 1, 11), harmonic);
        List<Integer> reversed = new java.util.ArrayList<>(harmonic);
        java.util.Collections.reverse(reversed);
        assertEquals(reversed, melodic);
    }

    @Test
    @DisplayName("Root choice prefers lower for fifth, upper for fourth; tritone undecided")
    void testRootChoice() {
        Series2 s = new Series2();
        // Perfect fifth 7 -> lower is root
        assertEquals(60, s.chooseRootNote(60, 67));
        assertEquals(60, s.chooseRootNoteOptional(60, 67).orElse(-1));
        // Perfect fourth 5 -> upper is root
        assertEquals(65, s.chooseRootNote(60, 65));
        assertEquals(65, s.chooseRootNoteOptional(60, 65).orElse(-1));
        // Tritone -> optional empty, fallback lower
        assertTrue(s.chooseRootNoteOptional(60, 66).isEmpty());
        assertEquals(60, s.chooseRootNote(60, 66));
    }

    @Test
    @DisplayName("getRelativeByDegree validates and returns expected pitch")
    void testGetRelativeByDegree() {
        Series2 s = new Series2();
        assertThrows(IllegalArgumentException.class, () -> s.getRelativeByDegree(60, -1));
        assertThrows(IllegalArgumentException.class, () -> s.getRelativeByDegree(60, 12));
        assertEquals(72, s.getRelativeByDegree(60, 0)); // octave
        assertEquals(66, s.getRelativeByDegree(60, 11)); // tritone
    }

    @Test
    @DisplayName("Consonance rank matches ordering (0=Octave, 11=Tritone)")
    void testConsonanceRank() {
        Series2 s = new Series2();
        assertEquals(0, s.getConsonanceRankForSemitones(12));
        assertEquals(0, s.getConsonanceRankForSemitones(0)); // 0 treated as 12
        assertEquals(1, s.getConsonanceRankForSemitones(7));
        assertEquals(11, s.getConsonanceRankForSemitones(6));
    }
}
