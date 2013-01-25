package jp.gr.java_conf.dyama.rink.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.SampleWriter;
import jp.gr.java_conf.dyama.rink.parser.SentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.CoNLLXSampleWriter;
import jp.gr.java_conf.dyama.rink.parser.core.CoNLLXSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.OriginalSampleWriter;

import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader.AnnotationLevel;


public class DependencyParser {
    static final String USAGE = "DependencyParser [options] -i train -m model";
    static final String DEFAULT_OUTPUT_DIR = "output";
    /** command line options */
    Options opts_ ;

    /** model file */
    File model_;

    /** test data file */
    File test_ ;

    /** the number of treads */
    int threads_ ;

    /** the number of parsing steps */
    int steps_ ;

    /** the number of parsed sentences */
    int sentences_ ;

    /** the flag verbose mode */
    boolean verbose_ ;

    /** the dependency parser */
    jp.gr.java_conf.dyama.rink.parser.core.DependencyParser parser_;

    /** the sentence reader */
    SentenceReader reader_ ;

    /** the output writer */
    SampleWriter writer_;

    /** the width of beam search */
    int beamWidth_ ;

    /** the format of input data */
    Common.Format input_format_ ;

    /** the format of output data */
    Common.Format output_format_ ;

    /** the output directory for multithread */
    File output_dir_ ;

    DependencyParser(){

        model_ = null ;
        test_  = null ;
        threads_ = 1;
        steps_   = 0;
        sentences_ = 0;
        verbose_ = false;
        parser_ = null;
        reader_ = null;
        beamWidth_ = 1;
        input_format_ = Common.Format.CoNLLX;
        output_format_ = Common.Format.CoNLLX;
        output_dir_    = new File(DEFAULT_OUTPUT_DIR);

        opts_ = new Options();
        opts_.addOption("h", "help", false, "show usage.");
        opts_.addOption("i", "input", true, "path to the input file.");
        opts_.addOption("m", "model", true, "path to the model file.");
        opts_.addOption("v", "verbos",  false, "verbos mode (default false).");
        opts_.addOption("t", "threads",  true, "the number of threads (default 1)");
        opts_.addOption("b", "beamWidth",  true, "the width for beam search. (default 1). It's not available now.");
        opts_.addOption("I", "input-format",  true, "the format of input data: ORIGINAL/CoNLLX (default CoNLLX) ");
        opts_.addOption("O", "output-foramt",  true, "the format of output data: ORIGINAL/CoNLLX (default CoNLLX)");
        opts_.addOption("D", "output-directory",  true, "the output directory for multithread: (default " + DEFAULT_OUTPUT_DIR + ")");

    }

    SentenceReader createSentenceReader(File file) throws FileNotFoundException{
        if (input_format_ == Common.Format.CoNLLX)
            return new CoNLLXSentenceReader(file.getPath(), CoNLLXSentenceReader.Mode.TEST);
        return new AnnotatedSentenceReader(file.getPath(), AnnotationLevel.POS);
    }

    SampleWriter createSampleWriter(){
        if (output_format_ == Common.Format.CoNLLX)
            return new CoNLLXSampleWriter();
        return new OriginalSampleWriter();
    }

