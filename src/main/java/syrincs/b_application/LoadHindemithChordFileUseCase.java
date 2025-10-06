package syrincs.b_application;

import syrincs.a_domain.ChordCalculator.Chord;
import syrincs.a_domain.ChordCalculator.FrameIntervalRange;
import syrincs.a_domain.ChordCalculator.Hindemith;

import java.util.*;

public class LoadHindemithChordFileUseCase {

    Hindemith hindemith = new Hindemith();

    public void load(){
        hindemith.loadAllChordsFromFile(54, 66);
    }

    public List<Chord> getSomeChords(){
        // Preferred selection as originally intended
        List<Chord> preferred = hindemith.getChordListAnyDissDegree("cIonic", 5, FrameIntervalRange.INTERVALS_8_TO_12);
        System.out.println("[MIDI][DEBUG_LOG] Preferred chord list size: " + preferred.size());
        if (preferred != null && !preferred.isEmpty()) return preferred;

        // Known-good fallback based on repository JSON content (gIonic, 3 notes, frame 13..16)
        List<Chord> knownGood = hindemith.getChordListAnyDissDegree("gIonic", 3, FrameIntervalRange.INTERVALS_13_TO_16);
        System.out.println("[MIDI][DEBUG_LOG] Known-good chord list size: " + knownGood.size());
        if (knownGood != null && !knownGood.isEmpty()) return knownGood;

        // Fallback: flatten all loaded chords across all scales/numNotes/ranges.
        // Limit to avoid overly long playback.
        List<Chord> result = new ArrayList<>();
        Map<String, Map<Integer, Map<Integer, Map<FrameIntervalRange, List<Chord>>>>> all = hindemith.getAllChords();
        if (all == null || all.isEmpty()) {
            System.out.println("[MIDI][DEBUG_LOG] allChords is null or empty after load.");
            return result;
        }

        for (Map<Integer, Map<Integer, Map<FrameIntervalRange, List<Chord>>>> dissMap : all.values()) {
            if (dissMap == null) continue;
            for (Map<Integer, Map<FrameIntervalRange, List<Chord>>> numMap : dissMap.values()) {
                if (numMap == null) continue;
                for (Map<FrameIntervalRange, List<Chord>> rangeMap : numMap.values()) {
                    if (rangeMap == null) continue;
                    for (List<Chord> list : rangeMap.values()) {
                        if (list == null || list.isEmpty()) continue;
                        result.addAll(list);
                        if (result.size() >= 128) {
                            System.out.println("[MIDI][DEBUG_LOG] Collected 128 chords from JSON.");
                            return result;
                        }
                    }
                }
            }
        }
        System.out.println("[MIDI][DEBUG_LOG] Flatten found " + result.size() + " chords.");
        return result;
    }
}
