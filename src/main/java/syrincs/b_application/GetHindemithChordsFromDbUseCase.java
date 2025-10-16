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

    public List<HindemithChord> getAllOfRootNoteAndGroup(Integer rootNote, Integer group){
        return repository.getAllOfRootNoteAndGroup(rootNote, group);
    }

    public List<HindemithChord> getAllOfRootNoteAndMaxGroup(Integer rootNote, Integer maxGroup){
        return repository.getAllOfRootNoteAndMaxGroup(rootNote, maxGroup);
    }

    public List<HindemithChord> loadHindemithChordsWithGroups(Integer rootNote, List<Integer> groups){
        List<HindemithChord> hindemithChords = new ArrayList<>();
        for(Integer group : groups){
            repository.getAllOfRootNoteAndGroup(rootNote, group).forEach(hindemithChords::add);
        }
        return hindemithChords;
    }



    /**
     * Loads Hindemith chords filtered by a specific root note, a set of groups and allowed numNotes sizes.
     * Delegates to the repository for an efficient single-query fetch.
     * Returns an empty list if groups or numNotes are null/empty.
     */
    public List<HindemithChord> getAllOfRootNoteGroupsAndNumNotes(int rootNote, List<Integer> groups, List<Integer> numNotes) {
        if (groups == null || groups.isEmpty() || numNotes == null || numNotes.isEmpty()) {
            return List.of();
        }
        return repository.findByRootNoteAndGroupsAndNumNotes(rootNote, groups, numNotes);
    }

    /**
     * Same as above but additionally restricts the maximum chord span (range) defined as (maxNote - minNote).
     */
    public List<HindemithChord> getAllOfRootNoteGroupsAndNumNotes(int rootNote, List<Integer> groups, List<Integer> numNotes, int range) {
        if (groups == null || groups.isEmpty() || numNotes == null || numNotes.isEmpty()) {
            return List.of();
        }
        return repository.findByRootNoteAndGroupsAndNumNotesAndRange(rootNote, groups, numNotes, range);
    }

    /**
     * Loads a single chord by its id.
     */
    public Optional<HindemithChord> getById(long id) {
        return repository.findById(id);
    }
}
