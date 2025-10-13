package syrincs.b_application;

import syrincs.a_domain.hindemith.ChordAnalysis;
import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.a_domain.chord.NoteCombinator;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenerateChordsUseCase {
    private final NoteCombinator combinator;
    private final ChordAnalysis analysis;
    private final int maxOctaves;
    private final Logger LOGGER = Logger.getLogger(GenerateChordsUseCase.class.getName());

    public GenerateChordsUseCase(NoteCombinator combinator, ChordAnalysis analysis, int maxOctaves) {
        this.combinator = combinator;
        this.analysis = analysis;
        this.maxOctaves = maxOctaves;
    }

    public List<HindemithChord> generateChordsForThreeNotes(int minLowerNote, int maxUpperNote) {
        return generate(3, minLowerNote, maxUpperNote);
    }

    public List<HindemithChord> generateAllChordsToFiveNotes(int minLowerNote, int maxUpperNote) {
        List<HindemithChord> chords = new ArrayList<>();
        List<Integer> numNotes = new ArrayList<>(List.of(3, 4, 5));
        LOGGER.info("Starting Generation of Chords: " + numNotes + " notes");
        for(Integer numNote : numNotes) {
            chords.addAll(generate(numNote, minLowerNote, maxUpperNote));
            LOGGER.log(Level.INFO, "Chords with {} notes generated.", numNote );
        }
        return chords;
    }

    public List<HindemithChord> generate(int numNote, int minLowerNote, int maxUpperNote) {
        List<List<Integer>> noteSets = combinator.generateChords(numNote, minLowerNote, maxUpperNote);
        noteSets = combinator.keepWidthLessThanOctaves(noteSets, maxOctaves);
        return analysis.analyzeList(noteSets);
    }
}
