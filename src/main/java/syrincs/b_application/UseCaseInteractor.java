package syrincs.b_application;

import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.a_domain.Tone;
import syrincs.a_domain.hindemith.ChordAnalysis;
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

    public void sendChordToDevice(HindemithChord hindemithChord, String deviceNameSubstring) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        midiOutput.sendChordToDevice(hindemithChord, deviceNameSubstring);
    }

    public void loadHindemithChordFile() {
        loadHindemithChordFileUseCase.load();
    }

    public List<HindemithChord> getSomeHindemithChords() {
        return loadHindemithChordFileUseCase.getSomeChords();
    }

    public ChordAnalysis.Result analyzeChordByHindemith(List<Integer> midiNotes) {
        return analyseChordByHindemithUseCase.analyze(midiNotes);
    }
}
