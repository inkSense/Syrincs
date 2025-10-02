package syrincs.a_domain.ChordCalculator;

import java.util.*;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import syrincs.a_domain.Interval;
import syrincs.a_domain.Scale.Scale;
import syrincs.a_domain.Scale.ScaleRepository;

public class ChordCalculator {

    private Map<Integer, Constraint> dissDegreeConstraints = new HashMap<>();


    public ChordCalculator() {

        dissDegreeConstraints.put(0, new Constraint(Set.of(1, 2, 6, 10, 11), null,  null, "==", false, false, false)); // A) I. 1.
        dissDegreeConstraints.put(1, new Constraint(Set.of(1, 2, 6, 10, 11), null,  null, "!=", false, false, false)); // A) I. 2.
        dissDegreeConstraints.put(2, new Constraint(Set.of(1, 2, 11), Set.of(6, 10),  Set.of(7,5,4,8,3,9), "==", false, false, false)); // B) II. a
        dissDegreeConstraints.put(3, new Constraint(Set.of(1, 11), Set.of(2,6),  Set.of(7,5,4,8,3,9), "==", false, false, false)); // B) II. b 1.
        dissDegreeConstraints.put(4, new Constraint(Set.of(1, 11), Set.of(2,6),  Set.of(7,5,4,8,3,9), "!=", false, false, false)); // B) II. b 2.
        dissDegreeConstraints.put(5, new Constraint(Set.of(1, 11), Set.of(2,6),  Set.of(7,5,4,8,3,9), null, true, false, false)); // B) II. b 3.
        dissDegreeConstraints.put(6, new Constraint(Set.of(1,6,11),  null, Set.of(2,10), "==", false, false, false)); // A) III. 1.1 (vgl. S. 127)
        dissDegreeConstraints.put(7, new Constraint(Set.of(1,6,11),  null, Set.of(2,10), "!=", false, false, false)); // A) III. 2.1 (vgl. S. 127)
        dissDegreeConstraints.put(8, new Constraint(Set.of(6),  null, Set.of(1,11), "==", false, false, false)); // A) III. 1.2 (vgl. S. 127)
        dissDegreeConstraints.put(9, new Constraint(Set.of(6),  null, Set.of(1,11), "!=", false, false, false)); // A) III. 2.2 (vgl. S. 127)
        dissDegreeConstraints.put(10, new Constraint(null, Set.of(6), Set.of(1, 11),  "==", false, false, false)); // B) IV. 1.
        dissDegreeConstraints.put(11, new Constraint(null, Set.of(6), Set.of(1, 11),  "!=", false, false, false)); // B) IV. 2.
        dissDegreeConstraints.put(12, new Constraint(null, null,  null, null, false, true, false)); // A) V.
        dissDegreeConstraints.put(13, new Constraint(null, Set.of(6),  null, null, false, false, true)); // B) VI.
    }


    public Map<Integer, Constraint> getDissDegreeConstraints() {
        return dissDegreeConstraints;
    }


