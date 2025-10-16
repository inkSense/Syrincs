package syrincs.c_adapters.cli;

import syrincs.a_domain.Tone;
import syrincs.b_application.UseCaseInteractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CLI controller responsible for parsing arguments and invoking the UseCaseInteractor.
 *
 * Also contains a lightweight router for the `play chords` subcommand that is covered by tests.
 */
public class CliController {
    public static final String COMMANDS = "list | play <note 0-127> [ms=500] [vel=0.8] [device?] | play chords [numnotes N...] [group G...] [rootnote R] [range X] | calculate <minLowerNote> <maxUpperNote> | analyze <note1> <note2> <note3> [more...] | delete";

    // Defaults for `play chords` subcommand
    private static final List<Integer> DEFAULT_NUM_NOTES = List.of(3, 4, 5);
    private static final List<Integer> DEFAULT_GROUPS = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
    private static final int DEFAULT_ROOT_NOTE = 60;
    private static final int DEFAULT_RANGE = 24;

    private final UseCaseInteractor interactor; // can be null for tests that use only route()

    public final Logger LOGGER = Logger.getLogger(CliController.class.getName());

    public CliController() {
        this.interactor = null;
    }

    public CliController(UseCaseInteractor interactor) {
        this.interactor = interactor;
    }

    /**
     * Entrypoint for handling CLI commands. Delegates to the UseCaseInteractor.
     */
    public void handle(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            System.out.println("Commands: " + COMMANDS);
            return;
        }
        requireInteractor();
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "list" -> handleList();
            case "play" -> handlePlay(args);
            case "calculate", "calc" -> handleCalculate(args);
            case "analyse", "analyze" -> handleAnalyze(args);
            case "delete" -> handleDelete();
            default -> System.out.println("Unknown. Try: " + COMMANDS);
        }
    }

    private void handleList() {
        Arrays.stream(interactor.listMidiOutputs())
                .forEach(i -> System.out.printf("[MIDI] %s | %s | %s%n", i.getName(), i.getDescription(), i.getVendor()));
    }

    private void handlePlay(String[] args) throws Exception {
        if (args.length >= 2 && "chords".equalsIgnoreCase(args[1])) {
            handlePlayChords(args);
            return;
        }
        int note = args.length >= 2 ? Integer.parseInt(args[1]) : 60;
        long ms = args.length >= 3 ? Long.parseLong(args[2]) : 100L;
        double vel = args.length >= 4 ? Double.parseDouble(args[3]) : 0.5;
        String device = args.length >= 5 ? args[4] : null; // delegate auto selection to adapter
        interactor.sendToneToDevice(new Tone(ms, note, vel), device);
    }

    private void handlePlayChords(String[] args) throws Exception {
        var routed = route(args);
        if (routed instanceof PlayChordsCommand cmd) {
            interactor.playChords(cmd.numNotes(), cmd.groups(), cmd.rootNote(), cmd.range(), 200L, null);
        } else {
            System.out.println("Unknown. Try: " + COMMANDS);
        }
    }

    private void handleCalculate(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: calculate <minLowerNote> <maxUpperNote>");
            return;
        }
        int minLowerNote = Integer.parseInt(args[1]);
        int maxUpperNote = Integer.parseInt(args[2]);
        var ids = interactor.calculateAndPersistAllChordsToFiveNotes(minLowerNote, maxUpperNote);
        LOGGER.info("[DB] Persisted " + ids.size() + " chords for range [" +  minLowerNote + ", " + maxUpperNote+ "].");
    }

    private void handleAnalyze(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: analyze <note1> <note2> <note3> [more...]");
            return;
        }
        List<Integer> notes = new ArrayList<>();
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

    private void handleDelete() {
        interactor.deleteHindemithChords();
    }

    private void requireInteractor() {
        if (interactor == null) {
            throw new IllegalStateException("CliController.handle requires a non-null UseCaseInteractor");
        }
    }

    /**
     * Route the given args to a command object. Currently supports only `play chords` command family
     * as specified by the TDD test.
     *
     * @param args tokens like ["play", "chords", ...]
     * @return a command object (e.g., PlayChordsCommand) or null if not recognized
     */
    public Object route(String[] args) {
        if (!isPlayChords(args)) return null;
        Options options = parseOptions(args, 2);
        return toCommandWithDefaults(options);
    }

    private boolean isPlayChords(String[] args) {
        if (args == null || args.length < 2) return false;
        String a0 = args[0].toLowerCase(Locale.ROOT);
        String a1 = args[1].toLowerCase(Locale.ROOT);
        return "play".equals(a0) && "chords".equals(a1);
    }

    private Options parseOptions(String[] args, int startIdx) {
        Options opt = new Options();
        String currentKey = null; // one of: numnotes | group | rootnote | range
        for (int i = startIdx; i < args.length; i++) {
            String token = args[i];
            String lower = token.toLowerCase(Locale.ROOT);
            switch (lower) {
                case "numnotes", "num", "notes" -> currentKey = "numnotes";
                case "group", "groups" -> currentKey = "group";
                case "rootnote", "root" -> currentKey = "rootnote";
                case "range" -> currentKey = "range";
                default -> {
                    if (currentKey != null) {
                        try {
                            int value = Integer.parseInt(token);
                            switch (currentKey) {
                                case "numnotes" -> opt.addNumNote(value);
                                case "group" -> opt.addGroup(value);
                                case "rootnote" -> opt.rootNote = value;
                                case "range" -> opt.range = value;
                            }
                        } catch (NumberFormatException ex) {
                            LOGGER.log(Level.WARNING, "Invalid token: " + token, ex);
                        }
                    }
                }
            }
        }
        return opt;
    }

    private PlayChordsCommand toCommandWithDefaults(Options opt) {
        List<Integer> numNotes = (opt.numNotes == null || opt.numNotes.isEmpty()) ? DEFAULT_NUM_NOTES : List.copyOf(opt.numNotes);
        List<Integer> groups = (opt.groups == null || opt.groups.isEmpty()) ? DEFAULT_GROUPS : List.copyOf(opt.groups);
        int root = (opt.rootNote == null) ? DEFAULT_ROOT_NOTE : opt.rootNote;
        int range = (opt.range == null) ? DEFAULT_RANGE : opt.range;
        PlayChordsCommand cmd = new PlayChordsCommand(numNotes, groups, root, range);
        LOGGER.info("Chords Command: " + cmd);
        return cmd;
    }

    private static final class Options {
        List<Integer> numNotes;
        List<Integer> groups;
        Integer rootNote;
        Integer range;

        void addNumNote(int v) {
            if (numNotes == null) numNotes = new ArrayList<>();
            numNotes.add(v);
        }

        void addGroup(int v) {
            if (groups == null) groups = new ArrayList<>();
            groups.add(v);
        }
    }

    public record PlayChordsCommand(List<Integer> numNotes, List<Integer> groups, Integer rootNote, Integer range) { }
}
