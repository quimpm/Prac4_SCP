package eps.scp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryTest {

    @Test
    void example2() {

        String[] argsConc = {"4", "123456789012345", "Output/example2", "test/example2.txt", "10"};
        String[] argsSeq = {"123456789012345", "Output/example2", "test/example2.txt", "10"};

        Query hash_conc= new Query(argsConc);
        QuerySeq hash_seq=new QuerySeq(argsSeq);

        assertEquals(hash_seq.get_InvertedIndex_seq().getHash(), hash_conc.get_InvertedIndex().getHash());
    }
    @Test
    void example3() {

        String[] argsConc = {"4", "345678902456789", "Output/example3", "test/example3.txt", "10"};
        String[] argsSeq = {"345678902456789", "Output/example3", "test/example3.txt", "10"};

        Query hash_conc= new Query(argsConc);
        QuerySeq hash_seq=new QuerySeq(argsSeq);

        assertEquals(hash_seq.get_InvertedIndex_seq().getHash(), hash_conc.get_InvertedIndex().getHash());
    }
    @Test
    void exampleQuijote() {

        String[] argsConc = {"4", "En un lugar de la Mancha", "Output/quijote/", "test/pg2000.txt", "10"};
        String[] argsSeq = {"En un lugar de la Mancha", "Output/quijote/", "test/pg2000.txt", "10"};

        Query hash_conc= new Query(argsConc);
        QuerySeq hash_seq=new QuerySeq(argsSeq);

        assertEquals(hash_seq.get_InvertedIndex_seq().getHash(), hash_conc.get_InvertedIndex().getHash());
    }
}