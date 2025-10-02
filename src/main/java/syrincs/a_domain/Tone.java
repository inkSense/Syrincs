package syrincs.a_domain;

public class Tone extends Sound{
    // Sound with Pitch

    public Tone(long durationInMilliseconds, double midiPitch, double loudness) {
        super(durationInMilliseconds, loudness);
        if(midiPitch < 21 || midiPitch > 108){
            throw new IllegalArgumentException("pitch must be between 21 and 108");
        }
        this.midiPitch = midiPitch;
    }

    double midiPitch;

    public double getMidiPitch() {
        return midiPitch;
    }
}
