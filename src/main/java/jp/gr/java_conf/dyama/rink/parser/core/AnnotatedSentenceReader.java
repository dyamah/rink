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
 * Sentence Reader for dependency annotated sentences.<br>
 * The format is as follows:<br>
 * $word $pos $parent<br>
 * these three columns is segmented by tab character.<br>
 * <br>
 * $word means the string of word surface.<br>
 * $pos means the string of the part-of-speech tag. <br>
 * $parent means the ID of dependency parent. If $parent is negative, the parent is root of the dependency tree/forest.   <br>
 * the empty line stands for EOS.<br>
 * For example:<br>
 * <br>
 * I       PRN   1<br>
 * bought  VBD  -1<br>
 * a       DT    3<br>
 * book    NN    1<br>
 * .       .     1<br>
 * <br>
 * @author Hiroyasu Yamada
 */
@Deprecated
public class AnnotatedSentenceReader implements SentenceReader {

    /** Annotation Level */
    public enum AnnotationLevel {
        POS(2),
        DEPENDENCY(3);

        int column_ ;
        private AnnotationLevel(int column){
            column_ = column;
        }

        public int getColumn(){
            return column_;
        }

    }

    private static String DEFAULT_DEP_STRING = "-1";

    private BufferedReader reader_;

    /** number of reading lines */
    private int lines_ ;

    /** annotation level */
    private AnnotationLevel level_ ;

    /**
     * @param path path to the annotated sentence file. throw IllegalArgumentException if path is null.
     * @param level  annotation level. throw IllegalArgumentException if level is null.
     * @throws FileNotFoundException
     */
    public AnnotatedSentenceReader(String path, AnnotationLevel level) throws FileNotFoundException {
        if (path == null)
            throw new IllegalArgumentException("the path is null.");
        if (level == null)
            throw new IllegalArgumentException("the level is null.");

        reader_ = new BufferedReader(new FileReader(path));
        lines_ = 0;
        level_ = level;
    }


    @Override
    synchronized public boolean hasAnySentences() throws IOException {
        return reader_.ready();
    }

    @Override
    synchronized public boolean read(Sentence sentence) throws IOException {
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

            if (columns.length < level_.getColumn()){
                throw new IllegalArgumentException("invalid columns at line " + lines_ + ".");
            }
            String dep = DEFAULT_DEP_STRING ;
            if (columns.length >= AnnotationLevel.DEPENDENCY.getColumn()){
                dep = columns[AnnotationLevel.DEPENDENCY.getColumn() - 1];
            }


            TagSet.PartOfSpeech pos = PTB.POS.parseString(columns[1]);
            assert(pos != null);
            if (pos == Common.POS.UNDEFINED){
                throw new IllegalArgumentException("undefined part-of-speech tag: " + columns[1] + " at line " + lines_ + ".");
            }

            int parent = -1;
            try {
                parent = Integer.parseInt(dep);
            } catch (NumberFormatException e){
                throw new IllegalArgumentException("invalid format as a dependency parent: " + columns[2] + " at line " + lines_ + ".");
            }
            WordImpl w = sent.getReusableWord();
            sent.addWord(generator.generate(columns[0], begin, begin + columns[0].length(), pos, parent, w));
            begin += (columns[0].length() + 1);
        }
        return bos;
    }

    @Override
    synchronized public void close() throws IOException{
        reader_.close();
    }
}
