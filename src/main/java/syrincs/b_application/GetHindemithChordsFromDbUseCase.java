package syrincs.b_application;

import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.b_application.ports.HindemithChordRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Use case for reading Hindemith chords from the database via the repository port.
 *
 * Clean Architecture placement:
 * - Application-layer interactor (b_application)
 * - Depends only on the repository port and domain types
 */
public class GetHindemithChordsFromDbUseCase {

    private final HindemithChordRepositoryPort repository;

    public GetHindemithChordsFromDbUseCase(HindemithChordRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    /**
     * Returns all persisted Hindemith chords.
     */
    public List<HindemithChord> getAll() {
        return repository.findAll();
    }

    public List<HindemithChord> getAllOf(List<Integer> groups){
        List<HindemithChord> hindemithChords = new ArrayList<>();
        for (Integer group : groups) {
            hindemithChords.addAll(repository.getAllOf(group));
        }
        return hindemithChords;
    }

    public List<HindemithChord> getAllOf(Integer rootNote){
        return repository.getAllOfRootNote(rootNote);
    }

    /**
     * Loads a single chord by its id.
     */
    public Optional<HindemithChord> getById(long id) {
        return repository.findById(id);
    }
}
