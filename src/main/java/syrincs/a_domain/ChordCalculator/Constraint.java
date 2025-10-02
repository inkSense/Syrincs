package syrincs.a_domain.ChordCalculator;

import java.util.Set;

public class Constraint {
    private final Set<Integer> excludeAll;
    private final Set<Integer> includeAll;
    private final Set<Integer> includeAtLeastOneOf; //Hindemith schreibt "Mit großer Sekunde UND kleiner Septime", er meint aber "oder".
    private final String rootNoteEqual;
    private final boolean mehrereTritoni;
    private final boolean layersOfMajor3OrPerfect4;
    private final boolean dimOrDim7;

    public Constraint(Set<Integer> excludeAll, Set<Integer> includeAll, Set<Integer> includeAtLeastOneOf, String rootNoteEqual, boolean mehrereTritoni, boolean layersOfMajor3OrPerfect4, boolean dimOrDim7) {
        this.excludeAll = excludeAll;
        this.includeAll = includeAll;
        this.includeAtLeastOneOf = includeAtLeastOneOf;
        this.rootNoteEqual = rootNoteEqual;
        this.mehrereTritoni = mehrereTritoni;
        this.layersOfMajor3OrPerfect4 = layersOfMajor3OrPerfect4;
        this.dimOrDim7 = dimOrDim7;
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
}

