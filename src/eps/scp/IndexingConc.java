package eps.scp;

import com.google.common.collect.HashMultimap;

import java.io.File;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
        InvertedIndexConc[] inverted_hashes;
        Thread[] threads_storage;
        int start=0;
        int end=0;
        int key_size = 10; //Default number
        if(debug.get()) System.err.println("Fi inicialització");

        /* Control argumentos */
        if(debug.get()) System.err.println("Control arguments");
        if (args.length <2 || args.length >4) {
            System.err.println("Error in Parameters. Usage: Indexing <TextFile> <Thread_number>[<Key_Size>] [<Index_Directory>]");
            throw new IllegalArgumentException();
        }
        String text_file = args[1];
        numThreads = Integer.parseInt( args[0] );
        inverted_hashes = new InvertedIndexConc[numThreads];
        threads_storage = new Thread[numThreads];
        if (args.length == 2){ /* Text file and thread number */
            for (int i = 0; i < numThreads; i++) inverted_hashes[i] = new InvertedIndexConc(text_file);
        }else{ /* Text file, thread number and key size (and possibly index directory) */
            key_size = Integer.parseInt(args[2]);
            for(int i = 0; i < numThreads; i++) inverted_hashes[i] = new InvertedIndexConc(text_file, key_size);
        }
        if(debug.get()) System.err.println("Fi control arguments");



        /* Balanceo de carga y creación de hilos */
        if(debug.get()) System.err.println("Balanceig carrega");
        threadCharge = balanceoCarga(text_file, key_size);
        if(debug.get()) System.err.println("Fi balanceig carrega");


        /* Creación threads */
        if(debug.get()) System.err.println("Creació threads");
        for(int i = 0; i < numThreads; i++){
            end+=threadCharge[i]-1;
            threads_storage[i] =  new Thread(new partsBuildIndex(start,end,inverted_hashes[i]));
            threads_storage[i].start();
            start+=threadCharge[i];
            end++;
        }
        if(debug.get()) System.err.println("Fi creació threads");



        /* Join de hilos */
        if(debug.get()) System.err.println("Juntar fils");
        try{
            for(int i = 0; i < numThreads; i++){
                threads_storage[i].join();
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        if(debug.get()) System.err.println("Fi juntar fils");


        /* Juntar hashes parciales */
        if(debug.get()) System.err.println("Juntar hashos parcials");
        HashMultimap<String, Long> mult_hash = inverted_hashes[0].getHash();
        for(int i = 1; i < numThreads; i++){
            if(debug.get()) System.err.println("Fusionant hash " + i);
            mult_hash.putAll(inverted_hashes[i].getHash());
            if(debug.get()) System.err.println("Fi fusionant hash " + i);
        }
        inverted_hashes[0].setHash(mult_hash);
        if(debug.get()) System.err.println("Fi juntar hashos parcials");



        // Save concurrente
        /*if(debug.get()) System.err.println("Guardar resultats");
        int numberOfFiles, remainingFiles;
        Set<String> keySet = inverted_hashes[0].getHash().keySet();


        /* if(debug.get()) System.err.println("Calcular numero de ficheros que toca");
        int DIndexMaxNumberOfFiles = 1000;
        if (keySet.size()> DIndexMaxNumberOfFiles)
            numberOfFiles = DIndexMaxNumberOfFiles;
        else{
            numberOfFiles = keySet.size();
        }
        if(debug.get()) System.err.println("Inicio guardado");
        int[] balanceFicheros = balanceoFicheros(numberOfFiles);
        String[] setString;
        int acum = 0;
        String[] keyList = keySet.toArray(new String[keySet.size()]);
        remainingFiles = numberOfFiles;
        long remainingKeys = keyList.length, keysByFile=0;

        Set<String> acumSet = ImmutableSet.copyOf(keySet);
        if (args.length > 3){
            if(debug.get()) System.err.println("Guardar resultats");
            for(int i = 0; i < numThreads; i++) {
                keysByFile =  remainingKeys / remainingFiles;
                remainingKeys -= keysByFile * balanceFicheros[i];

                if(debug.get()) System.err.println("Copiar substring");
                Set<String> keySubset = ImmutableSet.copyOf(Iterables.limit(acumSet, balanceFicheros[i]));
                setString = acumSet.toArray(new String[acumSet.size()]);
                if(debug.get()) System.err.println("Borrar llaves");
                for(int j = 0; j < balanceFicheros[i]*keysByFile; j++){
                   setString = ArrayUtils.remove(setString, 0);
                    //System.err.println(arr);
                    //arr.remove(0);
                }
                if(debug.get()) System.err.println("Creación de threads");
                acumSet = Set.of(setString);
//                System.err.println("Thread " + i + " guarda el subset " + keySubset + "des de "
//                       + acum + " fins a " + (acum+balanceFicheros[i]-1));
               new Thread(new partsSaveIndex(args[3], inverted_hashes[0], acum, acum+balanceFicheros[i]-1, keySubset)).start();
               acum += balanceFicheros[i];
           }
        }
        else inverted_hashes[0].PrintIndex();
        */

        /* Guardar resultado */
        if (args.length > 3) {
            inverted_hashes[0].SaveIndex(args[3]);
        }else{
            inverted_hashes[0].PrintIndex();
            }
        if(debug.get()) System.err.println("Fi guardar resultats");

        /* Actualizar método para el testing */
        inv_index.setHash(inverted_hashes[0].getHash());
    }
    private static int[] balanceoFicheros(int num_ficheros){

        int[] threadCharge = new int[numThreads];
        for(int i = 0; i < numThreads; i++){
            threadCharge[i]= (int) Math.floor((float)num_ficheros/ numThreads);
        }

        for(int i = 0; i<(int)num_ficheros% numThreads; i++){
            threadCharge[i]++;
        }
        return threadCharge;
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

        public int start;
        public int end;
        InvertedIndexConc inverted;

        private partsBuildIndex(int start, int end, InvertedIndexConc inverted){
            this.start=start;
            this.end=end;
            this.inverted =inverted;
        }

        public void run(){
            this.inverted.BuildIndex(start, end);
        }
    }
}


