package jp.gr.java_conf.dyama.rink.parser.core;
import jp.gr.java_conf.dyama.rink.parser.Sentence;

class DependencyRelations {
    static final int CAPACITY = 1024;

    /**
     * Dependency Node
     * @author Hiroyasu Yamada
     */
    static class Node{

        /** parent ID */
        private int parent_ ;

        /** set of children's ID */
        private int[] children_ ;

        private int size_ ;
        /**
         * Constructor
         */
        private Node(){
            parent_ = -1;
            size_ = 0;
            children_ = new int[CAPACITY];
        }

        private void addChild(int id){
            int k = 0;
            for(; k < size_ ; k++){
                if (children_[k] == id)
                    return ;
                if (children_[k] < id)
                    continue;
                break;
            }
            if (k == size_){
                children_[size_++] = id;
                return ;
            }

            for(;k < size_+1; k++){
                int n = children_[k];
                children_[k] = id;
                id = n ;
            }
            size_++;
        }

        /**
         * copy from a node.
         * @param node source node. it must not be null.
         */
        private void copy(Node node){
            parent_ = node.parent_;
            size_ = node.size_;
            for(int i = 0; i < size_; i++)
                children_[i] = node.children_[i];
        }
    }

    /** the size of nodes */
    private int size_ ;

    /** dependency nodes */
    private Node[] nodes_;

    /**
     * Constructor:
     */
    DependencyRelations(){
        size_ = 0;
        nodes_ = new Node[CAPACITY];
    }

    int size(){
        return size_ ;
    }

    /**
     * setup initial dependency relations from a sample.
     * @param sentence source sample.
     * @throws IllegalArgumentException if the sample is null.
     * @throws IllegalArgumentException if the size of sample is more than {@link CAPACITY}-1,
     */
    void setup(Sentence sentence){
        if (sentence == null)
            throw new IllegalArgumentException("the sentence is null.");
        int size = sentence.size();
        if (size >= CAPACITY)
            throw new IllegalArgumentException("the size of sentence is over the capacity.");
        size_ = size;
        for(int i = 0 ; i < size_ ; i++){
            if (nodes_[i] == null)
                nodes_[i] = new Node();
            nodes_[i].parent_ = -1;
            nodes_[i].size_ = 0;
        }
    }

    /**
     * build dependency relations from an annotated sample.
     * @param sentence annotated sample.
     * @throws IllegalArgumentException if the sentence is null.
     * @throws IllegalArgumentException if the size of sample is more than {@link CAPACITY}-1,
     */
    void build(SentenceImpl sentence){
        if (sentence == null)
            throw new IllegalArgumentException("the sample is null.");
        int size = sentence.size();
        if (size >= CAPACITY)
            throw new IllegalArgumentException("the size of sentence is over the capacity.");
        size_ = size;
        for(int i = 0 ; i < size_ ; i++){
            if (nodes_[i] == null)
                nodes_[i] = new Node();
            nodes_[i].parent_ = -1;
            nodes_[i].size_ = 0;
        }

        for(int i = 0 ; i < size_ ; i++){
            WordImpl word = (WordImpl) sentence.getWord(i);
            int p = word.getParent();
            if (p > -1)
                depend(p, i);
        }
    }


    /**
     * copy all dependency relations from a source dependency relations.
     * @param deps source dependency relations.
     * @throws IllegalArgumentException if the deps is null.
     */
    void copy(DependencyRelations deps){
        if (deps == null)
            throw new IllegalArgumentException("the source is null.");

        size_ = deps.size_;
        for(int i = 0; i < size_ ; i++){
            if (nodes_[i] == null)
                nodes_[i] = new Node();
            nodes_[i].copy(deps.nodes_[i]);
        }
    }

    private void check(int parentID, int childID){
        if (parentID < 0 || parentID >= size_)
            throw new IllegalArgumentException("the parent ID is out of range.");
        if (childID < 0 || childID >= size_)
            throw new IllegalArgumentException("the child ID is out of range.");
        if (parentID == childID)
            throw new IllegalArgumentException("the parent ID is same to the child's one.");
    }

    private void check(int nodeID){
        if (nodeID < 0 || nodeID >= size_)
            throw new IllegalArgumentException("the node ID is out of range.");
    }

    /**
     * check whether there is a dependency relation between parent and child.
     * @param parentID parent ID.
     * @param childID child ID
     * @return true if there is an dependency relation between the parent and child, otherwise false.
     * @throws IllegalArgumentException if either parentID or childID is out of range.
     */
    boolean hasDependencyRelation(int parentID, int childID){
        if (parentID < 0 || parentID >= size_)
            throw new IllegalArgumentException("the parent ID is out of range.");
        if (childID < 0 || childID >= size_)
            throw new IllegalArgumentException("the child ID is out of range.");

        return nodes_[childID].parent_ == parentID;
    }

    /**
     * create a new dependency relation between parent and child.
     * @param parentID parent ID.
     * @param childID child ID.
     * @throws IllegalArgumentException if either parentID or childID is out of range.
     */
    void depend(int parentID, int childID){
        check(parentID, childID);
        nodes_[childID].parent_ = parentID;
        nodes_[parentID].addChild(childID);
    }

    /**
     * get the children IDs.
     * @param nodeID node ID.
     * @param n n-th child.
     * @return children ID. if there is no child corresponding to the n, return -1.
     * @throws IllegalArgumentException if nodeID is out of range.
     */
     int getChildID(int nodeID, int n){
        check(nodeID);
        if (n < 0 || n > nodes_[nodeID].size_)
            return -1;
        return nodes_[nodeID].children_[n];
    }

     /**
      * get the number of children of a node.
      * @param nodeID target node.
      * @return the number of children of the nodeID.
      * @throws IllegalArgumentException if nodeID is out of range.
      */
     int getNumberOfChildren(int nodeID){
         check(nodeID);
         return nodes_[nodeID].size_;
     }

    /**
     * get the parent ID of a node.
     * @param nodeID node ID.
     * @return the parent ID.
     * @throws IllegalArgumentException if nodeID is out of range.
     */
    int getParentID(int nodeID){
        check(nodeID);
        return nodes_[nodeID].parent_;
    }

    /**
     * check whether the children of a node is same to other dependency relations or not.
     * @param nodeID node ID
     * @param deps other dependency relations.
     * @return true the children of the node is same to deps's ones, otherwise false.
     * @throws IllegalArgumentException the node ID is out of range.
     * @throws IllegalArgumentException the deps is null.
     */
    boolean isSameChildren(int nodeID, DependencyRelations deps){
        check(nodeID);
        if (deps == null)
            throw new IllegalArgumentException("the dependency relations is null.");

        if (size_ != deps.size_)
            return false;

        Node a = nodes_[nodeID];
        Node b = deps.nodes_[nodeID];
        if (a.size_ != b.size_)
            return false;

        for(int i = 0; i < a.size_; i++){
            if (a.children_[i] != b.children_[i])
                return false;
        }
        return true;
    }

}
