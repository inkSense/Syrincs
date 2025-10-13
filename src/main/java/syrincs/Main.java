// src/main/java/syrincs/Main.java
package syrincs;

import syrincs.a_domain.Tone;
import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.b_application.UseCaseInteractor;
import syrincs.c_adapters.JdkMidiOutputAdapter;
import syrincs.c_adapters.postgres.PostgresHindemithChordRepository;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    private static UseCaseInteractor interactor;
    static String commands = "list | play <note 0-127> [ms=500] [vel=0.8] [device?] | play chords | calculate <minLowerNote> <maxUpperNote> | analyze <note1> <note2> <note3> [more...] | delete";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws Exception {
        // Bootstrap interactor with MIDI and (optional) DB repository
        var midiAdapter = new JdkMidiOutputAdapter();

        String url = System.getenv().getOrDefault("HINDEMITH_DB_URL", "jdbc:postgresql://localhost:5432/hindemith");
        String user = System.getenv().getOrDefault("HINDEMITH_DB_USER", "philipp");
        String pass = System.getenv().getOrDefault("HINDEMITH_DB_PASSWORD", "philipp");

        var repo = new PostgresHindemithChordRepository(url, user, pass);
        interactor = new UseCaseInteractor(midiAdapter, repo);

        if (args.length > 0) { handle(args); }
        else {
            System.out.println("Commands: " + commands);
        }

    }

    private static void handle(String[] args) throws Exception {
        switch (args[0].toLowerCase()) {
            case "list" -> Arrays.stream(interactor.listMidiOutputs())
                    .forEach(i -> System.out.printf("[MIDI] %s | %s | %s%n", i.getName(), i.getDescription(), i.getVendor()));
            case "play" -> {
                if (args.length >= 2 && "chords".equalsIgnoreCase(args[1])) {

                    List<HindemithChord> hindemithChords = interactor.getSomeHindemithChords();
                    if (hindemithChords == null || hindemithChords.isEmpty()) {
                        System.out.println("[MIDI] No chords available after loading.");
                        return;
                    }
                    for (HindemithChord hindemithChord : hindemithChords) {
                        interactor.sendChordToDevice(hindemithChord, null, 200L); // auto-select device

                    }
                } else {
                    int note = args.length >= 2 ? Integer.parseInt(args[1]) : 60;
                    long ms = args.length >= 3 ? Long.parseLong(args[2]) : 100L;
                    double vel = args.length >= 4 ? Double.parseDouble(args[3]) : 0.5;
                    String device = args.length >= 5 ? args[4] : null; // delegate auto selection to adapter

                    interactor.sendToneToDevice(new Tone(ms, note, vel), device);
                }
            }
            case "calculate" -> {
                if (args.length < 3) {
                    System.out.println("Usage: calculate <minLowerNote> <maxUpperNote>");
                    return;
                }
                int minLowerNote = Integer.parseInt(args[1]);
                int maxUpperNote = Integer.parseInt(args[2]);
                var ids = interactor.calculateAndPersistAllChordsToFiveNotes(minLowerNote, maxUpperNote);
                LOGGER.info("[DB] Persisted " + ids.size() + " chords for range [" +  minLowerNote + ", " + maxUpperNote+ "].");
            }
            case "analyze" -> {
                if (args.length < 4) {
                    System.out.println("Usage: analyze <note1> <note2> <note3> [more...]");
                    return;
                }
                java.util.List<Integer> notes = new java.util.ArrayList<>();
                for (int i = 1; i < args.length; i++) {
                    try {
                        notes.add(Integer.parseInt(args[i]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid note: " + args[i] + ". Use integers 0-127.");
                        return;
                    }
                }
                var res = interactor.analyzeChordByHindemith(notes);
                System.out.println("[ANALYZE] Notes=" + res.notes + " | Column=" + res.column + " | Root=" + res.rootNote + " | Group=" + res.group + " | Frame=" + res.frameInterval);
            }
            case "delete" -> {
                interactor.truncateHindemithChords();
                System.out.println("[DB] TRUNCATE hindemithChords RESTART IDENTITY executed.");
            }
            default -> System.out.println("Unknown. Try: " + commands);
        }
    }
}
