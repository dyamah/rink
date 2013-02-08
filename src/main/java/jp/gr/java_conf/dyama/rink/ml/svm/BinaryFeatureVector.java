package jp.gr.java_conf.dyama.rink.ml.svm;

import java.util.Set;
import java.util.TreeSet;

import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.FeatureVector;


/**
 * Binary Feature Vector
 * @author Hiroyasu Yamada
 *
 */
public final class BinaryFeatureVector implements FeatureVector<FeatureSpace.Binary> {


    /**
     * Buffer for Feature IDs.
     * @author Hiroyasu Yamada
     *
     */
    static public class Buffer {

        /** the internal buffer */
        private Set<Integer> buffer_ ;

        /**
         * Constructor
         */
        public Buffer(){
            buffer_ = new TreeSet<Integer>();
        }

        /**
         * Adds the feature ID
         * @param id the feature ID.
         * @throws IllegalArgumentException if the id is negative.
         */
        public void add(int id){
            if (id < 0)
                throw new IllegalArgumentException("the feature ID is negative.");

            buffer_.add(id);
        }

        /**
         * Returns the number of features.
         * @return the number of features.
         */
        public int size() {
            return buffer_.size() ;
        }

        /**
         * clear this buffer.
         */
        public void clear(){
            buffer_.clear();
        }

        public Iterable<Integer> getFeatures() {
            return buffer_ ;
        }

        /**
         * Returns the internal set
         * @return the internal set.
         */
        private Set<Integer> getSet(){
            return buffer_ ;
        }
    }

    /** the default capacity */
    private static final int DEFAULT_CAPACITY = 100;

    /** the array for features */
    private int[] features_;

    /** the maximum size for features ( = features_.length) */
    private int capacity_;

    /** the number of available features */
    private int size_;

    Estimation estimation_ ;

    /**
     * Constructor
     * the capacity of the vector is set as DEFAULT_CAPACITY.
     * the size is initialized to 0.
     */
    public BinaryFeatureVector(){
        features_ = new int[DEFAULT_CAPACITY];
        for(int i = 0 ; i < features_.length; i++)
            features_[i] = -1;
        capacity_  = features_.length ;
        size_      = 0 ;
    }

    /**
     * Reset to a new feature vector
     * @param buffer the buffer for new features.
     * @throws IllegalArgumentException if the buffer is null.
     */
    public void reset(Buffer buffer) {
        if (capacity_ < buffer.size()){
            features_ = new int[buffer.size()];
            for(int i = 0 ; i < features_.length; i++)
                features_[i] = -1;
            capacity_ = features_.length;
        }

        size_ = 0 ;
        for(Integer e: buffer.getSet()){
            features_[size_++] = e;
        }
    }

    /**
     * Returns the number of available features.
     * @return the number of available features.
     */
    public int size(){
        return size_;
    }

    /**
     * Returns the value of the squared L2 norm.
     * @return the value of the squared L2 norm.
     */
    public double getSquareOfL2Norm(){
        return size_ ;
    }

    /**
     * Returns the feature.
     * @param i the index ( not the feature ID ).
     * @return feature corresponding to the index.
     * @throws IllegalArgumentException if the index is negative, or {@link #size()} and over.
     */
    public Feature getFeature(int i){
        if (i < 0 || size_ <= i)
            throw new IllegalArgumentException("the index is out of range.");
        return new Feature(features_[i], 1.0);
    }

    /**
     * Returns the feature ID.
     * @param i the index ( not the feature ID ).
     * @return feature ID corresponding to the index i
     * @throws IllegalArgumentException if i is negative, or {@link #size()} and over.
     */
    public int getFeatureID(int i){
        if (i < 0 || size_ <= i)
            throw new IllegalArgumentException("the index is out of range.");
        return features_[i];
    }

    /**
     * Returns the array of feature IDs. Note that the max index of available features is {@link size()} API, not equals to the length of the array.
     * @return the array of feature IDs
     */
    int[] getFeatureIDs(){
        return features_ ;
    }

    Estimation getEstimation(int classes, int svs){
        if (estimation_ == null){
            estimation_ = new Estimation(classes, svs);
            return estimation_ ;
        }
        estimation_.setup(classes, svs);
        return estimation_;
    }
}
