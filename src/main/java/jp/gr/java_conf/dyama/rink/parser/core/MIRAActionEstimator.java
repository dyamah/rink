package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.core.MIRAActionLearner.WeightVector;
import jp.gr.java_conf.dyama.rink.parser.core.SampleImpl.Agenda;

public class MIRAActionEstimator implements ActionEstimator {

    private static final long serialVersionUID = 6971559242328879541L;

    private Map<Integer, WeightVector[]> ws_ ;
    FeatureFunction function_ ;
    GroupIdentifier identifier_;

    MIRAActionEstimator(Map<Integer, WeightVector[]> ws, FeatureFunction function, GroupIdentifier identifier){
        ws_ = ws ;
        function_ = function;
        identifier_ = identifier;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        function_ = (FeatureFunction) in.readObject();
        identifier_ = (GroupIdentifier) in.readObject();
        int size = in.readInt();

        ws_ = new HashMap<Integer, WeightVector[]>();
        for(int i = 0; i < size; i++){
            int gid = in.readInt();
            int n = in.readInt();
            WeightVector[] ws = new WeightVector[n];
            for(int k = 1; k < n; k++)
                ws[k] = (WeightVector) in.readObject();
            ws_.put(gid, ws);
        }
    }
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeObject(function_);
        out.writeObject(identifier_);
        out.writeInt(ws_.size());
        for(Entry<Integer, WeightVector[]> entry : ws_.entrySet()){
            int gid = entry.getKey();
            WeightVector[] ws = entry.getValue();
            out.writeInt(gid);

            out.writeInt(ws.length);
            for(int i = 1 ; i < ws.length; i++)
                out.writeObject(ws[i]);
        }
    }

    @Override
    public Action estimate(SampleImpl sample) {
        function_.apply(sample);
        BinaryFeatureVector.Buffer x = sample.getFeatureBuffer();
        int gid = identifier_.getGroupID(sample);
        WeightVector[] ws = ws_.get(gid);

        Agenda agenda = sample.getAgenda();

        if (ws == null){
            ActionImpl action = new ActionImpl(Action.Type.SHIFT);
            if (agenda != null)
                agenda.addCandidate(sample.getState(), null);
            return action ;
        }


        if (agenda == null){

            int best = 0;
            double max_score = -Double.MAX_VALUE;
            for(int i = 1 ; i < ws.length; i++){
                double score = ws[i].dot(x);
                if (score > max_score){
                    max_score = score ;
                    best = i ;
                }
            }
            if (best == 0){
                best = 1;
                max_score = 0;
            }

            ActionImpl action = new ActionImpl(Action.Type.parseInt(best));
            action.setScore(max_score);
            return action;
        }


        double max_score = -Double.MAX_VALUE;

        ActionImpl shift = null;
        ActionImpl wait  = null;
        ActionImpl left  = null;
        ActionImpl right = null;

        int best = 0 ;
        for(int i = 1 ; i < ws.length; i++){
            double score = ws[i].dot(x);
            if (Action.Type.SHIFT.getID() == i){
                shift = new ActionImpl(Action.Type.SHIFT);
                shift.setScore(score);
            } else if (Action.Type.WAIT.getID() == i){
                wait = new ActionImpl(Action.Type.WAIT);
                wait.setScore(score);
            } else if (Action.Type.LEFT.getID() == i){
                left = new ActionImpl(Action.Type.LEFT);
                left.setScore(score);
            } else if (Action.Type.RIGHT.getID() == i){
                right = new ActionImpl(Action.Type.RIGHT);
                right.setScore(score);
            }

            if (score > max_score){
                max_score = score ;
                best = i ;
            }
        }
        if (best == 0){
            best = 1;
            max_score = 0;
        }

        State state = sample.getState();

        if (Action.Type.SHIFT.getID() == best){
            agenda.addCandidate(state, left);
            agenda.addCandidate(state, right);
        } else if (Action.Type.WAIT.getID() == best){
            agenda.addCandidate(state, left);
            agenda.addCandidate(state, right);
        } else if (Action.Type.LEFT.getID() == best){
            ActionImpl a = shift;
            if (shift.getScore() < wait.getScore())
                a = wait;
            agenda.addCandidate(state, a);
            agenda.addCandidate(state, right);
        } else if (Action.Type.RIGHT.getID() == best){
            ActionImpl a = shift;
            if (shift.getScore() < wait.getScore())
                a = wait;
            agenda.addCandidate(state, a);
            agenda.addCandidate(state, left);
        }

        if (best == 0)
            best = Action.Type.SHIFT.getID();

        ActionImpl bestAction = new ActionImpl(Action.Type.parseInt(best));
        bestAction.setScore(max_score);
        agenda.addCandidate(state, null);
        return bestAction;
    }
}
