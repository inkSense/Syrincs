package syrincs.a_domain.chord;

import syrincs.a_domain.Interval;
import syrincs.a_domain.hindemith.HindemithInterval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Chord {
    private final List<Integer> notes;

    private List<String> associatedScales = new ArrayList<>(); // Liste der Namen der zugrundeliegenden Skalen
    private final List<Interval> allIntervals;
    private final int frameInterval;

    public Chord(List<Integer> notes) {
        this.notes = notes;
        this.frameInterval = Collections.max(notes) - Collections.min(notes);
        this.allIntervals = calculateAllIntervals();
    }


    private List<Interval> calculateAllIntervals(){
        List<Interval> intervals = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            for (int j = i + 1; j < notes.size(); j++) {
                Interval interval = new Interval(notes.get(i), notes.get(j));
                intervals.add(interval);
            }
        }
        return intervals;
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

    public List<Interval> getAllIntervals() {
        return allIntervals;
    }

    public int getFrameInterval(){
        return frameInterval;
    }

    public Chord transpose(Chord chord, int pitch){
        List<Integer> newNotes = chord.getNotes().stream().map(n -> n + pitch).toList();
        return new Chord(newNotes);
    }



    public String toString(){
        return notes.toString();
    }
}
