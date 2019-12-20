/* ---------------------------------------------------------------
Práctica 4.
Código fuente: IndexingConc.java
Grau Informàtica
49383707Q i Joaquim Picó Mora.
47984615Z i Ian Palacín Aliana.
---------------------------------------------------------------*/

package eps.scp;

import com.google.common.collect.HashMultimap;

import java.io.File;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IndexingConc {

    /* Argument methods*/
    private static int numThreads;

    private static InvertedIndexConc inv_index = new InvertedIndexConc();

    public IndexingConc(String[] args){
        main(args);
    }

    public InvertedIndexConc get_InvertedIndex(){
        return inv_index;
    }


    public static void main(String[] args)
    {
        /* Inicialización de variables */
        AtomicBoolean debug = new AtomicBoolean(false);//TODO desatomitzar
        if(debug.get()) System.err.println("Inicialització");
        int[] threadCharge;
        InvertedIndexConc hash;
        Thread[] threads_storage;
        Semaphore sem;
        int start=0;
        int end=0;
        int key_size = 10; //Default number
        int percentage = 10;
        ReentrantLock printLocalLock=new ReentrantLock();
         ReentrantLock lock = new ReentrantLock();
        if(debug.get()) System.err.println("Fi inicialització");
        int countThread=0;


        /* Control argumentos */
        if(debug.get()) System.err.println("Control arguments");
        if (args.length <2 || args.length >5) {
            System.err.println("Error in Parameters. Usage: Indexing <Thread_number>  <TextFile> [<Key_Size>] [<Index_Directory>]");
            throw new IllegalArgumentException();
        }
        String text_file = args[1];
        numThreads = Integer.parseInt( args[0] );
        sem = new Semaphore(numThreads);
        threads_storage = new Thread[numThreads];
        if (args.length == 2){ /* Text file and thread number */
            hash = new InvertedIndexConc(text_file);
        }else if (args.length == 3){ /* Text file, thread number and key size (and possibly index directory) */
            key_size = Integer.parseInt(args[2]);
            hash = new InvertedIndexConc(text_file, key_size);
        }else{/* Text file, thread number and key size and percentage(and possibly index directory) */
            key_size = Integer.parseInt(args[2]);
            percentage= Integer.parseInt(args[4]);
            hash = new InvertedIndexConc(text_file, key_size);
        }
        Statistics global_stats = new Statistics(key_size);
        if(debug.get()) System.err.println("Fi control arguments");

        /* Balanceo de carga y creación de hilos */
        if(debug.get()) System.err.println("Balanceig carrega");
        threadCharge = balanceoCarga(text_file, key_size);
        if(debug.get()) System.err.println("Fi balanceig carrega");


        /* Creación threads */
        if(debug.get()) System.err.println("Creació threads");
        for(int i = 0; i < numThreads; i++){
            end+=threadCharge[i]-1;
            threads_storage[i] =  new Thread(new partsBuildIndex(start,end,hash,sem,global_stats,percentage,printLocalLock, countThread, numThreads, lock));
            threads_storage[i].start();
            start+=threadCharge[i];
            end++;
        }
        if(debug.get()) System.err.println("Fi creació threads");


        /* Join de hilos */
        try{
            sem.acquire(numThreads);
        }catch(InterruptedException ex){
            System.out.println("Error Writing File");
        }finally {
            if(debug.get()) System.err.println("Fi juntar fils");

            /* Guardar resultado */
            if (args.length > 3) {
                hash.SaveIndex(args[3]);
            }else{
                hash.PrintIndex();
            }
            if(debug.get()) System.err.println("Fi guardar resultats");

            /* Actualizar método para el testing */
            inv_index.setHash(hash.getHash());
            sem.release();
        }
        global_stats.printStats(100);

    }

    private static int[] balanceoCarga(String file_name, int key_size){

        File file = new File(file_name);
        int[] threadCharge = new int[numThreads];
        float real_end = file.length() - key_size + 1;
        for(int i = 0; i < numThreads; i++){
            threadCharge[i]= (int) Math.floor((float)real_end/ numThreads);
        }

        for(int i = 0; i<(int)real_end% numThreads; i++){
            threadCharge[i]++;
        }
        return threadCharge;
    }

    public static class partsSaveIndex implements Runnable{
        private String outputFile;
        private InvertedIndexConc inverted;
        private int lowerBound;
        private int upperBound;
        private Set keySubset;

        private partsSaveIndex(String outputFile, InvertedIndexConc inverted, int lowerBound, int upperBound, Set<String> keySubset){
            this.outputFile = outputFile;
            this.inverted = inverted;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.keySubset = keySubset;
        }
        public void run(){
            inverted.SaveIndexConc(outputFile, lowerBound, upperBound, keySubset);
        }
    }

    public static class partsBuildIndex implements Runnable{

        public int start,end,percentage;
        InvertedIndexConc inverted;
        Semaphore sem;
        Statistics global_stats;
        ReentrantLock printLocalLock, lock;
        int countThread, numThreads;



        private partsBuildIndex(int start, int end, InvertedIndexConc inverted, Semaphore sem, Statistics global_stats, int percentage, ReentrantLock printLocalLock, int countThread, int numThreads, ReentrantLock lock){
            this.start=start;
            this.end=end;
            this.inverted = inverted;
            this.sem=sem;
            this.global_stats=global_stats;
            this.percentage=percentage;
            this.printLocalLock=printLocalLock;
            this.lock=lock;
            this.countThread=countThread;
            this.numThreads=numThreads;
        }

        public void run(){
            try {
                sem.acquire();
                this.inverted.BuildIndex(start, end, global_stats,percentage, printLocalLock,countThread, numThreads,lock);
            }catch (InterruptedException ex){
                System.out.println("Error Writing File");
            }finally {
                sem.release();
            }

        }
    }
}


