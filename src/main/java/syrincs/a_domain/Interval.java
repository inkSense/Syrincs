package syrincs.a_domain;

import java.util.List;

public class Interval {
    private final int lowNote;
    private final int highNote;
    private final int realDifference;
    private final int differenceWithoutOctavations;
    private enum Harmonie  {KONSONANT, DISSONANT};
    private final Harmonie harmonie;
    private final List<Integer> konsonantIntervals = List.of(0,3,4,7,8); //Ohne Quarte, also wie Kontrapunkt
    public static final List<Integer> intervalsSortedByQualityFirst = List.of(7, 5, 4, 8, 3, 9, 2, 10, 1, 11, 6); //Die Quinte ist das Beste Interval, der Tritonus das Schlechteste
    private final int quality;
    private final List<Integer> chooseLowerNoteAsRootNote = List.of(7, 4, 3, 10, 11,6); //Der Grundton ist bei diesen Intervallen unten. oben: 5,8,9,2,1.
    private final int rootNote;


    public Interval(int lowNote, int highNote) {
        if(lowNote <= highNote){
            this.lowNote = lowNote;
            this.highNote = highNote;
        } else {
            this.highNote = lowNote;
            this.lowNote = highNote;
            try {
                throw new IllegalArgumentException("Variable lowNote ist größer als highNote. Die Werte wurden getauscht.");
            } catch (Exception e){
                System.out.println(e.getStackTrace()[0] + ": "+ e.getMessage());
            }

        }
        this.realDifference = (this.highNote - this.lowNote);
        this.differenceWithoutOctavations = realDifference % 12;
        if(konsonantIntervals.contains(differenceWithoutOctavations)){
            harmonie = Harmonie.KONSONANT;
        } else {
            harmonie = Harmonie.DISSONANT;
        }
        this.quality = intervalsSortedByQualityFirst.indexOf(differenceWithoutOctavations);
        this.rootNote = chooseLowerNoteAsRootNote.contains(differenceWithoutOctavations) ? lowNote : highNote;

    }

    public int getRealDifference() {
        return realDifference;
    }

    public int getDifferenceWithoutOctavations() {
        return differenceWithoutOctavations;
    }

    public Harmonie getHarmonie() {
        return harmonie;
    }

    public int getQuality(){
        return quality;
    }

    public int getRootNote(){
        return rootNote;
    }


}

