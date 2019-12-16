package eps.scp;

public class IndexingSeq {
    private static InvertedIndexSeq inv_index = new InvertedIndexSeq();

    public IndexingSeq(String[] args){
        main(args);
    }

    public InvertedIndexSeq get_InvertedIndex_seq(){
        return inv_index;
    }

    public static void main(String[] args)
    {
        InvertedIndexSeq hash;

        if (args.length <1 || args.length>4)
            System.err.println("Erro in Parameters. Usage: Indexing <TextFile> [<Key_Size>] [<Index_Directory>]");
        if (args.length < 2)
            hash = new InvertedIndexSeq(args[0]);
        else
            hash = new InvertedIndexSeq(args[0], Integer.parseInt(args[1]));

        hash.BuildIndex();

        if (args.length > 2)
            hash.SaveIndex(args[2]);
        else
            hash.PrintIndex();

        inv_index.setHash(hash.getHash());
    }

}
