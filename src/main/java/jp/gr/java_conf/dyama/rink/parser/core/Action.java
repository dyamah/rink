package jp.gr.java_conf.dyama.rink.parser.core;

/**
 * Interface of Dependency Parsing Action
 * @author Hiroyasu Yamada
 *
 */
public interface Action {

    /**
     * Type of Action
     */
    public static enum Type {

        /**
         * The action for the no dependency relation between left and right target node.
         * Move the current position to the right.
         */
        SHIFT(1),

        /**
         * The action that there is an dependency between the left and right target node,
         * but the subtree of a child node has not been complete yet.
         * Move the current position to the right.
         */
        WAIT(2),

        /**
         * The action for a dependency relation that the left node is the parent of the right node.
         * Move the current position to the left after applying this action.
         */
        LEFT(3),

        /**
         * The action for a dependency relation that the right node is the parent of the left node
         * stay current position after applying this action.
         */
        RIGHT(4);

        private int id_;
        private Type(int id){
            id_ = id ;
        }

        /**
         * get the action ID
         * @return action ID
         */
        public int getID(){
            return id_ ;
        }

        private static final Type[] types_ = Type.values();
        /**
         * parse integer into the type of actions.
         * @param i integer
         * @return type of actions. return null if i is undefined.
         */
        static Type parseInt(int i){
            for(int k = 0 ; k < types_.length ; k++ ){
                if (types_[k].getID() == i)
                    return types_[k];
            }
            return null;
        }
    }

    /**
     * get the score
     * @return score
     */
    public double getScore();

    /**
     * get the node ID of the left target node.
     * @return node ID
     */
    public int getLeftTarget();

    /**
     * get the node ID of the right target node.
     * @return node ID
     */
    public int getRightTarget();

    /**
     * get the type of Actions
     * @return type of Actions
     */
    public Type getType();


}
