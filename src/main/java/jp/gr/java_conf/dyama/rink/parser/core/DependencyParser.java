package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.Parser;
import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.SentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.OracleActionEstimator.SetOfActions;


public abstract class DependencyParser implements Parser {


    /**
     * Parser Builder
     * @author Hiroyasu Yamada
     *
     */
    public static class Builder{

        /**
         * Build Deterministic BottomUp Dependency Parser from model file.
         * @param path path to the model file. throw IllegalArgumentException if path is null.
         * @return dependency parser.
         * @throws FileNotFoundException
         * @throws IOException
         * @throws ClassNotFoundException
         */
        static public DependencyParser build(String path) throws FileNotFoundException, IOException, ClassNotFoundException{
            DeterministicBottomUpParser parser = DeterministicBottomUpParser.load(path);
            parser.toImmutable();
            return parser;
        }

        /**
         * Build a dependency learner by using SVMs.
         * @param params parameters for SVMs. throw IllegalArgumentException if params is null.
         * @return dependency learner.
         */
        static public DependencyParser buildSVMDependencyLearner(jp.gr.java_conf.dyama.rink.ml.svm.Parameters params){
            if (params == null)
                throw new IllegalArgumentException("the parameters is null.");
            FeatureFunction function = new IWPT2003BestFeatureFunction(2, 4);
            ActionEstimator estimator = new OracleActionEstimator(function);
            ActionLearner learner = new SVMActionLearner(params, function);
            return new DeterministicBottomUpParser(estimator,  learner);
        }

        /**
         * Build a faster dependency learner by using grouping training examples with POS tags.
         * @param params parameters for SVMs. throw IllegalArgumentException if params is null.
         * @return dependency learner.
         */
        static public DependencyParser buildPOSGroupingSVMDependencyLearner(jp.gr.java_conf.dyama.rink.ml.svm.Parameters params){
            if (params == null)
                throw new IllegalArgumentException("the parameters is null.");
            FeatureFunction function = new IWPT2003BestFeatureFunction(2, 4);
            ActionEstimator estimator = new OracleActionEstimator(function);
            ActionLearner learner = new SVMActionLearner(params, function, new GroupIdentifier.POSGroupIdentifier());
            return new DeterministicBottomUpParser(estimator,  learner);
        }

        /**
         * Build a  dependency learner by using MIRA
         * @param num_iterations the number of iterations. throw IllegalArgumentException if num_iterations is less than 1.
         * @return dependency learner.
         */
        static public void buildMIRADependencyLearner(List<DependencyParser> parsers){
            FeatureFunction function = new IWPT2003BestFeatureFunction(2, 4);
            // ActionEstimator estimator = new OracleActionEstimator(function, SetOfActions.FourAcctions);
            MIRAActionLearner learner = new MIRAActionLearner(function, new GroupIdentifier.ExtPOSGroupIdentifier());
            DependencyParser parser0 = new DeterministicBottomUpParser(learner.convert(),  learner);
            DependencyParser parser1 = new DeterministicBottomUpParser(parser0.getIDConverter(), learner.convert());
            parsers.add(parser0);
            parsers.add(parser1);
        }
    }

    private IDConverter idconverter_ ;
    DependencyParser(){
        idconverter_ = new IDConverterImpl.MutableIDConverter();
    }

    DependencyParser(IDConverter converter){
        idconverter_ = converter ;
    }

    /**
     * create a new sample with the sentence reader. the sample is able to parse dependency relations.
     * @param reader the sentence reader.
     * @return Sample
     * @throws IllegalArgumentException if the reader is null.
     */
    public abstract Sample createSample(SentenceReader reader);

    /**
     * create a new sample with the sentence reader. the sample is able to parse dependency relations with beam search.
     * @param reader the sentence reader.
     * @param beamWidth the width of beam search. the beam search is enable only if the beamWidth is more than 1.
     * @return Sample
     * @throws IllegalArgumentException if the reader is null.
     */
    public abstract Sample createSample(SentenceReader reader, int beamWidth);

    /**
     * get ID Converter.
     * @return ID Converter
     */
    final IDConverter getIDConverter(){
        return idconverter_ ;
    }

    final void toImmutable(){
        if (idconverter_ instanceof IDConverterImpl.MutableIDConverter)
            idconverter_ = ((IDConverterImpl.MutableIDConverter) idconverter_).toImmutable();
    }


    /**
     * parse dependency structures of a sample
     * @param sample sample.
     * @return true if the parser can parse one step. otherwise false.
     */
    abstract boolean parse(SampleImpl sample);


    /**
     * save the parser to a file.
     * @param path file path
     * @throws IOException
     */
    public abstract void save(String path) throws IOException ;

}
