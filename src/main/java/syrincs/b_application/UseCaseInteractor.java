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

public class UseCaseInteractor {

    private final GenerateChordsUseCase generateChordsUseCase;
    private final AnalyseChordByHindemithUseCase analyseChordByHindemithUseCase;
    private final PersistHindemithChordUseCase persistUseCase;
    private final GetHindemithChordsFromDbUseCase getHindemithChordsFromDbUseCase;
    private List<HindemithChord> hindemithChords;
    private final SendToMidiUseCase send;

    public UseCaseInteractor(MidiOutputPort midiOutput, HindemithChordRepositoryPort repository) {
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
        send.sendToneToDevice(tone, deviceNameSubstring);
    }

    public void sendChordToDevice(HindemithChord hindemithChord, String deviceNameSubstring) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        send.sendChordToDevice(hindemithChord, deviceNameSubstring);
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
        loadHindemithChordsWithGroups(60, List.of(3));
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

    public List<Long> calculateAndPersistAllChordsToFiveNotes(int minLowerNote, int maxUpperNote) {
        List<HindemithChord> chords = generateChordsUseCase.generateAllChordsToFiveNotes(minLowerNote, maxUpperNote);
        return persistUseCase.persist(chords);
    }

    public List<HindemithChord> getAllChordsFromDb() {
        return getHindemithChordsFromDbUseCase.getAll();
    }

}
