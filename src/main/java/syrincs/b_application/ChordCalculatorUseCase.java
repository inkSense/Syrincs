package syrincs.b_application;

import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.a_domain.chord.NoteCombinator;

import java.util.*;

public class ChordCalculatorUseCase {


    public List<HindemithChord> generateChordsForThreeNotes(int minLowerNote, int maxUpperNote) {
        var noteCombinator = new NoteCombinator();
        List<List<Integer>> notelistsWithThreeNotes;
        notelistsWithThreeNotes = noteCombinator.generateChords(3, minLowerNote, maxUpperNote);
        List<HindemithChord> hindemithChords = new ArrayList<>();
        for(List<Integer> chord:  notelistsWithThreeNotes) {
            hindemithChords.add(new HindemithChord(chord));
        }
        return hindemithChords;
    }
}
