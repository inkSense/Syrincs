package syrincs.b_application;

import syrincs.a_domain.chord.NoteCombinator;
import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.a_domain.Tone;
import syrincs.a_domain.hindemith.ChordAnalysis;
import syrincs.b_application.ports.HindemithChordRepositoryPort;
import syrincs.b_application.ports.MidiOutputPort;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UseCaseInteractor {

    private final GenerateChordsUseCase generateChordsUseCase;
    private final AnalyseChordByHindemithUseCase analyseChordByHindemithUseCase;
    private final PersistHindemithChordUseCase persistUseCase;
    private final GetHindemithChordsFromDbUseCase getHindemithChordsFromDbUseCase;
    private final SendToMidiUseCase send;
    private final HindemithChordRepositoryPort repository;
    private final Logger LOGGER = Logger.getLogger(UseCaseInteractor.class.getName());
    private List<HindemithChord> hindemithChords;


    public UseCaseInteractor(MidiOutputPort midiOutput, HindemithChordRepositoryPort repository) {
        this.repository = repository;
        this.generateChordsUseCase = new GenerateChordsUseCase(
                new NoteCombinator(), new ChordAnalysis(), 3
        );
        this.analyseChordByHindemithUseCase = new AnalyseChordByHindemithUseCase();
        this.persistUseCase = new PersistHindemithChordUseCase(repository);
        this.getHindemithChordsFromDbUseCase = new GetHindemithChordsFromDbUseCase(repository);
        this.send = new SendToMidiUseCase(midiOutput);
    }


    public MidiDevice.Info[] listMidiOutputs() {
        return send.listMidiOutputs();
    }

    public MidiDevice.Info findOutputByName(String nameSubstring) {
        return send.findOutputByName(nameSubstring);
    }

    public void sendToneToDevice(Tone tone, String deviceNameSubstring) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        System.out.printf("[MIDI] Playing note %d.", (int) tone.getMidiPitch());
        send.sendToneToDevice(tone, deviceNameSubstring);
    }

    public void sendChordToDevice(HindemithChord hindemithChord, String deviceNameSubstring, Long duration) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        send.sendChordToDevice(hindemithChord, deviceNameSubstring, duration);

    }

    public void loadHindemithChords() {
        hindemithChords = getHindemithChordsFromDbUseCase.getAll();
    }

    public void loadHindemithChords(Integer group){
        hindemithChords = getHindemithChordsFromDbUseCase.getAllOf(List.of(group));
    }

    public void loadHindemithChordsWithRootNote(Integer rootNote){
        hindemithChords = getHindemithChordsFromDbUseCase.getAllOf(rootNote);
    }

    public List<HindemithChord> getSomeHindemithChords() {
        loadHindemithChordsWithGroups(60, List.of(8));
        LOGGER.log(Level.INFO, "{0} chords loaded.", hindemithChords.size() );
        //Collections.shuffle(hindemithChords);
        return hindemithChords;
    }

    public void loadHindemithChordsWithMaxGroup(Integer rootNote, Integer maxGroup ){
        hindemithChords = getHindemithChordsFromDbUseCase.getAllOfRootNoteAndMaxGroup(rootNote, maxGroup);
    }

    public void loadHindemithChordsWithGroups(Integer rootNote, List<Integer> groups ){
        hindemithChords = getHindemithChordsFromDbUseCase.loadHindemithChordsWithGroups(rootNote, groups);
    }

    public ChordAnalysis.Result analyzeChordByHindemith(List<Integer> midiNotes) {
        return analyseChordByHindemithUseCase.analyze(midiNotes);
    }

    public List<Long> calculateAndPersistAllChordsToFiveNotes(int minLowerNote, int maxUpperNote) {
        LOGGER.info("Starting Calculation of Chords");
        List<HindemithChord> chords = generateChordsUseCase.generateAllChordsToFiveNotes(minLowerNote, maxUpperNote);
        return persistUseCase.persist(chords);
    }

    public List<HindemithChord> getAllChordsFromDb() {
        return getHindemithChordsFromDbUseCase.getAll();
    }

    public void truncateHindemithChords() {
        LOGGER.info("[DB] Truncating table hindemithChords (RESTART IDENTITY)");
        repository.truncate();
        LOGGER.info("[DB] TRUNCATE complete.");
    }

    // Orchestration for CLI: load chords for given filters
    public List<HindemithChord> findChordsFor(List<Integer> numNotes, List<Integer> groups, List<Integer> rootNotes) {
        Objects.requireNonNull(numNotes, "numNotes");
        Objects.requireNonNull(groups, "groups");
        Objects.requireNonNull(rootNotes, "rootNotes");

        List<HindemithChord> acc = new java.util.ArrayList<>();
        for (Integer root : rootNotes) {
            var part = getHindemithChordsFromDbUseCase
                    .getAllOfRootNoteGroupsAndNumNotes(root, groups, numNotes);
            acc.addAll(part);
        }
        return acc;
    }

    // Play the chords using the MIDI output adapter
    public void playChords(List<Integer> numNotes, List<Integer> groups, List<Integer> rootNotes,
                           Long durationMs, String deviceNameSubstring)
            throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        var chords = findChordsFor(numNotes, groups, rootNotes);
        if (chords == null || chords.isEmpty()) {
            System.out.println("[MIDI] No chords available after loading.");
            return;
        }
        for (var hc : chords) {
            sendChordToDevice(hc, deviceNameSubstring, durationMs);
        }
    }

}
