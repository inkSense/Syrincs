package syrincs.a_domain.ChordCalculator;

import syrincs.a_domain.Scale.*;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;






public class Hindemith {
    private ScaleRepository scaleRepository = new ScaleRepository();
    private ChordCalculator chordCalculator = new ChordCalculator();

    private Map<String, Map< Integer, Map<Integer, Map<FrameIntervalRange, List<Chord>>>>> allChords = new HashMap<>(); //SkalenName, DissonanzGrad, numNotes, Rahmenintervall

    public ScaleRepository getScaleRepository() {
        return scaleRepository;
    }

    public Map<String, Map< Integer, Map<Integer, Map<FrameIntervalRange, List<Chord>>>>> getAllChords() {return allChords;}


    public List<Chord> getSpecificChordList(String scale, int dissDegree, int numNotes, FrameIntervalRange range){
        List<Chord> specificChordList = new ArrayList<>();
        if (allChords.containsKey(scale)) {
            Map<Integer, Map<Integer, Map<FrameIntervalRange, List<Chord>>>> dissDegreeMap = allChords.get(scale);
            if (dissDegreeMap != null && dissDegreeMap.containsKey(dissDegree)) {
                Map<Integer, Map<FrameIntervalRange, List<Chord>>> numNotesMap = dissDegreeMap.get(dissDegree);
                if (numNotesMap != null && numNotesMap.containsKey(numNotes)) {
                    Map<FrameIntervalRange, List<Chord>> frameIntervalMap = numNotesMap.get(numNotes);
                    if (frameIntervalMap != null) {
                        specificChordList = frameIntervalMap.get(range);
                    } else { System.out.println( "No such frameInterval." + range); }
                } else { System.out.println( "No such numNotesMap." + numNotes); }
            } else { System.out.println( "No such dissDegree." + dissDegree); }
        } else { System.out.println("No such Scale-Key." + scale); }
        return specificChordList;
    }
    
    /**
     * Liefert alle Akkorde für die angegebene Skala, Notenzahl und Rahmenintervall,
     * unabhängig vom Dissonanzgrad. Gibt niemals null zurück.
     */
    public List<Chord> getChordListAnyDissDegree(String scale, int numNotes, FrameIntervalRange range){
        List<Chord> result = new ArrayList<>();
        Map<Integer, Map<Integer, Map<FrameIntervalRange, List<Chord>>>> dissDegreeMap = allChords.get(scale);
        if (dissDegreeMap == null) {
            System.out.println("No such Scale-Key." + scale);
            return result;
        }
        for (Map<Integer, Map<FrameIntervalRange, List<Chord>>> numNotesMap : dissDegreeMap.values()) {
            if (numNotesMap == null) continue;
            Map<FrameIntervalRange, List<Chord>> frameIntervalMap = numNotesMap.get(numNotes);
            if (frameIntervalMap == null) continue;
            List<Chord> chords = frameIntervalMap.get(range);
            if (chords != null) result.addAll(chords);
        }
        return result;
    }



    public void printSpecificChordList(List<Chord> specificChordList){
        if (specificChordList != null) {
            for (Chord chord : specificChordList) {
                System.out.println("Chord: " + chord + " rootNote: " + chord.getRootNote());
            }
        } else {
            System.out.println("Keine entsprechende Liste von Chords gefunden.");
        }
    }

    private FrameIntervalRange getFrameIntervalRange(int frameInterval) {
        if (frameInterval <= 7) {
            return FrameIntervalRange.INTERVALS_0_TO_7;
        } else if (frameInterval <= 12) {
            return FrameIntervalRange.INTERVALS_8_TO_12;
        } else if (frameInterval <= 16) {
            return FrameIntervalRange.INTERVALS_13_TO_16;
        } else if (frameInterval <= 19) {
            return FrameIntervalRange.INTERVALS_17_TO_19;
        } else if ( frameInterval <= 24){
            return FrameIntervalRange.INTERVALS_20_TO_24;
        } else {
            return FrameIntervalRange.INTERVALS_FROM_25;
        }
    }

