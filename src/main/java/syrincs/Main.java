// src/main/java/syrincs/Main.java
package syrincs;

import syrincs.a_domain.Tone;
import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.b_application.UseCaseInteractor;
import syrincs.c_adapters.JdkMidiOutputAdapter;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final UseCaseInteractor interactor = new UseCaseInteractor(new JdkMidiOutputAdapter());
    static String commands = "list | play <note 0-127> [ms=500] [vel=0.8] [device?] | play chords";

    public static void main(String[] args) throws Exception {

        if (args.length > 0) { handle(args); return; }
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
                    System.out.println("[MIDI] Loading Hindemith chords...");
                    interactor.loadHindemithChordFile();
                    List<HindemithChord> hindemithChords = interactor.getSomeHindemithChords();
                    if (hindemithChords == null || hindemithChords.isEmpty()) {
                        System.out.println("[MIDI] No chords available after loading.");
                        return;
                    }
                    long intervalMs = 1000L; // start-to-start interval
                    for (HindemithChord hindemithChord : hindemithChords) {
                        long start = System.currentTimeMillis();
                        System.out.println("[MIDI] Playing chord: " + hindemithChord);
                        interactor.sendChordToDevice(hindemithChord, null); // auto-select device
                        long elapsed = System.currentTimeMillis() - start;
                        long sleep = intervalMs - elapsed;
                        if (sleep > 0) Thread.sleep(sleep);
                    }
                } else {
                    int note = args.length >= 2 ? Integer.parseInt(args[1]) : 60;
                    long ms = args.length >= 3 ? Long.parseLong(args[2]) : 100L;
                    double vel = args.length >= 4 ? Double.parseDouble(args[3]) : 0.5;
                    String device = args.length >= 5 ? args[4] : null; // delegate auto selection to adapter
                    System.out.printf("[MIDI] Playing note %d for %d ms at vel %.2f on '%s'%n", note, ms, vel, device == null ? "(auto)" : device);
                    interactor.sendToneToDevice(new Tone(ms, note, vel), device);
                }
            }
            default -> System.out.println("Unknown. Try: " + commands);
        }
    }
}
