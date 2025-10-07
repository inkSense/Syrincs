package syrincs.a_domain.chord;

import syrincs.a_domain.hindemith.HindemithInterval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Chord {
    private final List<Integer> notes;

    private List<String> associatedScales = new ArrayList<>(); // Liste der Namen der zugrundeliegenden Skalen
    private final List<HindemithInterval> allHindemithIntervals;
    private final int frameInterval;

    public Chord(List<Integer> notes) {
        this.notes = notes;
        this.frameInterval = Collections.max(notes) - Collections.min(notes);
        this.allHindemithIntervals =  calculateAllIntervals();
    }

    private List<HindemithInterval> calculateAllIntervals(){
        List<HindemithInterval> hindemithIntervalList = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            for (int j = i + 1; j < notes.size(); j++) {
                HindemithInterval hindemithInterval = new HindemithInterval(notes.get(i), notes.get(j));
                hindemithIntervalList.add(hindemithInterval);
            }
        }
        return hindemithIntervalList;
    }

    public List<Integer> getNotes() {
        return notes;
    }

    public int getNumNotes() {
        return notes.size();
    }

    public List<String> getAssociatedScales() {
        return associatedScales;
    }

    public void setAssociatedScales(List<String> associatedScales) {
        this.associatedScales = associatedScales;
    }

    public List<HindemithInterval> getAllIntervals() {
        return allHindemithIntervals;
    }

    public int getFrameInterval(){
        return frameInterval;
    }

    public List<HindemithInterval> calculateAllIntervalsOfPitchClasses(){
        List<HindemithInterval> hindemithIntervalList = new ArrayList<>();
        Set<Integer> pitchClassesSet = notes.stream().map(n-> n%12).collect(Collectors.toSet());
        List<Integer> pitchClasses = pitchClassesSet.stream().toList();

        for (int i = 0; i < pitchClasses.size(); i++) {
            for (int gap = 1; i + gap < pitchClasses.size(); gap++) {
                HindemithInterval hindemithInterval = new HindemithInterval(pitchClasses.get(i), pitchClasses.get(i+gap));
                hindemithIntervalList.add(hindemithInterval);
            }
        }
        return hindemithIntervalList;
    }

    public String toString(){
        return notes.toString();
    }
}
