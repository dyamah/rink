package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.Sentence;
import jp.gr.java_conf.dyama.rink.parser.SentenceReader;
/**
 * Sample for Dependency Parsing.
 * @author Hiroyasu Yamada
 *
 */
public class SampleImpl implements Sample {


    /**
     * Agenda class for beam search.
     * @author Hiroyasu Yamada
     *
     */
    static class Agenda {

        /** the number of keeping states */
        private int size_;

        /** the state list within beam width */
        private State[] list_ ;

        /** the temporal list of states  */
        private List<State> candidates_ ;

        private List<ActionImpl> actions_ ;

        private State.StatePool state_pool_;

        /**
         * Constructor
         * @param beamWidth the beam width. It must be more than 1.
         */
        private Agenda(int beamWidth){
            size_ = 0;
            list_ = new State[beamWidth];
            candidates_ = new ArrayList<State>();
            actions_ = new ArrayList<ActionImpl>();
            int m = beamWidth * Action.Type.values().length;
            state_pool_ = new State.StatePool(m + 1);
        }

        /**
         * Returns the state pool.
         * @return the state pool
         */
        State.StatePool getStatePool(){
            return state_pool_ ;
        }

        /**
         * Initializes this agenda and setup the new state.
         * @param state the new state.
         */
        private void setup(State state){
            clear();
            list_[0] = state ;
            size_ ++ ;
        }

        /**
         * Clears this agenda.
         */
        private void clear(){
            size_ = 0;
            candidates_.clear();
            actions_.clear();
        }

        /**
         * Adds the candidate.
         * @param state the state
         * @param action the parsing action that will be apply to the state.
         */
        void addCandidate(State state, Action action){
            if (action == null){
                candidates_.add(state);
                return;
            }
            State newState = state_pool_.create();
            newState.copy(state);
            if (newState.apply(action)){
                candidates_.add(newState);
                return ;
            }
            state_pool_.release(newState);
        }

        /**
         * Returns the temporally list of actions.
         * @return the list of actions.
         */
        List<ActionImpl> getActionList(){
            return actions_ ;
        }
    }


    private WordImpl.Generator word_generator_ ;
    private SentenceReader reader_ ;
    private SentenceImpl sentence_;
    private DependencyRelations y_;
    private State state_;

    private BinaryFeatureVector.Buffer fbuffer_;
    private DependencyParser parser_;
    private FeatureImpl feature_;
    private BinaryFeatureVector fv_ ;

    private Agenda agenda_ ;
    /**
     * Constructor
     * @param reader the sentence reader.
     * @param converter the IDConverter.
     * @throws IllegalArgumentException if the sentence reader is null.
     * @throws IllegalArgumentException if the IDConverter is null.
     */
    SampleImpl(SentenceReader reader, IDConverter converter){
        if (reader == null)
            throw new IllegalArgumentException("the sentence reader is null.");
        if (converter == null)
            throw new IllegalArgumentException("the ID converter is null.");

        word_generator_ = new WordImpl.Generator(converter);
        reader_   = reader ;
        sentence_ = new SentenceImpl(word_generator_, null);
        y_ = new DependencyRelations();
        state_ = new State();
        fbuffer_ = new BinaryFeatureVector.Buffer();
        parser_ = null;
        feature_ = new FeatureImpl();
        fv_ = new BinaryFeatureVector();
        agenda_ = null;
    }

    /**
     * Sets the parser.
     * @param parser the parser.
     * @throws IllegalArgumentException if the parser is null.
     */
    void setParser(DependencyParser parser){
        if (parser == null)
            throw new IllegalArgumentException("the parser is null.");
        parser_ = parser ;
    }

    @Override
    public boolean read() throws IOException{
        if (! reader_.read(sentence_)){
            sentence_.clear();
            y_.setup(sentence_);
            state_.setup(sentence_);
            if (agenda_ != null)
                agenda_.setup(state_);
            return false;
        }

        state_.setup(sentence_);
        y_.build(sentence_);
        if (agenda_ != null)
            agenda_.setup(state_);

        return true;
    }

    /**
     * Returns the annotated dependency relations.
     * @return the annotated dependency relations
     */
    DependencyRelations getY(){
        return y_ ;
    }

    /**
     * Returns the agenda for beam search.
     * @return the agenda. return null if {@link #setAgenda(int)} has not been called yet.
     */
    Agenda getAgenda(){
        return agenda_ ;
    }

    /**
     * Sets agenda with n beam-width.
     * @param n the beam width. it's set only if n is more than 1.
     */
    void setAgenda(int n){
        agenda_ = null;
        if (n > 1){
            agenda_ = new Agenda(n);
            agenda_.list_[0] = state_ ;
            agenda_.size_ = 1;
        }
    }

    /**
     * Returns the parsing state.
     * @return the parsing state.
     */
    State getState(){
        return state_ ;
    }

    /**
     * Returns the buffer for the feature vector.
     * @return the buffer
     */
    BinaryFeatureVector.Buffer getFeatureBuffer(){
        return fbuffer_;
    }

    /**
     * Returns the feature.
     * @return the feature
     */
    FeatureImpl getFeature(){
        return feature_;
    }

    /**
     * Returns the feature vector for estimations.
     * @return the feature vector
     */
    BinaryFeatureVector getFeatureVector(){
        return fv_ ;
    }

    @Override
    public boolean parseOneStep(){
        if ( parser_ == null)
            return false;
        if (agenda_ == null)
            return parser_.parse(this);

        agenda_.candidates_.clear();
        for(int i = 0 ; i < agenda_.size_; i++){
            state_ = agenda_.list_[i];
            if (state_.isComplete()){
                agenda_.candidates_.add(state_);
            } else {
                parser_.parse(this);
            }
        }
        for(State state : agenda_.candidates_){
            if (state.isComplete() && state.size() > 1)
                state.disable();
        }
        boolean complete = true;
        Collections.sort(agenda_.candidates_);
        agenda_.size_ = 0 ;

        for(State state : agenda_.candidates_){
            if (agenda_.size_ >=  agenda_.list_.length){
                agenda_.state_pool_.release(state);
                continue;
            }

            if (! state.isComplete())
                complete = false;
            agenda_.list_[agenda_.size_++] = state ;
        }
        state_ = agenda_.list_[0];
        if (complete)
            parser_.parse(this);
        return ! complete ;
    }

    @Override
    public void reparse(){
        state_.setup(sentence_);
    }

    @Override
    public void reparse(int start) {
        state_.setup(sentence_);
        state_.setPosition(start);
    }

    @Override
    public Sentence getSentence() {
        return sentence_ ;
    }

    /**
     * Returns the number of correct dependencies.
     * @return the number of correct dependency parents in the sentence.
     */
    public int getNumberOfCorrectDependencies(){
        int correct = 0 ;
        DependencyRelations dep = state_.getDependencies();
        for(int i = 0; i < sentence_.size(); i++){
            WordImpl word = (WordImpl) sentence_.getWord(i);
            int y = word.getParent();
            if (y < 0)
                y = -1 ;

            int p = dep.getParentID(i);
            int y_ = -1;
            if (p >= 0)
                y_ = p;

            if (y == y_)
                correct ++;
        }
        return correct ;
    }
}
