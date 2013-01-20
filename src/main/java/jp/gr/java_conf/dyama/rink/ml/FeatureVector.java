package jp.gr.java_conf.dyama.rink.ml;


import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * The interface for Feature Vector
 * @author Hiroyasu Yamada
 *
 * @param <T> the type of feature space: Real, Binary
 */
public interface FeatureVector<T extends FeatureSpace> {
    /**
     * Feature
     * @author Hiroyasu Yamada
     *
     */
    public static final class Feature {

        /** Feature ID for unknown features */
        public static final int UNKNOWN_ID = -1 ;

        /** Feature ID */
        private int id_;

        /** Feature value */
        private double value_;

        /**
         * Constructor:
         * @param id Feature ID. throw IllegalArgumentException if id is negative.
         * @param value Feature Value
         */
        public Feature(int id, double value){
            if (id < 0)
                throw new IllegalArgumentException("the id is negative.");

            id_ = id ;
            value_ = value ;
        }

        /**
         * get Feature ID
         * @return Feature ID
         */
        public final int getID(){
            return id_ ;
        }

        /**
         * get Feature Value.
         * @return Feature Value.
         */
        public final double getValue(){
            return value_;
        }

        /**
         * set Feature ID and Feature Value.
         * @param id Feature ID. throw IllegalArgumentException if id is negative.
         * @param value Feature Value.
         */
        final void set(int id, double value){
            if (id < 0)
                throw new IllegalArgumentException("the id is negative.");
            id_ = id ;
            value_ = value ;
        }

    }

    /**
     * get the size of features ( = the number of none zero features).
     * @return size of features
     */
    public int size();


    /**
     * get the squared L2 norm
     * @return  squared L2 norm
     */
    public double getSquareOfL2Norm();


    /**
     * get a feature.
     * @param i index. throw IllegalArgumentException if i is negative, or {@link size()} and over.
     * @return feature
     */
    public Feature getFeature(int i) ;

    /**
     * Feature Vector for Machine Learning
     * @author Hiroyasu Yamada
     */
    public static class Impl implements FeatureVector<FeatureSpace.Real> {

        /**
         * Feature Buffer for creating a {@link Impl} instance.
         * @author Hiroyasu Yamada
         */
        public static class Buffer {

            /** buffer */
            private Map<Integer, Double> buffer_;

            /**
             * Constructor
             *
             */
            public Buffer(){
                buffer_ = new TreeMap<Integer, Double>();
            }

            /**
             * add a new Feature.
             * @param id feature ID. throw IllegalArgumentException if the id is negative.
             * @param value feature value.
             * If the buffer has included the same ID, the value of the buffer is updated to sum of value and buffer's one.
             */
            public void add(int id, double value){
                if (id < 0)
                    throw new IllegalArgumentException("the id is negative.");

                Double v = buffer_.get(id);
                if (v == null){
                    buffer_.put(id, value);
                    return ;
                }
                buffer_.put(id, value + v );
            }

            /**
             * clear the buffer.
             */
            public void clear(){
                buffer_.clear();
            }

            /**
             * get the size of buffered features
             * @return the size of buffered features.
             */
            public int size(){
                return buffer_.size();
            }

            /**
             * get the buffer.
             * @return buffer.
             */
            Map<Integer, Double> getBuffer(){
                return buffer_;
            }

        }

        /** array for features */
        private Feature[] features_;

        /** the size of available features */
        private int size_;

        /** The square value of L2 norm */
        private double sq_l2norm_ ;

        /**
         * Constructor
         * the capacity of the vector is set as DEFAULT_CAPACITY.
         * the size is initialized as 0.
         */
        public Impl(Buffer buffer){
            if (buffer == null)
                throw new IllegalArgumentException("the buffer is null.");
            features_ = new Feature[buffer.size()];
            size_      = 0 ;

            for(Entry<Integer, Double> e : buffer.getBuffer().entrySet()){
                features_[size_++] = new Feature(e.getKey(), e.getValue());
            }
            sq_l2norm_ = calcSquaredL2norm(features_);
        }

        private static double calcSquaredL2norm(Feature[] features ){
            double v = 0.0;
            for(Feature f : features){
                if( f != null)
                    v += f.getValue() * f.getValue();
            }
            return v;
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
            return sq_l2norm_;
        }

        /**
         * get a available feature.
         * @param i the index ( not Feature ID ). throw IllegalArgumentException if i is negative or more and over {@link #size()}.
         * @return
         */
        public Feature getFeature(int i){
            if (i < 0 || size_ <= i)
                throw new IllegalArgumentException("the index is out of range.");
            return features_[i];
        }
    }
}

