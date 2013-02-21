package jp.gr.java_conf.dyama.rink.parser.core;


final class ActionImpl implements Action {

    private Type type_;
    private double score_;
    private int left_;
    private int right_;

    /**
     * @param type the type of actions.
     * @throws IllegalArgumentException if the type of actions is null.
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
     * Sets score
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
     * Sets the target node's ID.
     * @param leftID the left target's ID.
     * @param rightID right target's ID.
     * @throws IllegalArgumentException if the leftID is negative.
     * @throws IllegalArgumentException if the leftID is not less than the rightID.
     */
    void setTarget(int leftID, int rightID){
        if (leftID < 0 )
            throw new IllegalArgumentException("the left ID is negative.");

        if (leftID >= rightID)
            throw new IllegalArgumentException("the left ID is not less than the right ID.");

        left_ = leftID;
        right_ = rightID;
    }

    @Override
    public Type getType(){
        return type_;
    }
}
