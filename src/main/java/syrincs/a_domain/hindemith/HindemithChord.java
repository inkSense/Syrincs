package syrincs.a_domain.hindemith;

import syrincs.a_domain.chord.Chord;

import java.util.*;

public class HindemithChord extends Chord {

    private Integer rootNote = null;
    private final List<HindemithInterval> rootHindemithIntervals; //intervals from rootNote to remaining notes
    private int group;

    public HindemithChord(List<Integer> notes) {
        super(notes);
        this.rootNote = calculateRootNote(getAllIntervals());
        this.rootHindemithIntervals = calculateRootIntervals();
    }

    public Integer getRootNote() {
            return rootNote;
    }

    public int getGroup() {
        return group;
    }

    public List<HindemithInterval> getRootIntervals() {
        return rootHindemithIntervals;
    }
    private List<HindemithInterval> calculateRootIntervals(){
        // Provided that Intervals ar in the right order:
        return getAllIntervals().subList(0, getNotes().size() - 1);
    }

    private Integer calculateRootNote(List<HindemithInterval> hindemithIntervals){
        List<HindemithInterval> bestHindemithInterval = calculateBestInterval(hindemithIntervals);

        if (bestHindemithInterval.isEmpty()) {
            return null;
        }
        if (bestHindemithInterval.size() == 1) {
            return bestHindemithInterval.getFirst().getRootNote();
        }

        // Immer das unterste nehmen. Vgl. Unterweisung im Tonsatz S. 120
        return bestHindemithInterval.stream().mapToInt(HindemithInterval::getRootNote).min().orElse(bestHindemithInterval.getFirst().getRootNote());

    }

    private List<HindemithInterval> calculateBestInterval(List<HindemithInterval> hindemithIntervals){
        List<HindemithInterval> bestHindemithInterval = new ArrayList<>();

        // Wir berechnen die besten Intervalle basierend auf allen Notenpaaren
        for(HindemithInterval hindemithInterval : hindemithIntervals){
            if(bestHindemithInterval.isEmpty()){
                bestHindemithInterval.add(hindemithInterval);
                continue;
            }

            if ( bestHindemithInterval.getFirst().getQuality() > hindemithInterval.getQuality() && hindemithInterval.getQuality() != -1 ) {
                bestHindemithInterval.clear();
                bestHindemithInterval.add(hindemithInterval);
            } else if (bestHindemithInterval.getFirst().getQuality() == hindemithInterval.getQuality()) {
                bestHindemithInterval.add(hindemithInterval);
            }
        }
        return bestHindemithInterval;
    }

    public List<HindemithInterval> calculateRootIntervals(Set<Integer> notes) {
        //Dies hab ich gemacht, damit man Sets berechnen kann. Das ist für den Dissgrad 6: verm. und verm. 7.
        List<Integer> notesList = new ArrayList<>(notes);
        List<HindemithInterval> result = new ArrayList<>();
        for (int gap = 1; gap < notesList.size(); gap++) {
            HindemithInterval hindemithInterval = new HindemithInterval(notesList.getFirst(), notesList.get(gap));
            result.add(hindemithInterval);
        }
        return result;
    }



}

