package syrincs.a_domain.counterpoint;

import syrincs.a_domain.Interval;

import java.util.List;

public class CounterpointInterval extends Interval {
    private enum Harmonie  {KONSONANT, DISSONANT};
    private final Harmonie harmonie;
    private final List<Integer> konsonantIntervals = List.of(0,3,4,7,8); //Ohne Quarte

    public CounterpointInterval(int lowNote, int highNote) {
        super(lowNote, highNote);
        if(konsonantIntervals.contains(getDifferenceWithoutOctavations())) {
            harmonie = Harmonie.KONSONANT;
        } else {
            harmonie = Harmonie.DISSONANT;
        }
    }

    public Harmonie getHarmonie() {
        return harmonie;
    }
}
