package jp.gr.java_conf.dyama.rink.ml.svm;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import jp.gr.java_conf.dyama.rink.ml.FeatureSpace;
import jp.gr.java_conf.dyama.rink.ml.FeatureVector;
import jp.gr.java_conf.dyama.rink.ml.FeatureSpace.Binary;
import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.Feature.Real.Value;
import jp.gr.java_conf.dyama.rink.ml.svm.KernelFunction.Linear;
import jp.gr.java_conf.dyama.rink.ml.svm.KernelFunction.NoneLinear;

/**
 * Abstract class for the interface 'Classifier'.
 *
 * @author Hiroyasu Yamada
 *
 * @param <K>
 *            the type of Kernel functions.
 * @param <FS>
 *            the type of Feature Space.
 */
abstract class ClassifierImpl<K extends KernelFunction, FS extends FeatureSpace> implements Classifier<K, FS> {

    private static final long serialVersionUID = 2917094435679636763L;

    /**
     * the hyperplane
     */
    static final class HyperPlane {
        /** the positive label ID */
        private int positive_;

        /** the negative label ID */
        private int negative_;

        /** the bias value */
        private double bias_;

        /**
         * Constructor
         *
         * @param pos the ID of the positive label.
         * @param neg the ID of the negative label.
         * @param bias the value of bias
         * @throws IllegalArgumentExcpetion if pos is negative.
         * @throws IllegalArgumentException if neg is less than pos.
         */
        HyperPlane(int pos, int neg, double bias) {
            if (pos < 0)
                throw new IllegalArgumentException("the positive ID is negative.");

            if (neg <= pos)
                throw new IllegalArgumentException("the negative ID is equal to the positive ID and fewer.");

            positive_ = pos;
            negative_ = neg;
            bias_ = bias;
        }

    }

    /**
     * Feature for SVs
     */
    interface Feature {

        static final class Real implements Feature {

            static class Value {
                /** the feature value */
                private double value_;

                /** the support vector ID */
                private int svid_;

                /**
                 * Constructor
                 * @param value the feature value
                 * @param svid  the support vector ID.
                 * @throws IllegalArgumentException if svid is negative.
                 */
                private Value(double value, int svid){
                    if (svid < 0)
                        throw new IllegalArgumentException("the support vector ID is negative.");
                    value_ = value ;
                    svid_  = svid ;
                }

                /**
                 * Returns the feature value.
                 * @return feature value
                 */
                double getValue(){
                    return value_;
                }

                /**
                 * Returns the support vector ID.
                 * @return support vector ID
                 */
                int getSvID(){
                    return svid_ ;
                }

            }

            static final int DEFAULT_VALUE_SIZE = 10;

            /** the feature ID */
            private int id_;

            /** the feature values for SVs */
            private Value[] values_;

            /** the size of feature values */
            private int size_ ;

            /**
             * Constructor
             * @param id the feature ID.
             * @param capacity the expected size of capacity. it's set to DEFAULT_VALUE_SIZE if the size of the capacity is negative.
             * @throws IllegalArgumentException if the feature ID is negative.
             */
            Real(int id, int capacity){
                if ( id < 0 )
                    throw new IllegalArgumentException("the feature ID is negative.");

                id_ = id ;
                int capa = capacity ;
                if (capacity < 0)
                    capa = DEFAULT_VALUE_SIZE ;

                values_ = new Value[capa];
                size_ = 0 ;
            }

            /**
             * add a feature value for SVs.
             * @param value the feature value.
             * @param svid the support vector ID.
             * @throws IlleaglArgumentException if svid is negative.
             */
            void addValue(double value, int svid){
                Value v = new Value(value, svid);
                if (values_.length == size_){
                    values_ = Arrays.copyOf(values_, values_.length + DEFAULT_VALUE_SIZE);
                    for(int i = size_ ;  i < values_.length; i++)
                        values_[i] = null ;
                }
                values_[size_ ++ ] = v ;
            }
        }

        static final class Binary implements Feature{
            static final int DEFAULT_VALUE_SIZE = 10;

            /** feature ID */
            private int id_;

            /**
             * IDs of Support Vectors
             */
            private int[] svids_;

            /** the size of values */
            private int size_;

