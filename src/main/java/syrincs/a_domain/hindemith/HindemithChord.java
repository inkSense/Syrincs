package syrincs.a_domain.hindemith;

import syrincs.a_domain.Interval;
import syrincs.a_domain.chord.Chord;

import java.util.*;
import java.util.stream.Collectors;

public class HindemithChord extends Chord {

    private final List<HindemithInterval> intervals;
    private final Integer rootNote;
    private final Integer rootNotePitchClass;

    private final List<HindemithInterval> rootHindemithIntervals; //intervals from rootNote to remaining notes
    private Integer group; // 1..14, or null if unknown

    public HindemithChord(List<Integer> notes) {
        super(notes);
        this.intervals = mapIntervals(getAllIntervals());
        this.rootHindemithIntervals = calculateRootIntervals();
        this.rootNote = calculateRootNote(this.intervals);
        this.rootNotePitchClass = this.rootNote % 12;
        this.group = null;
    }

    public HindemithChord(List<Integer> notes, Integer rootNote, Integer group) {
        super(notes);
        this.intervals = mapIntervals(getAllIntervals());
        this.rootHindemithIntervals = calculateRootIntervals();
        this.rootNote = rootNote;
        this.rootNotePitchClass = this.rootNote % 12;
        this.group = group;
    }

    private List<HindemithInterval> mapIntervals(List<Interval> from){
        return from.stream()
                .map(i -> new HindemithInterval(i.getLowNote(), i.getHighNote()))
                .collect(Collectors.toList());
    }

    public Integer getRootNote() {
            return rootNote;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public List<HindemithInterval> getRootIntervals() {
        return rootHindemithIntervals;
    }
    private List<HindemithInterval> calculateRootIntervals(){
        // Provided that Intervals ar in the right order:
        return intervals.subList(0, getNotes().size() - 1);
    }

    private Integer calculateRootNote(List<HindemithInterval> hindemithIntervals){
        List<HindemithInterval> bestHindemithInterval = calculateBestInterval(hindemithIntervals);

        if (bestHindemithInterval.isEmpty()) {
            throw new IllegalArgumentException("No best hindemith interval found");
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


    public List<HindemithInterval> calculateAllIntervalsOfPitchClasses(){
        List<HindemithInterval> hindemithIntervalList = new ArrayList<>();
        Set<Integer> pitchClassesSet = getNotes().stream().map(n-> n%12).collect(Collectors.toSet());
        List<Integer> pitchClasses = pitchClassesSet.stream().toList();

        for (int i = 0; i < pitchClasses.size(); i++) {
            for (int gap = 1; i + gap < pitchClasses.size(); gap++) {
                HindemithInterval hindemithInterval = new HindemithInterval(pitchClasses.get(i), pitchClasses.get(i+gap));
                hindemithIntervalList.add(hindemithInterval);
            }
        }
        return hindemithIntervalList;
    }


}

