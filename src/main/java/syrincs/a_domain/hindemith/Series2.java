package syrincs.a_domain.hindemith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Series2 {

    public static final List<Integer> series2 = List.of(12, 7, 5, 4, 8, 3, 9, 2, 10, 1, 11, 6);
    //"Die konsonanten Klänge wären demnach auf der linken Seite der Reihe 2 beheimatet, die dissonanten rechts." S. 108
    // "Der Tritonus kann weder in die Region des Wohlklanges eingeordnet noch als Missklang angesehen werden; er steht als das eigenartigste Intervall auch hier wieder abseits." S. 109
    // Ich denke eher Missklang!

    private final List<Integer> chooseLowerNoteAsRootNote = List.of(7, 4, 3, 10, 11); //Der Grundton ist bei diesen Intervallen unten.
    private final List<Integer> chooseUpperNoteAsRootNote = List.of(5, 8, 9, 2, 1 );

    // 80: "Wie die Tonverwandtschaften in unterschiedliche Werte abgestuft erscheinen, so bieten usch uns auch die Intervalle in einer natürlichen Wertfolge dar, der wir den Namen Reihe 2 geben."
    // 107: "In der Reihe 2 sind die Intervalle vereinigt ohne Beziehung zu einem Stammton."
    // 108: "Wir wissen, dass es unmöglich ist, eindeutig zu bestimmen, wo die "Konsonanz" in die "Dissonanz" übergeht."

    // 111: "Harmonik und Melodik sind gegensätzliche Prinzipien. Keines von beiden ist selbstständig genug, um allein bestehen zu können."
    // 111: "Die Melodik bringt die trägen harmonischen Massen in Fluss, jede Verbindung von Harmonien kann nur auf meldischem Wege, mittles Durchschreitung der Intervalle erzeielt werden.
    //      Die Harmonik wiederum bindet und gliedert die auseinandersterbenden meldischen Wellen."
    // 113: "Der Tritonus hat weder bestimmte harmonische noch melodische Bedeutung."
    // 119: "Der Tritonus hingegen überträgt seine früher geschilderten Eigenarten auf die Akkorde dergestalt, dass sei einene Teil seiner harmonischen Unbestimmtheit, aber auch seine Zielstrebigkeit mitübernehmen." Das heißt für mich einfach nur Instablilität.


    private final List<Integer> harmonicPower = new ArrayList<>(series2.subList(1, series2.size() - 1)); // "Das stärkste und eindeutige harmonische Intervall nächst der alleinstehenden Oktave die Quinte." S. 111
    /*  Das ist also stark vereinfacht. Vielleicht würden sich für eine Harmonie die Parameter "Schmiegsamkeit" und "Farbigkeit" unterscheiden:
        Die Oktave ist in ihrem harmonischen Sinne sicherlich am Schmiegsamsten, aber nicht farbig.
        Hingegen sind die Terzen und ihre Komplemente am Farbigsten, während die Schmiegsamkeit in Richtung der gr. Sekunde abnimmt.
        Das Maximum der Schmiegsamkeit + Farbigkeit liegt dann in er gr. Terz. */
    private final List<Integer> melodicPower = harmonicPower.reversed().stream().toList();

    // Algorithmus zur Bestimmung eines Akkordes:
    // "Die Bestandaufnahme der Klänge scheidet darum das gesamte Akkordmaterial zunächst in zwei Hauptgruppen: In der Gruppe A sind alle tritonusfreien Klänge. Den Akkorden mit Tritonus wird die Gruppe B zugewiesen." S.119



}
