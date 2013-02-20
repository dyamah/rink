package jp.gr.java_conf.dyama.rink.parser.core;

/**
 * Interface of Dependency Parsing Action
 * @author Hiroyasu Yamada
 *
 */
public interface Action {

    /**
     * The type of Action
     */
    public static enum Type {

        /**
         * The action for the no dependency relation between the left and right target node.
         * Move the current position to the right.
         */
        SHIFT(1),

        /**
         * The action that there is an dependency between the left and right target node,
         * but the subtree of the left/right target node has not been complete yet.
         * Move the current position to the right.
         */
        WAIT(2),

        /**
         * The action for building the dependency relation that the left node is the parent of the right node.
         * Move the current position to the left after applying this action.
         */
        LEFT(3),

        /**
         * The action for building the dependency relation that the right node is the parent of the left node
         * stay current position after applying this action.
         */
        RIGHT(4);

        private int id_;
        private Type(int id){
            id_ = id ;
        }

        /**
         * Returns the action's ID
         * @return the action's ID
         */
        public int getID(){
            return id_ ;
        }

        private static final Type[] types_ = Type.values();

        /**
         * Parses the integer into the type of actions.
         * @param i the integer
         * @return the type of actions. return null if i is undefined.
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
     * Returns the score
     * @return score
     */
    public double getScore();

    /**
     * Returns the node ID of the left target node.
     * @return the node ID
     */
    public int getLeftTarget();

    /**
     * Returns the node ID of the right target node.
     * @return the node ID
     */
    public int getRightTarget();

    /**
     * Returns the type of Actions
     * @return the type of Actions
     */
    public Type getType();
}
