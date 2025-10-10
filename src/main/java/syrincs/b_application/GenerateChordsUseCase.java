package syrincs.b_application;

import syrincs.a_domain.hindemith.ChordAnalysis;
import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.a_domain.chord.NoteCombinator;

import java.util.*;

public class GenerateChordsUseCase {
    NoteCombinator combinator;
    ChordAnalysis analysis;
    int maxOctaves;

    public GenerateChordsUseCase(NoteCombinator combinator, ChordAnalysis analysis, int maxOctaves) {
        this.combinator = combinator;
        this.analysis = analysis;
        this.maxOctaves = maxOctaves;
    }

    public List<HindemithChord> generateChordsForThreeNotes(int minLowerNote, int maxUpperNote) {
        List<List<Integer>> notelistsWithThreeNotes;
        notelistsWithThreeNotes = combinator.generateChords(3, minLowerNote, maxUpperNote);
        List<HindemithChord> hindemithChords = new ArrayList<>();
        for(List<Integer> chord:  notelistsWithThreeNotes) {
            hindemithChords.add(new HindemithChord(chord));
        }
        return hindemithChords;
    }

    public List<HindemithChord> generateAllChordsToFiveNotes(int minLowerNote, int maxUpperNote) {
        List<List<Integer>> noteSets = new ArrayList<>();
        List<Integer> numNotes = new ArrayList<>(List.of(3, 4, 5));
        for(Integer numNote : numNotes) {
            noteSets = combinator.generateChords(numNote, minLowerNote, maxUpperNote);
            noteSets = combinator.keepWidthLessThanOctaves(noteSets, maxOctaves);
        }
        return analysis.analyzeList(noteSets);
    }
}
