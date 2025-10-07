package syrincs.a_domain;

import java.util.List;

public class Interval {

    private final int lowNote;
    private final int highNote;
    private final int realDifference;
    private final int differenceWithoutOctavations;


    public Interval(int lowNote, int highNote) {
        if(lowNote <= highNote){
            this.lowNote = lowNote;
            this.highNote = highNote;
        } else {
            this.highNote = lowNote;
            this.lowNote = highNote;
            try { // Todo: Das in Logger packen
                throw new IllegalArgumentException("Variable lowNote ist größer als highNote. Die Werte wurden getauscht.");
            } catch (Exception e){
                System.out.println(e.getStackTrace()[0] + ": "+ e.getMessage());
            }
        }
        this.realDifference = (this.highNote - this.lowNote);
        this.differenceWithoutOctavations = realDifference % 12;

    }

    public int getLowNote() {
        return lowNote;
    }

    public int getHighNote() {
        return highNote;
    }

    public int getRealDifference() {
        return realDifference;
    }

    public int getDifferenceWithoutOctavations() {
        return differenceWithoutOctavations;
    }



}
