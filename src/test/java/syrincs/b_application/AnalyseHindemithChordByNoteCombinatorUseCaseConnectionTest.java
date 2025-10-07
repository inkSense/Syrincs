package syrincs.b_application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syrincs.a_domain.hindemith.ChordAnalysis;
import syrincs.c_adapters.JdkMidiOutputAdapter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalyseHindemithChordByNoteCombinatorUseCaseConnectionTest {

    private UseCaseInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new UseCaseInteractor(new JdkMidiOutputAdapter());
    }

    @Test
    @DisplayName("analyzeChordByHindemith: C-E-G yields column A, root C, group 1")
    void analyzeMajorTriad() {
        var res = interactor.analyzeChordByHindemith(List.of(60, 64, 67));
        assertEquals(ChordAnalysis.Column.A_TRITONE_FREE, res.column);
        assertTrue(res.rootNote.isPresent());
        assertEquals(60, res.rootNote.get());
        assertTrue(res.group.isPresent());
        assertEquals(1, res.group.get());
        assertEquals(List.of(60, 64, 67), res.notes);
    }

    @Test
    @DisplayName("analyzeChordByHindemith: tritone chord [60,66,69] yields column B, root 66, group 14")
    void analyzeTritoneChord() {
        var res = interactor.analyzeChordByHindemith(List.of(60, 66, 69));
        assertEquals(ChordAnalysis.Column.B_WITH_TRITONE, res.column);
        assertTrue(res.rootNote.isPresent());
        assertEquals(66, res.rootNote.get());
        assertTrue(res.group.isPresent());
        assertEquals(14, res.group.get());
        assertEquals(List.of(60, 66, 69), res.notes);
    }
}