    public List<Chord> generateChordsForThreeNotes(Scale scale, Constraint constraint, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 2; n0++) {
            if (!isInScale(n0, scale)) continue;
            for (int n1 = n0 + 1; n1 <= maxUpperNote - 1; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, constraint.getExcludeAll()) && isInScale(n1, scale) ) ) continue;
                for (int n2 = n1 + 1; n2 <= maxUpperNote; n2++) {
                    List<Integer> notes = Arrays.asList(n0, n1, n2);
                    Chord chord = new Chord(notes);
                    if ( !(checkIntervals(chord, constraint) &&
                                    isInScale(n2, scale) &&
                                    threeNotesAreDifferentFromEachOther(notes) &&
                                    rootNoteEqualsBassNote(n0, chord.getRootNote(), constraint.getRootNoteEqual())
                            )) continue;
                    chords.add(chord);
                }
            }
        }
        return chords;
    }

    public List<Chord> generateChordsForFourNotes(Scale scale, Constraint constraint, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 3; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 2; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, constraint.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 1; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for (int n3 = n2 + 1; n3 <= maxUpperNote; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if (!(checkIntervals(chord3, constraint) &&
                                isInScale(n3, scale) &&
                                threeNotesAreDifferentFromEachOther(notes3) &&
                                rootNoteEqualsBassNote(n0, chord3.getRootNote(), constraint.getRootNoteEqual())
                        )) continue;
                        chords.add(chord3);
                    }
                }
            }
        }
        return chords;
    }

    public List<Chord> generateChordsForFiveNotes(Scale scale, Constraint constraint, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 4; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 3; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, constraint.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 2; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for(int n3 = n2 +1; n3 <= maxUpperNote - 1; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if ( !( intervalsNotInSet(chord3.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n3, scale) ) ) continue;

                    for (int n4 = n3 + 1; n4 <= maxUpperNote; n4++) {
                            List<Integer> notes4 = Arrays.asList(n0, n1, n2, n3, n4);
                            Chord chord4 = new Chord(notes4);
                            if (!(checkIntervals(chord4, constraint) &&
                                    isInScale(n4, scale) &&
                                    threeNotesAreDifferentFromEachOther(notes4) &&
                                    rootNoteEqualsBassNote(n0, chord4.getRootNote(), constraint.getRootNoteEqual())
                            )) continue;
                            chords.add(chord4);
                        }
                    }
                }
            }
        }
        return chords;
    }

    public List<Chord> generateChordsForSixNotes(Scale scale, Constraint constraint, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 5; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 4; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, constraint.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 3; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for(int n3 = n2 +1; n3 <= maxUpperNote - 2; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if ( !( intervalsNotInSet(chord3.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n3, scale) ) ) continue;

                        for(int n4 = n3 +1; n4 <= maxUpperNote - 1; n4++) {
                            List<Integer> notes4 = Arrays.asList(n0, n1, n2, n3, n4);
                            Chord chord4 = new Chord(notes4);
                            if ( !( intervalsNotInSet(chord4.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n4, scale) ) ) continue;

                            for (int n5 = n4 + 1; n5 <= maxUpperNote; n5++) {
                                List<Integer> notes5 = Arrays.asList(n0, n1, n2, n3, n4, n5);
                                Chord chord5 = new Chord(notes5);
                                if (!(checkIntervals(chord5, constraint) &&
                                        isInScale(n5, scale) &&
                                        threeNotesAreDifferentFromEachOther(notes5) &&
                                        rootNoteEqualsBassNote(n0, chord5.getRootNote(), constraint.getRootNoteEqual())
                                )) continue;
                                chords.add(chord5);
                            }
                        }
                    }
                }
            }
        }
        return chords;
    }
    public List<Chord> generateChordsForSevenNotes(Scale scale, Constraint constraint, int minLowerNote,  int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 6; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 5; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, constraint.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 4; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for(int n3 = n2 +1; n3 <= maxUpperNote - 3; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if ( !( intervalsNotInSet(chord3.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n3, scale) ) ) continue;

                        for(int n4 = n3 +1; n4 <= maxUpperNote - 2; n4++) {
                            List<Integer> notes4 = Arrays.asList(n0, n1, n2, n3, n4);
                            Chord chord4 = new Chord(notes4);
                            if ( !( intervalsNotInSet(chord4.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n4, scale) ) ) continue;

                            for(int n5 = n4 +1; n5 <= maxUpperNote - 1; n5++) {
                                List<Integer> notes5 = Arrays.asList(n0, n1, n2, n3, n4, n5);
                                Chord chord5 = new Chord(notes5);
                                if ( !( intervalsNotInSet(chord5.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n5, scale) ) ) continue;

                                for (int n6 = n5 + 1; n6 <= maxUpperNote; n6++) {
                                    List<Integer> notes6 = Arrays.asList(n0, n1, n2, n3, n4, n5, n6);
                                    Chord chord6 = new Chord(notes6);
                                    if (!(checkIntervals(chord6, constraint) &&
                                            isInScale(n6, scale) &&
                                            threeNotesAreDifferentFromEachOther(notes6) &&
                                            rootNoteEqualsBassNote(n0, chord6.getRootNote(), constraint.getRootNoteEqual())
                                    )) continue;
                                    chords.add(chord6);
                                }
                            }
                        }
                    }
                }
            }
        }
        return chords;
    }

    public List<Chord> generateChordsForEightNotes(Scale scale, Constraint constraint, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 6; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 5; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, constraint.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 4; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for(int n3 = n2 +1; n3 <= maxUpperNote - 3; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if ( !( intervalsNotInSet(chord3.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n3, scale) ) ) continue;

                        for(int n4 = n3 +1; n4 <= maxUpperNote - 2; n4++) {
                            List<Integer> notes4 = Arrays.asList(n0, n1, n2, n3, n4);
                            Chord chord4 = new Chord(notes4);
                            if ( !( intervalsNotInSet(chord4.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n4, scale) ) ) continue;

                            for(int n5 = n4 +1; n5 <= maxUpperNote - 1; n5++) {
                                List<Integer> notes5 = Arrays.asList(n0, n1, n2, n3, n4, n5);
                                Chord chord5 = new Chord(notes5);
                                if ( !( intervalsNotInSet(chord5.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n5, scale) ) ) continue;

                                for(int n6 = n5 +1; n6 <= maxUpperNote - 1; n6++) {
                                    List<Integer> notes6 = Arrays.asList(n0, n1, n2, n3, n4, n5, n6);
                                    Chord chord6 = new Chord(notes6);
                                    if ( !( intervalsNotInSet(chord6.getAllIntervals(), constraint.getExcludeAll() ) && isInScale(n6, scale) ) ) continue;

                                    for (int n7 = n6 + 1; n7 <= maxUpperNote; n7++) {
                                        List<Integer> notes7 = Arrays.asList(n0, n1, n2, n3, n4, n5, n6, n7);
                                        Chord chord7 = new Chord(notes7);
                                        if (!(checkIntervals(chord7, constraint) &&
                                                isInScale(n7, scale) &&
                                                threeNotesAreDifferentFromEachOther(notes7) &&
                                                rootNoteEqualsBassNote(n0, chord7.getRootNote(), constraint.getRootNoteEqual())
                                        )) continue;
                                        chords.add(chord7);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return chords;
    }



    private boolean intervalNotInSet(int interval, Set excludeSet){
        return excludeSet == null || !excludeSet.contains( interval )  ;
    }


    private boolean intervalsNotInSet(List<Interval> intervals, Set excludeSet){
        List<Integer> differences = intervals.stream().map(i-> i.getDifferenceWithoutOctavations()).collect(Collectors.toList());
        return excludeSet == null || Collections.disjoint(differences, excludeSet) ;
    }
    private static boolean isInScale(int note, Scale scale){
        return scale.getNotesbyOctave(-1).contains(note % 12);
    }

    private boolean layersOfMajor3rdOrPerfect4th(List<Interval> intervals, boolean constraintValue) {
        Set<Integer> setOfMod4 = intervals.stream().map(n -> n.getDifferenceWithoutOctavations() % 4).collect(Collectors.toSet());
        Set<Integer> setOfMod5 = intervals.stream().map(n -> n.getRealDifference() % 5).collect(Collectors.toSet());
        boolean returnValue = constraintValue == ( setOfMod4.size() == 1 && setOfMod4.contains(0) ) || ( setOfMod5.size() == 1 && setOfMod5.contains(0) );
        return returnValue;
    }

    private boolean dimOrDim7(List<Interval> intervals, boolean constraintValue){


        //Set<Integer> notes = chord.getNotesWithoutOcatavation();
        //List<Integer> intervals = chord.calculateRootIntervals(notes);
        Set<Integer> setOfMod3 = intervals.stream().map(n -> n.getRealDifference() % 3).collect(Collectors.toSet());
        boolean returnValue = constraintValue == ( setOfMod3.size() == 1 && setOfMod3.contains(0) ) ;
        return returnValue;
        //return intervals == List.of(3,6) || intervals == List.of(3,9) || intervals == List.of(6,9) || intervals == List.of(3,6,9);
    }

    private boolean includesAtLeastOneOf(List<Interval> intervals, Set includeSet){
        Set<Integer> differencesWithoutOctavations = intervals.stream().map(i->i.getDifferenceWithoutOctavations()).collect(Collectors.toSet());
        //differencesWithoutOctavations.retainAll(includeSet);
        return includeSet == null || includeSet.stream().anyMatch(n-> differencesWithoutOctavations.contains(n));
    }

    private boolean includesAll(List<Interval> intervals, Set includeSet){
        List<Integer> differences = intervals.stream().map(i-> i.getDifferenceWithoutOctavations()).collect(Collectors.toList());
        return includeSet == null || differences.containsAll(includeSet);
    }

    private boolean hatMehrereTritoni(List<Interval> intervals, boolean constraintValue){
        //List<Interval> tritoni = intervals.stream().filter(i -> i.getDifferenceWithoutOctavations() == 6).collect(Collectors.toList());
        return constraintValue == false || intervals.stream().filter(i -> i.getDifferenceWithoutOctavations() == 6).count() >= 2;
    }

    private boolean checkIntervals(Chord chord, Constraint constraint) {
        List<Interval> allIntervals = chord.getAllIntervals();
        List<Interval> rootIntervals = chord.getRootIntervals();
        //List<Integer> notes = chord.getNotes();



        return  intervalsNotInSet(allIntervals, constraint.getExcludeAll()) &&
                layersOfMajor3rdOrPerfect4th(rootIntervals, constraint.getLayersOfMajor3OrPerfect4()) &&
                dimOrDim7(rootIntervals, constraint.getDimOrDim7()) &&
                includesAtLeastOneOf(allIntervals, constraint.getIncludeAtLeastOneOf()) &&
                includesAll(allIntervals, constraint.getIncludeAll()) &&
                hatMehrereTritoni(chord.calculateAllIntervalsOfPitchClasses(), constraint.getMehrereTritoni());
    }

    private boolean tritonusIstUntergeordnet(List<Interval> intervals){
        // Zum Begriff des Übergeordneten Tritonus steht bei Hindemith auf Seite 124:
        // "Als Erfahrungstatsache steht fest, dass der Tritonus, wenn er mit anderen Intervallen zum Akkord verbunden wird, sich der Herrschaft der stärksten Intervalle aus der Reihe 2 beugt."
        // "Die ersten beiden Paare (Quinte — Quarte, gr. Terz — kl. Sexte) unterdrücken seine Unbestimmtheit, überlassen sich aber willig der ihm eigenen Zielstrebigkeit. [...] "
        // "Das nächste Paar (kl. Terz — gr. Sexte) hat nicht mehr so viel harmonische Kraft, um die Unbestimmtheit des Tritonus in sich zu einem harmonisch sicheren Klang umzuformen."
        // "Ein Akkord, der außer dem Tritonus kein besseres Intervall als eines dieser beiden enthält, bleibt deshalb so unbestimmt wie der Tritonus selbst. [...] "

        List<Integer> sublist = Interval.intervalsSortedByQualityFirst.subList(0,4);
        for(Interval interval : intervals){
            if( sublist.contains( interval.getDifferenceWithoutOctavations() )){
                return true;
            }
        }
        return false;
    }

    private boolean threeNotesAreDifferentFromEachOther(List<Integer> notes){
        final int minNumberPitchClasses = 3; //If there are 4 Notes only one should double. If 5, only 2 (or less).
        Set<Integer> notesAsSet = notes.stream().map(n -> n % 12).collect(Collectors.toSet());
        return notesAsSet.size() >= minNumberPitchClasses;
    }

    private boolean rootNoteEqualsBassNote(int firstNote, Integer rootNote, String rootNoteEqualString){
        if(rootNoteEqualString == null){
            return true;
        } else {
            if( rootNoteEqualString.equals("==") ){
                return firstNote == rootNote;
            } else {
                return firstNote != rootNote;
            }
        }
    }

    public static void main(String[] args){
        ChordCalculator chordCalculator = new ChordCalculator();
        Scale scale = new ScaleRepository().getScales().get(0);
        Chord chord = new Chord(List.of(53,57,59));
        List<Integer> notes = chord.getNotes();
        int interval = notes.get(1) - notes.get(0);
        List<Interval> allIntervals = chord.getAllIntervals();
        List<Interval> rootIntervals = chord.getRootIntervals();
        int dissDegree = 3;

        Constraint constraint = chordCalculator.getDissDegreeConstraints().get(dissDegree); //Nimmt Dissdegree entgegenen, liefert constraint

        System.out.println( " "+
                chordCalculator.isInScale(notes.get(0), scale) + " " + chordCalculator.isInScale(notes.get(1), scale) + " "+ chordCalculator.isInScale(notes.get(2), scale) + " " +
                chordCalculator.intervalNotInSet(interval, constraint.getExcludeAll()) + " " +
                chordCalculator.intervalsNotInSet(allIntervals, constraint.getExcludeAll()) + " "+
                chordCalculator.layersOfMajor3rdOrPerfect4th(rootIntervals, constraint.getLayersOfMajor3OrPerfect4()) + " "+
                chordCalculator.dimOrDim7(rootIntervals, constraint.getDimOrDim7()) +" "+
                chordCalculator.includesAtLeastOneOf(allIntervals, constraint.getIncludeAtLeastOneOf()) +" "+
                chordCalculator.includesAll(allIntervals, constraint.getIncludeAll()) +" "+
                chordCalculator.hatMehrereTritoni(allIntervals, constraint.getMehrereTritoni())


        );

    }
}