            /**
             * Constructor
             *
             * @param id the feature ID.
             * @param capacity the expected size of the capacity. the size of the capacity is set to{@link DEFAULT_VALUE_SIZE} if the capacity is negative.
             * @throws IllegalArgumentException if the feature id is negative.
             */
            Binary(int id, int capacity) {
                if (id < 0)
                    throw new IllegalArgumentException("the feature ID is negative.");
                id_ = id;
                int capa = capacity;
                if (capacity < 0)
                    capa = DEFAULT_VALUE_SIZE;
                svids_ = new int[capa];
                for (int i = 0; i < svids_.length; i++)
                    svids_[i] = -1;
                size_ = 0;
            }

            /**
             * Adds a new Support Vector ID.
             *
             * @param svid the support vector ID.
             * @throw IllegalArgument Exception if the support vector ID is negative.
             */
            void addSvID(int svid) {
                if (svid < 0)
                    throw new IllegalArgumentException("the support vector ID is negative.");

                if (svids_.length == size_) {
                    svids_ = Arrays.copyOf(svids_, svids_.length + DEFAULT_VALUE_SIZE);
                    for (int i = size_; i < svids_.length; i++)
                        svids_[i] = -1;
                }
                svids_[size_++] = svid;
            }
        }
    }

    static final class SVCoefficient {

        static final class Coefficient {
            private int id_ ;
            private double coef_ ;
            Coefficient(int id, double coef){
                id_ = id;
                coef_ = coef ;
            }
        }
        /** the label ID */
        private int label_id_;

        private Coefficient[] coefs_ ;
        private int size_ ;

        /** the squared L2 norm */
        private double sq_l2norm_;

        /**
         * Constructor
         * @param label_id   label ID. label_id must be a positive number or 0.
         * @param sq_l2norm  the value of squared L2 norm. sq_l2norm must be positive.
         * @param num_labels the number of labels. num_labels must be more than 0.
         */
        SVCoefficient(int label_id, double sq_l2norm, int num_labels) {
            assert(label_id >= 0);
            assert(sq_l2norm >= 0.0);
            assert(num_labels > 0);
            label_id_ = label_id;
            sq_l2norm_ = sq_l2norm;
            coefs_       = new Coefficient[num_labels];
            size_        = 0 ;
        }

        /**
         * Adds a new support vector coefficient of the hyperplane: label_id vs. k.
         * @param k the label ID. k must be a positive number or 0.
         * @param coef the coefficient
         */
        void addCoefficient(int k, double coef) {
            assert(k >= 0);
            assert(size_ < coefs_.length);
            coefs_[size_++] = new Coefficient(k, coef);
        }

        private void sumKernelvalue(KernelFunction kernel, int ip, double sq_l2norm, double distance[]) {
            for(int i = 0 ; i < size_ ; i++){
                distance[coefs_[i].id_] += coefs_[i].coef_ * kernel.getValue(sq_l2norm_, ip, sq_l2norm);
            }
        }

        private void sumKernelvalue(KernelFunction kernel, double ip, double sq_l2norm, double distance[]) {
            for(int i = 0 ; i < size_ ; i++){
                distance[coefs_[i].id_] += coefs_[i].coef_ * kernel.getValue(sq_l2norm_, ip, sq_l2norm);
            }
        }
    }

    /**
     * Arguments for Classifiers
     */
    static class Arguments {
        int[] labels_;
        KernelFunction kernel_;
        HyperPlane[] hps_;
        SVCoefficient[] svcoefs_;
        Feature[] features_;
        Parameters params_;
        int default_label_ ;
        Arguments() {
            labels_ = null;
            hps_ = null;
            svcoefs_ = null;
            features_ = null;
            params_ = null;
            default_label_ = 0;
        }
        static Arguments createDummyArguments(){
            Arguments args = new Arguments();
            args.labels_ = new int[2]; args.labels_[0] = 1; args.labels_[1] = -1;
            args.hps_ = new HyperPlane[1]; args.hps_[0] = new HyperPlane(0, 1, 1);
            args.svcoefs_ = new SVCoefficient[1];
            args.svcoefs_[0] = new SVCoefficient(0, 1.0, 1); args.svcoefs_[0].addCoefficient(1, 1.0);
            args.features_ = new Feature[0];
            return args;
        }
    }

    // ClassifierImpl //

    /** the array of labels for classifications */
    private int[] labels_;

    /** the kernel function */
    private K kernel_;

    /** the information about hyperplanes */
    private HyperPlane[] hps_;

    /** the information about support vectors */
    private SVCoefficient[] svcoefs_;


    /** the set of training parameters */
    private Parameters params_;

    /** the features of support vectors */
    private Feature[] features_;

