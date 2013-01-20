package jp.gr.java_conf.dyama.rink.parser.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jp.gr.java_conf.dyama.rink.parser.Sentence;

/**
 * Dependency Graph
 * @author Hiroyasu Yamada
 *
 */
public class DependencyGraph {
    static final int DEFAULT_CAPACITY = 100;
   /**
    * Dependency Node
    */
    static public final class Node {

        /** ID  */
        private int id_;

        /** the edge to the parent node */
        private Edge[] parents_;

        private int parent_ ;

        /** edges to the children's node */
        private Edge[] children_;

        /** array for children IDs   */
        private int[] childrenIDs_ ;

        private int numOfChildren_ ;

        /** the number of words in the sentence */
        private int size_;

        /**
         * Constructor
         */
        Node(){
            id_          = -1;
            parents_     = new Edge[DEFAULT_CAPACITY];
            parent_      = -1;
            children_    = new Edge[DEFAULT_CAPACITY];
            childrenIDs_ = new int[DEFAULT_CAPACITY];
            size_        = 0 ;
            numOfChildren_ = 0;
        }

        /**
         * setup a dependency node
         * @param id word index.
         * @param size number of words in the sentence
         */
        void setup(int id, int size){
            id_ = id ;
            size_ = size;
            numOfChildren_ = 0 ;
            if (parents_.length < size){
                parents_ = Arrays.copyOf(parents_, size_);
                children_ = Arrays.copyOf(children_, size_);
                childrenIDs_ = Arrays.copyOf(childrenIDs_, size_);
            }

            parent_ = -1;
            for(int i = 0; i < size_; i++){
                if (parents_[i] == null)
                    parents_[i] = new Edge();

                parents_[i].disable();

                if (children_[i] == null)
                    children_[i] = new Edge();
                children_[i].disable();
            }


            if (size_ < DEFAULT_CAPACITY && parents_.length > DEFAULT_CAPACITY * 5 ){
                parents_ = Arrays.copyOf(parents_, DEFAULT_CAPACITY);
                children_ = Arrays.copyOf(children_, DEFAULT_CAPACITY);
                childrenIDs_ = Arrays.copyOf(childrenIDs_, DEFAULT_CAPACITY);
            }
        }

        /**
         * check whether the children of the node is same to the children of argument node.
         * @param node target node.
         * @return true if the children of this node is same to the children of the target node.
         */
        boolean isSameChildren(Node node){
            if (node == null)
                return false ;
            if (this == node)
                return true;
            if (id_ != node.id_)
                return false;

            if (numOfChildren_ != node.numOfChildren_)
                return false;

            for(int i = 0;  i < numOfChildren_; i++){
                if (childrenIDs_[i] != node.childrenIDs_[i])
                    return false;
            }
            return true;
        }

        /**
         * get the ID
         * @return ID
         */
        int getID() {
            return id_ ;
        }


        /**
         * get parent node
         * @return parent node. return null if the node has no parent node.
         */
        Node getParent(){
            if (parent_ < 0 || parent_ >= size_ )
                return null;
            Edge e = parents_[parent_];
            if (e == null || ! e.isActive())
                return null ;
            return e.parent_ ;
        }

        /**
         * get the number of children
         * @return the number of children
         */
        int getNumOfChildren(){
            return numOfChildren_;
        }

        /**
         * get the child node
         * @param i the index of child.
         * @return child node. return null if i is out of range
         */
        Node getChild(int i){
            if (i < 0 || i >= numOfChildren_)
                return null ;

            Edge e = children_[childrenIDs_[i]];
            if (e == null || ! e.isActive())
                return null;
            return e.child_;
        }
    }

    /**
     * A Edge in a Dependency Graph
     */
    static class Edge {

        /** parent node */
        private Node parent_;

        /** child node */
        private Node child_;

        /** score */
        private double score_;

        /** flag for acitive or not */
        private boolean active_ ;

        /**
         * Constructor: inactive edge
         */
        Edge(){
            parent_ = null;
            child_  = null;
            score_  = 0.0;
            active_ = false;
        }

        /**
         * get the parent node
         * @return parent node
         */
        Node getParent(){
            return parent_ ;
        }

        /**
         * get the child node
         * @return child node
         */
        Node getChild(){
            return child_;
        }

        /**
         * get the score.
         * @return score
         */
        double getScore(){
            return score_;
        }

        /**
         * set score.
         * @param s score
         */
        void setScore(double s){
            score_ = s;
        }

        /**
         * activate this edge
         */
        void enable(){
            active_ = true;
        }

        /**
         * to be inactive
         */
        void disable(){
            active_ = false ;
        }

