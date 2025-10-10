package syrincs.b_application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import syrincs.a_domain.hindemith.HindemithChord;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GetHindemithChordsFromDbUseCaseTest {

    private FakeHindemithChordRepository repo;
    private GetHindemithChordsFromDbUseCase uc;
    private long idCmaj;
    private long idDGroup5;
    private long idCRootGroup12;

    @BeforeEach
    void setup() {
        repo = new FakeHindemithChordRepository();
        uc = new GetHindemithChordsFromDbUseCase(repo);

        // Seed a few chords with pre-assigned root and group for filtering
        idCmaj = repo.put(new HindemithChord(List.of(60, 64, 67), 60, 1));
        idDGroup5 = repo.put(new HindemithChord(List.of(62, 66, 70), 62, 5));
        idCRootGroup12 = repo.put(new HindemithChord(List.of(60, 64, 71, 77), 60, 12));
    }

    @Test
    @DisplayName("getAll: returns all persisted chords")
    void getAll_returnsAll() {
        List<HindemithChord> all = uc.getAll();
        assertEquals(3, all.size());
    }

    @Test
    @DisplayName("getAllOf(groups): unions results for each group")
    void getAllOf_groups_union() {
        List<HindemithChord> res = uc.getAllOf(List.of(1, 5));
        assertEquals(2, res.size());
    }

    @Test
    @DisplayName("getAllOf(root): filters by root note")
    void getAllOf_rootNote_filter() {
        List<HindemithChord> res = uc.getAllOf(60);
        assertEquals(2, res.size());
    }

    @Test
    @DisplayName("getAllOfRootNoteAndGroup: filters by root+group")
    void getAllOf_rootAndGroup() {
        List<HindemithChord> res = uc.getAllOfRootNoteAndGroup(62, 5);
        assertEquals(1, res.size());
    }

    @Test
    @DisplayName("getAllOfRootNoteAndMaxGroup: filters by root and max group")
    void getAllOf_rootAndMaxGroup() {
        List<HindemithChord> res = uc.getAllOfRootNoteAndMaxGroup(60, 10);
        assertEquals(1, res.size()); // only the group 1 chord
    }

    @Test
    @DisplayName("getById: returns present when existing, empty when not")
    void getById_presentAndEmpty() {
        Optional<HindemithChord> found = uc.getById(idCmaj);
        assertTrue(found.isPresent());

        assertTrue(uc.getById(99999L).isEmpty());
    }
}
