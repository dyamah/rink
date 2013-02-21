package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;


class OracleActionEstimator implements ActionEstimator {

    static enum SetOfActions {
        ThreeActions,
        FourAcctions;
    }

    private static final long serialVersionUID = 6014086701845464338L;

    static final double DEFAULT_SCORE = 1.0;

    /** the feature function */
    private FeatureFunction function_ ;

    private Action.Type type_;

    /**
     * @param function The feature function.
     * @throws IllegalArgumentException if the feature function is null.
     */
    OracleActionEstimator(FeatureFunction function){
        if (function == null)
            throw new IllegalArgumentException("the feature function is null.");
        function_ = function ;
        type_ = Action.Type.WAIT;
    }

    /**
     * @param function The feature function.
     * @throws IllegalArgumentException if the feature function is null.
     */
    OracleActionEstimator(FeatureFunction function, SetOfActions setOfActions){
        this(function);
        if (setOfActions == SetOfActions.ThreeActions)
            type_ = Action.Type.SHIFT;
    }

    @Override
    public Action estimate(SampleImpl sample){
        function_.apply(sample);

        DependencyRelations y = sample.getY();

        State       state = sample.getState();
        DependencyRelations y_ = state.getDependencies();
        int left  = state.getLeftTarget();
        int right = state.getRightTarget();

        if (left < 0 || right < 0){
            ActionImpl action = new ActionImpl(Action.Type.SHIFT);
            action.setScore(DEFAULT_SCORE);
            return action ;
        }


        if (y.hasDependencyRelation(left, right)){
            if (y.isSameChildren(right, y_)){
                ActionImpl action = new ActionImpl(Action.Type.LEFT);
                action.setScore(DEFAULT_SCORE);
                return action ;
            }

            ActionImpl action = new ActionImpl(type_);
            action.setScore(DEFAULT_SCORE);
            return action ;

        } else if (y.hasDependencyRelation(right, left)){
            if (y.isSameChildren(left, y_)){
                ActionImpl action = new ActionImpl(Action.Type.RIGHT);
                action.setScore(DEFAULT_SCORE);
                return action ;
            }

            ActionImpl action = new ActionImpl(type_);
            action.setScore(DEFAULT_SCORE);
            return action ;

        } else {
            ActionImpl action = new ActionImpl(Action.Type.SHIFT);
            action.setScore(DEFAULT_SCORE);
            return action ;
        }
    }


    private void readObject(ObjectInputStream in){
        throw new UnsupportedOperationException("Serialization is unsupported.");
    }

    private void writeObject(ObjectOutputStream out){
        throw new UnsupportedOperationException("Serialization is unsupported.");
    }
}
