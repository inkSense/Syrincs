package syrincs.a_domain.ChordCalculator;

import java.util.*;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import syrincs.a_domain.Interval;
import syrincs.a_domain.Scale.Scale;
import syrincs.a_domain.Scale.ScaleRepository;

public class ChordCalculator {

    private Map<Integer, ChordSpecification> dissDegreeConstraints = new HashMap<>();


    public ChordCalculator() {
        // Define original 0..13 rules using the new Builder API for internal compatibility.
        dissDegreeConstraints.put(0, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 2, 6, 10, 11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) I. 1.

        dissDegreeConstraints.put(1, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 2, 6, 10, 11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) I. 2.

        dissDegreeConstraints.put(2, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 2, 11))
                .requireIntervals(Set.of(6, 10))
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // B) II. a

        dissDegreeConstraints.put(3, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 11))
                .requireIntervals(Set.of(2,6))
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // B) II. b 1.

        dissDegreeConstraints.put(4, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 11))
                .requireIntervals(6)
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .requireAllWithAlternatives(Set.of(2,10))
                .build()); // B) II. b 2. (6 AND (2 OR 10))

        dissDegreeConstraints.put(5, ChordSpecification.builder()
                .excludeIntervals(Set.of(1, 11))
                .requireIntervals(Set.of(2,6))
                .requireAnyIntervals(Set.of(7,5,4,8,3,9))
                .requireMultipleTritones(true)
                .build()); // B) II. b 3.

        dissDegreeConstraints.put(6, ChordSpecification.builder()
                .excludeIntervals(Set.of(1,6,11))
                .requireAnyIntervals(Set.of(2,10))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) III. 1.1 (vgl. S. 127) mit gr. 2 und kl. 7 ist für Hi. wertvoller. als kl 2 & gr. 7

        dissDegreeConstraints.put(7, ChordSpecification.builder()
                .excludeIntervals(Set.of(1,6,11))
                .requireAnyIntervals(Set.of(2,10))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) III. 2.1 (vgl. S. 127)

        dissDegreeConstraints.put(8, ChordSpecification.builder()
                .excludeIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1,11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // A) III. 1.2 (vgl. S. 127) mit gr. 2 und kl. 7 ist für Hi. wertvoller. als kl 2 & gr. 7

        dissDegreeConstraints.put(9, ChordSpecification.builder()
                .excludeIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1,11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // A) III. 2.2 (vgl. S. 127)

        dissDegreeConstraints.put(10, ChordSpecification.builder()
                .requireIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1, 11))
                .rootRelation(ChordSpecification.RootRelation.EQUALS_BASS)
                .build()); // B) IV. 1.

        dissDegreeConstraints.put(11, ChordSpecification.builder()
                .requireIntervals(Set.of(6))
                .requireAnyIntervals(Set.of(1, 11))
                .rootRelation(ChordSpecification.RootRelation.NOT_EQUALS_BASS)
                .build()); // B) IV. 2.

        dissDegreeConstraints.put(12, ChordSpecification.builder()
                .layeringM3orP4(true)
                .build()); // A) V.

        dissDegreeConstraints.put(13, ChordSpecification.builder()
                .requireIntervals(Set.of(6))
                .dimOrDim7(true)
                .build()); // B) VI.
    }


    public Map<Integer, ChordSpecification> getDissDegreeConstraints() {
        return dissDegreeConstraints;
    }

    /** New: group specifications 1..14 (mapping from legacy 0..13 by +1). */
    public Map<Integer, ChordSpecification> getGroupSpecifications() {
        Map<Integer, ChordSpecification> map = new LinkedHashMap<>();
        for (int i = 0; i <= 13; i++) {
            ChordSpecification cs = dissDegreeConstraints.get(i);
            if (cs != null) map.put(i + 1, cs);
        }
        return map;
    }


    public List<Chord> generateChordsForThreeNotes(Scale scale, ChordSpecification chordSpecification, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 2; n0++) {
            if (!isInScale(n0, scale)) continue;
            for (int n1 = n0 + 1; n1 <= maxUpperNote - 1; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, chordSpecification.getExcludeAll()) && isInScale(n1, scale) ) ) continue;
                for (int n2 = n1 + 1; n2 <= maxUpperNote; n2++) {
                    List<Integer> notes = Arrays.asList(n0, n1, n2);
                    Chord chord = new Chord(notes);
                    if (!(syrincs.a_domain.hindemith.ChordRules.matches(chord, n0, chordSpecification)
                            && isInScale(n2, scale)
                            && threeNotesAreDifferentFromEachOther(notes)
                    )) continue;
                    chords.add(chord);
                }
            }
        }
        return chords;
    }

    public List<Chord> generateChordsForFourNotes(Scale scale, ChordSpecification chordSpecification, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 3; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 2; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, chordSpecification.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 1; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for (int n3 = n2 + 1; n3 <= maxUpperNote; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if (!(syrincs.a_domain.hindemith.ChordRules.matches(chord3, n0, chordSpecification)
                                && isInScale(n3, scale)
                                && threeNotesAreDifferentFromEachOther(notes3)
                        )) continue;
                        chords.add(chord3);
                    }
                }
            }
        }
        return chords;
    }

    public List<Chord> generateChordsForFiveNotes(Scale scale, ChordSpecification chordSpecification, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 4; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 3; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, chordSpecification.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 2; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for(int n3 = n2 +1; n3 <= maxUpperNote - 1; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if ( !( intervalsNotInSet(chord3.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n3, scale) ) ) continue;

                    for (int n4 = n3 + 1; n4 <= maxUpperNote; n4++) {
                            List<Integer> notes4 = Arrays.asList(n0, n1, n2, n3, n4);
                            Chord chord4 = new Chord(notes4);
                            if (!(syrincs.a_domain.hindemith.ChordRules.matches(chord4, n0, chordSpecification)
                                    && isInScale(n4, scale)
                                    && threeNotesAreDifferentFromEachOther(notes4)
                            )) continue;
                            chords.add(chord4);
                        }
                    }
                }
            }
        }
        return chords;
    }

    public List<Chord> generateChordsForSixNotes(Scale scale, ChordSpecification chordSpecification, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 5; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 4; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, chordSpecification.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 3; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for(int n3 = n2 +1; n3 <= maxUpperNote - 2; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if ( !( intervalsNotInSet(chord3.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n3, scale) ) ) continue;

                        for(int n4 = n3 +1; n4 <= maxUpperNote - 1; n4++) {
                            List<Integer> notes4 = Arrays.asList(n0, n1, n2, n3, n4);
                            Chord chord4 = new Chord(notes4);
                            if ( !( intervalsNotInSet(chord4.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n4, scale) ) ) continue;

                            for (int n5 = n4 + 1; n5 <= maxUpperNote; n5++) {
                                List<Integer> notes5 = Arrays.asList(n0, n1, n2, n3, n4, n5);
                                Chord chord5 = new Chord(notes5);
                                if (!(syrincs.a_domain.hindemith.ChordRules.matches(chord5, n0, chordSpecification)
                                        && isInScale(n5, scale)
                                        && threeNotesAreDifferentFromEachOther(notes5)
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
    public List<Chord> generateChordsForSevenNotes(Scale scale, ChordSpecification chordSpecification, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 6; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 5; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, chordSpecification.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 4; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for(int n3 = n2 +1; n3 <= maxUpperNote - 3; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if ( !( intervalsNotInSet(chord3.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n3, scale) ) ) continue;

                        for(int n4 = n3 +1; n4 <= maxUpperNote - 2; n4++) {
                            List<Integer> notes4 = Arrays.asList(n0, n1, n2, n3, n4);
                            Chord chord4 = new Chord(notes4);
                            if ( !( intervalsNotInSet(chord4.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n4, scale) ) ) continue;

                            for(int n5 = n4 +1; n5 <= maxUpperNote - 1; n5++) {
                                List<Integer> notes5 = Arrays.asList(n0, n1, n2, n3, n4, n5);
                                Chord chord5 = new Chord(notes5);
                                if ( !( intervalsNotInSet(chord5.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n5, scale) ) ) continue;

                                for (int n6 = n5 + 1; n6 <= maxUpperNote; n6++) {
                                    List<Integer> notes6 = Arrays.asList(n0, n1, n2, n3, n4, n5, n6);
                                    Chord chord6 = new Chord(notes6);
                                    if (!(syrincs.a_domain.hindemith.ChordRules.matches(chord6, n0, chordSpecification)
                                            && isInScale(n6, scale)
                                            && threeNotesAreDifferentFromEachOther(notes6)
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

    public List<Chord> generateChordsForEightNotes(Scale scale, ChordSpecification chordSpecification, int minLowerNote, int maxUpperNote) {
        List<Chord> chords = new ArrayList<>();
        for (int n0 = minLowerNote; n0 <= maxUpperNote - 6; n0++) {
            if (!isInScale(n0, scale)) continue;

            for (int n1 = n0 + 1; n1 <= maxUpperNote - 5; n1++) {
                int interval = (n1 - n0) % 12;
                if( !( intervalNotInSet(interval, chordSpecification.getExcludeAll()) && isInScale(n1, scale) ) ) continue;

                for(int n2 = n1 +1; n2 <= maxUpperNote - 4; n2++) {
                    List<Integer> notes2 = Arrays.asList(n0, n1, n2);
                    Chord chord2 = new Chord(notes2);
                    if ( !( intervalsNotInSet(chord2.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n2, scale) ) ) continue;

                    for(int n3 = n2 +1; n3 <= maxUpperNote - 3; n3++) {
                        List<Integer> notes3 = Arrays.asList(n0, n1, n2, n3);
                        Chord chord3 = new Chord(notes3);
                        if ( !( intervalsNotInSet(chord3.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n3, scale) ) ) continue;

                        for(int n4 = n3 +1; n4 <= maxUpperNote - 2; n4++) {
                            List<Integer> notes4 = Arrays.asList(n0, n1, n2, n3, n4);
                            Chord chord4 = new Chord(notes4);
                            if ( !( intervalsNotInSet(chord4.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n4, scale) ) ) continue;

                            for(int n5 = n4 +1; n5 <= maxUpperNote - 1; n5++) {
                                List<Integer> notes5 = Arrays.asList(n0, n1, n2, n3, n4, n5);
                                Chord chord5 = new Chord(notes5);
                                if ( !( intervalsNotInSet(chord5.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n5, scale) ) ) continue;

                                for(int n6 = n5 +1; n6 <= maxUpperNote - 1; n6++) {
                                    List<Integer> notes6 = Arrays.asList(n0, n1, n2, n3, n4, n5, n6);
                                    Chord chord6 = new Chord(notes6);
                                    if ( !( intervalsNotInSet(chord6.getAllIntervals(), chordSpecification.getExcludeAll() ) && isInScale(n6, scale) ) ) continue;

                                    for (int n7 = n6 + 1; n7 <= maxUpperNote; n7++) {
                                        List<Integer> notes7 = Arrays.asList(n0, n1, n2, n3, n4, n5, n6, n7);
                                        Chord chord7 = new Chord(notes7);
                                        if (!(syrincs.a_domain.hindemith.ChordRules.matches(chord7, n0, chordSpecification)
                                                && isInScale(n7, scale)
                                                && threeNotesAreDifferentFromEachOther(notes7)
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
        if (includeSet == null || (includeSet instanceof java.util.Collection && ((java.util.Collection<?>) includeSet).isEmpty())) {
            return true;
        }
        Set<Integer> differencesWithoutOctavations = intervals.stream().map(i->i.getDifferenceWithoutOctavations()).collect(Collectors.toSet());
        return includeSet.stream().anyMatch(n-> differencesWithoutOctavations.contains(n));
    }

    private boolean includesAll(List<Interval> intervals, Set includeSet){
        List<Integer> differences = intervals.stream().map(i-> i.getDifferenceWithoutOctavations()).collect(Collectors.toList());
        return includeSet == null || differences.containsAll(includeSet);
    }

    private boolean includesAllWithAlternatives(List<Interval> intervals, List<Set<Integer>> groups) {
        if (groups == null || groups.isEmpty()) return true;
        java.util.Set<Integer> diffs = intervals.stream()
                .map(i -> i.getDifferenceWithoutOctavations())
                .collect(java.util.stream.Collectors.toSet());
        for (java.util.Set<Integer> group : groups) {
            if (group == null || group.isEmpty()) continue; // leere Gruppen ignorieren
            boolean any = group.stream().anyMatch(diffs::contains);
            if (!any) return false;
        }
        return true;
    }

    private boolean hatMehrereTritoni(List<Interval> intervals, boolean constraintValue){
        //List<Interval> tritoni = intervals.stream().filter(i -> i.getDifferenceWithoutOctavations() == 6).collect(Collectors.toList());
        return constraintValue == false || intervals.stream().filter(i -> i.getDifferenceWithoutOctavations() == 6).count() >= 2;
    }

    private boolean checkIntervals(Chord chord, ChordSpecification chordSpecification) {
        // Delegate interval-rule evaluation to shared utility to avoid duplication
        return syrincs.a_domain.hindemith.ChordRules.matchesIntervalsOnly(chord, chordSpecification);
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

        ChordSpecification chordSpecification = chordCalculator.getDissDegreeConstraints().get(dissDegree); //Nimmt Dissdegree entgegenen, liefert constraint

        System.out.println( " "+
                chordCalculator.isInScale(notes.get(0), scale) + " " + chordCalculator.isInScale(notes.get(1), scale) + " "+ chordCalculator.isInScale(notes.get(2), scale) + " " +
                chordCalculator.intervalNotInSet(interval, chordSpecification.getExcludeAll()) + " " +
                chordCalculator.intervalsNotInSet(allIntervals, chordSpecification.getExcludeAll()) + " "+
                chordCalculator.layersOfMajor3rdOrPerfect4th(rootIntervals, chordSpecification.getLayersOfMajor3OrPerfect4()) + " "+
                chordCalculator.dimOrDim7(rootIntervals, chordSpecification.getDimOrDim7()) +" "+
                chordCalculator.includesAtLeastOneOf(allIntervals, chordSpecification.getIncludeAtLeastOneOf()) +" "+
                chordCalculator.includesAll(allIntervals, chordSpecification.getIncludeAll()) +" "+
                chordCalculator.hatMehrereTritoni(allIntervals, chordSpecification.getMehrereTritoni())


        );

    }
}
