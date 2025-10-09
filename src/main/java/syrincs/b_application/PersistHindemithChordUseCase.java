package syrincs.b_application;

import syrincs.a_domain.chord.NoteCombinator;
import syrincs.a_domain.hindemith.ChordAnalysis;
import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.b_application.ports.HindemithChordRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Use case for generating Hindemith chords via NoteCombinator and persisting them through the repository port.
 *
 * Clean Architecture placement:
 * - This class is an application-layer interactor (b_application).
 * - It depends only on the repository port (interface) and domain types, not on concrete adapters.
 */
public class PersistHindemithChordUseCase {

    private final HindemithChordRepositoryPort repository;
    private final Logger LOGGER = Logger.getLogger(PersistHindemithChordUseCase.class.getName());

    public PersistHindemithChordUseCase(HindemithChordRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    /**
     * Generates all k-note chords within the given MIDI range and persists them.
     * Returns the generated database IDs in insertion order.
     */
    public List<Long> generateAndPersist(int k, int minLowerNote, int maxUpperNote) {
        NoteCombinator combinator = new NoteCombinator();
        List<List<Integer>> noteSets = combinator.generateChords(k, minLowerNote, maxUpperNote);
        List<Long> ids = new ArrayList<>(noteSets.size());
        ChordAnalysis analysis = new ChordAnalysis();
        for (List<Integer> notes : noteSets) {
            HindemithChord chord = new HindemithChord(notes); // constructor computes needed properties
            // Determine group via ChordAnalysis and attach to entity so the repository can persist it
            var result = analysis.analyze(notes);
            chord.setGroup(result.group);
            long id = repository.save(chord);
            ids.add(id);
        }
        return ids;
    }

    /**
     * Convenience method to generate and persist all triads in the range.
     */
    public List<Long> persistAllChordsToFiveNotes(int minLowerNote, int maxUpperNote) {
        List<Long> ids = new ArrayList<>();
        List<Integer> numNotes = new ArrayList<>(List.of(3, 4, 5));
        for(Integer numNote : numNotes) {
            List<Long> idsOfThisGroup = generateAndPersist(numNote, minLowerNote, maxUpperNote);
            ids.addAll(idsOfThisGroup);
            LOGGER.info("Generated " + idsOfThisGroup.size() + " ids with " + numNote + " notes.");
        }
        return ids;
    }
}
