package syrincs.c_adapters;

import syrincs.a_domain.ChordCalculator.Chord;
import syrincs.a_domain.Tone;
import syrincs.b_application.ports.MidiOutputPort;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Frameworks & Drivers implementation of MidiOutputPort using the JDK javax.sound.midi API.
 */
public class JdkMidiOutputAdapter implements MidiOutputPort {

    @Override
    public MidiDevice.Info[] listMidiOutputs() {
        MidiDevice.Info[] all = MidiSystem.getMidiDeviceInfo();
        List<MidiDevice.Info> outs = new ArrayList<>();
        for (MidiDevice.Info info : all) {
            try {
                MidiDevice dev = MidiSystem.getMidiDevice(info);
                int maxReceivers = dev.getMaxReceivers();
                if (maxReceivers != 0) { // -1 unlimited or >0
                    outs.add(info);
                }
            } catch (MidiUnavailableException ignored) {
            }
        }
        return outs.toArray(new MidiDevice.Info[0]);
    }

    @Override
    public MidiDevice.Info findOutputByName(String nameSubstring) {
        if (nameSubstring == null) return null;
        String needle = nameSubstring.toLowerCase();
        for (MidiDevice.Info info : listMidiOutputs()) {
            String hay = (info.getName() + " " + info.getDescription() + " " + info.getVendor()).toLowerCase();
            if (hay.contains(needle)) {
                return info;
            }
        }
        return null;
    }

    @Override
    public void sendToneToDevice(Tone tone, String deviceNameSubstring) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        MidiDevice.Info info = (deviceNameSubstring != null && !deviceNameSubstring.isEmpty())
                ? findOutputByName(deviceNameSubstring)
                : null;
        if (info == null) {
            // Centralized auto-selection previously in Main
            info = autoSelectDefaultOutput();
        }
        if (info == null) {
            throw new MidiUnavailableException("No suitable MIDI output device found" +
                    (deviceNameSubstring != null ? " for substring '" + deviceNameSubstring + "'" : ""));
        }
        send(tone, info, 0);
    }

    @Override
    public void sendChordToDevice(Chord chord, String deviceNameSubstring) {
        if (chord == null) return;
        try {
            MidiDevice.Info info = (deviceNameSubstring != null && !deviceNameSubstring.isEmpty())
                    ? findOutputByName(deviceNameSubstring)
                    : null;
            if (info == null) {
                info = autoSelectDefaultOutput();
            }
            if (info == null) {
                System.out.println("[MIDI] No suitable output device found" +
                        (deviceNameSubstring != null ? " for substring '" + deviceNameSubstring + "'" : ""));
                return;
            }

            MidiDevice device = MidiSystem.getMidiDevice(info);
            boolean openedHere = false;
            try {
                if (!device.isOpen()) { device.open(); openedHere = true; }
                Receiver receiver = device.getReceiver();
                try {
                    sendChordViaReceiver(receiver, chord, 0);
                } finally {
                    try { receiver.close(); } catch (Exception ignored) {}
                }
            } finally {
                if (openedHere && device.isOpen()) device.close();
            }
        } catch (Exception e) {
            // Do not propagate checked exceptions: the port method does not declare throws.
            // Log to console to aid debugging in a console tool.
            System.out.println("[MIDI] Failed to send chord: " + e.getMessage());
        }
    }

    private void sendChordViaReceiver(Receiver receiver, Chord chord, int channel) throws InvalidMidiDataException, InterruptedException {
        if (receiver == null) throw new IllegalArgumentException("receiver must not be null");
        if (chord == null) throw new IllegalArgumentException("chord must not be null");
        if (channel < 0 || channel > 15) throw new IllegalArgumentException("channel must be between 0 and 15");

        List<Integer> notes = chord.getNotes();
        if (notes == null || notes.isEmpty()) return;

        int velocity = 32; // default soft
        long durationMs = 100; // default short duration for a chord
        long now = -1; // immediate

        // Note ON for all notes
        for (Integer n : notes) {
            if (n == null) continue;
            int pitch = n;
            if (pitch < 0) pitch = 0; if (pitch > 127) pitch = 127;
            ShortMessage on = new ShortMessage();
            on.setMessage(ShortMessage.NOTE_ON, channel, pitch, velocity);
            receiver.send(on, now);
        }

        // Hold duration
        if (durationMs > 0) Thread.sleep(durationMs);

        // Note OFF for all notes
        for (Integer n : notes) {
            if (n == null) continue;
            int pitch = n;
            if (pitch < 0) pitch = 0; if (pitch > 127) pitch = 127;
            ShortMessage off = new ShortMessage();
            off.setMessage(ShortMessage.NOTE_OFF, channel, pitch, 0);
            receiver.send(off, now);
        }
    }

    // Prefer Roland piano if present, otherwise the first available MIDI OUT
    private MidiDevice.Info autoSelectDefaultOutput() {
        String[] needles = {"Roland Digital Piano", "DP603"};
        for (String n : needles) {
            MidiDevice.Info info = findOutputByName(n);
            if (info != null) return info;
        }
        MidiDevice.Info[] outs = listMidiOutputs();
        return outs.length > 0 ? outs[0] : null;
    }

    private void send(Tone tone, MidiDevice.Info info, int channel) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        MidiDevice device = MidiSystem.getMidiDevice(info);
        boolean openedHere = false;
        try {
            if (!device.isOpen()) { device.open(); openedHere = true; }
            Receiver receiver = device.getReceiver();
            try {
                sendViaReceiver(receiver, tone, channel);
            } finally {
                try { receiver.close(); } catch (Exception ignored) {}
            }
        } finally {
            if (openedHere && device.isOpen()) device.close();
        }
    }

    private void sendViaReceiver(Receiver receiver, Tone tone, int channel) throws InvalidMidiDataException, InterruptedException {
        if (receiver == null) throw new IllegalArgumentException("receiver must not be null");
        if (tone == null) throw new IllegalArgumentException("tone must not be null");
        if (channel < 0 || channel > 15) throw new IllegalArgumentException("channel must be between 0 and 15");

        int pitch = (int) Math.round(tone.getMidiPitch());
        if (pitch < 0) pitch = 0; if (pitch > 127) pitch = 127;
        int velocity = (int) Math.round(Math.max(0, Math.min(1, tone.getLoudness())) * 127);
        if (velocity < 1) velocity = 1;

        long now = -1; // immediate
        ShortMessage noteOn = new ShortMessage();
        noteOn.setMessage(ShortMessage.NOTE_ON, channel, pitch, velocity);
        receiver.send(noteOn, now);

        long durationMs = tone.getDurationInMilliseconds();
        if (durationMs < 0) durationMs = 0;
        Thread.sleep(durationMs);

        ShortMessage noteOff = new ShortMessage();
        noteOff.setMessage(ShortMessage.NOTE_OFF, channel, pitch, 0);
        receiver.send(noteOff, now);
    }
}
