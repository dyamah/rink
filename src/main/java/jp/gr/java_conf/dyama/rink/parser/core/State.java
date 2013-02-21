package jp.gr.java_conf.dyama.rink.parser.core;

import jp.gr.java_conf.dyama.rink.common.Resource;

/**
 * State for Parsing Progress
 * @author Hiroyasu Yamada
 */
class State implements Comparable<State> {

    static class StatePool {
        private int size_ ;
        private State[] pool_ ;

        /**
         * Constructor
         * @param size the size of maximum pooling. the size is set to 1 if the size is less than 1.
         */
        StatePool(int size){
            if (size < 1)
                size = 1;
            pool_ = new State[size];
            for(int size_ = 0 ; size_ < pool_.length; size_++)
                pool_[size_] = new State();
        }

        /**
         * Creates a new state from this pool. The new instance is generated if the pool is empty.
         * @return a new state.
         */
        State create(){
            if (size_ == 0)
                return new State();
            State state = pool_[size_ - 1];
            pool_[size_ - 1] = null;
            size_ --;
            return state ;
        }

        /**
         * Releases the used state to this pool.
         * @param state the used state.
         * @throws IllegalArgumentException if the used state is null.
         */
        void release(State state){
            if (state == null)
                throw new IllegalArgumentException("the state is null.");
            if (size_ == pool_.length)
                return ;
            pool_[size_++] = state;
        }
    }

    /** the progress of parsing process */
    private int[] nodes_ ;

    /** the size of parsing progress */
    private int size_;

    /** the current position */
    private int position_;

    /** the flag whether the parsing is complete or not*/
    private boolean complete_;

    /** the last applied actions */
    private ActionImpl last_action_;

    /** analyzed dependencies */
    private DependencyRelations deps_ ;

    /** score */
    private double score_ ;

    /** the number of applied actions */
    int num_actions_ ;

    /**
     * Constructor
     */
    State(){
        nodes_ = new int[Resource.MAXIMUM_NUMBER_OF_WORDS];
        size_ = 0;
        position_ = 0 ;
        complete_ = true ;
        last_action_ = null;
        deps_    = new DependencyRelations();
        score_ = 0.0;
        num_actions_ = 0;
    }

    /**
     * Copies internal information from the source state.
     * @param state the source state.
     * @throws IllegalArgumentException if the source state is null.
     */
    void copy(State state){
        if (state == null)
            throw new IllegalArgumentException("the source state is null.");
        size_ = state.size_;
        for(int i = 0; i < size_; i++)
            nodes_[i] = state.nodes_[i];
        position_ = state.position_;
        complete_ = state.complete_;
        last_action_ = state.last_action_;
        deps_.copy(state.deps_);
        score_ = state.score_;
        num_actions_ = state.num_actions_;
    }

    /**
     * Initializes this state as the input sentence.
     * @param sentence the input sentence.
     * @throws IllegalArgumentException if the input sentence is null.
     */
    void setup(SentenceImpl sentence){
        if (sentence == null)
            throw new IllegalArgumentException("the sentence is null.");
        deps_.setup(sentence);
        position_  = 0 ;
        complete_ = true;
        last_action_ = null;
        size_ = deps_.size();
        score_ = 0.0;
        num_actions_ = 0;
        for(int i = 0 ; i < deps_.size(); i ++)
            nodes_[i] = i;
    }


    private void removeNode(int position){
        for(int k = position; k < size_-1; k++){
            nodes_[k] = nodes_[k+1];
        }
        size_ --;
    }

    private void setLastAction(ActionImpl action){
        last_action_ = action ;
        if (action.getType() == Action.Type.LEFT || action.getType() == Action.Type.RIGHT) // TODO :
            score_ += action.getScore();
        num_actions_ ++ ;
    }

