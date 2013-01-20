package jp.gr.java_conf.dyama.rink.ml.svm;

interface Score {

    /**
     * get the best label ( = high scored label). if there are tie scored labels, the smallest labelID (= label's ID itself)  is the best one.
     * throw IllegalStateException if the no judge has been added.
     * @return best labelID
     */
    int getBestLabelID();

    /**
     * clear score information.
     */
    void clear();

    /**
     * get the number of labels.
     * @return the number of labels
     */
    int getNumberOfLabels();


    /**
     * add a judge of a classifier
     * @param pos the label ID of positive label. throw IllegalArgumentException if pos is negative number.
     * @param neg the label ID of negative label. throw IllegalArgumentException if neg is pos and fewer.
     * @param dist a distance of  a classifier
     */
    void addJudge(int pos, int neg, double dist);

    /**
     * Pairwise score class for Multi-Class SVMs
     * @author Hiroyasu Yamada
     */
    static class PairwiseScore implements Score{

        /** the number of votes polled */
        private int[] score_ ;
        private boolean add_ ;

        /**
         * Constructor:
         * @param num the number of classes. throw IllegalArgumentException if num is 0 and fewer.
         */
        PairwiseScore(int num){
            if (num <= 0)
                throw new IllegalArgumentException("the number of classes is 0 and fewer.");
            score_ = new int[num];
            add_   = false;
        }

        @Override
        public int getBestLabelID() {
            if(add_ == false)
                throw new IllegalStateException("No judge has been added.");

            int best = 0;
            for(int i = 0 ; i < score_.length ; i++){
                if (score_[best] < score_[i])
                    best = i ;
            }
            return best ;
        }

        @Override
        public void clear() {
            for(int i = 0 ; i < score_.length; i++ )
                score_[i] = 0 ;
            add_ = false ;
        }

        @Override
        public void addJudge(int pos, int neg, double dist) {
            if (pos < 0 || neg < 0)
                throw new IllegalArgumentException("the label ID is negative.");

            if (neg <= pos)
                throw new IllegalArgumentException("the negative label ID is same to the positive label ID and fewer.");

            if (dist > 0.0){
                score_[pos] ++ ;
            } else {
                score_[neg] ++ ;
            }
            add_ = true ;
        }

        @Override
        public int getNumberOfLabels() {
            return score_.length;
        }
    }

}
