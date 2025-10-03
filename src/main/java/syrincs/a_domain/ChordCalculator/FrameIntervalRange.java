package syrincs.a_domain.ChordCalculator;

public enum FrameIntervalRange {
    INTERVALS_0_TO_7,  //bis Quinte
    INTERVALS_8_TO_12, //bis Oktave
    INTERVALS_13_TO_16, //bis Dezime
    INTERVALS_17_TO_19, //bis Duodezime
    INTERVALS_20_TO_24, //bis zwei Oktaven
    INTERVALS_FROM_25;

    private static FrameIntervalRange[] array = FrameIntervalRange.values();

    public static FrameIntervalRange getFrameIntervalRange(int i){
        return array[i];
    }


}
