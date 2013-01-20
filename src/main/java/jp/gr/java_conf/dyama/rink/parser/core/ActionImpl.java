package jp.gr.java_conf.dyama.rink.parser.core;


final class ActionImpl implements Action {

    private Type type_;
    private double score_;
    private int left_;
    private int right_;

    /**
     * Constructor
     * @param type type of actions.
     * @throws IllegalArgumentException if action is null.
     */
    ActionImpl(Type type){
        if ( type == null)
            throw new IllegalArgumentException("the type of action is null.");
        type_ = type ;
        score_ = 0.0;
        left_  = -1;
        right_ = -1;
    }

    @Override
    public double getScore(){
        return score_;
    }

    /**
     * set score
     * @param score score.
     */
    void setScore(double score){
        score_ = score ;
    }

    @Override
    public int getLeftTarget(){
        return left_;
    }
    @Override
    public int getRightTarget(){
        return right_;
    }

    /**
     * set target node's ID
     * @param left left target's ID. throw IllegalArgumentException if left is negative.
     * @param right right target's ID. throw IllegalArgumentException if left is not less than right.
     */
    void setTarget(int left, int right){
        if (left < 0 )
            throw new IllegalArgumentException("the left ID is negative.");

        if (left >= right)
            throw new IllegalArgumentException("the left ID is not less than the right ID.");

        left_ = left;
        right_ = right;
    }

    @Override
    public Type getType(){
        return type_;
    }
}