    /**
     * Applies the parsing action to this state.
     * @param action the parsing action.
     * @return true if the parsing action could be applied, otherwise false.
     * @throws IllegalArgumentException if the parsing action is null.
     */
    boolean apply(Action action_){
        if (action_ == null)
            throw new IllegalArgumentException("the action is null.");

        ActionImpl action = (ActionImpl) action_ ;

        int left  = getLeftTarget();
        int right = getRightTarget();

       if ( left < 0)
           return false;

       if (action.getType() == Action.Type.SHIFT || action.getType() == Action.Type.WAIT){

           int r = deps_.size();
           if (right >= 0)
               r = right;
           action.setTarget(left, r);
           setLastAction(action);

           if (position_ < size())
               position_  ++;
           return true;
       }

       if (right < 0)
           return false;

       complete_ = false;
       action.setTarget(left, right);

       if ( action.getType() == Action.Type.LEFT){
           deps_.depend(left, right);
           removeNode(position_ + 1);
       } else if ( action.getType() == Action.Type.RIGHT ) {
           deps_.depend(right, left);
           removeNode(position_);

       } else {
           throw new IllegalArgumentException("unknown type of actions"); // OK
       }
       setLastAction(action);

       if ( position_ > 0)
           position_ --;

        return true;
    }

    /**
     * Checks whether the parsing process is complete or not (= No reduce action can be applied)
     * @return true if the parsing process is complete, otherwise false.
     * the conditions of the completeness:<br>
     * [1] {@link #size()} < 2 <br>
     * [2] the number of applied actions is more than 2 * #words - 1. (#words means the number of words in the input sentence.)<br>
     * [3] No Left/Right actions have been applied when the parsing position arrived at the end of the input sentence.
     */
    boolean isComplete(){
        if (size_ < 2)
            return true;
        if (num_actions_ > ( 2 * deps_.size() - 1))
            return true;

        return isEOS() && complete_ ;
    }

    void disable(){ // TODO :
        num_actions_ = Integer.MAX_VALUE;
    }

    /**
     * Returns the current position.
     * @return the current position
     */
    int getPosition(){
        return position_ ;
    }

    /**
     * Returns the ID of the left target node at the current position
     * @return the ID of the left target node. return -1 if the current position is out of range.
     */
    int getLeftTarget(){
        if (position_ < 0 || position_ >= size_)
            return -1 ;
        return nodes_[position_];
    }

    /**
     * Returns the ID of the left node corresponding to the relative position from the current position.
     * @param position the relative position from the current position.
     * @return the ID of the left node. return -1 if the absolute position arrives at BOS.
     * @throws IllegalArgumentException if the relative position is greater than 0.
     */
    int getIDofLeftNode(int position){
        if (position > 0)
            throw new IllegalArgumentException("the relative position is greater than 0.");

        int p = position_ + position ;
        if (p <  0 || p >= size())
            return -1 ;
        return nodes_[p];
    }

    /**
     * Returns the ID of the right target node at the current position
     * @return the ID of the right target node. return -1 if the current position is out of range.
     */
    int getRightTarget(){
        if (position_ + 1 < 0 || position_ + 1 >= size_)
            return -1;
        return nodes_[position_ + 1];
    }

    /**
     * Returns the ID of the right node corresponding to the relative position from the current position.
     * @param position the relative position from the current position.
     * @return the ID of right node corresponding the relative position. return -1 if the absolute position arrives at EOS.
     * @throws IllegalArgumentException if the relative position is greater than 0.
     */
    int getRightNode(int position){
        if (position < 0)
            throw new IllegalArgumentException("the relative position is less than 0.");

        int p = position_ + 1 + position ;
        if (p < 0 || p >= size_)
            return -1 ;
        return nodes_[p];
    }

    /**
     * Returns the last action.
     * @return the action. return null if no action have been applied.
     */
    Action getLastAction(){
        return last_action_ ;
    }

    /**
     * Sets the parsing position.
     * @param position the parsing position.
     * @throws IllegalArgumentException if the parsing position is out of range.
     */
    void setPosition(int position){
        if (position < 0 || position > size())
            throw new IllegalArgumentException("the position is out of range.");
        if (position == 0)
            complete_ = true;
        position_ = position;
    }

    /**
     * Returns the number of active dependency nodes
     * @return the number of target dependency nodes
     */
    int size(){
        return size_;
    }

    /**
     * Checks whether the current position arrives at EOS.
     * @return true if the current position arrives at EOS, otherwise false.
     */
    boolean isEOS(){
        return position_ == size_;
    }


    /**
     * Returns the analyzed dependency relations.
     * @return dependency relations
     */
    DependencyRelations getDependencies() {
        return deps_;
    }

    /**
     * Returns the score of this state.
     * @return the score of this state.
     */
    double getScore(){
        return score_ ;
    }

    @Override
    public int compareTo(State state) {
        if (state == null)
            return -1;
        if (score_ > state.score_)
            return -1 ;
        if (score_ < state.score_)
            return 1 ;
        return 0 ;
    }

}
