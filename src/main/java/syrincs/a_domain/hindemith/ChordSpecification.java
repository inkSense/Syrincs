package syrincs.a_domain.hindemith;

import java.util.*;

/**
 * ChordSpecification – immutable rule definition for Hindemith chord groups.
 * Provides a single Builder-based construction API and keeps legacy getter names
 * for backward compatibility with existing code paths.
 */
public final class ChordSpecification {

    // Domain enums
    public enum ColumnRequirement { ANY, TRITONE_FREE, WITH_TRITONE }
    public enum RootRelation { ANY, EQUALS_BASS, NOT_EQUALS_BASS }

    // Domain flags
    private final ColumnRequirement columnRequirement;
    private final RootRelation rootRelation;
    private final boolean tritoneSubordinated;
    private final boolean mehrereTritoni;
    private final boolean layersOfMajor3OrPerfect4;
    private final boolean dimOrDim7;

    // Interval conditions
    private final Set<Integer> excludeIntervals;
    private final Set<Integer> requireIntervals;
    private final Set<Integer> requireAnyIntervals; // ODER-Menge
    private final Set<Integer> requireAnyIntervals2; // optional zweiter ODER-Block
    private final Set<Integer> requireAnyIntervals3; // optional dritter ODER-Block

    // Optional metadata
    private final Integer groupNumber; // 1..14
    private final String label;


    private ChordSpecification(Builder b) {
        this.columnRequirement = b.columnRequirement;
        this.rootRelation = b.rootRelation;
        this.tritoneSubordinated = b.tritoneSubordinated;
        this.mehrereTritoni = b.mehrereTritoni;
        this.layersOfMajor3OrPerfect4 = b.layersOfMajor3OrPerfect4;
        this.dimOrDim7 = b.dimOrDim7;
        this.excludeIntervals = unmodifiableOrEmpty(b.excludeIntervals);
        this.requireIntervals = unmodifiableOrEmpty(b.requireIntervals);
        this.requireAnyIntervals = unmodifiableOrEmpty(b.requireAnyIntervals);
        this.requireAnyIntervals2 = unmodifiableOrEmpty(b.requireAnyIntervalsTwo);
        this.requireAnyIntervals3 = unmodifiableOrEmpty(b.requireAnyIntervalsThree);
        this.groupNumber = b.groupNumber;
        this.label = b.label;
    }

    private static <T> Set<T> unmodifiableOrEmpty(Set<T> s) { return (s == null) ? Set.of() : Set.copyOf(s); }

    // --- New API getters ---
    public ColumnRequirement getColumnRequirement() { return columnRequirement; }
    public RootRelation getRootRelationEnum() { return rootRelation; }
    public boolean isTritoneSubordinated() { return tritoneSubordinated; }
    public boolean isMehrereTritoni() { return mehrereTritoni; }
    public boolean isLayersOfMajor3OrPerfect4() { return layersOfMajor3OrPerfect4; }
    public boolean isDimOrDim7() { return dimOrDim7; }
    public Set<Integer> getExcludeIntervals() { return excludeIntervals; }
    public Set<Integer> getRequireIntervals() { return requireIntervals; }
    public Set<Integer> getRequireAnyIntervals() { return requireAnyIntervals; }
    public Set<Integer> getRequireAnyIntervals2() { return requireAnyIntervals2; }
    public Set<Integer> getRequireAnyIntervals3() { return requireAnyIntervals3; }
    public Optional<Integer> getGroupNumber() { return Optional.ofNullable(groupNumber); }
    public Optional<String> getLabel() { return Optional.ofNullable(label); }

    // --- Legacy compatibility getters (used by existing code) ---
    public Set<Integer> getExcludeAll() { return getExcludeIntervals(); }
    public Set<Integer> getIncludeAll() { return getRequireIntervals(); }
    public Set<Integer> getIncludeAtLeastOneOf() { return getRequireAnyIntervals(); }
    public Set<Integer> getIncludeAtLeastOneOf2() { return getRequireAnyIntervals2(); }
    public Set<Integer> getIncludeAtLeastOneOf3() { return getRequireAnyIntervals3(); }
    public String getRootNoteEqual() {
        return switch (rootRelation) {
            case EQUALS_BASS -> "==";
            case NOT_EQUALS_BASS -> "!=";
            default -> null;
        };
    }
    public boolean getMehrereTritoni() { return isMehrereTritoni(); }
    public boolean getLayersOfMajor3OrPerfect4() { return isLayersOfMajor3OrPerfect4(); }
    public boolean getDimOrDim7() { return isDimOrDim7(); }

    // --- Builder: single construction API ---
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private ColumnRequirement columnRequirement = ColumnRequirement.ANY;
        private RootRelation rootRelation = RootRelation.ANY;
        private boolean tritoneSubordinated = false;
        private boolean mehrereTritoni = false;
        private boolean layersOfMajor3OrPerfect4 = false;
        private boolean dimOrDim7 = false;
        private Set<Integer> excludeIntervals;
        private Set<Integer> requireIntervals;
        private Set<Integer> requireAnyIntervals;
        private Set<Integer> requireAnyIntervalsTwo;
        private Set<Integer> requireAnyIntervalsThree;
        private Integer groupNumber;
        private String label;

        public Builder column(ColumnRequirement c) { this.columnRequirement = c; return this; }
        public Builder rootRelation(RootRelation r) { this.rootRelation = r; return this; }
        public Builder requireTritoneSubordinated(boolean b) { this.tritoneSubordinated = b; return this; }
        public Builder requireMultipleTritones(boolean b) { this.mehrereTritoni = b; return this; }
        public Builder layeringM3orP4(boolean b) { this.layersOfMajor3OrPerfect4 = b; return this; }
        public Builder dimOrDim7(boolean b) { this.dimOrDim7 = b; return this; }

        public Builder excludeIntervals(Set<Integer> s) { this.excludeIntervals = s; return this; }
        public Builder excludeIntervals(Integer... s) { this.excludeIntervals = toSet(s); return this; }
        public Builder requireIntervals(Set<Integer> s) { this.requireIntervals = s; return this; }
        public Builder requireIntervals(Integer... s) { this.requireIntervals = toSet(s); return this; }
        public Builder requireAnyIntervals(Set<Integer> s) { this.requireAnyIntervals = s; return this; }
        public Builder requireAnyIntervals(Integer... s) { this.requireAnyIntervals = toSet(s); return this; }
        // Zweiter optionaler ODER-Block: überschreibt nicht den ersten
        public Builder requireAnyIntervalsTwo(Set<Integer> s) { this.requireAnyIntervalsTwo = s; return this; }
        public Builder requireAnyIntervalsTwo(Integer... s) { this.requireAnyIntervalsTwo = toSet(s); return this; }
        // Dritter optionaler ODER-Block: überschreibt nicht die ersten beiden
        public Builder requireAnyIntervals3(Set<Integer> s) { this.requireAnyIntervalsThree = s; return this; }
        public Builder requireAnyIntervals3(Integer... s) { this.requireAnyIntervalsThree = toSet(s); return this; }

        public Builder groupNumber(int n) { this.groupNumber = n; return this; }
        public Builder label(String s) { this.label = s; return this; }

        public ChordSpecification build() { return new ChordSpecification(this); }

        private static Set<Integer> toSet(Integer... ints) { return (ints == null) ? Set.of() : Set.of(ints); }
    }
}

