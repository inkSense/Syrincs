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
        backtrack(buf, 0, minLowerNote, maxUpperNote, out);
        return out;
    }

    private void backtrack(int[] buf, int index, int min, int max, List<List<Integer>> out) {
        int k = buf.length;
        if (index == k) {  // buf ist vollständig
            List<Integer> chord = new ArrayList<>(k);
            for (int v : buf) chord.add(v); //copy to chord
            out.add(chord);
            return;
        }
        // Rekursiver Fall
        for (int start = min; start <= max - (k - index - 1); start++) {
            buf[index] = start;
            backtrack(buf, index + 1, start + 1, max, out);
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
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
        LOGGER.info("File saved.");
    }

    private void print(List<List<Integer>> chords) {
        for (List<Integer> chord : chords) {
            System.out.println(chord);
        }
    }


}
