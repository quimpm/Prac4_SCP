package eps.scp;

/**
 * Created by Nando on 8/10/19.
 */
public class QuerySeq
{
    private static InvertedIndexSeq inv_index = new InvertedIndexSeq();

    public QuerySeq(String[] args){
        main(args);
    }

    public InvertedIndexSeq get_InvertedIndex_seq(){
        return inv_index;
    }

    public static void main(String[] args)
    {
        InvertedIndexSeq hash;
        String queryString=null, indexDirectory=null, fileName=null;

        if (args.length <2 || args.length>4)
            System.err.println("Erro in Parameters. Usage: Query <String> <IndexDirectory> <filename> [<Key_Size>]");
        if (args.length > 0)
            queryString = args[0];
        if (args.length > 1)
            indexDirectory = args[1];
        if (args.length > 2)
            fileName = args[2];
        if (args.length > 3)
            hash = new InvertedIndexSeq(Integer.parseInt(args[3]));
        else
            hash = new InvertedIndexSeq();

        hash.LoadIndex(indexDirectory);
        hash.SetFileName(fileName);
        //hash.PrintIndex();
        hash.Query(queryString);

        inv_index.setHash(hash.getHash());
    }

}
