package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;

public class MIRAActionLearner implements ActionLearner {

    static class WeightVector implements Serializable {

        private static final long serialVersionUID = -1301842233365824538L;

        private BitVector[] w_ ;
        private int type_bit_length_ ;
        private int type_mask_;
        private int value_bit_length_ ;
        private int value_mask_;


        WeightVector(){
            type_bit_length_ = FeatureImpl.MAX_BIT_LENGTH - FeatureImpl.VALUE_BIT;
            value_bit_length_ = FeatureImpl.VALUE_BIT;
            type_mask_ = ((1 << type_bit_length_) - 1) << value_bit_length_ ;
            value_mask_ = (1 << value_bit_length_) - 1;

            int m = 1 << type_bit_length_;
            w_ = new BitVector[m];
            for(int  i = 0 ; i < m ; i++)
                w_[i] = null;

        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{

            type_bit_length_ = in.readInt();
            type_mask_ = in.readInt();
            value_bit_length_ = in.readInt();
            value_mask_ = in.readInt();
            int size  = in.readInt();
            w_ = new BitVector[size];
            for(int i = 0 ; i < size; i++){
                w_[i] = (BitVector) in.readObject();
            }
        }

        private void writeObject(ObjectOutputStream out) throws IOException{
            out.writeInt(type_bit_length_);
            out.writeInt(type_mask_);
            out.writeInt(value_bit_length_);
            out.writeInt(value_mask_);
            out.writeInt(w_.length);
            for(int i = 0 ; i < w_.length; i++){
                out.writeObject(w_[i]);
            }
        }

        double squaredL2norm(){
            double s = 0.0;
            for(BitVector w : w_ )
                if (w != null)
                    s += w.squaredL2norm();
            return s ;
        }

        double get(int fid){
            int type = ((fid & type_mask_) >> value_bit_length_);
            int value = (fid & value_mask_);

            if (type >= w_.length)
                return 0.0;
            BitVector w = w_[type];
            if (w == null)
                return 0.0;
            return w.get(value);
        }

        double dot(BinaryFeatureVector.Buffer buffer){
            double dot = 0 ;
            for(int fid : buffer.getFeatures())
                dot += get(fid) ;
            return dot;
        }

        void set(int fid, double weight){
            int type = ((fid & type_mask_) >> value_bit_length_);
            int value = (fid & value_mask_);
            if (type >= w_.length)
                throw new IllegalArgumentException("");

            BitVector w = w_[type];
            if (w == null){
                w = new BitVector(new double[value+1]);
                w_[type] = w;
            }
            w.set(value, weight);
        }

        void update(BinaryFeatureVector.Buffer x, double weight){
            for(int fid : x.getFeatures()){
                int type = ((fid & type_mask_) >> value_bit_length_);
                int value = (fid & value_mask_);
                if (type >= w_.length)
                    throw new IllegalArgumentException("the feature ID is out of range.");

                BitVector w = w_[type];
                if (w == null){
                    w = new BitVector(new double[value+1]);
                    w_[type] = w;
                }
                w.set(value, w.get(value) + weight);
            }
        }
    }

    private Map<Integer, WeightVector[]> ws_ ;
    private FeatureFunction function_ ;
    private int num_examples_ ;
    private GroupIdentifier identifier_;

    MIRAActionLearner(FeatureFunction function, GroupIdentifier identifier){
        ws_ = new HashMap<Integer, WeightVector[]>();
        function_ = function;
        num_examples_ = 0 ;
        identifier_ = identifier;
    }

    private WeightVector[] setupWeightVector(int gid){
        WeightVector[] ws = ws_.get(gid);
        if (ws != null)
            return ws;
        Action.Type[] actions = Action.Type.values();
        ws = new WeightVector[actions.length + 1];
        for(Action.Type t : actions )
            ws[t.getID()] = new WeightVector();
        ws_.put(gid, ws);
        return ws;
    }

    @Override
    public void addExample(SampleImpl x, Action y) {
        function_.apply(x);
        BinaryFeatureVector.Buffer buffer = x.getFeatureBuffer();
        learn(buffer, y.getType(), x);
        num_examples_ += 1;
    }

    private void learn(BinaryFeatureVector.Buffer x, Action.Type y, SampleImpl sample){
        Action.Type best =  null;
        double score_y = - Double.MAX_VALUE;
        double score_b = - Double.MAX_VALUE;
        WeightVector[] ws = setupWeightVector(identifier_.getGroupID(sample));
        for(int i = 1 ; i < ws.length; i++){
            double score = ws[i].dot(x);
            if (i == y.getID())
                score_y = score;
            if (score > score_b){
                best = Action.Type.parseInt(i) ;
                score_b = score;
            }
        }
        if (best == null){
            best = Action.Type.SHIFT;
            score_b = 0;
        }

        if (best == y)
            return ;

        double loss = score_b - score_y + 1;
        double tau = loss / (2 * x.size());
        ws[best.getID()].update(x, -tau);
        ws[y.getID()].update(x, tau);
    }

    @Override
    public ActionEstimator learn() {
        for(WeightVector[] ws : ws_.values()){
            for(WeightVector wv : ws){
                if (wv == null)
                    continue;
                for(int i = 0 ; i < wv.w_.length; i++){
                    if (wv.w_[i] == null)
                        continue;
                    wv.w_[i].product(1.0 / num_examples_);
                }
            }
        }
        return new MIRAActionEstimator(ws_, function_, identifier_);
    }

    public ActionEstimator convert(){
        return new MIRAActionEstimator(ws_, function_, identifier_);
    }

    @Override
    public void setProgressPrintStream(PrintStream out) {
    }

}
