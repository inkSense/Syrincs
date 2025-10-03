package syrincs.a_domain.hindemith;

import java.util.ArrayList;
import java.util.List;

public class Series1 {
    /**
     *  Series 1 - Reihe 1
     *  Paul Hindemiths notion of "Series 1" that describes the degree of kinship (Verwandtschaftsgrad) of a musical note.
     *  Source: "Unterweisung im Tonsatz 1" p.50ff
     *  It is called a "Rangliste der Tonverwandtschaften" at page 76.
    */

    public List<Integer> getSeries1Of(Integer midiNote){
        return midiIntervalleAufGrundton.stream().map(x -> x + midiNote).toList();
    }

    public List<Integer> getChildrenOf(Integer midiNote){
        List<Integer> children = new ArrayList<>();
        for(Integer childIndex : childrenIndices){
            Integer child = midiIntervalleAufGrundton.get(childIndex) +  midiNote;
            children.add(child);
        }
        return children;
    }

    public List<Integer> getGrandChildren(Integer midiNote){
        List<Integer> grandChildren = new ArrayList<>();
        for(Integer grandChildIndex : grandChildrenIndices){
            Integer grandChild = midiIntervalleAufGrundton.get(grandChildIndex) + midiNote;
            grandChildren.add(grandChild);
        }
        return grandChildren;
    }

    public Integer getRelativeByDegree(Integer midiNote, Integer degree){
        if(degree < 0 || 11 < degree){
            throw new IllegalArgumentException("degree must be between 0 and 11");
        }
        return midiIntervalleAufGrundton.get(degree) * midiNote;
    }

    // Faktoren der Grundfrequenzen: 1, 3/2, 4/3, 5/3, 5/4, 6/5, 4/5*2, 3/2*3/4, 4/3*4/3, 4/3*4/5, 5/4*3/2, 3/2*3/4*5/4
    // Beispieltöne:                C,  G,  F,  A,  E,  Es,  As,  D,  B,  Des,  H,  Fis
    private final List<Double> faktorenDerGrundfrequenzen = new ArrayList<>(
            List.of(
                1.,
                3./2,
                4./3,
                5./3,
                5./4,
                6./5,
                4./5*2,
                3./2*3/4,
                4./3*4/3,
                4./3*4/5,
                5./4*3/2,
                3./2*3/4*5/4
        )
    );

    private final List<Integer> midiIntervalleAufGrundton = new ArrayList<>(
            List.of(0, 7, 5, 9, 4, 3, 8, 2, 10, 1, 11, 6)
    );

    private final List<Integer> childrenIndices = List.of(1,2,3,4,5,6); // Söhne
    private final List<Integer> grandChildrenIndices = List.of(7,8,9,10);

    private List<Double> getSeries1of(Double grundfrequenz){
        return faktorenDerGrundfrequenzen.stream().map(x -> x * grundfrequenz).toList();
    }


}
