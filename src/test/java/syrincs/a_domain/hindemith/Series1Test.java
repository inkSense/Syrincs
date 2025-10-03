package syrincs.a_domain.hindemith;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Series1Test {

    @Test
    @DisplayName("getSeries1Of returns Hindemith Series 1 MIDI relatives based on base note")
    void testGetSeries1Of() {
        Series1 s = new Series1();
        int c4 = 60;
        List<Integer> expected = List.of(60, 67, 65, 69, 64, 63, 68, 62, 70, 61, 71, 66);
        assertEquals(expected, s.getSeries1Of(c4));
    }

    @Test
    @DisplayName("getChildrenOf returns the six children")
    void testGetChildrenOf() {
        Series1 s = new Series1();
        int c4 = 60;
        List<Integer> expected = List.of(67, 65, 69, 64, 63, 68);
        assertEquals(expected, s.getChildrenOf(c4));
    }

    @Test
    @DisplayName("getGrandChildren returns the four grandchildren")
    void testGetGrandChildren() {
        Series1 s = new Series1();
        int c4 = 60;
        List<Integer> expected = List.of(62, 70, 61, 71);
        assertEquals(expected, s.getGrandChildren(c4));
    }

    @Test
    @DisplayName("getRelativeByDegree throws on invalid degree and allows bounds")
    void testGetRelativeByDegreeValidation() {
        Series1 s = new Series1();
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> s.getRelativeByDegree(60, -1));
        assertTrue(ex1.getMessage().toLowerCase().contains("degree"));
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> s.getRelativeByDegree(60, 12));
        assertTrue(ex2.getMessage().toLowerCase().contains("degree"));
        // boundary cases should NOT throw
        assertDoesNotThrow(() -> s.getRelativeByDegree(60, 0));
        assertDoesNotThrow(() -> s.getRelativeByDegree(60, 11));
    }
}
