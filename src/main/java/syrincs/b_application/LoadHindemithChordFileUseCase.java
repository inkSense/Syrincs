package syrincs.b_application;

import syrincs.a_domain.ChordCalculator.Chord;
import syrincs.a_domain.hindemith.FrameIntervalRange;
import syrincs.a_domain.hindemith.Hindemith;
import syrincs.a_domain.Scale.*;

import java.util.List;

public class LoadHindemithChordFileUseCase {

    Hindemith hindemith = new Hindemith();

    public void load(){
        hindemith.loadAllChordsFromFile(52, 68);
    }

    public List<Chord> getSomeChords(){
        return hindemith.getChordListAnyDissDegree("cIonic", 5, FrameIntervalRange.INTERVALS_8_TO_12 );
    }
}