    void parse() throws IOException{

        if (threads_ < 2){
            Sample sample = parser_.createSample(reader_, beamWidth_);

            while(sample.read()){
                sentences_ ++;
                while(sample.parseOneStep()){
                    steps_ ++ ;
                }
                if (verbose_)
                    writer_.write(sample, System.out);
            }
            return ;
        }

        ParsingThread[] threads = new ParsingThread[threads_];
        if (! output_dir_.mkdir() ){
            if (! output_dir_.exists())
                throw new RuntimeException("fail to create the output dir: " + output_dir_.getPath());
        }

        for(int i = 0; i < threads.length; i++ ){
            threads[i] = new ParsingThread(this, output_dir_.getPath() + "/thread" + i + ".out.txt");
            threads[i].start();
        }

        for(int i = 0; i < threads.length; i++ ){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    boolean parseCommandLineArguments(String[] args) throws ParseException, FileNotFoundException, IOException, ClassNotFoundException{


        BasicParser parser = new BasicParser();
        CommandLine cl = parser.parse(opts_, args);

        if ( cl.hasOption('h') ) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp(USAGE, opts_);
            return false;
        }

        if (cl.hasOption("i")){
            test_ = new File(cl.getOptionValue("i"));

            if (! test_.exists())
                throw new ParseException(test_.getPath() + " was not found.");
            if (test_.isDirectory())
                throw new ParseException(test_.getPath() + " is a directory.");

        } else {
            throw new ParseException("option -i was not found.");
        }

        if (cl.hasOption("m")){
            model_ = new File(cl.getOptionValue("m"));
            if (model_.isDirectory())
                throw new ParseException(model_.getPath() + " is a directory.");
            if (! model_.exists())
                throw new ParseException(model_.getPath() + " was not found.");

        } else {
            throw new ParseException("option -m was not found.");
        }

        if (cl.hasOption("t")){
            try {
                threads_ = Integer.parseInt(cl.getOptionValue("t"));
            } catch (NumberFormatException e){
                throw new ParseException("NumberFormatException: " + e.getMessage() );
            }
        }

        if (cl.hasOption("b")){
            try {
                beamWidth_ = Integer.parseInt(cl.getOptionValue("b"));
            } catch (NumberFormatException e){
                throw new ParseException("NumberFormatException: " + e.getMessage() );
            }
            throw new ParseException("the beam seach is not available now.");
        }

        if (cl.hasOption("I")){
            String optValue = cl.getOptionValue("I");
            input_format_ = Common.Format.parseString(optValue);
            if (input_format_ == null)
                throw new ParseException("unknown format: " + optValue);
        }

        if (cl.hasOption("O")){
            String optValue = cl.getOptionValue("O");
            output_format_ = Common.Format.parseString(optValue);
            if (output_format_ == null)
                throw new ParseException("unknown format: " + optValue);
        }

        if (cl.hasOption("D")){
            output_dir_ = new File(cl.getOptionValue("D"));
        }
        if (cl.hasOption("v"))
            verbose_ = true ;

        assert(model_ != null);
        assert(test_ != null);
        parser_ = jp.gr.java_conf.dyama.rink.parser.core.DependencyParser.Builder.build(model_.getPath());
        reader_ = createSentenceReader(test_);
        writer_ = createSampleWriter();
        return true;
    }

    static class ParsingThread extends Thread {


        Sample sample_ ;
        boolean verbose_ ;
        SentenceReader reader_ ;
        SampleWriter writer_ ;
        PrintStream out_ ;
        jp.gr.java_conf.dyama.rink.parser.core.DependencyParser parser_ ;
        DependencyParser main_ ;

        ParsingThread(DependencyParser parser, String outPath) throws FileNotFoundException{
            main_ = parser;
            parser_ = parser.parser_ ;
            reader_ = parser.createSentenceReader(parser.test_);
            writer_ = parser.createSampleWriter();
            sample_ = parser_.createSample(reader_, parser.beamWidth_);
            verbose_ = parser.verbose_;
            out_ = new PrintStream(new FileOutputStream(outPath));
        }

        @Override
        public void run(){
            try {
                while(sample_.read()){
                    main_.sentences_ += 1 ;
                    while(sample_.parseOneStep());
                    if (verbose_)
                        writer_.write(sample_, out_);
                }
                reader_.close();
                out_.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param args command line options
     */
    public static void main(String[] args) {
        PerformanceMeasuring pm = new PerformanceMeasuring();
        DependencyParser parser = new DependencyParser();

        try {
            {
                PerformanceMeasuring loading = new PerformanceMeasuring();
                if (! parser.parseCommandLineArguments(args)){
                    HelpFormatter f = new HelpFormatter();
                    f.printHelp(USAGE, parser.opts_);
                    System.exit(0);
                }
                double time = loading.getTime();
                System.err.printf("loading time[s]       :\t%.2f", time / 1000 );
                System.err.println();
            }

            {
                PerformanceMeasuring parsing = new PerformanceMeasuring();
                parser.parse();
                double time = parsing.getTime();
                System.err.printf("parsing time[s]       :\t%.2f", time / 1000 );
                System.err.println();
                double speed = parser.sentences_;
                System.err.printf("parsing speed[sent/s] :\t%.1f",  speed / (time / 1000) );
                System.err.println();
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter f = new HelpFormatter();
            f.printHelp(USAGE, parser.opts_);
            System.exit(1);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (parser.reader_ != null)
                try {
                    parser.reader_.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
        }

        pm.show("Total ", System.err);
        System.exit(0);

    }

}
