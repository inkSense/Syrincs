package syrincs.a_domain.chord;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;


public class NoteCombinator {

    private final List<List<Integer>> allChords = new ArrayList<>();

    public List<List<Integer>> getAllChords() {return allChords;}

    private static final Logger LOGGER = Logger.getLogger(NoteCombinator.class.getName());

    public List<List<Integer>> generateChords(int k, int minLowerNote, int maxUpperNote) {
        if (k <= 0) return List.of();
        if (minLowerNote > maxUpperNote) return List.of();
        // Wenn nicht genug Töne in der Range vorhanden sind, gibt es keine Kombinationen
        if ((maxUpperNote - minLowerNote + 1) < k) return List.of();

        List<List<Integer>> out = new ArrayList<>();
        int[] buf = new int[k];
        backtrack(buf, 0, minLowerNote, maxUpperNote, out, new boolean[12], 3);
        return out;
    }

    private void backtrack(int[] buf, int index, int min, int max, List<List<Integer>> out, boolean[] usedPc, int octaves) {
        int k = buf.length;
        if (index == k) {
            List<Integer> chord = new ArrayList<>(k);
            for (int v : buf) chord.add(v);
            out.add(chord);
            return;
        }

        int remaining = k - index - 1;

        if (index == 0) {
            // Grundlegende Kombinations-Grenze (ohne Spannweitenkappung):
            int lastStart = max - remaining;
            for (int start = min; start <= lastStart; start++) {
                int pc = Math.floorMod(start, 12);
                if (usedPc[pc]) continue;
                usedPc[pc] = true;
                buf[index] = start;

                // Kappen jetzt relativ zur tatsächlich gewählten tiefsten Note (start)
                int spanCapMax = Math.min(max, start + (octaves * 12 - 1));
                backtrack(buf, index + 1, start + 1, spanCapMax, out, usedPc, octaves);
                usedPc[pc] = false;
            }
            return;
        }

        // index > 0: tiefste Note ist bereits gewählt in buf[0]
        int lowest = buf[0];
        int spanCapMax = Math.min(max, lowest + (octaves * 12 - 1));

        int lastStart = spanCapMax - remaining;
        if (lastStart < min) return;

        for (int start = min; start <= lastStart; start++) {
            int pc = Math.floorMod(start, 12);
            if (usedPc[pc]) continue;
            usedPc[pc] = true;
            buf[index] = start;
            backtrack(buf, index + 1, start + 1, spanCapMax, out, usedPc, octaves);
            usedPc[pc] = false;
        }
    }

    private void print(List<List<Integer>> chords) {
        for (List<Integer> chord : chords) {
            System.out.println(chord);
        }
    }


}
