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
    private final Boolean mehrereTritoni;
    private final Boolean layersOfMajor3OrPerfect4;
    private final boolean dimOrDim7;

    // Interval conditions
    private final Set<Integer> excludeIntervals;
    private final Set<Integer> requireIntervals;
    private final Set<Integer> includeExactlyOne;
    private final Set<Integer> requireAnyIntervals; // ODER-Menge
    private final Set<Integer> requireAnyIntervalsTwo; // optional zweiter ODER-Block

    private ChordSpecification(Builder b) {
        this.columnRequirement = b.columnRequirement;
        this.rootRelation = b.rootRelation;
        this.mehrereTritoni = b.mehrereTritoni;
        this.layersOfMajor3OrPerfect4 = b.layersOfMajor3OrPerfect4;
        this.dimOrDim7 = b.dimOrDim7;
        this.excludeIntervals = unmodifiableOrEmpty(b.excludeIntervals);
        this.requireIntervals = unmodifiableOrEmpty(b.requireIntervals);
        this.includeExactlyOne = unmodifiableOrEmpty(b.includeExactlyOne);
        this.requireAnyIntervals = unmodifiableOrEmpty(b.requireAnyIntervals);
        this.requireAnyIntervalsTwo = unmodifiableOrEmpty(b.requireAnyIntervalsTwo);
    }

    private static <T> Set<T> unmodifiableOrEmpty(Set<T> s) { return (s == null) ? Set.of() : Set.copyOf(s); }

    // getters
    public ColumnRequirement getColumnRequirement() { return columnRequirement; }
    public RootRelation getRootRelationEnum() { return rootRelation; }
    public Boolean isLayersOfMajor3OrPerfect4() { return layersOfMajor3OrPerfect4; }
    public boolean isDimOrDim7() { return dimOrDim7; }
    public Set<Integer> getExcludeAllIntervals() { return excludeIntervals; }
    public Set<Integer> getIncludeAllIntervals() { return requireIntervals; }
    public Set<Integer> getIncludeExactlyOne() { return includeExactlyOne; }
    public Set<Integer> getRequireAnyIntervals() { return requireAnyIntervals; }
    public Set<Integer> getRequireAnyIntervalsTwo() { return requireAnyIntervalsTwo; }
    public RootRelation getRootNoteEqual() { return rootRelation; }
    public Boolean getMehrereTritoni() { return mehrereTritoni; }

    // --- Builder: single construction API ---
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private ColumnRequirement columnRequirement = ColumnRequirement.ANY;
        private RootRelation rootRelation = RootRelation.ANY;
        private Boolean mehrereTritoni = null;
        private Boolean layersOfMajor3OrPerfect4 = null;
        private boolean dimOrDim7 = false;
        private Set<Integer> excludeIntervals;
        private Set<Integer> requireIntervals;
        private Set<Integer> includeExactlyOne;
        private Set<Integer> requireAnyIntervals;
        private Set<Integer> requireAnyIntervalsTwo;

        public Builder column(ColumnRequirement c) { this.columnRequirement = c; return this; }
        public Builder rootRelation(RootRelation r) { this.rootRelation = r; return this; }
        public Builder requireMultipleTritones(boolean b) { this.mehrereTritoni = b; return this; }
        public Builder layeringM3orP4(boolean b) { this.layersOfMajor3OrPerfect4 = b; return this; }
        public Builder dimOrDim7(boolean b) { this.dimOrDim7 = b; return this; }

        public Builder excludeIntervals(Set<Integer> s) { this.excludeIntervals = s; return this; }
        public Builder requireIntervals(Set<Integer> s) { this.requireIntervals = s; return this; }
        public Builder includeExclusivelyOne(Set<Integer> s) { this.includeExactlyOne = s; return this; }
        public Builder requireIntervals(Integer... s) { this.requireIntervals = toSet(s); return this; }
        public Builder requireAnyIntervals(Set<Integer> s) { this.requireAnyIntervals = s; return this; }
        // Zweiter optionaler ODER-Block: überschreibt nicht den ersten
        public Builder requireAnyIntervalsTwo(Set<Integer> s) { this.requireAnyIntervalsTwo = s; return this; }

        public ChordSpecification build() { return new ChordSpecification(this); }

        private static Set<Integer> toSet(Integer... ints) { return (ints == null) ? Set.of() : Set.of(ints); }
    }
}