    /**
     * default label
     */
    private int default_label_ ;

    /**
     * Constructor
     *
     * @param args
     *            model arguments. throw IllegalArgumentException if args is null.<br>
     *            args.labels_ must not be null.<br>
     *            args.hps_ must not be null.<br>
     *            args.svs_ must not be null.<br>
     *            args.features_ must not be null.<br>
     *            args.params must not be null.<br>
     *
     */
    ClassifierImpl(Arguments args) {
        if (args == null)
            throw new IllegalArgumentException("the arguments is null.");
        assert(args.labels_   != null);
        assert(args.hps_      != null);
        assert(args.svcoefs_  != null);
        assert(args.params_   != null);
        assert(args.features_ != null);

        labels_ = args.labels_;
        assert(labels_.length > 1);
        hps_ = args.hps_;
        svcoefs_ = args.svcoefs_;
        params_ = args.params_;
        features_ = args.features_;
        default_label_ = args.default_label_;

    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        try {

            int n = in.readInt();
            if (n < 2)
                throw new InvalidObjectException("the number of labels is less than 2."); // OK
            labels_ = new int[n];
            for(int i = 0 ; i < n; i++ )
                labels_[i] = in.readInt();

            n = in.readInt();
            if (n < 1)
                throw new InvalidObjectException("the number of hyperplanes is less than 1."); // OK
            hps_ = new HyperPlane[n];
            for(int i = 0 ; i < n; i++ ){
                int pos = in.readInt();
                int neg = in.readInt();
                double bias  = in.readDouble();
                hps_[i] = new HyperPlane(pos, neg, bias);
            }

            n = in.readInt();

            if ( n < labels_.length - 1)
                throw new InvalidObjectException("the number of SVs is invalid."); // OK

            svcoefs_ = new SVCoefficient[n];

            for(int i = 0; i < n; i++){
                int label_id = in.readInt();
                double l2nsq = in.readDouble();
                int size = in.readInt();
                svcoefs_[i] = new SVCoefficient(label_id, l2nsq, size);
                for(int j = 0 ; j < size; j++ ){
                    int k = in.readInt();
                    double coef = in.readDouble();
                    svcoefs_[i].addCoefficient(k, coef);
                }
            }
            params_ = (Parameters) in.readObject();
        } catch (IllegalArgumentException e){
            throw new InvalidObjectException(e.getMessage()); // OK
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeInt(labels_.length);
        for(int i = 0 ; i < labels_.length; i++)
            out.writeInt(labels_[i]);
        out.writeInt(hps_.length);
        for(int i = 0; i < hps_.length; i++){
            out.writeInt(hps_[i].positive_);
            out.writeInt(hps_[i].negative_);
            out.writeDouble(hps_[i].bias_);
        }

        out.writeInt(svcoefs_.length);
        for(int i = 0; i < svcoefs_.length; i++){
            out.writeInt(svcoefs_[i].label_id_);
            out.writeDouble(svcoefs_[i].sq_l2norm_);
            out.writeInt(svcoefs_[i].size_);
            for(int j = 0 ; j < svcoefs_[i].size_; j++){
                out.writeInt(svcoefs_[i].coefs_[j].id_);
                out.writeDouble(svcoefs_[i].coefs_[j].coef_);
            }
        }
        out.writeObject(params_);
    }

    /**
     * Returns the label corresponding to the label ID
     *
     * @param labelID  the label ID.
     * @return the label ID.
     * @throws IllegalArgumentException if the label ID is negative, or greater than {@link #getNumberOfLabels()}-1.
     */
    final int getLabel(int labelID) {
        if (labelID < 0)
            throw new IllegalArgumentException("the label ID is negative.");

        if (labelID >= labels_.length)
            throw new IllegalArgumentException("the label ID is out of range.");

        return labels_[labelID];
    }

    /**
     * Returns the number of labels for classifications
     * @return the number of labels
     */
    final int getNumberOfLabels() {
        return labels_.length;
    }

    /**
     * A Linear Classifier Implementation for Binary Feature Space.
     */
    static class LinearBinary extends ClassifierImpl<KernelFunction.Linear, FeatureSpace.Binary> {

        private static final long serialVersionUID = 496882953148504188L;

        /** the features for support vectors */
        private Feature.Real[] features_;

        /**
         * Constructor
         *
         * @param args the classifier arguments. see {@link ClassifierImpl(Arguments)}.
         */
        LinearBinary(Arguments args) {
            super(args);
            features_ = (Feature.Real[]) super.features_;
            super.kernel_ = (Linear) args.kernel_;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
            try {
                super.kernel_ = (Linear) in.readObject();
                int n = in.readInt();
                if (n < 0)
                    throw new InvalidObjectException("the degree of feature space is negative."); // OK
                features_ = new Feature.Real[n];
                super.features_ = features_ ;
                for(int i = 0 ; i < n ; i++){
                    int id = in.readInt();
                    int capa = in.readInt();
                    features_[i] = new Feature.Real(id, capa);
                    for(int j = 0 ; j < capa; j++){
                        double value = in.readDouble();
                        int svid = in.readInt();
                        features_[i].addValue(value, svid);
                    }
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidObjectException(e.getMessage());
            }

        }

        private void writeObject(ObjectOutputStream out) throws IOException{
            out.writeObject(super.kernel_);
            out.writeInt(features_.length);
            for(int i = 0; i < features_.length; i++){
                out.writeInt(features_[i].id_);
                out.writeInt(features_[i].size_);
                for(int j = 0; j < features_[i].size_; j++){
                    out.writeDouble(features_[i].values_[j].value_);
                    out.writeInt(features_[i].values_[j].svid_);
                }
            }
        }

        /**
         * Calculates the inner products between the feature vector and each SV
         *
         * @param x the feature vector.
         * @param estimation the results for this estimation.
         */
        private void ips(BinaryFeatureVector x, Estimation estimation) {
            int i = 0;
            int i_max = x.size();
            int j = 0;
            int j_max = features_.length;

            int fi = 0;
            int fj = 0;
            Feature.Real rf = null;

            int[] x_ = x.getFeatureIDs();
            double[] ips = estimation.getDoubleArrayForInnerProducts();
            while (i < i_max && j < j_max) {
                fi = x_[i];
                rf = features_[j];
                fj = rf.id_;
                if (fi < fj) {
                    i++;
                } else if (fi > fj) {
                    j++;
                } else {
                    Value[] svids = rf.values_;
                    for (int k = 0; k < rf.size_; k++) {
                        ips[svids[k].getSvID()] += svids[k].getValue();
                    }
                    i++;
                    j++;
                }
            }
        }

        /**
         * Calculates the kernel values between the feature vector and each SV
         *
         * @param x the feature vector
         * @param estimation the results for this estimation.
         */
        private void kernel_values(BinaryFeatureVector x, Estimation estimation) {
            double[] dist = estimation.getDistances();
            double[] ips = estimation.getDoubleArrayForInnerProducts();
            for (int k = 0; k < dist.length; k++)
                dist[k] = -super.hps_[k].bias_;

            SVCoefficient[] svcoefs = super.svcoefs_;
            double sql2norm = x.getSquareOfL2Norm();
            for (int l = 0; l < ips.length; l++) {
                svcoefs[l].sumKernelvalue(super.kernel_, ips[l], sql2norm, dist);
            }
        }

        /**
         * Calculates pairwise predictions for all hyperplanes.
         *
         * @param estimation the results for this estimation.
         */
        private void predict_values(Estimation estimation) {
            Score.PairwiseScore score = estimation.getScore();
            double[] dist = estimation.getDistances();

            for (int k = 0; k < dist.length; k++) {
                int pos = super.hps_[k].positive_;
                int neg = super.hps_[k].negative_;
                score.addJudge(pos, neg, dist[k]);
            }
        }

        @Override
        public int classify(FeatureVector<Binary> x) {
            if (x == null)
                throw new IllegalArgumentException("the feature vector is null.");



            // calculating inner products for all SVs
            BinaryFeatureVector _x = (BinaryFeatureVector) x;
            Estimation estimation = _x.getEstimation(super.labels_.length, super.svcoefs_.length);

            ips(_x, estimation);

            // calculating kernel values for all SVs
            kernel_values(_x, estimation);

            // Pairwise
            predict_values(estimation);
            int y = getLabel(estimation.getScore().getBestLabelID());

            return y;
        }
    }

    /**
     * SVM Implementation for None Linear Kernel Functions in Binary Feature Space
     */
    static class NoneLinearBinary extends ClassifierImpl<KernelFunction.NoneLinear, FeatureSpace.Binary> {

        private static final long serialVersionUID = 5739868221057807257L;

        /** the features of SVs */
        private Feature.Binary[] features_;

        /**
         * Constructor
         *
         * @param args the classifier arguments. see {@link ClassifierImpl(Arguments)}.
         */
        NoneLinearBinary(Arguments args) {
            super(args);
            features_ = (Feature.Binary[]) super.features_;
            super.kernel_ = (NoneLinear) args.kernel_;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
            try {
                super.kernel_ = (NoneLinear) in.readObject();
                int n = in.readInt();
                if (n < 0)
                    throw new InvalidObjectException("the degree of feature space is negative.");  // OK
                features_ = new Feature.Binary[n];
                super.features_ = features_ ;
                for(int i = 0 ; i < n ; i++){
                    int id = in.readInt();
                    int capa = in.readInt();
                    features_[i] = new Feature.Binary(id, capa);
                    for(int j = 0 ; j < capa; j++){
                        int svid = in.readInt();
                        features_[i].addSvID(svid);
                    }
                }

            } catch (IllegalArgumentException e) {
                throw new InvalidObjectException(e.getMessage()); // OK
            }

        }

        private void writeObject(ObjectOutputStream out) throws IOException{
            out.writeObject(super.kernel_);
            out.writeInt(features_.length);
            for(int i = 0; i < features_.length; i++){
                out.writeInt(features_[i].id_);
                out.writeInt(features_[i].size_);
                for(int j = 0; j < features_[i].size_; j++){
                    out.writeInt(features_[i].svids_[j]);
                }
            }
        }
        /**
         * Calculates the inner products between the feature vector and each SV
         *
         * @param x the feature vector.
         * @param estimation the results for this estimation.
         */
        private void ips(BinaryFeatureVector x, Estimation estimation) {
            int i = 0;
            int i_max = x.size();
            int j = 0;
            int j_max = features_.length;

            int[] ips = estimation.getIntArrayForInnerProducts();
            int[] features = x.getFeatureIDs();

            while (i < i_max && j < j_max) {
                int fi = features[i];
                Feature.Binary bf = features_[j];
                int fj = bf.id_;

                if (fi < fj) {
                    i++;
                } else if (fi > fj) {
                    j++;
                } else {
                    for (int k = 0; k < bf.size_; k++) {
                        ips[bf.svids_[k]]++;
                    }
                    i++;
                    j++;
                }
            }
        }

        /**
         * Calculates the kernel values between the feature vector and each SV.
         *
         * @param x the feature vector.
         * @param estimation the results for this estimation.
         */
        private void kernel_values(BinaryFeatureVector x, Estimation estimation) {
            double[] dist = estimation.getDistances();
            int[] ips = estimation.getIntArrayForInnerProducts();
            for (int k = 0; k < dist.length; k++)
                dist[k] = -super.hps_[k].bias_;

            SVCoefficient[] svcoefs = super.svcoefs_;
            double sql2norm = x.getSquareOfL2Norm();

            for (int l = 0; l < ips.length; l++)
                svcoefs[l].sumKernelvalue(super.kernel_, ips[l], sql2norm, dist);
        }

        /**
         * Calculates the pairwise predictions for all hyperplanes.
         *
         * @param estimation the results for this estimation.
         */
        private void predict_values(Estimation estimation) {
            Score.PairwiseScore score = estimation.getScore();
            double[] dist = estimation.getDistances();

            for (int k = 0; k < dist.length; k++) {
                int pos = super.hps_[k].positive_;
                int neg = super.hps_[k].negative_;
                score.addJudge(pos, neg, dist[k]);
            }
        }

        @Override
        public int classify(FeatureVector<FeatureSpace.Binary> x) {
            if (x == null)
                throw new IllegalArgumentException("the feature vector is null.");

            // calculating inner products for all SVs
            BinaryFeatureVector _x = (BinaryFeatureVector) x;

            Estimation estimation = _x.getEstimation(super.labels_.length, super.svcoefs_.length);
            ips(_x, estimation);

            // calculating kernel values for all SVs
            kernel_values(_x, estimation);

            // Pairwise
            predict_values(estimation);
            int y = getLabel(estimation.getScore().getBestLabelID());
            // Estimation.release(estimation);
            return y;
        }
    }
    static class DefaultBinary extends ClassifierImpl<KernelFunction, FeatureSpace.Binary> {
        private static final long serialVersionUID = -2860113445090206789L;

        DefaultBinary(ClassifierImpl.Arguments args) {
            super(args);
        }

        @Override
        public int classify(FeatureVector<Binary> x) {
            if (x == null)
                throw new IllegalArgumentException("the feature vector is null.");
            return super.default_label_;
        }
    }
}
