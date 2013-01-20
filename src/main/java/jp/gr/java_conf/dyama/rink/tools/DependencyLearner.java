package jp.gr.java_conf.dyama.rink.tools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImpl;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader.AnnotationLevel;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class DependencyLearner {
    static enum LearnerType {
        SVM,
        MIRA,
    }
    /** Exit Status : OK */
    static final int OK      = 0 ;

    /** Exit Status : ERROR */
    static final int ERROR   = 1 ;

    static final String USAGE = "DependencyLeraner [options] -i train  -o model";


    /** parameters for SVMs */
    Parameters params_ ;

    /** training data file */
    File train_ ;

    /** model file */
    File model_ ;

    /** dictionary file */
    File dic_ ;

    /** flag for verbose mode */
    boolean verbose_ ;

    /** dependency parser */
    jp.gr.java_conf.dyama.rink.parser.core.DependencyParser parser_ ;
    jp.gr.java_conf.dyama.rink.parser.core.DependencyParser subparser_ ;
    jp.gr.java_conf.dyama.rink.parser.core.DependencyParser evalparser_ ;



    /** sentence reader */
    AnnotatedSentenceReader reader_ ;

    /** CLI Options */
    Options opts_ ;

    /** the number of parsing steps */
    int steps_ ;

    /** the number of training sentences */
    int num_sentences_;


    boolean grouping_ ;

    int iterations_ ;

    LearnerType learner_type_ ;

    DependencyLearner() {
        params_ = new Parameters.ParametersImpl();
        params_.setKernelType(Parameters.KernelType.POLYNOMIAL);
        params_.setDegree(2);
        params_.setGamma(1.0);
        params_.setCoef0(1.0);
        params_.setC(1.0);
        params_.setCacheSize(30.0);
        params_.setEpsilon(0.001);
        train_ = null;
        model_ = null;
        dic_   = null;
        verbose_ = false;

        steps_ = 0 ;
        num_sentences_ = 0;

        grouping_ = false;
        iterations_ = 1;

        opts_ = new Options();
        opts_.addOption("h", "help", false, "show usage.");
        opts_.addOption("i", "input", true, "path to the input file.");
        opts_.addOption("o", "output", true, "path to the output file.");

        opts_.addOption("l", "learner", true, "the type of learners: SVM, MIRA (default SVM).");
        opts_.addOption("t", "kernel",  true, "the type of kernel functions: [LINEAR, POLYNOMIAL, RBF, SIGMOID] (default POLYNOMIAL).");
        opts_.addOption("d", "degree",  true, "the degree of polynomial kernel (default 2).");
        opts_.addOption("s", "gamma",   true, "the kernel parameter for Polynomial, RBF, Sigmoid (default 1.0).");
        opts_.addOption("r", "coef0",   true, "the kernel parameter for Polynmoinal, Sigmoid (default 1.0).");
        opts_.addOption("c", "C",       true, "Soft magin parameter (default 1.0).");
        opts_.addOption("m", "cache",   true, "the memory size of kernel cache [MB] (default 30.0).");
        opts_.addOption("e", "epsilon", true, "the threshold for convergence (default 0.001).");

        opts_.addOption("g", "grouping",  false, "grouping mode faster training.  (default false for SVM, true for MIRA).");
        opts_.addOption("I", "iterations",  true, "the number of iteration when the MIRA learner has been elected by option -l. (default 100)");

        opts_.addOption("v", "verbos",  false, "verbos mode (default false).");

    }

    void parse() throws IOException{
        System.err.println("Parsing the input training data ...");
        PerformanceMeasuring pm = new PerformanceMeasuring();

        for(int n = 0 ; n < iterations_; n++){
            int correct = 0 ;
            int words   = 0 ;
            steps_ = 0;
            num_sentences_ = 0;
            System.err.print("[" + n + "] ");
            reader_ = new AnnotatedSentenceReader(train_.getPath(), AnnotationLevel.DEPENDENCY);
            int beamWidth = 1;
            if (learner_type_ == LearnerType.MIRA)
                beamWidth = 5;
            Sample sample = parser_.createSample(reader_, beamWidth);


            while(sample.read()){
                num_sentences_ ++ ;
                int num_w = sample.getSentence().size();
                int num_c = 0 ;

                while(sample.parseOneStep()){
                    steps_++;
                }

                if (num_sentences_ % 2000 == 0){
                    System.err.print(".");
                }
                num_c = ((SampleImpl)sample).getNumberOfCorrectDependencies();
                //if (num_w != num_c)
                //    throw new IllegalStateException("can not parse completely.");

                if (verbose_){
                    sample.show(System.out);
                }
            }
            reader_.close();
            if (n % 5 == 0 && evalparser_ != null){

                reader_ = new AnnotatedSentenceReader(train_.getPath(), AnnotationLevel.DEPENDENCY);
                sample = evalparser_.createSample(reader_, beamWidth);

                while(sample.read()){
                    while(sample.parseOneStep()){
                    }
                    correct += ((SampleImpl)sample).getNumberOfCorrectDependencies();
                    words   += sample.getSentence().size();
                }
                reader_.close();
                double mem = pm.getPeakHeapMemorySize();
                System.err.printf(" Accuraccy: %.3f\tPeak Memory: %.1f [MB]", (((double)correct) / words), mem / 1024 / 1024 );
                System.err.println();
            } else {
                System.err.println();
            }
        }

        System.err.println("");
        System.err.println("Total "  + num_sentences_ + " sentences, " + steps_ + " steps.");
        System.err.println("");
    }

    void save() throws IOException{
        System.err.println("learning dependency relations...");
        parser_.save(model_.getPath());
        System.err.println("done.");
    }

    /**
     * parse command line arguments
     * @param args command line arguments
     * @return true if  CLI arguments can be parsed. return false if the option -h ( --help) is found.
     * @throws ParseException if a invalid argument is found.
     */
    boolean parseCommandLineArguments(String[] args) throws ParseException {

        BasicParser parser = new BasicParser();
        CommandLine cl = parser.parse(opts_, args);

        if ( cl.hasOption('h') )
            return false;

        if (cl.hasOption("i")){
            train_ = new File(cl.getOptionValue("i"));

            if (train_.isDirectory())
                throw new ParseException(train_.getPath() + " is a directory.");

        } else {
            throw new ParseException("not found the option -i.");
        }

        if (cl.hasOption("o")){
            model_ = new File(cl.getOptionValue("o"));

            if (model_.isDirectory())
                throw new ParseException(model_.getPath() + " is a directory.");


            File parent = model_.getParentFile();

            if (parent != null && ! parent.exists())
                throw new ParseException("can not write to " + model_.getPath());

            if (model_.exists() && ! model_.canWrite())
                throw new ParseException("can not write to " + model_.getPath());


        } else {
            throw new ParseException("not found the option -o.");
        }

        learner_type_ = LearnerType.SVM;
        if (cl.hasOption("l")){

            String type = cl.getOptionValue("l");

            if (type.equals("SVM")){
                learner_type_ = LearnerType.SVM;
            } else if (type.equals("MIRA")){
                learner_type_ = LearnerType.MIRA;
                iterations_ = 100;
                if (cl.hasOption("I")){
                    iterations_ = Integer.parseInt(cl.getOptionValue("I"));
                }
            } else {
                throw new ParseException("undefined learner type: " + type);

            }

        }
        if (cl.hasOption("t")){
            Parameters.KernelType type = null;
            String v = cl.getOptionValue("t");
            for(Parameters.KernelType t : Parameters.KernelType.values()){
                if (t.toString().equals(v))
                    type = t ;
            }
            if (type == null)
                throw new ParseException("undefined kernel type: " + v);
            params_.setKernelType(type);
        }

        try {
            if (cl.hasOption("d"))
                params_.setDegree(Integer.parseInt(cl.getOptionValue("d")));


            if (cl.hasOption("s"))
                params_.setGamma(Double.parseDouble(cl.getOptionValue("s")));


            if (cl.hasOption("r"))
                params_.setCoef0(Double.parseDouble(cl.getOptionValue("r")));


            if (cl.hasOption("c"))
                params_.setC(Double.parseDouble(cl.getOptionValue("c")));


            if (cl.hasOption("m"))
                params_.setCacheSize(Double.parseDouble(cl.getOptionValue("m")));


            if (cl.hasOption("e"))
                params_.setEpsilon(Double.parseDouble(cl.getOptionValue("e")));

        } catch (NumberFormatException e){
            throw new ParseException("NumberFormatException: " + e.getMessage());
        }


        if (cl.hasOption("v"))
            verbose_ = true ;

        grouping_ = false ;

        if (cl.hasOption("g")){
            grouping_ = true;
        }

        assert(train_ != null);
        assert(model_ != null);

        if (learner_type_ == LearnerType.SVM){
            if (grouping_){
                parser_ = jp.gr.java_conf.dyama.rink.parser.core.DependencyParser.Builder.buildPOSGroupingSVMDependencyLearner(params_);
            } else {
                parser_ = jp.gr.java_conf.dyama.rink.parser.core.DependencyParser.Builder.buildSVMDependencyLearner(params_);
            }
        }
        if (learner_type_ == LearnerType.MIRA){
            List<jp.gr.java_conf.dyama.rink.parser.core.DependencyParser> parsers = new ArrayList<jp.gr.java_conf.dyama.rink.parser.core.DependencyParser>();
            jp.gr.java_conf.dyama.rink.parser.core.DependencyParser.Builder.buildMIRADependencyLearner(parsers);
            parser_ = parsers.get(0);
            // subparser_ = parsers.get(1);
            evalparser_ = parsers.get(1);
        }

        try {
            reader_ = new AnnotatedSentenceReader(train_.getPath(), AnnotationLevel.DEPENDENCY);
            reader_.close();
        } catch (FileNotFoundException e) {
            throw new ParseException(e.getMessage());
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        }
        return true;

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        PerformanceMeasuring pm = new PerformanceMeasuring();
        DependencyLearner learner =  new DependencyLearner();

        try {
            if (! learner.parseCommandLineArguments(args)){
                HelpFormatter f = new HelpFormatter();
                f.printHelp(USAGE, learner.opts_);
                System.exit(OK);
            }
            learner.parse();
            learner.save();
        } catch (ParseException e){
            System.out.println(e.getMessage());
            HelpFormatter f = new HelpFormatter();
            f.printHelp(USAGE, learner.opts_);
            System.exit(1);
        } catch (Exception e){
            e.printStackTrace();
            System.err.println(" parsing at " + learner.num_sentences_ + "-th sentence. (" + learner.steps_ + " setps).");
            System.exit(1);
        } finally {
            if (learner.reader_ != null)
                try {
                    learner.reader_.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(ERROR);
                }
        }
        pm.show(System.err);
        System.exit(OK);
    }
}
