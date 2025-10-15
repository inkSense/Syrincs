package syrincs.c_adapters.cli;

import syrincs.a_domain.Tone;
import syrincs.b_application.UseCaseInteractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * CLI controller responsible for parsing arguments and invoking the UseCaseInteractor.
 *
 * Also contains a lightweight router for the `play chords` subcommand that is covered by tests.
 */
public class CliController {
    public static final String COMMANDS = "list | play <note 0-127> [ms=500] [vel=0.8] [device?] | play chords [numnotes N...] [group G...] [rootnote R] | calculate <minLowerNote> <maxUpperNote> | analyze <note1> <note2> <note3> [more...] | delete";

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
        if (interactor == null) {
            throw new IllegalStateException("CliController.handle requires a non-null UseCaseInteractor");
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "list" -> Arrays.stream(interactor.listMidiOutputs())
                    .forEach(i -> System.out.printf("[MIDI] %s | %s | %s%n", i.getName(), i.getDescription(), i.getVendor()));
            case "play" -> {
                if (args.length >= 2 && "chords".equalsIgnoreCase(args[1])) {
                    var routed = route(args);
                    if (routed instanceof PlayChordsCommand cmd) {
                        interactor.playChords(cmd.numNotes(), cmd.groups(), cmd.rootNotes(), 200L, null);
                    } else {
                        System.out.println("Unknown. Try: " + COMMANDS);
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
            case "delete" -> {
                interactor.deleteHindemithChords();
                System.out.println("[DB] TRUNCATE hindemithChords RESTART IDENTITY executed.");
            }
            default -> System.out.println("Unknown. Try: " + COMMANDS);
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
        if (args == null || args.length < 2) return null;
        String a0 = args[0].toLowerCase(Locale.ROOT);
        String a1 = args[1].toLowerCase(Locale.ROOT);
        if (!("play".equals(a0) && "chords".equals(a1))) {
            return null;
        }

        // Defaults
        List<Integer> defaultNumNotes = List.of(3, 4, 5);
        List<Integer> defaultGroups = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<Integer> defaultRootNotes = List.of(60);

        List<Integer> numNotes = null;
        List<Integer> groups = null;
        List<Integer> rootNotes = null;

        // Parse suboptions starting at index 2
        int i = 2;
        String currentKey = null; // one of: numnotes | group | rootnote
        while (i < args.length) {
            String token = args[i];
            String lower = token.toLowerCase(Locale.ROOT);
            switch (lower) {
                case "numnotes", "num", "notes" -> currentKey = "numnotes";
                case "group", "groups" -> currentKey = "group";
                case "rootnote", "root", "rootnotes" -> currentKey = "rootnote";
                default -> {
                    // Not a key; try to parse as int and assign to the current key bucket
                    if (currentKey == null) {
                        // No current key: ignore stray numbers to keep behavior minimal/safe for now
                        // Could extend later to smarter inference.
                    } else {
                        try {
                            int value = Integer.parseInt(token);
                            switch (currentKey) {
                                case "numnotes" -> {
                                    if (numNotes == null) numNotes = new ArrayList<>();
                                    numNotes.add(value);
                                }
                                case "group" -> {
                                    if (groups == null) groups = new ArrayList<>();
                                    groups.add(value);
                                }
                                case "rootnote" -> {
                                    if (rootNotes == null) rootNotes = new ArrayList<>();
                                    if (rootNotes.isEmpty()) { // accept only a single root note
                                        rootNotes.add(value);
                                    }
                                }
                            }
                        } catch (NumberFormatException ex) {
                            // Ignore invalid numbers silently for now to keep test-focused minimalism
                        }
                    }
                }
            }
            i++;
        }

        if (numNotes == null || numNotes.isEmpty()) numNotes = defaultNumNotes;
        if (groups == null || groups.isEmpty()) groups = defaultGroups;
        if (rootNotes == null || rootNotes.isEmpty()) rootNotes = defaultRootNotes;

        PlayChordsCommand playChordsCommand = new PlayChordsCommand(List.copyOf(numNotes), List.copyOf(groups), List.copyOf(rootNotes));
        LOGGER.info("Chords Command: " + playChordsCommand);
        return playChordsCommand;
    }

    public record PlayChordsCommand(List<Integer> numNotes, List<Integer> groups, List<Integer> rootNotes) { }
}
