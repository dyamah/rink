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


public abstract class DependencyParser implements Parser {


    /**
     * Parsers Builder
     * @author Hiroyasu Yamada
     *
     */
    public enum Builder{
        INSTANCE;
        /**
         * Builds the deterministic bottom-up dependency parser from the trained model file.
         * @param path the path to the model file.
         * @return the dependency parser builded from the model file. .
         * @throws FileNotFoundException
         * @throws IOException
         * @throws ClassNotFoundException
         * @throws IllegalArgumentException if the path is null.
         */
        static public DependencyParser build(String path) throws FileNotFoundException, IOException, ClassNotFoundException{
            DeterministicBottomUpParser parser = DeterministicBottomUpParser.load(path);
            parser.toImmutable();
            return parser;
        }

        /**
         * Builds the SVM dependency learner.
         * @param params the parameters for SVMs.
         * @return dependency learner.
         * @throws IllegalArgumentException if the set of parameters is null.
         */
        static public DependencyParser buildSVMDependencyLearner(jp.gr.java_conf.dyama.rink.ml.svm.Parameters params){
            if (params == null)
                throw new IllegalArgumentException("the set of parameters is null.");
            FeatureFunction function = new IWPT2003BestFeatureFunction(2, 4);
            ActionEstimator estimator = new OracleActionEstimator(function);
            ActionLearner learner = new SVMActionLearner(params, function);
            return new DeterministicBottomUpParser(estimator,  learner);
        }

        /**
         * Builds SVM dependency learner (training examples are grouping some sets by using the POS tags of the left target node.)
         * @param params the parameters for SVMs.
         * @return dependency learner.
         * @throws IllegalArgumentException if the set of parameters is null.
         */
        static public DependencyParser buildPOSGroupingSVMDependencyLearner(jp.gr.java_conf.dyama.rink.ml.svm.Parameters params){
            if (params == null)
                throw new IllegalArgumentException("the set of parameters is null.");
            FeatureFunction function = new IWPT2003BestFeatureFunction(2, 4);
            ActionEstimator estimator = new OracleActionEstimator(function);
            ActionLearner learner = new SVMActionLearner(params, function, new GroupIdentifier.POSGroupIdentifier());
            return new DeterministicBottomUpParser(estimator,  learner);
        }

        /**
         * Builds IWPT2003 best model (SVM dependency learner (training examples are grouping some sets by using the POS tags of the left target node.))
         * @param params the parameters for SVMs.
         * @return dependency learner.
         * @throws IllegalArgumentException if the set of parameters is null.
         */
        static public DependencyParser buildIWPT2003Learner(jp.gr.java_conf.dyama.rink.ml.svm.Parameters params){
            if (params == null)
                throw new IllegalArgumentException("the set of parameters is null.");
            FeatureFunction function = new IWPT2003BestFeatureFunction(2, 4);
            ActionEstimator estimator = new OracleActionEstimator(function, OracleActionEstimator.SetOfActions.ThreeActions);
            ActionLearner learner = new SVMActionLearner(params, function, new GroupIdentifier.POSGroupIdentifier());
            return new DeterministicBottomUpParser(estimator,  learner);
        }

        /**
         * Builds the MIRA dependency learner.
         * @param parsers the list of parsers.
         * @throws IllegalArgumentException if the list of parsers is null.
         */
        static public void buildMIRADependencyLearner(List<DependencyParser> parsers){
            if (parsers == null)
                throw new IllegalArgumentException("the list of parsers is null.");
            FeatureFunction function  = new IWPT2003BestFeatureFunction(2, 4);
            MIRAActionLearner learner = new MIRAActionLearner(function, new GroupIdentifier.ExtPOSGroupIdentifier());
            ActionEstimator estimator = new OracleActionEstimator(function);
            DependencyParser parser0  = new DeterministicBottomUpParser(estimator,  learner);
            DependencyParser parser1  = new DeterministicBottomUpParser(parser0.getIDConverter(), learner.convert());
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
     * Creates a new sample attaching  the sentence reader and this parser. The new sample is able to parse dependency relations.
     * @param reader the sentence reader.
     * @return a new sample
     * @throws IllegalArgumentException if the sentence reader is null.
     */
    public abstract Sample createSample(SentenceReader reader);

    /**
     * Create a new sample attaching the sentence reader. The new sample is able to parse dependency relations.
     * @param reader the sentence reader.
     * @param beamWidth the width of beam search. the beam search is enable only if the beamWidth is more than 1.
     * @return a new sample
     * @throws IllegalArgumentException if the sentence reader is null.
     */
    public abstract Sample createSample(SentenceReader reader, int beamWidth);

    /**
     * Returns the ID Converter.
     * @return the ID Converter
     */
    final IDConverter getIDConverter(){
        return idconverter_ ;
    }

    /**
     * Change the mutable ID Converter to the immutable one.
     */
    final void toImmutable(){
        if (idconverter_ instanceof IDConverterImpl.MutableIDConverter)
            idconverter_ = ((IDConverterImpl.MutableIDConverter) idconverter_).toImmutable();
    }


    /**
     * Parses dependency relations of the input sample.
     * @param sample the input sample.
     * @return true if the parser can parse one step. otherwise false.
     */
    abstract boolean parse(SampleImpl sample);


    /**
     * Saves the parser to the model file.
     * @param path the path to the model file.
     * @throws IOException
     */
    public abstract void save(String path) throws IOException ;

}
