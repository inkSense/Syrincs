package syrincs.a_domain;


import syrincs.b_application.PersistHindemithChordUseCase;

import java.util.logging.Logger;

public class Interval {

    private final int lowNote;
    private final int highNote;
    private final int realDifference;
    private final int differenceWithoutOctavations;

    private final Logger LOGGER = Logger.getLogger(Interval.class.getName());
    public Interval(int lowNote, int highNote) {
        if(lowNote <= highNote){
            this.lowNote = lowNote;
            this.highNote = highNote;
        } else {
            this.highNote = lowNote;
            this.lowNote = highNote;
            LOGGER.info("Variable lowNote ist größer als highNote. Die Werte wurden getauscht.");
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
