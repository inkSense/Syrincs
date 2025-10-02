package syrincs.a_domain;

public class Sound extends Time {


    long durationInMilliseconds;
    double loudness;

    public Sound(long durationInMilliseconds, double loudness) {
        this.durationInMilliseconds = durationInMilliseconds;
        if(loudness < 0 || loudness > 1) {
            throw new IllegalArgumentException("loudness must be between 0 and 1");
        }
        this.loudness = loudness; // Must between 0 and 1
    }

    public long getDurationInMilliseconds() {
        return durationInMilliseconds;
    }

    public void setDurationInMilliseconds(long durationInMilliseconds) {
        this.durationInMilliseconds = durationInMilliseconds;
    }

    public double getLoudness() {
        return loudness;
    }

    public void setLoudness(double loudness) {
        this.loudness = loudness;
    }
}
