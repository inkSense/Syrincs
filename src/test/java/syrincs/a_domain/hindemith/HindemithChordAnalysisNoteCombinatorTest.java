package syrincs.a_domain.hindemith;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HindemithChordAnalysisNoteCombinatorTest {

    @Test
    @DisplayName("analyze: C-E-G is group A, root=C, degree=0, frame interval = 7")
    void analyze_majorTriad_basic() {
        ChordAnalysis ca = new ChordAnalysis();
        var res = ca.analyze(List.of(60, 64, 67));
        assertEquals(ChordAnalysis.Column.A_TRITONE_FREE, res.column);
        assertTrue(res.rootNote.isPresent());
        assertEquals(60, res.rootNote.get());
        assertTrue(res.group.isPresent());
        assertEquals(1, res.group.get());
        assertEquals(7, res.frameInterval);
        assertEquals(List.of(60, 64, 67), res.notes);
    }

    @Test
    @DisplayName("analyze: chord with tritone goes to group B; verifies root and degree")
    void analyze_tritoneChord_groupB_root_and_degree() {
        ChordAnalysis ca = new ChordAnalysis();
        // Contains a tritone 60-66; best interval is minor third 66-69, hence root 66 per project logic
        var res = ca.analyze(List.of(60, 66, 69));
        assertEquals(ChordAnalysis.Column.B_WITH_TRITONE, res.column);
        assertTrue(res.rootNote.isPresent());
        assertEquals(66, res.rootNote.get());
        assertTrue(res.group.isPresent());
        assertEquals(14, res.group.get());
        assertEquals(9, res.frameInterval);
        assertEquals(List.of(60, 66, 69), res.notes);
    }


    // ------------------------------------------------------------
    // Redundante/erweiterbare Beispiele: Hier kannst du eigene Akkorde eintragen
    // ------------------------------------------------------------

    /**
     * Füge einfach weitere Tests ein, z. B.:
     * new Example(List.of(59, 62, 65), ChordAnalysis.Group.A_TRITONE_FREE, 59, null)
     * - expectedRoot und expectedGroup dürfen null sein, wenn du nur die Gruppe testen willst.
     */
    private static List<Example> userExamples() {
        return List.of(
                // 1
                new Example(List.of(60, 64, 67), ChordAnalysis.Column.A_TRITONE_FREE, 60, 1),
                new Example(List.of(60, 63, 67), ChordAnalysis.Column.A_TRITONE_FREE, 60, 1),

                // 2
                new Example(List.of(60, 63, 68), ChordAnalysis.Column.A_TRITONE_FREE, 68, 2),
                new Example(List.of(60, 64, 69), ChordAnalysis.Column.A_TRITONE_FREE, 69, 2),
                new Example(List.of(60, 65, 69), ChordAnalysis.Column.A_TRITONE_FREE, 65, 2),
                new Example(List.of(60, 65, 68), ChordAnalysis.Column.A_TRITONE_FREE, 65, 2),
                new Example(List.of(60, 65, 68), ChordAnalysis.Column.A_TRITONE_FREE, 65, 2),

                // 3
                new Example(List.of(60, 64, 70), ChordAnalysis.Column.B_WITH_TRITONE, 60, 3),
                new Example(List.of(60, 64, 67, 70), ChordAnalysis.Column.B_WITH_TRITONE, 60, 3),

                // 4
                new Example(List.of(60, 64, 67, 70, 74), ChordAnalysis.Column.B_WITH_TRITONE, 60, 4),
                new Example(List.of(60, 64, 70, 74), ChordAnalysis.Column.B_WITH_TRITONE, 60, 4),
                new Example(List.of(60, 63, 67, 69), ChordAnalysis.Column.B_WITH_TRITONE, 60, 4),
                new Example(List.of(60, 63, 65, 67, 69), ChordAnalysis.Column.B_WITH_TRITONE, 60, 4),
                new Example(List.of(60, 64, 68, 70), ChordAnalysis.Column.B_WITH_TRITONE, 60, 4),
                new Example(List.of(60, 64, 66), ChordAnalysis.Column.B_WITH_TRITONE, 60, 4),
                new Example(List.of(60, 62, 64, 66), ChordAnalysis.Column.B_WITH_TRITONE, 60, 4),
                new Example(List.of(60, 62, 64, 70), ChordAnalysis.Column.B_WITH_TRITONE, 60, 4),
                new Example(List.of(60, 62, 64, 68), ChordAnalysis.Column.B_WITH_TRITONE, 60, 4),

                // 5
                new Example(List.of(60, 66, 70), ChordAnalysis.Column.B_WITH_TRITONE, 66, 5),
                new Example(List.of(60, 63, 66, 68), ChordAnalysis.Column.B_WITH_TRITONE, 68, 5),
                new Example(List.of(60, 63, 65, 69), ChordAnalysis.Column.B_WITH_TRITONE, 65, 5),
                new Example(List.of(60, 62, 66, 69), ChordAnalysis.Column.B_WITH_TRITONE, 62, 5),
                new Example(List.of(60, 62, 65, 68), ChordAnalysis.Column.B_WITH_TRITONE, 65, 5),
                new Example(List.of(60, 62, 66, 70), ChordAnalysis.Column.B_WITH_TRITONE, 62, 5),
                new Example(List.of(60, 64, 66, 69), ChordAnalysis.Column.B_WITH_TRITONE, 69, 5),
                new Example(List.of(60, 62, 68, 70), ChordAnalysis.Column.B_WITH_TRITONE, 68, 5),
                new Example(List.of(60, 62, 66), ChordAnalysis.Column.B_WITH_TRITONE, 62, 5),
                new Example(List.of(60, 62, 68), ChordAnalysis.Column.B_WITH_TRITONE, 68, 5),
                new Example(List.of(60, 63, 66, 70), ChordAnalysis.Column.B_WITH_TRITONE, 63, 5),

                // 6
                new Example(List.of(60, 64, 66, 70), ChordAnalysis.Column.B_WITH_TRITONE, 60, 6),
                new Example(List.of(60, 62, 64, 68, 70), ChordAnalysis.Column.B_WITH_TRITONE, 60, 6),
                new Example(List.of(60, 62, 66, 68), ChordAnalysis.Column.B_WITH_TRITONE, 62, 6),
                new Example(List.of(60, 62, 64, 66, 68, 70), ChordAnalysis.Column.B_WITH_TRITONE, 60, 6),

                // 7 9 eins
                new Example(List.of(60, 62, 67), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 65, 67), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 67, 69), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 67, 70), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 67, 71), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 62, 64, 67), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 64, 65, 67), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 64, 67, 69), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 64, 67, 68), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 63, 67, 68), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 62, 63, 67), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 62, 65, 67), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),

                // 7 9 zwei
                new Example(List.of(60, 63, 67, 70), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 63, 67, 71), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 64, 67, 71), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 64, 67, 71, 74), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 63, 67, 70, 74), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 67, 70, 77), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 63, 67, 70, 74, 77), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 67, 69, 71, 74), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 64, 75), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),
                new Example(List.of(60, 67, 74), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 67, 69, 76), ChordAnalysis.Column.A_TRITONE_FREE, 60, 7),
                new Example(List.of(60, 67, 68, 75), ChordAnalysis.Column.A_TRITONE_FREE, 60, 9),

                //8 10
                new Example(List.of(60, 62, 65), ChordAnalysis.Column.A_TRITONE_FREE, 65, 8),
                new Example(List.of(60, 62, 69), ChordAnalysis.Column.A_TRITONE_FREE, 62, 8),
                new Example(List.of(60, 62, 65, 69), ChordAnalysis.Column.A_TRITONE_FREE, 62, 8),
                new Example(List.of(60, 63, 70), ChordAnalysis.Column.A_TRITONE_FREE, 63, 8),
                new Example(List.of(60, 61, 65, 68), ChordAnalysis.Column.A_TRITONE_FREE, 61, 10),
                new Example(List.of(60, 65, 69, 70), ChordAnalysis.Column.A_TRITONE_FREE, 65, 10),
                new Example(List.of(60, 62, 69, 70), ChordAnalysis.Column.A_TRITONE_FREE, 62, 10),
                new Example(List.of(60, 62, 69, 71), ChordAnalysis.Column.A_TRITONE_FREE, 62, 10),
                new Example(List.of(60, 65, 74), ChordAnalysis.Column.A_TRITONE_FREE, 65, 8),
                new Example(List.of(60, 65, 68, 75), ChordAnalysis.Column.A_TRITONE_FREE, 68, 8),
                new Example(List.of(60, 65, 69, 76), ChordAnalysis.Column.A_TRITONE_FREE, 69, 10),
                new Example(List.of(60, 65, 69, 70, 74), ChordAnalysis.Column.A_TRITONE_FREE, 65, 10),

                //11
                new Example(List.of(60, 64, 67, 70, 73), ChordAnalysis.Column.B_WITH_TRITONE, 60, 11),
                new Example(List.of(60, 64, 70, 73), ChordAnalysis.Column.B_WITH_TRITONE, 60, 11),
                new Example(List.of(60, 66, 67, 73), ChordAnalysis.Column.B_WITH_TRITONE, 60, 11),
                new Example(List.of(60, 67, 68, 74), ChordAnalysis.Column.B_WITH_TRITONE, 60, 11),
                new Example(List.of(60, 64, 66, 67, 74), ChordAnalysis.Column.B_WITH_TRITONE, 60, 11),
                new Example(List.of(60, 66, 67, 73, 74, 80), ChordAnalysis.Column.B_WITH_TRITONE, 60, 11),

                //12
                new Example(List.of(60, 65, 66, 69, 75), ChordAnalysis.Column.B_WITH_TRITONE, 65, 12),
                new Example(List.of(60, 64, 65, 70, 73), ChordAnalysis.Column.B_WITH_TRITONE, 65, 12),
                new Example(List.of(60, 62, 66, 69, 73, 76), ChordAnalysis.Column.B_WITH_TRITONE, 62, 12),
                new Example(List.of(60, 61, 66, 69, 77), ChordAnalysis.Column.B_WITH_TRITONE, 66, 12),
                new Example(List.of(60, 64, 71, 77), ChordAnalysis.Column.B_WITH_TRITONE, 64, 12),
                new Example(List.of(60, 64, 66, 71), ChordAnalysis.Column.B_WITH_TRITONE, 64, 12),
                new Example(List.of(60, 64, 70, 75), ChordAnalysis.Column.B_WITH_TRITONE, 75, 12),

                //13
                new Example(List.of(60, 64, 68), ChordAnalysis.Column.A_TRITONE_FREE, 60, 13),
                new Example(List.of(60, 65, 70), ChordAnalysis.Column.A_TRITONE_FREE, 65, 13),

                //14
                new Example(List.of(60, 63, 66), ChordAnalysis.Column.B_WITH_TRITONE, 60, 14),
                new Example(List.of(61, 64, 67), ChordAnalysis.Column.B_WITH_TRITONE, 61, 14),
                new Example(List.of(60, 63, 69), ChordAnalysis.Column.B_WITH_TRITONE, 60, 14),
                new Example(List.of(60, 66, 69), ChordAnalysis.Column.B_WITH_TRITONE, 66, 14),
                new Example(List.of(60, 63, 66, 69), ChordAnalysis.Column.B_WITH_TRITONE, 60, 14)
        );
    }

    @Test
    @DisplayName("User-extendable examples: validate group (and optionally root/degree)")
    void analyze_userExtendableExamples() {
        ChordAnalysis ca = new ChordAnalysis();
        for (Example example : userExamples()) {
            var res = ca.analyze(example.notes());
            assertEquals(example.column(), res.column, "Group mismatch for " + example.notes());
            if (example.expectedRoot() != null) {
                assertTrue(res.rootNote.isPresent(), "Expected root present for " + example.notes());
                assertEquals(example.expectedRoot().intValue(), res.rootNote.get().intValue(), "Root mismatch for " + example.notes());
            }
            if (example.expectedGroup() != null) {
                assertTrue(res.group.isPresent(), "Expected degree present for " + example.notes());
                assertEquals(example.expectedGroup().intValue(), res.group.get().intValue(), "Degree mismatch for " + example.notes());
            }
        }
    }

    // Kleines DTO für die Beispiele
    private record Example(List<Integer> notes,
                           ChordAnalysis.Column column,
                           Integer expectedRoot,
                           Integer expectedGroup) {}
}
