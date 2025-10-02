package syrincs.b_application;

import syrincs.a_domain.Tone;
import syrincs.b_application.ports.MidiOutputPort;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

public class UseCaseInteractor {
    private final MidiOutputPort midiOutput;


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
}
