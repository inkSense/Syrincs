package syrincs.a_domain.Scale;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Scale {
    private final Set<Integer> intervals;
    private final String name;
    private int tonic; //finalis, Grundton, 'first degree' in der SubkontraOktave (wiss: C0 - H0)
    private final Set<Integer> lowestNotes; //innerhalb SubkontraOktave (wiss: C0 - H0). Liegen also ungeordnet vor.
    private final Set<Integer> lowestFullScale; //Das soll die erste Skala in "Grundstellung" innerhalb der SubkontraOktave (wiss: C0 - H0) sein.
    List<Integer> stock; //Tonvorrat
    private final int midiC0 = 12; //Midinote von wiss. C0. (Zur Erinnerung: Klavier beginnt bei A0.)

    private final int midiA0 = midiC0 + 9;
    private final int midiC8 = 108;

    public Scale(Set<Integer> intervals, String name, int tonic) {
        this.intervals = intervals;
        this.name = name;
        if(0 <= tonic && tonic < 12) {
            this.tonic = tonic;
        } else {
            throw new IllegalArgumentException("Der Grundton liegt außerhalb des erlaubten Bereichs: " + tonic);
        }
        this.lowestNotes = intervals.stream().map(i -> i + midiC0 ).collect(Collectors.toSet()); //Alle Noten in der Oktave 0
        this.lowestFullScale = lowestNotes.stream().map(note -> note < tonic ? note + 12 : note).collect(Collectors.toSet()); //vollst. Skala auf Grundton in Oktave 0
    }

    public String getName() {
        return name;
    }
    public int getTonicByOctave(int octave){
        int tonicOctaved = octave * 12 + tonic;
        if(tonicOctaved < midiA0){
            throw new IllegalArgumentException("Die Oktave " + octave + " liefert einen zu niedrigen Ton.");
        } else if (tonicOctaved > midiC8){
            throw new IllegalArgumentException("Die Oktave " + octave + " liefert einen zu hohen Ton.");
        }
        return tonicOctaved;
    }
    public List<Integer> getNotesbyOctave(int octave){
        List<Integer> list;
        if( octave > 8){
            throw new IllegalArgumentException("Die Oktave " + octave + " ist zu hoch.");
        } else if (octave < -1) {
            throw new IllegalArgumentException("Die Oktave " + octave + " ist zu tief.");
        } else {
            //Noten werden oktaviert:
            list = lowestNotes.stream().map(note-> octave * 12 + note).collect(Collectors.toList());
            //Wenn die Töne klein genug sind, also nicht höher als auf dem Klavier, werden sie beibehalten:
            if(octave == 8){
                list = list.stream().filter(note -> note < midiC8).collect(Collectors.toList());
            }
            return list;
        }
    }
    public List<Integer> getScaleWithinOctaveStartingFromTonic(int octave){
        List<Integer> list;
        if(octave > 8){
            throw new IllegalArgumentException("Die Oktave " + octave + " ist zu hoch.");
        } else if (octave < -1) {
            throw new IllegalArgumentException("Die Oktave " + octave + " ist zu tief.");
        } else {
            //Noten werden oktaviert:
            list = lowestFullScale.stream().map(note-> octave * 12 + note).collect(Collectors.toList());
            //Wenn die Töne klein genug sind, also nicht höher als auf dem Klavier, werden sie beibehalten:
            return list.stream().filter(note -> note < midiC8).collect(Collectors.toList());
        }
    }

    public List<Integer> getStockByOctaves(int lowerOctave, int upperOctave){
        List<Integer> stock = new ArrayList<>(); //Das ist der Tonvorrat.

        if(lowerOctave < 0){
            throw new IllegalArgumentException("Die untere Grenze ist zu niedrig.");
        }
        if(upperOctave > 8 ){
            throw new IllegalArgumentException("Die obere Grenze ist zu hoch.");
        }
        if(lowerOctave > upperOctave){
            throw new IllegalArgumentException("lowerOctave ist größer als upperOctave.");
        }

        for(int octave = lowerOctave; octave <= upperOctave; octave++){
            List<Integer> notes = getScaleWithinOctaveStartingFromTonic(octave);
            for(Integer note : notes){
                if( midiA0 <= note  && note <= midiC8 ){
                    stock.add(note);
                }
            }
        }
        return stock;
    }

    private void makeStockByConstraints(int lowestNote, int highestNote){
        List<Integer> stock = new ArrayList<>(); //Das ist der Tonvorrat.
        if(lowestNote < midiA0 ){
            throw new IllegalArgumentException("Die untere Grenze ist zu niedrig.");
        }
        if(highestNote > midiC8 ){
            throw new IllegalArgumentException("Die obere Grenze ist zu hoch.");
        }

        for(int octave = 0; octave < 9; octave++){
            List<Integer> octaveNotes = getScaleWithinOctaveStartingFromTonic(octave);
            for( Integer note : octaveNotes){
                if(lowestNote < note && note < highestNote){
                    stock.add(note);
                }
            }
        }
    this.stock =  stock;
    }

    public List<Integer> getStock() {
        if(stock == null){
            makeStockByConstraints(midiA0, midiC8);
        }
        return stock;
    }

    public void setTonic(int tonic) {
        this.tonic = tonic;
    }

    public void setStock(List<Integer> stock) {
        this.stock = stock;
    }

}