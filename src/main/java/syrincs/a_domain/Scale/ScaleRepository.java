package syrincs.a_domain.Scale;

import java.util.*;

public class ScaleRepository {
    private final List<Scale> scales = new ArrayList<>();
    public ScaleRepository() {
        scales.add(new Scale(Set.of(0, 2, 4, 5, 7, 9, 11), "cIonic", 0));
        scales.add(new Scale(Set.of(0, 2, 4, 6, 7, 9, 11), "gIonic", 7));
        scales.add(new Scale(Set.of(1, 2, 4, 6, 7, 9, 11), "dIonic",2 ));
        scales.add(new Scale(Set.of(1, 2, 4, 6, 8, 9, 11), "aIonic", 9));
        scales.add(new Scale(Set.of(1, 3, 4, 6, 8, 9, 11), "eIonic", 4));
        scales.add(new Scale(Set.of(1, 3, 4, 6, 8, 10, 11), "hIonic", 11));
        scales.add(new Scale(Set.of(1, 3, 5, 6, 8, 10, 11), "fisIonic", 6));
        scales.add(new Scale(Set.of(0, 1, 3, 5, 6, 8, 10), "desIonic", 1));
        scales.add(new Scale(Set.of(0, 1, 3, 5, 7, 8, 10), "asIonic", 8));
        scales.add(new Scale(Set.of(0, 2, 3, 5, 7, 8, 10), "esIonic", 3));
        scales.add(new Scale(Set.of(0, 2, 3, 5, 7, 9, 10), "bIonic", 10));
        scales.add(new Scale(Set.of(0, 2, 4, 5, 7, 9, 10), "fIonic", 5));

        // Sollten hier noch Äolische hinzugefügt werden?
        // Ich hab in Erinnerung, dass mit Hackmack immer kein Moll kam.
        // Eigentlich müsste sich Hindemith aber die Molls heraus suchen ...

        scales.add(new Scale(Set.of(0, 1, 4, 5, 8, 9), "cConstruct", 0));
        scales.add(new Scale(Set.of(1, 2, 5, 6, 9, 10), "cisConstruct", 1));
        scales.add(new Scale(Set.of(2, 3, 6, 7, 10, 11), "dConstruct", 2));
        scales.add(new Scale(Set.of(0, 3, 4, 7, 8, 11), "disConstruct", 3));


        scales.add(new Scale(Set.of(0, 1, 3, 4, 6, 7, 9, 10), "cDiminished", 0));
        scales.add(new Scale(Set.of(1, 2, 4, 5, 7, 8, 10, 11), "cisDiminished", 1));
        scales.add(new Scale(Set.of(0, 2, 3, 5, 6, 8, 9, 11), "dDiminished", 2));

        scales.add(new Scale(Set.of(0, 2, 4, 6, 8, 10), "cWholeTone", 0));
        scales.add(new Scale(Set.of(1, 3, 5, 7, 9, 11), "cisWholeTone", 1));

    }

    public List<Scale> getScales() {
        return scales;
    }

    public Scale getScale(String name) {
        Scale outputScale = null;
        for(Scale scale : scales){
            if(name.equals(scale.getName())){
                outputScale = scale;
            }
        }
        if(outputScale == null){throw new NoSuchElementException("Eine Skala mit diesem Namen gibt es nicht.");}
        return outputScale;
    }

}
