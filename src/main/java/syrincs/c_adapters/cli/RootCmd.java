package syrincs.c_adapters.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import syrincs.a_domain.Tone;
import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.b_application.UseCaseInteractor;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * PicoCli-based command tree for Syrincs. This coexists with the legacy CliController for now.
 */
@Command(
        name = "syrincs",
        mixinStandardHelpOptions = true,
        version = "syrincs 1.0",
        description = "MIDI Utilities and Hindemith chords",
        subcommands = {
                RootCmd.ListCmd.class,
                RootCmd.PlayCmd.class,
                RootCmd.CalculateCmd.class,
                RootCmd.AnalyzeCmd.class,
                RootCmd.DeleteCmd.class
        }
)
public class RootCmd implements Runnable {
    final UseCaseInteractor interactor;

    public RootCmd(UseCaseInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void run() {
        // Show root usage and also explicitly show help for 'play' and 'play chords' subcommands
        CommandLine root = new CommandLine(this);
        root.usage(System.out);
        CommandLine play = root.getSubcommands().get("play");
        if (play != null) {
            System.out.println();
            System.out.println("Subcommand 'play' usage:");
            play.usage(System.out);
            CommandLine chords = play.getSubcommands().get("chords");
            if (chords != null) {
                System.out.println();
                System.out.println("Subcommand 'play chords' usage:");
                chords.usage(System.out);
            }
        }
    }

    @Command(name = "list", description = "List MIDI outputs")
    public static class ListCmd implements Callable<Integer> {
        @ParentCommand RootCmd parent;

        @Override
        public Integer call() {
            var interactor = parent.interactor;
            for (var i : interactor.listMidiOutputs()) {
                System.out.printf("[MIDI] %s | %s | %s%n", i.getName(), i.getDescription(), i.getVendor());
            }
            return 0;
        }
    }

    @Command(name = "play", description = "Play a single note or use subcommand 'chords'", subcommands = { PlayChordsCmd.class })
    public static class PlayCmd implements Callable<Integer> {
        @ParentCommand RootCmd parent;

        @Option(names = "note", description = "MIDI note (0-127)", defaultValue = "60")
        int note;

        @Option(names = "ms", description = "Duration in ms", defaultValue = "100")
        long ms;

        @Option(names = "vel", description = "Velocity 0..1", defaultValue = "0.5")
        double vel;

        @Option(names = "device", description = "MIDI device name substring")
        String device;

        @Override
        public Integer call() throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
            var interactor = parent.interactor;
            interactor.sendToneToDevice(new Tone(ms, note, vel), device);
            return 0;
        }
    }

    @Command(name = "chords", description = "Play chords from DB. Options: numnotes|num|notes (multi), group|groups (multi), rootnote|root (single, default=60), range (single, default=24), ms (default=200), device (optional)")
    public static class PlayChordsCmd implements Callable<Integer> {
        @ParentCommand PlayCmd parentPlay; // Access via parentPlay.parent.interactor

        // Accept tokenized options like today: "numnotes 3 4"
        @Option(names = {"numnotes", "num", "notes"}, arity = "1..*", description = "Chord sizes (e.g. 3 4 5)", split = " ")
        int[] numNotes;

        @Option(names = {"group", "groups"}, arity = "1..*", description = "Hindemith groups (1..9)", split = " ")
        int[] groups;

        @Option(names = {"rootnote", "root"}, description = "Root note (default: 60)", defaultValue = "60")
        int rootNote;

        @Option(names = "range", description = "Max chord span (maxNote - minNote), default: 24", defaultValue = "24")
        int range;

        @Option(names = "ms", description = "Duration per chord in ms (default: 200)", defaultValue = "200")
        long ms;

        @Option(names = "device", description = "MIDI device name substring (optional)")
        String device;

        @Override
        public Integer call() throws Exception {
            var interactor = parentPlay.parent.interactor;
            List<Integer> nn = (numNotes == null || numNotes.length == 0) ? List.of(3,4,5) : Arrays.stream(numNotes).boxed().toList();
            List<Integer> gr = (groups   == null || groups.length   == 0) ? List.of(1,2,3,4,5,6,7,8,9) : Arrays.stream(groups).boxed().toList();
            interactor.playChords(nn, gr, rootNote, range, ms, device);
            return 0;
        }
    }

    @Command(name = "calculate", aliases = {"calc"}, description = "Generate chords and persist")
    public static class CalculateCmd implements Callable<Integer> {
        @ParentCommand RootCmd parent;

        @Parameters(index = "0", description = "minLowerNote")
        int minLowerNote;

        @Parameters(index = "1", description = "maxUpperNote")
        int maxUpperNote;

        @Override
        public Integer call() {
            var interactor = parent.interactor;
            var ids = interactor.calculateAndPersistAllChordsToFiveNotes(minLowerNote, maxUpperNote);
            System.out.printf("[DB] Persisted %d chords for range [%d, %d].%n", ids.size(), minLowerNote, maxUpperNote);
            return 0;
        }
    }

    @Command(name = "analyze", aliases = {"analyse"}, description = "Analyze chord by Hindemith")
    public static class AnalyzeCmd implements Callable<Integer> {
        @ParentCommand RootCmd parent;

        @Parameters(arity = "3..*", description = "MIDI notes")
        int[] notes;

        @Override
        public Integer call() {
            var interactor = parent.interactor;
            var list = Arrays.stream(notes).boxed().toList();
            var res = interactor.analyzeChordByHindemith(list);
            System.out.println("[ANALYZE] Notes=" + res.notes + " | Column=" + res.column + " | Root=" + res.rootNote + " | Group=" + res.group + " | Frame=" + res.frameInterval);
            return 0;
        }
    }

    @Command(name = "delete", description = "Truncate Hindemith chords table")
    public static class DeleteCmd implements Callable<Integer> {
        @ParentCommand RootCmd parent;
        @Override public Integer call() {
            parent.interactor.deleteHindemithChords();
            return 0;
        }
    }
}
