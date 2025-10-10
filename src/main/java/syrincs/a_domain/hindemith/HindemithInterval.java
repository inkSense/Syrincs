package syrincs.a_domain.hindemith;

import syrincs.a_domain.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HindemithInterval extends Interval {


    public static final List<Integer> intervalsSortedByQualityFirst = List.of(7, 5, 4, 8, 3, 9, 2, 10, 1, 11, 6); //Die Quinte ist das Beste Interval, der Tritonus das Schlechteste
    private final int quality;
    private final List<Integer> chooseLowerNoteAsRootNote = List.of(7, 4, 3, 10, 11,6); //Der Grundton ist bei diesen Intervallen unten. oben: 5,8,9,2,1.
    private final int rootNote;


    public HindemithInterval(int lowNote, int highNote) {
        super(lowNote, highNote);
        this.quality = intervalsSortedByQualityFirst.indexOf(getDifferenceWithoutOctavations());
        this.rootNote = chooseLowerNoteAsRootNote.contains(getDifferenceWithoutOctavations()) ? lowNote : highNote;
    }

    public int getQuality(){
        return quality;
    }

    public int getRootNote(){
        return rootNote;
    }



}

