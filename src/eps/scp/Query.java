package eps.scp;

import com.google.common.collect.HashMultimap;

import java.io.File;

import static java.lang.System.exit;

/**
 * Created by Nando on 8/10/19.
 */
public class Query
{

    private static InvertedIndexConc inv_index = new InvertedIndexConc();

    public Query(String[] args){
        main(args);
    }

    public InvertedIndexConc get_InvertedIndex(){
        return inv_index;
    }

    public static void main(String[] args)
    {
        //TODO: Generalitzar, descomentar per agafar dels arguments
        String queryString=null, indexDirectory=null, fileName=null;
        int start=0,end=0,index;
        File folder;
        int[] threadsCharge;
        Boolean debug=false;


        if (args.length <4 || args.length>5)
            System.err.println("Error in Parameters. Usage: Query <String> <IndexDirectory> <filename> [<Key_Size>]");

        int num_threads = Integer.parseInt( args[0] );
        queryString = args[1];
        indexDirectory = args[2];
        fileName = args[3];

        Thread[] threads_storage = new Thread[num_threads];
        InvertedIndexConc[] inverted_hashes = new InvertedIndexConc[num_threads];

        if (args.length == 5)
            for (int i = 0; i < num_threads; i++) inverted_hashes[i] = new InvertedIndexConc(Integer.parseInt(args[4]));
        else
            for (int i = 0; i < num_threads; i++) inverted_hashes[i] = new InvertedIndexConc();


        //Agafem la llista de fitxers continguts dincs de la carpeta folder
        folder= new File(indexDirectory);
         File[] listOfFiles = folder.listFiles();

        //Fem el balanceo de carga per cada thread
        threadsCharge=balanceoCarga(listOfFiles.length, num_threads );

        if(debug) System.err.println("Load");
        //Creació fils
        threads_storage=startThreads(num_threads, threadsCharge, listOfFiles, inverted_hashes);


        /* Join de fils */
        try{
            for(int i = 0; i < num_threads; i++){
                threads_storage[i].join();
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        if(debug) System.err.println("Fi load");

        if(debug) System.err.println("PutAll");
        /* Juntar hashes parciales */
        HashMultimap<String, Long> mult_hash = inverted_hashes[0].getHash();
        for(int i = 1; i < num_threads; i++) mult_hash.putAll(inverted_hashes[i].getHash());
        inv_index.setHash(mult_hash);
        inverted_hashes[0].setHash(mult_hash);
        if(debug) System.err.println("Fi PutAll");

        inverted_hashes[0].SetFileName(fileName);

        //inverted_hashes[0].PrintIndex();
        inverted_hashes[0].Query(queryString);
    }

    public static Thread[] startThreads(int num_threads, int[] threadsCharge, File[] listOfFiles, InvertedIndexConc[] inverted_hashes){
        Thread[] threads_storage = new Thread[num_threads];
        int index, end=0, start=0;
        File[] threadListOfFiles;

        for(int i = 0; i < num_threads; i++){
            index=0;
            end += threadsCharge[i] - 1;

            //Creem i omplim la llista de referències a fitxers els quals cada thread haurà de llegir.
            threadListOfFiles = new File[threadsCharge[i]];
            for(int j=start;j<=end;j++){
                threadListOfFiles[index]=listOfFiles[j];
                index++;
            }

            //Creem thread
            threads_storage[i] =  new Thread(new partsLoadIndex(threadListOfFiles, inverted_hashes[i]));
            threads_storage[i].start();

            start += threadsCharge[i];
            end++;
        }

        return threads_storage;
    }
    public static int[] balanceoCarga(int num_files, int num_threads){

        int[] threadCharge = new int[num_threads];
        for(int i = 0;i < num_threads;i++){
            threadCharge[i]= (int) Math.floor(num_files/num_threads);
        }

        for(int i = 0; i<num_files%num_threads; i++){
            threadCharge[i]++;
        }
        return threadCharge;
    }

    public static class partsLoadIndex implements Runnable{


        public int start, end;
        public InvertedIndexConc hash;
        File[] listOfFiles;

        public partsLoadIndex(File[] listOfFiles, InvertedIndexConc hash){
            this.listOfFiles=listOfFiles;
            this.hash=hash;
        }

        public void run(){
            hash.LoadIndex(listOfFiles);
        }

    }

}

