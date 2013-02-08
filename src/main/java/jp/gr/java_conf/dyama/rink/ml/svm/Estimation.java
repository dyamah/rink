package jp.gr.java_conf.dyama.rink.ml.svm;

/**
 * Data structure for results of SVM estimations .
 * @author Hiroyasu Yamada
 *
 */
final class Estimation {

    /** the array for inner products (integer) */
    private int[] ips_int_;

    /** the array for inner products (double) */
    private double[] ips_double_;

    /** the array for distances form hyperplanes */
    private double[] distances_;

    /** the score for pairwise classifications */
    private Score.PairwiseScore score_;

    /**
     * Constructor:
     * @param classes the number of classes. The number of classes must be more than 0.
     * @param svs the number of support vectors. The number of support vectors must be 0 and over.
     */
    Estimation(int classes, int svs) {
        assert(classes > 0);
        assert(svs >=0 );
        ips_int_    = new int[svs];
        ips_double_ = new double[svs];
        distances_  = new double[ classes * (classes - 1) / 2 ];
        score_      = new Score.PairwiseScore(classes);
    }

    /**
     * Setup
     * @param classes the number of classes. The number of classes must be more than 0.
     * @param svs svs the number of support vectors. The number of support vectors must be 0 and over.
     */
    void setup(int classes, int svs){
        assert(classes > 0);
        assert(svs >= 0);

        int m = classes * (classes - 1) / 2 ;

        if (distances_.length != m)
            distances_ = new double[m];

        for(int i = 0 ;  i < distances_.length; i++)
            distances_[i] = 0.0;

        if (ips_double_.length != svs)
            ips_double_ = new double[svs];

        for(int i = 0 ;  i < ips_double_.length; i++)
            ips_double_[i] = 0.0;

        if (ips_int_.length != svs)
            ips_int_ = new int[svs];

        for(int i = 0 ;  i < ips_int_.length; i++)
            ips_int_[i] = 0;

        if (score_.getNumberOfLabels() != classes)
            score_ = new Score.PairwiseScore(classes);

        score_.clear();
    }

    /**
     * Returns the integer array for inner products.
     * @return the array
     */
    int[] getIntArrayForInnerProducts() {
        return ips_int_;
    }

    /**
     * Returns the double array for inner products.
     * @return the array
     */
    double[] getDoubleArrayForInnerProducts() {
        return ips_double_;
    }

    /**
     * Returns the array for distances from hyperplanes.
     * @return array
     */
    double[] getDistances() {
        return distances_;
    }

    Score.PairwiseScore getScore(){
        return score_ ;
    }
}
