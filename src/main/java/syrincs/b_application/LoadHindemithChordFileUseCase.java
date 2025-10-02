package syrincs.b_application;

import syrincs.a_domain.hindemith.Hindemith;

public class LoadHindemithChordFileUseCase {

    Hindemith hindemith = new Hindemith();

    public void load(){
        hindemith.loadAllChordsFromFile(52, 68);
    }
}
