package syrincs.b_application;

import syrincs.a_domain.ChordCalculator.Chord;
import syrincs.a_domain.Tone;
import syrincs.a_domain.hindemith.ChordAnalysisHindemith;
import syrincs.b_application.ports.MidiOutputPort;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import java.util.List;

public class UseCaseInteractor {
    private final MidiOutputPort midiOutput;
    private final LoadHindemithChordFileUseCase loadHindemithChordFileUseCase = new LoadHindemithChordFileUseCase();
    private final AnalyseChordByHindemithUseCase analyseChordByHindemithUseCase = new AnalyseChordByHindemithUseCase();


    public UseCaseInteractor(MidiOutputPort midiOutput) {
        this.midiOutput = midiOutput;
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

    public void sendChordToDevice(Chord chord, String deviceNameSubstring) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        midiOutput.sendChordToDevice(chord, deviceNameSubstring);
    }

    public void loadHindemithChordFile() {
        loadHindemithChordFileUseCase.load();
    }

    public List<Chord> getSomeHindemithChords() {
        return loadHindemithChordFileUseCase.getSomeChords();
    }

    public ChordAnalysisHindemith.Result analyzeChordByHindemith(List<Integer> midiNotes) {
        return analyseChordByHindemithUseCase.analyze(midiNotes);
    }
}
