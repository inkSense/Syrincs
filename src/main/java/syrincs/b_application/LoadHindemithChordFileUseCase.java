package syrincs.b_application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.a_domain.chord.NoteCombinator;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadHindemithChordFileUseCase {

    private static final Logger LOGGER = Logger.getLogger(LoadHindemithChordFileUseCase.class.getName());

    //SkalenName, DissonanzGrad, numNotes, Rahmenintervall
    Map<String, Map<Integer, Map<Integer, Map<FrameIntervalRange, List<HindemithChord>>>>> allChords = new HashMap<>();

    NoteCombinator noteCombinator = new NoteCombinator();

    public void load(){
        loadAllChordsFromFile(54, 66);
    }

    public void loadAllChordsFromFile(int minLowerNote, int maxUpperNote) {
        Path path = Paths.get(System.getProperty("user.dir"),
                "data/minLowerNote" + minLowerNote + "_maxUpperNote" + maxUpperNote + ".json");
        String filePath = path.toString();
        LOGGER.info(() -> "Loading Hindemith chord JSON from " + filePath + " ...");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<Integer, Map<Integer, Map<FrameIntervalRange, List<HindemithChord>>>>>>(){}.getType();


        try (FileReader reader = new FileReader(filePath)) {
            Map<String, Map<Integer, Map<Integer, Map<FrameIntervalRange, List<HindemithChord>>>>> loaded = gson.fromJson(reader, type);
            if (loaded == null) {
                LOGGER.warning("JSON parsed to null, initializing empty chord map.");
                allChords = new HashMap<>();
            } else {
                allChords = loaded;
            }
            LOGGER.info("Chord JSON loaded successfully.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading chord JSON from " + filePath, e);
            allChords = new HashMap<>(); // Rückgabe einer leeren Map im Fehlerfall
        }

    }

    public List<HindemithChord> getSomeChords(){
        // Preferred selection as originally intended
        List<HindemithChord> preferred = getChordListAnyDissDegree("cIonic", 5, FrameIntervalRange.INTERVALS_8_TO_12);
        LOGGER.info(() -> "Preferred chord list size: " + preferred.size());
        if (preferred != null && !preferred.isEmpty()) return preferred;

        // Known-good fallback based on repository JSON content (gIonic, 3 notes, frame 13..16)
        List<HindemithChord> knownGood = getChordListAnyDissDegree("gIonic", 3, FrameIntervalRange.INTERVALS_13_TO_16);
        LOGGER.info(() -> "Known-good chord list size: " + knownGood.size());
        if (knownGood != null && !knownGood.isEmpty()) return knownGood;

        // Fallback: flatten all loaded chords across all scales/numNotes/ranges.
        // Limit to avoid overly long playback.
        List<HindemithChord> result = new ArrayList<>();

        if (allChords == null || allChords.isEmpty()) {
            LOGGER.warning("allChords is null or empty after load.");
            return result;
        }

        for (Map<Integer, Map<Integer, Map<FrameIntervalRange, List<HindemithChord>>>> dissMap : allChords.values()) {
            if (dissMap == null) continue;
            for (Map<Integer, Map<FrameIntervalRange, List<HindemithChord>>> numMap : dissMap.values()) {
                if (numMap == null) continue;
                for (Map<FrameIntervalRange, List<HindemithChord>> rangeMap : numMap.values()) {
                    if (rangeMap == null) continue;
                    for (List<HindemithChord> list : rangeMap.values()) {
                        if (list == null || list.isEmpty()) continue;
                        result.addAll(list);
                        if (result.size() >= 128) {
                            LOGGER.info("Collected 128 chords from JSON.");
                            return result;
                        }
                    }
                }
            }
        }
        LOGGER.info(() -> "Flatten found " + result.size() + " chords.");
        return result;
    }

    /**
     * Liefert alle Akkorde für die angegebene Skala, Notenzahl und Rahmenintervall,
     * unabhängig vom Dissonanzgrad. Gibt niemals null zurück.
     */
    public List<HindemithChord> getChordListAnyDissDegree(String scale, int numNotes, FrameIntervalRange range){
        List<HindemithChord> result = new ArrayList<>();
        Map<Integer, Map<Integer, Map<FrameIntervalRange, List<HindemithChord>>>> dissDegreeMap = allChords.get(scale);
        if (dissDegreeMap == null) {
            LOGGER.warning("No such Scale-Key: " + scale);
            return result;
        }
        for (Map<Integer, Map<FrameIntervalRange, List<HindemithChord>>> numNotesMap : dissDegreeMap.values()) {
            if (numNotesMap == null) continue;
            Map<FrameIntervalRange, List<HindemithChord>> frameIntervalMap = numNotesMap.get(numNotes);
            if (frameIntervalMap == null) continue;
            List<HindemithChord> hindemithChords = frameIntervalMap.get(range);
            if (hindemithChords != null) result.addAll(hindemithChords);
        }
        return result;
    }
}
