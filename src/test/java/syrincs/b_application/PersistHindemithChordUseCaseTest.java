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
    @DisplayName("persist: speichert alle Chords und liefert IDs zurück")
    void persist_savesAndReturnsIds() {
        List<HindemithChord> chords = List.of(new HindemithChord(List.of(60, 64, 67), 60, 1));
        List<Long> ids = uc.persist(chords);
        assertEquals(chords.size(), ids.size());
        assertTrue(ids.stream().allMatch(id -> repo.findById(id).isPresent()));
    }
}
