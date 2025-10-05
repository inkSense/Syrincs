package syrincs.a_domain.ChordCalculator;

import java.util.List;
import java.util.Set;

public class Constraint {
    private final Set<Integer> excludeAll;
    private final Set<Integer> includeAll;
    private final Set<Integer> includeAtLeastOneOf; // Hindemith schreibt "Mit großer Sekunde UND kleiner Septime", er meint aber "oder".
    private final String rootNoteEqual;
    private final boolean mehrereTritoni;
    private final boolean layersOfMajor3OrPerfect4;
    private final boolean dimOrDim7;
    // NEU: UND-verknüpfte ODER-Gruppen
    private final List<Set<Integer>> includeAllWithAlternatives;

    // Alter Konstruktor bleibt zur Rückwärtskompatibilität bestehen
    public Constraint(Set<Integer> excludeAll, Set<Integer> includeAll, Set<Integer> includeAtLeastOneOf, String rootNoteEqual, boolean mehrereTritoni, boolean layersOfMajor3OrPerfect4, boolean dimOrDim7) {
        this(excludeAll, includeAll, includeAtLeastOneOf, rootNoteEqual, mehrereTritoni, layersOfMajor3OrPerfect4, dimOrDim7, java.util.List.of());
    }

    // Neuer Voll-Konstruktor
    public Constraint(Set<Integer> excludeAll, Set<Integer> includeAll, Set<Integer> includeAtLeastOneOf, String rootNoteEqual, boolean mehrereTritoni, boolean layersOfMajor3OrPerfect4, boolean dimOrDim7, List<Set<Integer>> includeAllWithAlternatives) {
        this.excludeAll = excludeAll;
        this.includeAll = includeAll;
        this.includeAtLeastOneOf = includeAtLeastOneOf;
        this.rootNoteEqual = rootNoteEqual;
        this.mehrereTritoni = mehrereTritoni;
        this.layersOfMajor3OrPerfect4 = layersOfMajor3OrPerfect4;
        this.dimOrDim7 = dimOrDim7;
        this.includeAllWithAlternatives = includeAllWithAlternatives == null ? java.util.List.of() : java.util.List.copyOf(includeAllWithAlternatives);
    }

    public Set<Integer> getExcludeAll() {
        return excludeAll;
    }

    public Set<Integer> getIncludeAll() {
        return includeAll;
    }

    public Set<Integer> getIncludeAtLeastOneOf() {
        return includeAtLeastOneOf;
    }

    public String getRootNoteEqual() {
        return rootNoteEqual;
    }

    public boolean getMehrereTritoni() {
        return mehrereTritoni;
    }

    public boolean getLayersOfMajor3OrPerfect4() {
        return layersOfMajor3OrPerfect4;
    }

    public boolean getDimOrDim7() {
        return dimOrDim7;
    }

    public List<Set<Integer>> getIncludeAllWithAlternatives() {
        return includeAllWithAlternatives;
    }
}

