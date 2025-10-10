package syrincs.b_application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syrincs.a_domain.hindemith.HindemithChord;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistHindemithChordUseCaseTest {

    private FakeHindemithChordRepository repo;
    private PersistHindemithChordUseCase uc;

    @BeforeEach
    void setup() {
        repo = new FakeHindemithChordRepository();
        uc = new PersistHindemithChordUseCase(repo);
    }

    @Test
    @DisplayName("generate: weist Gruppen zu und liefert Liste zurück")
    void generate_assignsGroups() {
        List<HindemithChord> chords = uc.generate(3, 60, 62, 2);
        assertEquals(1, chords.size(), "60..62 with k=3 should produce exactly 1 chord");
        HindemithChord c = chords.getFirst();
        assertNotNull(c.getGroup(), "Generated chords should be analyzed and have a group");
    }

    @Test
    @DisplayName("persist: speichert alle Chords und liefert IDs zurück")
    void persist_savesAndReturnsIds() {
        List<HindemithChord> chords = uc.generate(3, 60, 62, 2);
        List<Long> ids = uc.persist(chords);
        assertEquals(chords.size(), ids.size());
        assertTrue(ids.stream().allMatch(id -> repo.findById(id).isPresent()));
    }
}
