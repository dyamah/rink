package jp.gr.java_conf.dyama.rink.ml.svm;

import java.util.Set;
import java.util.TreeSet;

import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.FeatureVector;


/**
 * Binary Feature Vector for efficiency
 * @author Hiroyasu Yamada
 *
 */
public final class BinaryFeatureVector implements FeatureVector<FeatureSpace.Binary> {


    /**
     * Feature ID buffer
     * @author Hiroyasu Yamada
     *
     */
    static public class Buffer {

        /** buffer */
        private Set<Integer> buffer_ ;

        /**
         * Constructor
         */
        public Buffer(){
            buffer_ = new TreeSet<Integer>();
        }

        /**
         * add feature ID
         * @param id feature ID. throw IllegalArgumentException if id is negative.
         */
        public void add(int id){
            if (id < 0)
                throw new IllegalArgumentException("the feature ID is negative.");

            buffer_.add(id);
        }

        /**
         * get the size of feature IDs ( = the number of features).
         * @return size of feature IDs
         */
        public int size() {
            return buffer_.size() ;
        }

        /**
         * clear the buffer.
         */
        public void clear(){
            buffer_.clear();
        }

        public Iterable<Integer> getFeatures() {
            return buffer_ ;
        }

        /**
         * get the inner set .
         * @return Set
         */
        private Set<Integer> getSet(){
            return buffer_ ;
        }
    }

    /** default capacity */
    private static final int DEFAULT_CAPACITY = 100;

    /** array for features */
    private int[] features_;

    /** maximum size for features ( = features_.length) */
    private int capacity_;

    /** the size of available features */
    private int size_;

    Estimation estimation_ ;

    /**
     * Constructor
     * the capacity of the vector is set as DEFAULT_CAPACITY.
     * the size is initialized as 0.
     */
    public BinaryFeatureVector(){
        features_ = new int[DEFAULT_CAPACITY];
        for(int i = 0 ; i < features_.length; i++)
            features_[i] = -1;
        capacity_  = features_.length ;
        size_      = 0 ;
    }

    /**
     * reset a new feature vector
     * @param buffer buffer for features. throw IllegalArgumentException if buffer is null.
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
     * get the size of available features.
     * @return the size of available features.
     */
    public int size(){
        return size_;
    }

    /**
     * get squared L2 norm.
     * @return the value of squared L2 norm.
     */
    public double getSquareOfL2Norm(){
        return size_ ;
    }

    /**
     * get a available feature.
     * @param i the index ( not Feature ID ). throw IllegalArgumentException if i is negative, or m{@link #size()} and over.
     * @return feature corresponding to the index i
     */
    public Feature getFeature(int i){
        if (i < 0 || size_ <= i)
            throw new IllegalArgumentException("the index is out of range.");
        return new Feature(features_[i], 1.0);
    }

    /**
     * get a available feature ID.
     * @param i the index ( not Feature ID ). throw IllegalArgumentException if i is negative, or {@link #size()} and over.
     * @return feature ID corresponding to the index i
     */
    public int getFeatureID(int i){
        if (i < 0 || size_ <= i)
            throw new IllegalArgumentException("the index is out of range.");
        return features_[i];
    }

    /**
     * get the array of feature IDs. Note that the max index of available features is {@link size()} API, not equals to the length of this array.
     * @return array of feature IDs
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
