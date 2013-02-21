package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jp.gr.java_conf.dyama.rink.corpus.Common;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.corpus.TagSet;
import jp.gr.java_conf.dyama.rink.parser.Sentence;
import jp.gr.java_conf.dyama.rink.parser.SentenceReader;

/**
 * Sentence Reader for CoNLL-X format.
 * About the detail of CoNLL-X format, @see  http://nextens.uvt.nl/depparse-wiki/DataFormat
 * @author Hiroyasu Yamada
 */
public class CoNLLXSentenceReader implements SentenceReader {

    public enum Mode {
        TRAIN(7),
        TEST(6);

        private int columns_;
        private Mode(int columns){
            columns_ = columns;
        }
    }

    private static final int ID      = 0; // OK
    private static final int FORM    = 1;
    private static final int LEMMA   = 2; // OK
    private static final int CPOSTAG = 3;
    private static final int POSTAG  = 4; // OK
    private static final int FEATS   = 5; // OK
    private static final int HEAD    = 6;
    private static final int DEPLEL  = 7; // OK
    private static final int PHEAD   = 8;
    private static final int PDEPREL = 9; // OK

    private static final int START_ID = 1 ;

    private BufferedReader reader_;

    /** the number of reading lines */
    private int lines_ ;

    /** the annotation level */
    private Mode mode_ ;

    /**
     * Constructor
     *
     * @param path the path to the sentence file in CoNLL-X format.
     * @param mode the training/test mode.
     * @throws IllegalArgumentException if the path is null.
     * @throws IllegalArgumentException if the mode is null.
     * @throws FileNotFoundException if the path has not been found.
     */
    public CoNLLXSentenceReader(String path, Mode mode) throws FileNotFoundException {
        if (path == null)
            throw new IllegalArgumentException("the path is null.");
        if (mode == null)
            throw new IllegalArgumentException("the mode is null.");

        reader_ = new BufferedReader(new FileReader(path));
        lines_ = 0;
        mode_ = mode;
    }
    @Override
    public boolean hasAnySentences() throws IOException {
        return reader_.ready();
    }

    @Override
    public boolean read(Sentence sentence) throws IOException {
        if (sentence == null)
            throw new IllegalArgumentException("the sentence is null.");
        SentenceImpl sent =  (SentenceImpl) sentence;
        sent.clear();
        WordImpl.Generator generator = sent.getWordGenerator();

        boolean bos = false;
        int begin = 0;

        while (reader_.ready()) {
            String line = reader_.readLine();
            lines_ ++ ;
            if (line.isEmpty()) {
                if (bos)
                    break;
                continue;
            }
            String[] columns = line.split("\t");
            bos = true;

            if (columns.length < mode_.columns_)
                throw new IllegalArgumentException("invalid columns at line " + lines_ + ".");

            TagSet.PartOfSpeech pos = PTB.POS.parseString(columns[CPOSTAG]);
            assert(pos != null);
            if (pos == Common.POS.UNDEFINED)
                throw new IllegalArgumentException("undefined part-of-speech tag: " + columns[CPOSTAG] + " at line " + lines_ + ".");

            int head = -1;
            try {
                if (mode_ == Mode.TRAIN){
                    head = Integer.parseInt(columns[HEAD]);
                    if (head < 0)
                        throw new IllegalArgumentException("invalid head ID: " + columns[HEAD] + " at line " + lines_ + ".");
                    head -= START_ID;
                }
            } catch (NumberFormatException e){
                throw new IllegalArgumentException("invalid head ID: " + columns[HEAD] + " at line " + lines_ + ".");
            }
            WordImpl w = sent.getReusableWord();
            sent.addWord(generator.generate(columns[FORM], begin, begin + columns[FORM].length(), pos, head, w));
            begin += (columns[FORM].length() + 1);
        }
        return bos;
    }

    @Override
    synchronized public void close() throws IOException{
        reader_.close();
    }
}
