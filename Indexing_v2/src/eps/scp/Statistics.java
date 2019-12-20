/* ---------------------------------------------------------------
Práctica 4.
Código fuente: IndexingConc.java
Grau Informàtica
49383707Q i Joaquim Picó Mora.
47984615Z i Ian Palacín Aliana.
---------------------------------------------------------------*/
package eps.scp;

import com.google.common.collect.HashMultimap;

import java.util.ArrayList;

public class Statistics {

    public int numKeys;
    public long numOffset;
    public int numBytes;
    public int key_size;
    public ArrayList<Double> percentages= new ArrayList<Double>();
    double percentage;

    public Statistics(){
        numKeys=0;
        numOffset=0;
        numBytes=0;
    }

    public Statistics(int key_size){
        numKeys=0;
        numOffset=0;
        numBytes=0;
        this.key_size=key_size;

    }

    public synchronized void sumKey(){
        this.numKeys++;
    }

    public synchronized void sumOffset(){
        this.numOffset++;
    }

    public synchronized void sumBytes(){
        this.numBytes++;
    }

    public synchronized void printStats(double percentage) {
        System.out.print("Statistics "+percentage+"%-------------------------------------\n");
        System.out.print("Number of generated Keys: " + this.numKeys + "\n");
        System.out.print("Number of generated offsets: " + this.numOffset + "\n");
        System.out.print("Number of procesed bytes: " + this.numBytes + "\n");
    }

}
