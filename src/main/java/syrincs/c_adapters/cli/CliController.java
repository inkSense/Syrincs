package syrincs.c_adapters.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Minimal CLI controller to route `play chords` and parse suboptions with independent defaults.
 *
 * Supported suboptions (case-insensitive):
 * - numnotes: accepts variable number of integers; default [3,4,5]
 * - group: accepts variable number of integers; default [1..9]
 * - rootnote: accepts a single integer; default [60]
 */
public class CliController {
    public final Logger LOGGER = Logger.getLogger(CliController.class.getName());

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
