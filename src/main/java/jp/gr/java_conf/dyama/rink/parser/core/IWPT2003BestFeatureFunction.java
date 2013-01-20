package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector.Buffer;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.Sentence;
import jp.gr.java_conf.dyama.rink.parser.Word;
import jp.gr.java_conf.dyama.rink.parser.core.FeatureImpl.POSITION;
import jp.gr.java_conf.dyama.rink.parser.core.FeatureImpl.RELATION;
import jp.gr.java_conf.dyama.rink.parser.core.FeatureImpl.TYPE;

public class IWPT2003BestFeatureFunction implements FeatureFunction {

    private static final long serialVersionUID = 5752622580255058238L;

    /** the length of left context: maximum 7  */
    private int length_of_left_context_;

    /** the length of right context: maximum 7   */
    private int length_of_right_context_;

    /**
     * Constructor
     * @param left the length of left context for extracting features. throw IllegalArgumentException if the length is not from 1 to 7.
     * @param right the length of right context for extracting features. throw IllegalArgumentException if the length is not from 1 to 7.
     */
    public IWPT2003BestFeatureFunction(int left, int right) {
        if (left < 1 || left > 7)
            throw new IllegalArgumentException("the length of left context is out of range.");

        if (right < 1 || right > 7)
            throw new IllegalArgumentException("the length of right context is out of range.");

        length_of_left_context_ = left ;
        length_of_right_context_ = right ;
    }

    /**
     * convert  a relative position to the POSITION instance.
     * @param pos relative position (a negative number means that the position is in the left context, 0 means left/right target node).
     * @param left flag of left or right context. the flag is used to identify left/right target node when pos is 0.
     * @return POSITION instance
     */
    private POSITION toPosition(int pos, boolean left) {
        if (left){
            switch (pos) {
            case -7: return POSITION.L7 ;
            case -6: return POSITION.L6 ;
            case -5: return POSITION.L5 ;
            case -4: return POSITION.L4 ;
            case -3: return POSITION.L3 ;
            case -2: return POSITION.L2 ;
            case -1: return POSITION.L1 ;
            case  0: return POSITION.L0 ;
            }
        }
        switch (pos) {
        case  0: return POSITION.R0 ;
        case  1: return POSITION.R1 ;
        case  2: return POSITION.R2 ;
        case  3: return POSITION.R3 ;
        case  4: return POSITION.R4 ;
        case  5: return POSITION.R5 ;
        case  6: return POSITION.R6 ;
        case  7: return POSITION.R7 ;
        }
        return null ; //

    }

    /**
     * add a encoded feature to the buffer.
     *
     * @param position
     *            position
     * @param relation
     *            relation
     * @param type
     *            type
     * @param value
     *            feature value. add no feature if value is 0 and fewer.
     * @param buffer
     *            buffer
     * @return true if a encoded feature is added to the buffer, otherwise
     *         false.
     */
    private boolean addFeature(POSITION position, RELATION relation, TYPE type, int value, FeatureImpl f, Buffer buffer) {
        f.set(position, relation, type, value);
        int i = f.encode();
        if (i > 0){
            buffer.add(i);
            return true;
        }
        return false ;
    }

    /**
     * add left context features
     *
     * @param sample a sample, features are extracted from the left context of the current position.
     */
    private void addLeftContextFeature(SampleImpl sample) {

        FeatureImpl f = sample.getFeature();
        BinaryFeatureVector.Buffer buffer = sample.getFeatureBuffer();
        State state = sample.getState();
        Sentence sentence = sample.getSentence();

        for (int t = -length_of_left_context_; t <= 0; t++) {
            int nodeID = state.getIDofLeftNode(t);
            POSITION position = toPosition(t, true);
            Word word = WordImpl.BOS;
            if (nodeID >= 0)
                word = sentence.getWord(nodeID);

            addFeature(position, RELATION.SELF, TYPE.LEXCON, word.getID(), f, buffer);
            addFeature(position, RELATION.SELF, TYPE.POS, word.getPOS().getID(), f, buffer);

            if (nodeID < 0)
                continue;

            DependencyRelations deps = state.getDependencies();
            if (deps.getNumberOfChildren(nodeID) == 0)
                continue;

            for (int i = 0 ; i < deps.getNumberOfChildren(nodeID); i++) {
                int childID = deps.getChildID(nodeID, i);
                word = sentence.getWord(childID);
                RELATION rel = RELATION.LEFT_CHILD;
                if (nodeID < childID)
                    rel = RELATION.RIGHT_CHILD;
                addFeature(position, rel, TYPE.LEXCON, word.getID(), f, buffer);
                addFeature(position, rel, TYPE.POS, word.getPOS().getID(), f, buffer);
            }
        }
    }

    /**
     * add right context features
     *
     * @param sample a sample, features are extracted from the right context of the current position.
     */
    private void addRightContextFeature(SampleImpl sample) {
        FeatureImpl f = sample.getFeature();
        BinaryFeatureVector.Buffer buffer = sample.getFeatureBuffer();
        State state = sample.getState();
        Sentence sentence = sample.getSentence();

        for (int t = 0; t <= length_of_right_context_; t++) {
            int nodeID = state.getRightNode(t);
            POSITION position = toPosition(t, false);
            Word word = WordImpl.EOS;
            if (nodeID >= 0)
                word = sentence.getWord(nodeID);

            addFeature(position, RELATION.SELF, TYPE.LEXCON, word.getID(), f, buffer);
            addFeature(position, RELATION.SELF, TYPE.POS, word.getPOS().getID(), f, buffer);

            if (nodeID < 0)
                continue;

            DependencyRelations deps = state.getDependencies();
            if (deps.getNumberOfChildren(nodeID) == 0)
                continue;

            for (int i = 0 ; i <  deps.getNumberOfChildren(nodeID); i++) {
                int childID = deps.getChildID(nodeID, i);
                word = sentence.getWord(childID);
                RELATION rel = RELATION.LEFT_CHILD;
                if (nodeID < childID)
                    rel = RELATION.RIGHT_CHILD;
                addFeature(position, rel, TYPE.LEXCON, word.getID(), f, buffer);
                addFeature(position, rel, TYPE.POS, word.getPOS().getID(), f, buffer);
            }
        }

    }

    @Override
    public void apply(Sample _sample) {
        if (_sample == null)
            throw new IllegalArgumentException("the sample is null.");
        SampleImpl sample = (SampleImpl) _sample;
        sample.getFeatureBuffer().clear();
        addLeftContextFeature(sample);
        addRightContextFeature(sample);
    }

    private void readObject(ObjectInputStream in) throws IOException{
        int l = in.readInt();
        int r = in.readInt();
        if (l < 1 || l > 7)
            throw new InvalidObjectException("the length of left context is out of range.");
        if (r < 1 || r > 7)
            throw new InvalidObjectException("the length of right context is out of range.");
        length_of_left_context_ = l ;
        length_of_right_context_ = r ;
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeInt(length_of_left_context_);
        out.writeInt(length_of_right_context_);
    }

}
