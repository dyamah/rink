package jp.gr.java_conf.dyama.rink.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImpl;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader.AnnotationLevel;


public class DependencyParser {
    static final String USAGE = "DependencyParser [options] -i train -m model";

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

    /** flag verbose mode */
    boolean verbose_ ;

    /** dependency parser */
    jp.gr.java_conf.dyama.rink.parser.core.DependencyParser parser_;

    /** sentence reader */
    AnnotatedSentenceReader reader_ ;

    int beamWidth_ ;

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


        opts_ = new Options();
        opts_.addOption("h", "help", false, "show usage.");
        opts_.addOption("i", "input", true, "path to the input file.");
        opts_.addOption("m", "model", true, "path to the model file.");
        opts_.addOption("v", "verbos",  false, "verbos mode (default false).");
        opts_.addOption("t", "threads",  true, "the number of threads (default 1)");
        opts_.addOption("b", "beamWidth",  true, "the width for beam searching. (default 1)");

    }

    void parse() throws IOException{

        if (threads_ < 2){
            Sample sample = parser_.createSample(reader_, beamWidth_);

            while(sample.read()){
                sentences_ ++;
                int n = sample.getSentence().size();

                for(int t = 0; t < 2*n -1 ; t ++){
                    if (! sample.parseOneStep())
                        break ;
                }

//                while(sample.parseOneStep()){
//                    steps_ ++ ;
//                }
                if (verbose_)
                    sample.show(System.out);
            }
            return ;
        }

        ParsingThread[] threads = new ParsingThread[threads_];
        for(int i = 0; i < threads.length; i++ ){
            threads[i] = new ParsingThread(this);
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
        ParsingThread.showResult(verbose_);
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
        }

        if (cl.hasOption("v"))
            verbose_ = true ;

        assert(model_ != null);
        assert(test_ != null);
        parser_ = jp.gr.java_conf.dyama.rink.parser.core.DependencyParser.Builder.build(model_.getPath());
        reader_ = new AnnotatedSentenceReader(test_.getPath(), AnnotationLevel.POS);
        return true;
    }

    static class ParsingThread extends Thread {

        static class Output {
            StringBuilder sb_ ;
            Output(){
                sb_ = null;
            }
        }

        static List<Output> outout_queue = new LinkedList<Output>();

        Sample sample_ ;
        boolean verbose_ ;
        DependencyParser main_ ;

        ParsingThread(DependencyParser parser){
            main_ = parser;
            sample_ = main_.parser_.createSample(main_.reader_, main_.beamWidth_);
            verbose_ = main_.verbose_;
        }

        static void showResult(boolean verbose){
            synchronized(outout_queue){
                while (outout_queue.size() > 0){
                    Output x = outout_queue.get(0);
                    if (x.sb_ == null)
                        break ;
                    if (verbose)
                        System.out.print(x.sb_.toString());
                    outout_queue.remove(0);
                }
            }
        }

        @Override
        public void run(){

            while(true){
                boolean eof = false;
                Output output = null;
                synchronized (outout_queue){
                    try {
                        eof = (! sample_.read()) ;
                    } catch (IOException e) {
                        throw new IllegalStateException(e.getMessage());
                    }
                    if (eof == false ){
                        main_.sentences_ ++ ;
                        output = new Output();
                        outout_queue.add(output);
                    }
                                    }
                if (eof)
                    break ;
                int steps = 0;
                while(sample_.parseOneStep()){
                    steps ++ ;
                }
                main_.steps_ += steps ;
                StringBuilder sb = new StringBuilder();
                ((SampleImpl)sample_).show(sb);
                output.sb_ = sb;
                showResult(verbose_);
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
