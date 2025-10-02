package syrincs.a_domain.ChordCalculator;

import syrincs.a_domain.Interval;

import java.util.*;
import java.util.stream.Collectors;

public class Chord {
    private final List<Integer> notes;
    private Integer rootNote = null;
    private List<String> associatedScales = new ArrayList<>(); // Liste der Namen der zugrundeliegenden Skalen
    private final List<Interval> allIntervals;
    private List<Interval> rootIntervals; //intervals from rootNote to remaining notes
    private int dissDegree;
    private final int frameInterval;


    public Chord(List<Integer> notes) {
        this.notes = notes;
        this.frameInterval = Collections.max(notes) - Collections.min(notes);
        this.allIntervals =  calculateAllIntervals();
        this.rootNote = calculateRootNote(this.allIntervals);
    }

    public List<Integer> getNotes() {
        return notes;
    }


    public Integer getRootNote() {
            return rootNote;
    }

    public int getNumNotes() {
        return notes.size();
    }

    public List<String> getAssociatedScales() {
        return associatedScales;
    }

    public int getDissDegree() {
        return dissDegree;
    }


    public List<Interval> getAllIntervals() {
        // Methode, um die Intervalle zwischen den Noten des Akkords zu berechnen
        if(allIntervals == null){
            calculateAllIntervals();
            return allIntervals;
        } else
            return allIntervals;
    }

    public int getFrameInterval(){
        return frameInterval;
    }



    public List<Interval> getRootIntervals() {
        if(rootIntervals == null) {
            rootIntervals = allIntervals.subList(0, notes.size() - 1);
            return rootIntervals;
        } else return rootIntervals;

    }

    public void setAssociatedScales(List<String> associatedScales) {
        this.associatedScales = associatedScales;
    }

    public void setDissDegree(int dissDegree) {
        this.dissDegree = dissDegree;
    }

    public List<Interval> calculateAllIntervals(){
        List<Interval> intervalList = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            for (int j = i + 1; j < notes.size(); j++) {
                Interval interval = new Interval(notes.get(i), notes.get(j));
                intervalList.add(interval);
            }
        }
        return intervalList;
    }
    
    public Integer calculateRootNote(List<Interval> intervals){
        List<Interval> bestInterval = calculateBestInterval(intervals);

        if (bestInterval.isEmpty()) {
            return null;
        }
        if (bestInterval.size() == 1) {
            return bestInterval.get(0).getRootNote();
        }

        boolean chooseLowest = bestInterval.get(0).getDifferenceWithoutOctavations() != 5; // ToDo: Warum noch mal?
        return chooseLowest
                ? bestInterval.stream().mapToInt(Interval::getRootNote).min().orElse(bestInterval.get(0).getRootNote())
                : bestInterval.stream().mapToInt(Interval::getRootNote).max().orElse(bestInterval.get(0).getRootNote());
    }

    private List<Interval> calculateBestInterval(List<Interval> intervals){
        List<Interval> bestInterval = new ArrayList<>();

        // Wir berechnen die besten Intervalle basierend auf allen Notenpaaren
        for(Interval interval : intervals){
            if(bestInterval.isEmpty()){
                bestInterval.add(interval);
                continue;
            }

            if ( bestInterval.get(0).getQuality() > interval.getQuality() && interval.getQuality() != -1 ) {
                bestInterval.clear();
                bestInterval.add(interval);
            } else if (bestInterval.get(0).getQuality() == interval.getQuality()) {
                bestInterval.add(interval);
            }
        }
        return bestInterval;
    }

    public List<Interval> calculateRootIntervals(Set<Integer> notes) {
        //Dies hab ich gemacht, damit man Sets berechnen kann. Das ist für den Dissgrad 6: verm. und verm. 7.
        List<Integer> notesList = new ArrayList<>(notes);
        List<Interval> result = new ArrayList<>();
        for (int gap = 1; gap < notesList.size(); gap++) {
            Interval interval = new Interval(notesList.get(0), notesList.get(gap));
            result.add(interval);
        }
        return result;
    }

    public List<Interval> calculateAllIntervalsOfPitchClasses(){
        List<Interval> intervalList = new ArrayList<>();
        Set<Integer> pitchClassesSet = notes.stream().map(n-> n%12).collect(Collectors.toSet());
        List<Integer> pitchClasses = pitchClassesSet.stream().toList();

        for (int i = 0; i < pitchClasses.size(); i++) {
            for (int gap = 1; i + gap < pitchClasses.size(); gap++) {
                Interval interval = new Interval(pitchClasses.get(i), pitchClasses.get(i+gap));
                intervalList.add(interval);
            }
        }
        return intervalList;
    }

    public String toString(){
        return notes.toString();
    }

}