        /**
         * check whether the edge is active or not
         * @return true if the edge is active, otherwise false.
         */
        boolean isActive(){
            return active_;
        }

        /**
         * initialize to the default.
         */
        void clear(){
            parent_ = null;
            child_  = null;
            score_  = 0.0;
            active_ = false;
        }
    }

    private int root_ ;
    private int size_ ;
    private Node[] nodes_ ;

    /**
     * Constructor
     */
    public DependencyGraph(){
        nodes_ = new Node[DEFAULT_CAPACITY];
        size_  = 0;
        root_   = -1;
    }

    /**
     * setup a new dependency graph
     * @param sentence throw IllegalArgumentException if sentence is null.
     */
    void setup(Sentence sentence){
        if (nodes_.length < sentence.size()){
            nodes_ = Arrays.copyOf(nodes_, sentence.size());
        }
        size_ = sentence.size();

        for(int i = 0 ; i < size_; i++){
            if (nodes_[i] == null)
                nodes_[i] = new Node();
            nodes_[i].setup(i, size_);
        }
        root_ = -1;
    }

    /**
     * build dependencies by using dependency annotations of the sentence.
     * @param sentence throw IllegalArgumentException if sentence is null.
     */
    void buildDependencies(SentenceImpl sentence){
        setup(sentence);
        for(int i = 0; i < sentence.size(); i++){
            WordImpl word = (WordImpl)sentence.getWord(i);
            int  p = word.getParent();
            if (p >= 0){
                this.depend(p, i, Double.MAX_VALUE);
            } else {
                root_ = i ;
            }
        }
    }

    /**
     * check whether there is a dependency relation between two nodes.
     * @param parent parent node ID.
     * @param child child node ID.
     * @return true if there is a dependency relation. otherwise false.
     */
    boolean hasDependencyRelation(int parent, int child){
        if (parent < 0 || parent >= size_ )
            return false;

        if (child < 0 || child >= size_ )
            return false ;

        if (child == parent)
            return false;

        Node p = nodes_[parent];
        Node c = nodes_[child];
        return c.getParent() == p;
    }

    /**
     * construct dependency relation between two nodes.
     * @param ID of parent parent node. throw IllegalArgumentException if parent is out of range or same to the child.
     * @param ID of child node. throw IllegalArgumentException if child is out of range.
     * @param score  score.
     */
    void depend(int parent, int child, double score){
        if (parent < 0 || parent >= size_ )
            throw new IllegalArgumentException("the parent ID is out of range.");

        if (child < 0 || child >= size_ )
            throw new IllegalArgumentException("the child ID is out of range.");

        if (child == parent)
            throw new IllegalArgumentException("the parent ID is same to the child ID.");


        Node p = nodes_[parent];
        Node c = nodes_[child];

        Edge e = p.children_[child];
        if (e == null){
            e = new Edge() ;
            p.children_[child] = e;
        }
        e.enable();

        int m = 0 ;
        for(int k = 0 ; k < size_ ; k++){
            if (p.children_[k].isActive())
                p.childrenIDs_[m++] = k;
        }
        p.numOfChildren_ = m ;

        e.parent_ = p;
        e.child_  = c ;
        e.score_  = score;
        c.parent_ = parent;
        c.parents_[parent] = e ;
    }

    /**
     * get the node
     * @param i index
     * @return node. return null if i is out of range.
     */
    Node getNode(int i){
        if (i < 0 || i >= size_ )
            return null;
        return nodes_[i];
    }

    /**
     * get the number of nodes.
     * @return the number of nodes
     */
    int size(){
        return size_ ;
    }

    /**
     * get the root node
     * @return root node. return null  if the root node has been decided yet.
     */
    Node getRoot() {
        if (root_ < 0)
            return null;
        return nodes_[root_];
    }

    public static class DepthFirstTraverser implements Iterator<Node> {
        private static class State {
            private Node node_ ;
            private int progress_ ;
            State(Node node, int progress){
                node_ = node;
                progress_ = progress;
            }
        }

        private List<State> stack_ ;

        private DepthFirstTraverser(Node node){
            stack_ = new ArrayList<State>();
            stack_.add(new State(node, -1));
        }

        @Override
        public boolean hasNext() {
            return stack_.size() != 0;
        }

        @Override
        public Node next() {
            if (stack_.size() == 0)
                return null ;

            State top = stack_.remove(stack_.size() - 1);

            if ( top.node_.getNumOfChildren() == 0 || top.node_.getNumOfChildren() == top.progress_ ){
                return top.node_ ;
            }
            top.progress_ ++ ;
            stack_.add(top);
            Node ch = top.node_.getChild(top.progress_);
            stack_.add(new State(ch, -1));
            return next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("");
        }
    }
}
