package jp.gr.java_conf.dyama.rink.ml.svm;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface KernelFunction extends Serializable{

    interface NoneLinear extends KernelFunction {
    }

    /**
     * get Kernel Value: K(x0, x1)
     * @param sql2norm0 the value of squared L2 norm of x0.
     * @param ip  the value of inner products between x0 and x1.
     * @param sql2norm1 the value of squared L2 norm of x1.
     * @return kernel value
     */
    public double getValue(double sql2norm0, double ip, double sql2norm1);

    /**
     * Liner Kernel Function: x0 * x1
     * @author Hiroyasu Yamada
     */
    static final class Linear implements KernelFunction {

        private static final long serialVersionUID = -3772658840530263079L;

        Linear(){
        }
        @Override
        public double getValue(double sql2norm0, double ip, double sql2norm1) {
            return ip;
        }
    }

    /**
     * Polynomial Kernel Function: (gamma (x0 * x1) + coef0)^degree
     * @author Hiroyasu Yamada
     *
     */
    static final class Polynomial implements KernelFunction, NoneLinear {

        private static final long serialVersionUID = 8655765573688338731L;

        /** the parameter 'degree' */
        private int degree_;

        /** the parameter 'gamma' */
        private double gamma_;

        /** the parameter 'coef0' */
        private double coef0_;

        /**
         * Constructor:
         * @param degree the parameter of this function: (gamma (x0 * x1) + coef0)^degree. throw IllegalArgumentException if degree is less than 1.
         * @param gamma the parameter of this function: (gamma (x0 * x1) + coef0)^degree.
         * @param coef0 the parameter of this function: (gamma (x0 * x1) + coef0)^degree.
         */
        Polynomial(int degree, double gamma, double coef0){
            if (degree < 1)
                throw new IllegalArgumentException("the degree is less than 1.");
            degree_ = degree;
            gamma_  = gamma ;
            coef0_  = coef0;
        }
        @Override
        public double getValue(double sql2norm0, double ip, double sql2norm1) {
            double v = gamma_ * ip + coef0_ ;
            if (degree_ == 1)
                return v ;

            if (degree_ == 2)
                return v * v ;

            return Math.pow(v, degree_);
        }

        private void readObject(ObjectInputStream in) throws IOException{
            int d = in.readInt();
            if (d  < 1)
                throw new InvalidObjectException("the degree is less than 1.");
            degree_ = d ;
            gamma_ = in.readDouble();
            coef0_ = in.readDouble();

        }

        private void writeObject(ObjectOutputStream out) throws IOException{
            out.writeInt(degree_);
            out.writeDouble(gamma_);
            out.writeDouble(coef0_);
        }


    }

    /**
     * RBF Kernel: exp(-gamma |x0 - x1|^2) = exp( -gamma * (|x0|^2 - 2 x0 * x1 - |x1|^2)
     * @author Hiroyasu Yamada
     *
     */
    static final class RBF implements KernelFunction, NoneLinear {

        private static final long serialVersionUID = -2094063714235544409L;

        /** paramter 'gammna' */
        private double gamma_ ;

        /**
         * Constructor
         * @param gamma the parameter of this function: exp(-gamma |x0 - x1|^2).
         */
        RBF(double gamma){
            gamma_ = gamma;
        }

        @Override
        public double getValue(double sql2norm0, double ip, double sql2norm1) {
            return Math.exp(-gamma_ * (sql2norm0 - 2 * ip + sql2norm1));
        }

    }

    /**
     * Sigmoid Kernel: tanh( gamma* x0 * x1 + coef0)
     * @author Hiroyasu Yamada
     *
     */
    static final class Sigmoid implements KernelFunction, NoneLinear {

        private static final long serialVersionUID = 3419389631396516455L;

        /** the parameter 'gamma' */
        private double gamma_ ;

        /** the parameter 'coef0' */
        private double coef0_ ;

        /**
         * Constructor:
         * @param gamma the parameter of this function: tanh( gamma* x1 * x2 + coef0).
         * @param coef0 the parameter of this function: tanh( gamma* x1 * x2 + coef0).
         */
        Sigmoid(double gamma, double coef0){
            gamma_ = gamma;
            coef0_ = coef0;
        }
        @Override
        public double getValue(double sql2norm0, double ip, double sql2norm1) {
            return Math.tanh(gamma_ * ip + coef0_);
        }

    }
}
