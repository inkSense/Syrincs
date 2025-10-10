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

    public List<HindemithChord> generate(int k, int minLowerNote, int maxUpperNote, int maxOctaves) {
        NoteCombinator combinator = new NoteCombinator();
        List<List<Integer>> noteSets = combinator.generateChords(k, minLowerNote, maxUpperNote);
        noteSets = combinator.keepWidthLessThanOctaves(noteSets, maxOctaves);
        List<HindemithChord> hindemithChords = new ArrayList<>();
        ChordAnalysis analysis = new ChordAnalysis();
        for (List<Integer> notes : noteSets) {
            HindemithChord chord = new HindemithChord(notes);
            var result = analysis.analyze(notes);
            chord.setGroup(result.group);
            hindemithChords.add(chord);
        }
        return hindemithChords;
    }


    public List<Long> persist(List<HindemithChord> chords) {
        List<Long> ids = new ArrayList<>(chords.size());
        for (HindemithChord chord : chords) {
            long id  = repository.save(chord);
            ids.add(id);
        }
        return ids;
    }


}
