package syrincs.b_application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import syrincs.a_domain.Tone;
import syrincs.c_adapters.JdkMidiOutputAdapter;

import javax.sound.midi.MidiDevice;

import static org.junit.jupiter.api.Assertions.*;

class SendMidiNoteUseCaseConnectionTest {

    private UseCaseInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new UseCaseInteractor(new JdkMidiOutputAdapter());
    }

    @Test
    @DisplayName("Liste der MIDI-Outputs kann abgefragt werden")
    void listOutputsDoesNotThrow() {
        MidiDevice.Info[] outs = interactor.listMidiOutputs();
        assertNotNull(outs, "Device list should not be null");

        for (MidiDevice.Info info : outs) {
            System.out.println("[MIDI] Output device: " + info.getName()
                    + " | " + info.getDescription()
                    + " | " + info.getVendor());
        }
    }

    @Test
    @DisplayName("Sende Note, falls Roland/DP603 verfügbar (sonst überspringen)")
    void sendNoteIfRolandPresent() {
        String[] needles = {"Roland Digital Piano", "DP603", "Roland"};
        MidiDevice.Info target = null;
        for (String needle : needles) {
            target = interactor.findOutputByName(needle);
            if (target != null) break;
        }

        Assumptions.assumeTrue(target != null,
                "[MIDI] No Roland/DP603 output found. Skipping send test.");

        final String deviceName = target.getName();

        System.out.println("[MIDI] Found target output: " + deviceName + " -> attempting to send a note");

        Tone tone = new Tone(100, 60, 0.25);

        assertDoesNotThrow(() -> interactor.sendToneToDevice(tone, deviceName),
                "Sending tone to device should not throw");
    }
}
