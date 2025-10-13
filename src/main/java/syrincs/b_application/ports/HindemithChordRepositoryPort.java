package syrincs.b_application.ports;

import syrincs.a_domain.hindemith.HindemithChord;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Application-side repository port for persisting and loading Hindemith chords.
 *
 * Clean Architecture placement:
 * - This interface is a driving/primary port and lives in the application layer (b_application/ports).
 * - Implementations belong to the outer layer (c_adapters), e.g. a Postgres adapter.
 */
public interface HindemithChordRepositoryPort {

    /**
     * Persists the chord and returns the generated database id.
     */
    long save(HindemithChord chord);

    /**
     * Persists all given chords efficiently (preferably batched) and returns generated ids in insertion order.
     * Implementations should use a single connection and transaction with JDBC batching where possible.
     */
    List<Long> saveAll(List<HindemithChord> chords);

    /**
     * Loads a chord by its database id.
     */
    Optional<HindemithChord> findById(long id);

    /**
     * Returns all persisted chords.
     */
    List<HindemithChord> findAll();

    /**
     * Deletes a chord by id. No-op if not present.
     */
    void deleteById(long id);

    /**
     * Truncates the hindemithChords table and restarts the identity/sequence.
     * Implementations should use a fast table-level TRUNCATE operation if possible.
     */
    void truncate();

    List<HindemithChord> getAllOf(Integer group);

    List<HindemithChord> getAllOfRootNote(Integer rootNote);

    List<HindemithChord> getAllOfRootNoteAndGroup(Integer rootNote, Integer group);

    List<HindemithChord> getAllOfRootNoteAndMaxGroup(Integer rootNote, Integer maxGroup);

    // New method: rootNote + multiple groups + multiple numNotes
    List<HindemithChord> findByRootNoteAndGroupsAndNumNotes(int rootNote,
                                                            Collection<Integer> groups,
                                                            Collection<Integer> numNotes);

}
