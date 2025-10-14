package syrincs.a_domain.hindemith;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChordSpecificationRepository {
    private final  Map<Integer, ChordSpecification> chordGroupSpecifications = new HashMap<>();

    public ChordSpecificationRepository() {
        fillChordGroupSpecifications();
    }

    public Map<Integer, ChordSpecification> getGroupSpecifications() {
        return chordGroupSpecifications;
    }

    private void fillChordGroupSpecifications() {
        chordGroupSpecifications.put(0, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 2, 6, 10, 11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) I. 1.

        chordGroupSpecifications.put(1, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 2, 6, 10, 11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) I. 2.

        chordGroupSpecifications.put(2, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 2, 11)) //check
                .requireIntervals(Set.of(6, 10)) //check
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))//check
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // B) II. a

        chordGroupSpecifications.put(3, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 11)) //check
                .requireIntervals(Set.of(6)) //check
                .requireAnyIntervals(Set.of(7,5,4,8,3,9)) //check
                .requireAnyIntervalsTwo(Set.of(2,10))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .requireMultipleTritones(false)
                .build()); // B) II. b 1.

        chordGroupSpecifications.put(4, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 11))
                .requireIntervals(6)
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))
                .requireAnyIntervalsTwo(Set.of(2,10))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)//check
                .build()); // B) II. b 2. (6 AND (2 OR 10))

        chordGroupSpecifications.put(5, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 11))//check
                .requireIntervals(Set.of(6))//check
                .requireAnyIntervals(Set.of(7,5,4,8,3,9)) //check
                .requireAnyIntervalsTwo(Set.of(2,10)) //check
                .requireMultipleTritones(true)
                .build()); // B) II. b 3.

        chordGroupSpecifications.put(6, ChordSpecification.builder()
                .excludeIntervals(Set.of(1,6,11))
                .requireAnyIntervals(Set.of(2,10))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) III. 1. i (vgl. S. 127) mit Sekunden und Septimen und wie Gruppe 1

        chordGroupSpecifications.put(7, ChordSpecification.builder()
                .excludeIntervals(Set.of(1,6))
                .requireAnyIntervals(Set.of(2,10,11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) III. 1. ii (vgl. S. 127) mit gr. Sekunden und kl. Septimen

        chordGroupSpecifications.put(8, ChordSpecification.builder()
                .excludeIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1,2,10,11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) III. 1. iii (vgl. S. 127) mit kl. Sekunden und gr. Septimen

        chordGroupSpecifications.put(9, ChordSpecification.builder()
                .excludeIntervals(Set.of(1,6,11))
                .requireAnyIntervals(Set.of(2,10))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) III. 1. i (vgl. S. 127) mit Sekunden und Septimen und wie Gruppe 2

        chordGroupSpecifications.put(10, ChordSpecification.builder()
                .excludeIntervals(Set.of(1,6))
                .requireAnyIntervals(Set.of(2,10,11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) III. 1. ii (vgl. S. 127) mit gr. Sekunden und kl. Septimen

        chordGroupSpecifications.put(11, ChordSpecification.builder()
                .excludeIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1,2,10,11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) III. 1. iii (vgl. S. 127) mit kl. Sekunden und gr. Septimen

        chordGroupSpecifications.put(12, ChordSpecification.builder()
                .requireIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1, 11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // B) IV. 1.

        chordGroupSpecifications.put(13, ChordSpecification.builder()
                .requireIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1, 11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // B) IV. 2.

        chordGroupSpecifications.put(14, ChordSpecification.builder()
                .layeringM3orP4(true)
                .build()); // A) V.

        chordGroupSpecifications.put(15, ChordSpecification.builder()
                .requireIntervals(Set.of(6))
                .dimOrDim7(true)
                .build()); // B) VI.
    }
}
