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

        StatePool(int size){
            pool_ = new State[size];
            for(int size_ = 0 ; size_ < pool_.length; size_++)
                pool_[size_] = new State();
        }

        State create(){
            if (size_ == 0)
                return new State();
            State state = pool_[size_ - 1];
            pool_[size_ - 1] = null;
            size_ --;
            return state ;
        }

        void release(State state){
            if (size_ == pool_.length)
                return ;
            pool_[size_++] = state;
        }

        void full(){
            for(int i = size_; i < pool_.length ; i++)
                pool_[i] = new State();
            size_ = pool_.length;
        }
    }

    /** the progress of parsing process */
    private int[] nodes_ ;

    /** the size of parsing progress */
    private int size_;

    /** the current position */
    private int position_;

    /** the flag whether parsing is complete or not*/
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
     * setup a new state
     * @param sentence throw IllegalArgumentException if sentence null.
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
     * apply an action at the current position.
     * @param action parsing action. throw IllegalArgumentException if action is null.
     * @return true if action could be applied, otherwise false.
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
           throw new IllegalArgumentException("unknown type of actions");
       }
       setLastAction(action);

       if ( position_ > 0)
           position_ --;

        return true;
    }

    /**
     * check whether parsing is complete or not (= No reduce action can be applied)
     * @return true if parsing is complete, otherwise false.
     * the conditions of completeness:<br>
     * [1] nodes_.size() < 2 <br>
     * [2] no applying left or right actions from BOS to EOS. <br>
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
        // score_ = -Double.MAX_VALUE;
    }

    /**
     * get the current position
     * @return current position
     */
    int getPosition(){
        return position_ ;
    }

    /**
     * get the ID of left target node at the current position
     * @return the ID of left target node.
     */
    int getLeftTarget(){
        if (position_ < 0 || position_ >= size_)
            return -1 ;
        return nodes_[position_];
    }

    /**
     * get the ID of left node from the current position
     * @param pos relative position from the current position.
     * @return the ID of left node. return -1 if the absolute position arrives at BOS.
     * @throws IllegalArgumentException if pos is greater than 0.
     */
    int getIDofLeftNode(int pos){
        if (pos > 0)
            throw new IllegalArgumentException("the relative position is greater than 0.");

        int p = position_ + pos ;
        if (p <  0 || p >= size())
            return -1 ;
        return nodes_[p];
    }

    /**
     * get the ID of right target node at the current position
     * @return the ID of right target node.
     */
    int getRightTarget(){
        if (position_ + 1 < 0 || position_ + 1 >= size_)
            return -1;
        return nodes_[position_ + 1];
    }

    /**
     * get the ID of  right node from the current position
     * @param pos relative position from the current position.
     * @return the ID of right node corresponding the relative position. return -1 if the absolute position arrives at BOS.
     * @throws IllegalArgumentException if pos is greater than 0.
     */
    int getRightNode(int pos){
        if (pos < 0)
            throw new IllegalArgumentException("the relative position is less than 0.");

        int p = position_ + 1 + pos ;
        if (p < 0 || p >= size_)
            return -1 ;
        return nodes_[p];
    }

    /**
     * get the latest action which could be applied.
     * @return action. return null if no action is applied.
     */
    Action getLastAction(){
        return last_action_ ;
    }

    /**
     * set the parsing position
     * @param pos new position. throw IllegalArgumentException if pos is out of range. (pos < 0 || pos > {@link #size()})
     */
    void setPosition(int pos){
        if (pos < 0 || pos > size())
            throw new IllegalArgumentException("the position is out of range.");
        if (pos == 0)
            complete_ = true;
        position_ = pos;
    }

    /**
     * the number of active dependency nodes
     * @return the number of target dependency nodes
     */
    int size(){
        return size_;
    }

    /**
     * check whether the current position arrives at EOS.
     * @return true if the current position arrives at EOS, otherwise false.
     */
    boolean isEOS(){
        return position_ == size_;
    }


    /**
     * get the analyzed dependency relations.
     * @return dependency relations
     */
    DependencyRelations getDependencies() {
        return deps_;
    }

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
