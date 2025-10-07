package syrincs.a_domain.hindemith;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ChordSpecificationRepository {
    private Map<Integer, ChordSpecification> chordGroupSpecifications = new HashMap<>();

    public ChordSpecificationRepository() {
        fillChordGroupSpecifications();
    }

    /** New: group specifications 1..14 (mapping from legacy 0..13 by +1). */
    public Map<Integer, ChordSpecification> getGroupSpecifications() {
        Map<Integer, ChordSpecification> map = new LinkedHashMap<>();
        for (int i = 0; i <= 13; i++) {
            ChordSpecification cs = chordGroupSpecifications.get(i);
            if (cs != null) map.put(i + 1, cs);
        }
        return map;
    }
    private void fillChordGroupSpecifications() {
        // Define original 0..13 rules using the new Builder API for internal compatibility.
        chordGroupSpecifications.put(0, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 2, 6, 10, 11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) I. 1.

        chordGroupSpecifications.put(1, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 2, 6, 10, 11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) I. 2.

        chordGroupSpecifications.put(2, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 2, 11))
                .requireIntervals(Set.of(6, 10))
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // B) II. a

        chordGroupSpecifications.put(3, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 11))
                .requireIntervals(Set.of(2,6))
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // B) II. b 1.

        chordGroupSpecifications.put(4, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 11))
                .requireIntervals(6)
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .requireAllWithAlternatives(Set.of(2,10))
                .build()); // B) II. b 2. (6 AND (2 OR 10))

        chordGroupSpecifications.put(5, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 11))
                .requireIntervals(Set.of(2,6))
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))
                .requireMultipleTritones(true)
                .build()); // B) II. b 3.

        chordGroupSpecifications.put(6, ChordSpecification.builder()
                .excludeIntervals(Set.of(1,6,11))
                .requireAnyIntervals(Set.of(2,10))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) III. 1.1 (vgl. S. 127) mit gr. 2 und kl. 7 ist für Hi. wertvoller. als kl 2 & gr. 7

        chordGroupSpecifications.put(7, ChordSpecification.builder()
                .excludeIntervals(Set.of(1,6,11))
                .requireAnyIntervals(Set.of(2,10))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) III. 2.1 (vgl. S. 127)

        chordGroupSpecifications.put(8, ChordSpecification.builder()
                .excludeIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1,11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) III. 1.2 (vgl. S. 127) mit gr. 2 und kl. 7 ist für Hi. wertvoller. als kl 2 & gr. 7

        chordGroupSpecifications.put(9, ChordSpecification.builder()
                .excludeIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1,11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) III. 2.2 (vgl. S. 127)

        chordGroupSpecifications.put(10, ChordSpecification.builder()
                .requireIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1, 11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // B) IV. 1.

        chordGroupSpecifications.put(11, ChordSpecification.builder()
                .requireIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1, 11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // B) IV. 2.

        chordGroupSpecifications.put(12, ChordSpecification.builder()
                .layeringM3orP4(true)
                .build()); // A) V.

        chordGroupSpecifications.put(13, ChordSpecification.builder()
                .requireIntervals(Set.of(6))
                .dimOrDim7(true)
                .build()); // B) VI.
    }
}
