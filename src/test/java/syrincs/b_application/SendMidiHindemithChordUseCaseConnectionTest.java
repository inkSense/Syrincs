package syrincs.b_application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.c_adapters.JdkMidiOutputAdapter;

import javax.sound.midi.MidiDevice;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SendMidiHindemithChordUseCaseConnectionTest {

    private UseCaseInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new UseCaseInteractor(new JdkMidiOutputAdapter());
    }

    @Test
    @DisplayName("Sende Akkord, falls Roland/DP603 verfügbar (sonst überspringen)")
    void sendChordIfRolandPresent() {
        String[] needles = {"Roland Digital Piano", "DP603", "Roland"};
        MidiDevice.Info target = null;
        for (String needle : needles) {
            target = interactor.findOutputByName(needle);
            if (target != null) break;
        }

        Assumptions.assumeTrue(target != null,
                "[MIDI] No Roland/DP603 output found. Skipping chord send test.");

        final String deviceName = target.getName();
        System.out.println("[MIDI] Found target output: " + deviceName + " -> attempting to send a chord");

        // C major triad within safe MIDI range
        HindemithChord hindemithChord = new HindemithChord(List.of(64, 67, 72));

        assertDoesNotThrow(() -> interactor.sendChordToDevice(hindemithChord, deviceName),
                "Sending chord to device should not throw");
    }
}
