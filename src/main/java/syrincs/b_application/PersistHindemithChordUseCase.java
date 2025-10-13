package syrincs.b_application;

import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.b_application.ports.HindemithChordRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
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
        this.repository = repository;
    }


    public List<Long> persist(List<HindemithChord> chords) {
        LOGGER.log(Level.INFO, "Starting persistence of {0} Chords.", chords.size() );
        return repository.saveAll(chords);
    }


}