    public void calculateAllChords(int minLowerNote, int maxUpperNote) {

        // c3 bis c5, 48-72=Δ24, dauert 40''
        // h2 bis c#5, 47-73=Δ26, dauert 1'20''
        // a#2 bis d5, 46-74=Δ28, dauert 2'20''
        // a2 bis d#5, 45-75=Δ30, dauert 4'15''
        // g#2 bis e5, 44-76=Δ32, dauert 7'40''
        // g2 bis f5, 43-77=Δ34, wirft nach 12'20'' einen OutOfMemoryError bei den verminderten Skalen. Hab ich von 2027 auf 5000 verändert.

        List<Scale> scales = scaleRepository.getScales();
        int[] numNotesAll = {3, 4, 5, 6, 7, 8};
        int[] dissDegreesAll = {0,1,2,3,4,5,6,7,8,9,10,11,12,13};   //{}; //{0,1,2,3,4,5,6,7,8,9,10,11,12,13}
        Map<Integer, ChordSpecification> constraints = chordCalculator.getDissDegreeConstraints();



        for(Scale scale : scales) {
            System.out.println();
            System.out.println("Skala: " + scale.getName());
            for (int numNotes : numNotesAll) {
                System.out.println("    numNotes: " + numNotes);
                for (int dissDegree : dissDegreesAll) {
                    System.out.print("        dissDegree: " + dissDegree + ",");
                    ChordSpecification chordSpecification = constraints.get(dissDegree);
                    List<Chord> chords = null;
                    switch(numNotes) {
                        case 3:
                            chords = chordCalculator.generateChordsForThreeNotes(scale, chordSpecification, minLowerNote, maxUpperNote);
                            break;
                        case 4:
                            chords = chordCalculator.generateChordsForFourNotes(scale, chordSpecification, minLowerNote, maxUpperNote);
                            break;
                        case 5:
                            chords = chordCalculator.generateChordsForFiveNotes(scale, chordSpecification, minLowerNote, maxUpperNote);
                            break;
                        case 6:
                            chords = chordCalculator.generateChordsForSixNotes(scale, chordSpecification, minLowerNote, maxUpperNote);
                            break;
                        case 7:
                            chords = chordCalculator.generateChordsForSevenNotes(scale, chordSpecification, minLowerNote, maxUpperNote);
                            break;
                        case 8:
                            chords = chordCalculator.generateChordsForEightNotes(scale, chordSpecification, minLowerNote, maxUpperNote);
                            break;
                    }

                    //Map<FrameIntervalRange, List<Chord>> frameIntervalMap = new HashMap<>(5);


                    for(Chord chord: chords){
                        int frameInterval = chord.getFrameInterval();
                        FrameIntervalRange intervalRange = getFrameIntervalRange(frameInterval);

                        allChords.computeIfAbsent(scale.getName(), k -> new HashMap<>())
                                .computeIfAbsent(dissDegree, k -> new HashMap<>())
                                .computeIfAbsent(numNotes, k -> new HashMap<>())
                                .computeIfAbsent(intervalRange, k-> new ArrayList<>())
                                .add(chord);
                    }


                    //Map<Integer, Map<FrameIntervalRange, List<Chord>>> dissDegreeMap = new HashMap<>();
                    //dissDegreeMap.computeIfAbsent(dissDegree, k -> new HashMap<>()).put(dissDegree, freeIntervalMap);

                    //numNotesMap.put(numNotes, frameIntervalMap);

                    //allChords.put(scale.getName(), numNotesMap);

                    System.out.println("    Size of chords: " + chords.size());

                }
            }
        }
    }

    public void saveAllChordsToFile(int minLowerNote, int maxUpperNote) {
        Path path = Paths.get(System.getProperty("user.dir"),
                "/data/minLowerNote" + minLowerNote + "_maxUpperNote" + maxUpperNote + ".json");
        String filePath = path.toString();
        System.out.println("save to file: " + filePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(allChords, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("File saved.");
    }

    public void loadAllChordsFromFile(int minLowerNote, int maxUpperNote) {
        Path path = Paths.get(System.getProperty("user.dir"),
                "data/minLowerNote" + minLowerNote + "_maxUpperNote" + maxUpperNote + ".json");
        String filePath = path.toString();
        System.out.println("lade " + filePath + " ..." );
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<Integer, Map<Integer, Map<FrameIntervalRange, List<Chord>>>>>>(){}.getType();

        try (FileReader reader = new FileReader(filePath)) {
            Map<String, Map<Integer, Map<Integer, Map<FrameIntervalRange, List<Chord>>>>> loaded = gson.fromJson(reader, type);
            if (loaded == null) {
                System.out.println("[MIDI][DEBUG_LOG] JSON parsed to null, setting empty map.");
                allChords = new HashMap<>();
            } else {
                allChords = loaded;
            }
            System.out.println("Datei erfolgreich geladen.");
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Datei " + filePath + ".");
            e.printStackTrace();
            allChords = new HashMap<>(); // Rückgabe einer leeren Map im Fehlerfall
        }

    }

}
