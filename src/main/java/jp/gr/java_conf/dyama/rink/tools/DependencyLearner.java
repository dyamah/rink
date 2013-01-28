package jp.gr.java_conf.dyama.rink.tools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.SampleWriter;
import jp.gr.java_conf.dyama.rink.parser.SentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.CoNLLXSampleWriter;
import jp.gr.java_conf.dyama.rink.parser.core.CoNLLXSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.OriginalSampleWriter;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImpl;
import jp.gr.java_conf.dyama.rink.parser.core.AnnotatedSentenceReader.AnnotationLevel;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class DependencyLearner {


    static enum LearningAlgorithm {
        SVM,
        MIRA,
        IWPT2003
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

    jp.gr.java_conf.dyama.rink.parser.core.DependencyParser eval_parser_ ;

    /** sentence reader */
    SentenceReader reader_ ;

    /** CLI Options */
    Options opts_ ;

    /** the number of parsing steps */
    int steps_ ;

    /** the number of training sentences */
    int num_sentences_;

    /** the flag for grouping training examples */
    boolean grouping_ ;

    /** training iterations for MIRA */
    int iterations_ ;

    /** the type of learning algorithms */
    LearningAlgorithm learning_algorithm_ ;

    /** the data format */
    Common.Format format_ ;

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

        opts_.addOption("l", "learner", true, "the type of learners: SVM/MIRA/IWPT2003 (default SVM).");
        opts_.addOption("t", "kernel",  true, "the type of kernel functions: [LINEAR, POLYNOMIAL, RBF, SIGMOID] (default POLYNOMIAL).");
        opts_.addOption("d", "degree",  true, "the degree of polynomial kernel (default 2).");
        opts_.addOption("s", "gamma",   true, "the kernel parameter for Polynomial, RBF, Sigmoid (default 1.0).");
        opts_.addOption("r", "coef0",   true, "the kernel parameter for Polynmoinal, Sigmoid (default 1.0).");
        opts_.addOption("c", "C",       true, "Soft magin parameter (default 1.0).");
        opts_.addOption("m", "cache",   true, "the memory size of kernel cache [MB] (default 30.0).");
        opts_.addOption("e", "epsilon", true, "the threshold for convergence (default 0.001).");

        opts_.addOption("g", "grouping",  false, "grouping mode faster training.  (default false for SVM, true for MIRA).");
        opts_.addOption("f", "format",     true, "the verbose output format: ORIGINAL/CoNLLX (default CoNLLX).");
        opts_.addOption("I", "iterations", true, "the number of iteration when the MIRA learner has been elected by option -l. (default 100)");

        opts_.addOption("v", "verbose",  false, "verbose mode (default false).");

    }

    SentenceReader createSentenceReader(File file) throws FileNotFoundException {
        if (format_ == Common.Format.CoNLLX)
            return new CoNLLXSentenceReader(file.getPath(), CoNLLXSentenceReader.Mode.TRAIN);

        return new AnnotatedSentenceReader(file.getPath(), AnnotationLevel.DEPENDENCY);
    }

    SampleWriter createSampleWriter(){
        if (format_ == Common.Format.CoNLLX)
            return new CoNLLXSampleWriter();
        return new OriginalSampleWriter();
    }

    void useSVMs() throws IOException {
        System.err.println("Parsing the input training data ...");

        steps_ = 0;
        num_sentences_ = 0;

        SentenceReader reader = createSentenceReader(train_);
        SampleWriter writer   = createSampleWriter();

        Sample sample = parser_.createSample(reader);

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
            if (num_w != num_c)
                throw new IllegalStateException("can not parse completely.");

            if (verbose_)
                writer.write(sample, System.out);

        }
        reader.close();
        System.err.println("");
        System.err.println("Total "  + num_sentences_ + " sentences, " + steps_ + " steps.");
        System.err.println("");
    }


    private double evaluateTrainingData() throws IOException{
        int n = 0;
        double c = 0;
        SentenceReader reader = createSentenceReader(train_);
        SampleImpl sample = (SampleImpl) eval_parser_.createSample(reader);


        while(sample.read()){
            while(sample.parseOneStep());
            n += sample.getSentence().size();
            c += sample.getNumberOfCorrectDependencies();
        }
        reader.close();
        return c / n;
    }

    void useMIRA() throws IOException {
        System.err.println("Parsing the input training data ...");

        steps_ = 0;
        num_sentences_ = 0;


        SampleWriter writer   = createSampleWriter();


        for(int i = 0 ; i < iterations_ ; i++){
            System.err.printf("#iterations: " + i);
            SentenceReader reader = createSentenceReader(train_);
            Sample sample = parser_.createSample(reader);
            while(sample.read()){
                num_sentences_ ++ ;
                int num_w = sample.getSentence().size();
                int num_c = 0 ;

                while(sample.parseOneStep())
                    steps_++;

                if (num_sentences_ % 2000 == 0){
                    System.err.print(".");
                }
                num_c = ((SampleImpl)sample).getNumberOfCorrectDependencies();
                if (num_w != num_c)
                    throw new IllegalStateException("can not parse completely.");

                if (verbose_)
                    writer.write(sample, System.out);
            }
            if (i % 10 == 0)
                System.err.printf(" Accuracy: %.3f", evaluateTrainingData());

            System.err.println();
            reader.close();
        }
        System.err.println("");
        System.err.println("Total "  + num_sentences_ + " sentences, " + steps_ + " steps.");
        System.err.println("");
    }

    void parse() throws IOException{
        if (learning_algorithm_ == LearningAlgorithm.SVM || learning_algorithm_ == LearningAlgorithm.IWPT2003){
            useSVMs();
        }

        if (learning_algorithm_ == LearningAlgorithm.MIRA)
            useMIRA();

    }

    void save() throws IOException{
        System.err.println("learning dependency relations...");
        parser_.save(model_.getPath());
        System.err.println("done.");
    }

    /**
     * parse command line arguments
     * @param args the command line arguments
     * @return true if  CLI arguments can be parsed. return false if the option -h ( --help) has been found.
     * @throws ParseException if any invalid arguments have been found.
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

        learning_algorithm_ = LearningAlgorithm.SVM;
        if (cl.hasOption("l")){

            String type = cl.getOptionValue("l");

            if (type.equals("SVM")){
                learning_algorithm_ = LearningAlgorithm.SVM;
            } else if (type.equals("MIRA")){
                learning_algorithm_ = LearningAlgorithm.MIRA;
            } else if (type.equals("IWPT2003")){
                learning_algorithm_ = LearningAlgorithm.IWPT2003;
            } else {
                throw new ParseException("undefined learner type: " + type);
            }
        }

        if (cl.hasOption("f")){
            String format = cl.getOptionValue("f");
            format_ = Common.Format.CoNLLX;
            if (format.equals(Common.Format.ORIGINAL.toString()))
                format_ = Common.Format.ORIGINAL;
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

        if (learning_algorithm_ == LearningAlgorithm.SVM){
            if (grouping_){
                parser_ = jp.gr.java_conf.dyama.rink.parser.core.DependencyParser.Builder.buildPOSGroupingSVMDependencyLearner(params_);
            } else {
                parser_ = jp.gr.java_conf.dyama.rink.parser.core.DependencyParser.Builder.buildSVMDependencyLearner(params_);
            }
        }

        if (learning_algorithm_ == LearningAlgorithm.IWPT2003){
            params_.setKernelType(Parameters.KernelType.POLYNOMIAL);
            params_.setDegree(2);
            params_.setGamma(1.0);
            params_.setCoef0(1.0);
            parser_ = jp.gr.java_conf.dyama.rink.parser.core.DependencyParser.Builder.buildIWPT2003Learner(params_);
        }

        if (learning_algorithm_ == LearningAlgorithm.MIRA){
            iterations_ = 1;
            if (cl.hasOption("I"))
                iterations_ = Integer.parseInt(cl.getOptionValue("I"));

            List<jp.gr.java_conf.dyama.rink.parser.core.DependencyParser> parsers = new ArrayList<jp.gr.java_conf.dyama.rink.parser.core.DependencyParser>();
            jp.gr.java_conf.dyama.rink.parser.core.DependencyParser.Builder.buildMIRADependencyLearner(parsers);
            parser_ = parsers.get(0);
            eval_parser_ = parsers.get(1);
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
