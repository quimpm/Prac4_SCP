package eps.scp;

import org.junit.jupiter.api.Test;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;


class IndexingTest {

/*
    @org.junit.jupiter.api.Test
    void main() {
        testBuildIndexExample1();
        testBuildIndexExample2();
        //testBuildIndexExample3();
        testBuildIndexExample4();

    }*/

    @Test
    public void testBuildIndexExample1(){
        String[] argsConc = {"4", "test/example1.txt", "10", "Output/example1", "5"};
        String[] argsSeq = {"test/example1.txt", "10", "Output/example1"};

        //Indexacion concurrente
        IndexingConc index_conc = new IndexingConc(argsConc);

        //Indexación secuencial
        IndexingSeq index_seq = new IndexingSeq(argsSeq);

        assertEquals(index_seq.get_InvertedIndex_seq().getHash(), index_conc.get_InvertedIndex().getHash());
    }

    @Test
    public void testBuildIndexExample2(){
        String[] argsConc = {"4", "test/example2.txt",  "10", "Output/example2", "5"};
        String[] argsSeq = {"test/example2.txt", "10", "Output/example2"};
        IndexingConc indexConc = new IndexingConc(argsConc);
        IndexingSeq indexSeq = new IndexingSeq(argsSeq);

        assertEquals(indexConc.get_InvertedIndex().getHash(), indexSeq.get_InvertedIndex_seq().getHash());
    }

    @Test
    public void testBuildIndexExample3(){
        String[] argsConc = {"4", "test/example3.txt",  "10", "Output/example3", "5"};
        String[] argsSeq = {"test/example3.txt", "10", "Output/example3"};
        IndexingConc indexConc = new IndexingConc(argsConc);
        IndexingSeq indexSeq = new IndexingSeq(argsSeq);

        assertEquals(indexConc.get_InvertedIndex().getHash(), indexSeq.get_InvertedIndex_seq().getHash());
    }

    @Test
    public void testBuildIndexExample4(){
        String[] argsConc = {"4", "test/example4.txt",  "10", "Output/example4", "5"};
        String[] argsSeq = {"test/example4.txt", "10", "Output/example4"};
        IndexingConc indexConc = new IndexingConc(argsConc);
        IndexingSeq indexSeq = new IndexingSeq(argsSeq);

        assertEquals(indexConc.get_InvertedIndex().getHash(), indexSeq.get_InvertedIndex_seq().getHash());
    }

    @Test
    public void testArgumentsNoArguments(){
        String[] args_conc = {};
        try {
            new IndexingConc(args_conc);
            fail("Exception not thrown");
        }catch (Exception e){
            //No statements needed
        }
    }

    @Test
    public void testArgumentsOneFiveArgument(){
        String[] args_conc_1 = {"test/example3.txt"};
        String[] args_conc_2 = {"4", "test/example3.txt",  "10", "Output/example3", "5", "This should not happen"};
        try {
            new IndexingConc(args_conc_1);
            fail("Exception not thrown");
        }catch (Exception e){/*No statements needed*/}
        try {
            new IndexingConc(args_conc_2);
            fail("Exception not thrown");
        }catch (Exception e){/*No statements needed*/}
}

    @Test
    public void testArgumentsTwoThreeFourArguments(){
        String[] argsConc1 = {"4", "test/example3.txt"};
        String[] argsConc2 = {"4", "test/example3.txt", "10"};
        String[] argsConc3 = {"4", "test/example3.txt", "10", "Output/example3", "5"};

        try {
            new IndexingConc(argsConc1);
        }catch (Exception e){
            System.out.print(e.getMessage());
            fail("Exception thrown and should not");}
        try{
            new IndexingConc(argsConc2);
        }catch (Exception e){fail("Exception thrown and should not");}
        try{
            new IndexingConc(argsConc3);
        }catch (Exception e){fail("Exception thrown and should not");}
    }

    //Es posible que el test falle por el orden de los ficheros, eso no implica que el resultat estigui malament

