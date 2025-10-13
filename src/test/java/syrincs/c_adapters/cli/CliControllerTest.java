package syrincs.c_adapters.cli;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD first step: specify desired routing/parsing for `syrincs play chords`.
 *
 * We expect a controller that can parse suboptions with variable argument counts:
 * - numNotes: default [3,4,5]
 * - group: default [1,2,3,4,5,6,7,8,9]
 * - rootNote: default [60]
 * Each suboption is optional and independent; if missing, its default applies.
 */
public class CliControllerTest {

    @Test
    void playChords_defaults_are_applied_when_no_suboptions_given() {
        // Given
        String[] args = {"play", "chords"};

        // When
        CliController controller = new CliController();
        var command = controller.route(args);

        // Then
        assertNotNull(command, "route must return a command");
        assertTrue(command instanceof CliController.PlayChordsCommand, "command must be PlayChordsCommand");
        var pc = (CliController.PlayChordsCommand) command;

        assertEquals(List.of(3,4,5), pc.numNotes(), "default numNotes should be [3,4,5]");
        assertEquals(List.of(1,2,3,4,5,6,7,8,9), pc.groups(), "default groups should be [1..9]");
        assertEquals(List.of(60), pc.rootNotes(), "default rootNote should be [60]");
    }

    @Test
    void playChords_allows_partial_suboptions_and_keeps_defaults_for_missing_ones_case1() {
        // Given: only numNotes and group provided
        String[] args = {"play", "chords", "numnotes", "3", "4", "group", "4", "5", "6"};

        // When
        CliController controller = new CliController();
        var command = controller.route(args);

        // Then
        var pc = assertIsPlayChords(command);
        assertEquals(List.of(3,4), pc.numNotes());
        assertEquals(List.of(4,5,6), pc.groups());
        assertEquals(List.of(60), pc.rootNotes(), "rootNotes should default to [60]");
    }

    @Test
    void playChords_allows_partial_suboptions_and_keeps_defaults_for_missing_ones_case2() {
        // Given: group and rootNote provided, numNotes missing
        String[] args = {"play", "chords", "group", "1", "2", "rootnote", "72"};

        // When
        CliController controller = new CliController();
        var command = controller.route(args);

        // Then
        var pc = assertIsPlayChords(command);
        assertEquals(List.of(3,4,5), pc.numNotes());
        assertEquals(List.of(1,2), pc.groups());
        assertEquals(List.of(72), pc.rootNotes());
    }

    private CliController.PlayChordsCommand assertIsPlayChords(Object command) {
        assertNotNull(command, "route must return a command");
        assertTrue(command instanceof CliController.PlayChordsCommand, "command must be PlayChordsCommand");
        return (CliController.PlayChordsCommand) command;
    }
}
