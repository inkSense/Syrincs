package syrincs.a_domain.hindemith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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


    // -------- Public API (ähnlich zu Series1, aber für Reihen-2-Intervalle) --------

    /**
     * Liefert die Reihen-2-Intervalle in ihrer Wert-Reihenfolge (Oktave bis Tritonus).
     * Werte sind Halbtonschritte: [12, 7, 5, 4, 8, 3, 9, 2, 10, 1, 11, 6]
     */
    public List<Integer> getIntervals() {
        return series2; // already unmodifiable
    }

    /**
     * Gibt die harmonische Wertigkeit (ohne Oktave und Tritonus) zurück, links = konsonanter.
     */
    public List<Integer> getHarmonicPowerOrder() {
        return List.copyOf(harmonicPower);
    }

    /**
     * Gibt die melodische Wertigkeit zurück (Umkehrung der harmonischen Liste).
     */
    public List<Integer> getMelodicPowerOrder() {
        return List.copyOf(melodicPower);
    }

    /**
     * Berechnet absolute MIDI-Töne (Grundton + Intervall der Reihe 2 in Halbtonschritten).
     */
    public List<Integer> getSeries2Of(int midiBase) {
        List<Integer> res = new ArrayList<>(series2.size());
        for (Integer st : series2) {
            res.add(midiBase + st);
        }
        return res;
    }

    /**
     * Liefert zu einem Verwandtschaftsgrad (0..11) den entsprechenden Ton (Grundton + Intervall).
     * 0 = Oktave (+12), 11 = Tritonus (+6). Wir validieren streng 0..11.
     */
    public int getRelativeByDegree(int midiBase, int degree) {
        if (degree < 0 || degree > 11) {
            throw new IllegalArgumentException("degree must be between 0 and 11");
        }
        int semitones = series2.get(degree);
        return midiBase + semitones;
    }

    /**
     * Bestimmt aus einem Zweiklang den zu bevorzugenden Grundton nach der Root-Logik:
     * - Für 7,4,3,10,11: unterer Ton ist Grundton
     * - Für 5,8,9,2,1: oberer Ton ist Grundton
     * - Für Tritonus (6): keine klare Tendenz – optional leer; Overload gibt unteren Ton zurück
     */
    public Optional<Integer> chooseRootNoteOptional(int lowerMidi, int upperMidi) {
        if (upperMidi < lowerMidi) {
            throw new IllegalArgumentException("upperMidi must be >= lowerMidi");
        }
        int iv = Math.floorMod(upperMidi - lowerMidi, 12);
        if (iv == 0) return Optional.of(lowerMidi); // Einklang
        if (iv == 6) return Optional.empty(); // Tritonus unbestimmt
        if (chooseLowerNoteAsRootNote.contains(iv)) return Optional.of(lowerMidi);
        if (chooseUpperNoteAsRootNote.contains(iv)) return Optional.of(upperMidi);
        // Fallback: falls etwas außerhalb der Listen liegt, bevorzugen wir den unteren Ton
        return Optional.of(lowerMidi);
    }

    /**
     * Wie chooseRootNoteOptional, aber gibt bei Unbestimmtheit (Tritonus) den unteren Ton zurück.
     */
    public int chooseRootNote(int lowerMidi, int upperMidi) {
        return chooseRootNoteOptional(lowerMidi, upperMidi).orElse(lowerMidi);
    }

    public boolean isTritone(int semitones) {
        return Math.floorMod(semitones, 12) == 6;
    }

    /**
     * Liefert die Konsonanz-Rangzahl (Index in series2, 0 ist am konsonantesten: Oktave). 0 Halbton wird als 12 behandelt.
     */
    public int getConsonanceRankForSemitones(int semitones) {
        int mod = Math.floorMod(semitones, 12);
        int key = (mod == 0) ? 12 : mod;
        return series2.indexOf(key);
    }
}
