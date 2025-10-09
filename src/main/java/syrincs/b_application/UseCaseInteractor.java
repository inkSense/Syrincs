package syrincs.b_application;

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

public class UseCaseInteractor {
    private final MidiOutputPort midiOutput;
    private final AnalyseChordByHindemithUseCase analyseChordByHindemithUseCase = new AnalyseChordByHindemithUseCase();
    private PersistHindemithChordUseCase persistUseCase;
    private GetHindemithChordsFromDbUseCase getHindemithChordsFromDbUseCase;
    private List<HindemithChord> hindemithChords;

    public UseCaseInteractor(MidiOutputPort midiOutput) {
        this.midiOutput = midiOutput;
    }

    // Convenience: construct with repository to wire persistence use cases
    public UseCaseInteractor(MidiOutputPort midiOutput, HindemithChordRepositoryPort repository) {
        this.midiOutput = Objects.requireNonNull(midiOutput);
        attachRepository(repository);
    }

    public void attachRepository(HindemithChordRepositoryPort repository) {
        Objects.requireNonNull(repository, "repository must not be null");
        this.persistUseCase = new PersistHindemithChordUseCase(repository);
        this.getHindemithChordsFromDbUseCase = new GetHindemithChordsFromDbUseCase(repository);
    }

    public MidiDevice.Info[] listMidiOutputs() {
        return midiOutput.listMidiOutputs();
    }

    public MidiDevice.Info findOutputByName(String nameSubstring) {
        return midiOutput.findOutputByName(nameSubstring);
    }

    public void sendToneToDevice(Tone tone, String deviceNameSubstring) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        midiOutput.sendToneToDevice(tone, deviceNameSubstring);
    }

    public void sendChordToDevice(HindemithChord hindemithChord, String deviceNameSubstring) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        midiOutput.sendChordToDevice(hindemithChord, deviceNameSubstring);
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
        loadHindemithChordsWithGroups(60, List.of(1,2,7,8));
        System.out.println(hindemithChords.size() + " chords loaded.");
        Collections.shuffle(hindemithChords);
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

    // === New: Persistence API exposed via interactor ===
    public List<Long> calculateAndPersistAllChordsToFiveNotes(int minLowerNote, int maxUpperNote) {
        ensurePersistenceConfigured();
        return persistUseCase.persistAllChordsToFiveNotes(minLowerNote, maxUpperNote);
    }

    public List<HindemithChord> getAllChordsFromDb() {
        ensurePersistenceConfigured();
        return getHindemithChordsFromDbUseCase.getAll();
    }

    private void ensurePersistenceConfigured() {
        if (persistUseCase == null || getHindemithChordsFromDbUseCase == null) {
            throw new IllegalStateException("Repository not attached. Provide a HindemithChordRepositoryPort when constructing UseCaseInteractor or call attachRepository().");
        }
    }
}
