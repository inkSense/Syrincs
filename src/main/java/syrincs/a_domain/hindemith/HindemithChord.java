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

    public HindemithChord(
            List<Integer> notes,
            Integer rootNote,
            Integer group) {
        super(notes);
        this.intervals = mapIntervals(getAllIntervals());
        this.rootHindemithIntervals = intervals.subList(0, notes.size() - 1);
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

    List<HindemithInterval> getAllHindemithIntervals(){
        return this.intervals;
    }

    public HindemithChord transpose(int pitch){
        List<Integer> newNotes = getNotes().stream().map(n -> n + pitch).toList();
        return new HindemithChord(
                newNotes,
                getRootNote() + pitch,
                getGroup());
    }


}

