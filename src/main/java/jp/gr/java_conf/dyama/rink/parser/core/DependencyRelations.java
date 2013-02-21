package jp.gr.java_conf.dyama.rink.parser.core;
import jp.gr.java_conf.dyama.rink.parser.Sentence;

class DependencyRelations {
    static final int CAPACITY = 1024;

    /**
     * Dependency Node
     * @author Hiroyasu Yamada
     */
    static class Node{

        /** the parent ID */
        private int parent_ ;

        /** the set of children's ID */
        private int[] children_ ;

        /** the number of children */
        private int size_ ;

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
         * Copies the internal state from the source node.
         * @param node the source node. It must not be null.
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


    DependencyRelations(){
        size_ = 0;
        nodes_ = new Node[CAPACITY];
    }

    /**
     * Returns the number of dependency nodes.
     * @return the number of dependency nodes.
     */
    int size(){
        return size_ ;
    }

    /**
     * Setup initial dependency relations from the source sentence.
     * @param sentence the source sample.
     * @throws IllegalArgumentException if the sentence is null.
     * @throws IllegalArgumentException if the size of sentence is more than {@link CAPACITY}-1,
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
     * Builds dependency relations from the sentence.
     * @param sentence the sentence.
     * @throws IllegalArgumentException if the sentence is null.
     * @throws IllegalArgumentException if the size of sentence is more than {@link CAPACITY}-1,
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
     * Copies all dependency relations from the source dependency relations.
     * @param deps the source dependency relations.
     * @throws IllegalArgumentException if the source dependency relations is null.
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
     * Checks whether there is a dependency relation between parent and child.
     * @param parentID the parent ID.
     * @param childID the child ID
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
     * Creates a new dependency relation between parent and child.
     * @param parentID the parent ID.
     * @param childID the child ID.
     * @throws IllegalArgumentException if either parentID or childID is out of range.
     */
    void depend(int parentID, int childID){
        check(parentID, childID);
        nodes_[childID].parent_ = parentID;
        nodes_[parentID].addChild(childID);
    }

    /**
     * Returns the children IDs.
     * @param nodeID the node ID.
     * @param n n-th child.
     * @return children ID. if there is no child corresponding to the n, return -1.
     * @throws IllegalArgumentException if nodeID is out of range.
     */
     int getChildID(int nodeID, int n){
        check(nodeID);
        if (n < 0 || n >= nodes_[nodeID].size_)
            return -1;
        return nodes_[nodeID].children_[n];
    }

     /**
      * Returns the number of children of the target node.
      * @param nodeID the ID of the target node.
      * @return the number of children of the target node.
      * @throws IllegalArgumentException if nodeID is out of range.
      */
     int getNumberOfChildren(int nodeID){
         check(nodeID);
         return nodes_[nodeID].size_;
     }

    /**
     * Returns the parent ID of the target node.
     * @param nodeID the ID of the target node.
     * @return the parent ID.
     * @throws IllegalArgumentException if nodeID is out of range.
     */
    int getParentID(int nodeID){
        check(nodeID);
        return nodes_[nodeID].parent_;
    }

    /**
     * Checks whether the children of the target node is same to the other dependency relations or not.
     * @param nodeID the ID of the target node.
     * @param deps the other dependency relations.
     * @return true the children of the target node is same to deps's ones, otherwise false.
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
