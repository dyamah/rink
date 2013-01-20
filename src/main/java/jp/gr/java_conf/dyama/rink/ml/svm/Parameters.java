package jp.gr.java_conf.dyama.rink.ml.svm;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface Parameters extends Serializable {

    /**
     * Types of Kernel Functions
     * @author Hiroyasu Yamada
     *
     */
    public enum KernelType {

        /** linear kernel */
        LINEAR,

        /** polynomial kernel */
        POLYNOMIAL,

        /** RBF kernel */
        RBF,

        /** sigmoid kernel */
        SIGMOID
    }

    /**
     * set size of cache memory for learning.
     * @param size the size of cache memory [MB]. This value is at least 30.0 [MB] if the size is less than 30.0 [MB].
     */
    public void setCacheSize(double size);

    /**
     * set the parameter 'degree' of polynomial kernel.
     * @param degree the degree of polynomial kernel. throw IllegalArgumentException if the degree is less than 1.
     */
    public void setDegree(int degree);

    /**
     * set the parameter 'gamma' of kernel functions.
     * @param gamma the parameter gamma
     */
    public void setGamma(double gamma) ;

    /**
     * set the parameter 'coef0' of kernel functions.
     * @param coef0
     */
    public void setCoef0(double coef0);

    /**
     * set the parameter 'c' for soft margin.
     * @param c
     */
    public void setC(double c);

    /**
     * set the parameter 'Epsilon' for the threshold of convergence.
     * @param e epsilon
     */
    public void setEpsilon(double e);

    /**
     * set the type of kernel functions.
     * @param type the type of kernel function: {LINEAR, POLYNOMIAL, RBF, SIGMOID} is available, otherwise throw IllegalArgumentException.
     */
    public void setKernelType(KernelType type);

    /**
     * get the size of cache memory [MB].
     * @return the size of cache memory.
     */
    public double getCacheSize();

    /**
     * get the degree of polynomial kernel functions.
     * @return the degree of polynomial kernel functions.
     */
    public int getDegree();

    /**
     * get the parameter 'gamma' of kernel functions.
     * @return gamma
     */
    public double getGamma();

    /**
     * get the parameter 'coef0' of kernel functions.
     * @return coef0
     */
    public double getCoef0();

    /**
     * get the parameter 'C" for soft magin optimization.
     * @return C
     */
    public double getC();

    /**
     * get the parameter 'Epsilon' for the threshold of convergence.
     * @return Epsilon
     */
    public double getEpsilon();

    /**
     * get the type of kernel functions.
     * @return the type of kernel functions.
     */
    public KernelType getKernelType();

    /**
     * The implementation of Parameters
     * @author Hiroyasu Yamada
     *
     */
    public static class ParametersImpl implements Parameters {

        private static final long serialVersionUID = 7141127472174278931L;

        /** the size of cache memory */
        private double cache_;

        /** the degree for polyomial kernel functions. */
        private int degree_;

        /** the parameter 'gamma' of kernel functions. */
        private double gamma_;

        /** the parameter 'coef0' of kernel functions. */
        private double coef0_;

        /** the parameter 'C" for the soft-margin optimization */
        private double c_;

        /** the parameter 'Epsilon' for the threshold of convergence. */
        private double epsilon_;

        /** the type of kernel functions */
        private KernelType kernel_type_;

        /**
         * Default Constructor: each parameter is initialized to the libsvm's one except for gamma (see svm_train.java).
         * gamma is initialized to 0.0 ( 1/#features in libsvm).
         */
        public ParametersImpl(){
            cache_       = 100.0;
            degree_      = 3;
            gamma_       = 0.0;
            coef0_       = 0.0;
            c_           = 1.0;
            epsilon_     = 0.001;
            kernel_type_ = KernelType.RBF;
        }

        /**
         * create a new instance that the parameters are same to the argument's ones.
         * @param params source parameters.
         * @return clone of the params. return null if the params is null.
         */
        static ParametersImpl copy(Parameters params){
            if ( params == null)
                return null ;

            ParametersImpl clone = null;
            synchronized (params) {

                clone = new ParametersImpl();
                clone.setCacheSize(params.getCacheSize());
                clone.setDegree(params.getDegree());
                clone.setGamma(params.getGamma());
                clone.setCoef0(params.getCoef0());
                clone.setC(params.getC());
                clone.setEpsilon(params.getEpsilon());
                clone.setKernelType(params.getKernelType());
            }
            return clone;
        }

        @Override
        public void setCacheSize(double size){
            cache_ = size ;
            if (size < 30.0)
                cache_ = 30.0 ;
        }

        @Override
        public void setDegree(int degree){
            if (degree < 1)
                throw new IllegalArgumentException("the degree is less than 1.");
            degree_ = degree ;
        }

        @Override
        public void setGamma(double gamma){
            gamma_ = gamma ;
        }

        @Override
        public void setCoef0(double coef0){
            coef0_ = coef0;
        }

        @Override
        public void setC(double c){
            c_ = c ;
        }

        @Override
        public void setEpsilon(double e){
            epsilon_ = e ;
        }

        @Override
        public void setKernelType(KernelType type){
            kernel_type_ = type ;
        }

        @Override
        public double getCacheSize(){
            return cache_;
        }

        @Override
        public int getDegree(){
            return degree_;
        }

        @Override
        public double getGamma(){
            return gamma_;
        }

        @Override
        public double getCoef0(){
            return coef0_ ;
        }

        @Override
        public double getC(){
            return c_;
        }

        @Override
        public double getEpsilon(){
            return epsilon_ ;
        }

        @Override
        public KernelType getKernelType(){
            return kernel_type_ ;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
            cache_ = in.readDouble();
            if (cache_ < 30)
                throw new InvalidObjectException("the cache size is invalid.");

            degree_ = in.readInt();
            if (degree_ < 1)
                throw new InvalidObjectException("the degree is less than 1.");

            gamma_ = in.readDouble();
            coef0_ = in.readDouble();
            c_     = in.readDouble();
            epsilon_ = in.readDouble();
            kernel_type_ = (KernelType) in.readObject();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeDouble(cache_);
            out.writeInt(degree_);
            out.writeDouble(gamma_);
            out.writeDouble(coef0_);
            out.writeDouble(c_);
            out.writeDouble(epsilon_);
            out.writeObject(kernel_type_);
        }

    }
}