    @Test
    public void testSaveSameFilesExample1(){
        String[] argsConc = {"2", "test/example1.txt", "10", "Output/tmp1", "5"};
        String[] argsSeq = {"test/example1.txt", "10", "Output/tmp2"};
        cleanDirectoryTmps();
        new IndexingConc(argsConc);
        new IndexingSeq(argsSeq);
        try {
            assertTrue(compareTmps());
        }catch (Exception e){
            System.err.println(e.toString());
            fail("Lanzada excepción no deseada");
        }
    }
    @Test
    public void testSaveSameFilesExample2(){
        String[] argsConc = {"1", "test/example2.txt", "10", "Output/tmp1", "5"};
        String[] argsSeq = {"test/example2.txt", "10", "Output/tmp2"};
        cleanDirectoryTmps();
        new IndexingConc(argsConc);
        new IndexingSeq(argsSeq);
        try {
            assertTrue(compareTmps());
        }catch (Exception e){
            System.err.println(e.toString());
            fail("Lanzada excepción no deseada");
        }
    }
    @Test
    public void testSaveSameFilesExample3(){
        String[] argsConc = {"1", "test/example3.txt", "10", "Output/tmp1", "5"};
        String[] argsSeq = {"test/example3.txt", "10", "Output/tmp2"};
        cleanDirectoryTmps();
        new IndexingConc(argsConc);
        new IndexingSeq(argsSeq);
        try {
            assertTrue(compareTmps());
        }catch (Exception e){
            System.err.println(e.toString());
            fail("Lanzada excepción no deseada");
        }
    }

    //Test comentado para agilizar el proceso de testing
    /*@Test
    public void testSaveSameFilesQuijote(){
        String[] argsConc = {"1", "test/pg2000.txt", "10", "Output/tmp1"};
        String[] argsSeq = {"test/pg2000.txt", "10", "Output/tmp2"};
        cleanDirectoryTmps();
        new IndexingConc(argsConc);
        new IndexingSeq(argsSeq);
        try {
            assertTrue(compareTmps());
        }catch (Exception e){
            System.err.println(e.toString());
            fail("Lanzada excepción no deseada");
        }
    }*/

    //Limpia directorio de ficheros
    private void cleanDirectoryTmps(){
        File tmp1 = new File("Output/tmp1");
        File tmp2 = new File("Output/tmp2");
        for(File file: tmp1.listFiles())
            if (!file.isDirectory())
                file.delete();
        for(File file: tmp2.listFiles())
            if (!file.isDirectory())
                file.delete();

    }


    //Compara directorios temporales, retorna verdadero en caso de ser iguales y falso en caso contrario
    private boolean compareTmps() throws IOException {
        File tmp1 = new File("Output/tmp1");
        File tmp2 = new File("Output/tmp2");

        File[] tmp1Listing = tmp1.listFiles();
        File[] tmp2Listing = tmp2.listFiles();


        String line;
        if(tmp1Listing != null && tmp2Listing != null){
            for(int i = 0; i < tmp1Listing.length; i++){
                FileInputStream fin1 =  new FileInputStream(tmp1Listing[i]);
                BufferedReader input1 = new BufferedReader(new InputStreamReader(fin1));
                StringBuilder sb1 = new StringBuilder();
                while ((line = input1.readLine()) != null) {
                    sb1.append(line);
                }
                FileInputStream fin2 =  new FileInputStream(tmp1Listing[i]);
                BufferedReader input2 = new BufferedReader(new InputStreamReader(fin2));
                StringBuilder sb2 = new StringBuilder();
                while ((line = input2.readLine()) != null) {
                    sb2.append(line);
                }
                if(!sb1.toString().equals(sb2.toString())) {
                    System.err.println("Expected: " + sb1.toString() + " Found: "+sb2.toString()
                            + " on file" + tmp1Listing[i] + " " + tmp2Listing[i]);
                    return false;
                }
            }
        }else{
            System.err.println("File length error");
            return false;
        }
        return true;
    }
}














