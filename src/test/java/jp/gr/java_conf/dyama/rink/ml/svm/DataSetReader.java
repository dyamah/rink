package jp.gr.java_conf.dyama.rink.ml.svm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;

/**
 * Data Set Reader for Unit Tests.
 * @author Hiroyasu Yamada
 *
 */
public class DataSetReader {
    static final double E = 0.0000000000001;

    /**
     * Pair container
     * @author Hiroyasu Yamada
     *
     * @param <X>
     * @param <Y>
     */
    static class Pair<X, Y> {
        X x_ ;
        Y y_ ;
        Pair(){
            x_ = null ;
            y_ = null ;
        }
    }

    public DataSetReader() {

    }

    /**
     * read libsvm's data set from a file.
     * @param path file path. it must be not null.
     * @return libsvm's data set
     * @throws IOException
     */
    static libsvm.svm_problem read_problem(String path) throws IOException {
        BufferedReader fp = new BufferedReader(new FileReader(path));
        List<Double> vy = new ArrayList<Double>();
        List<libsvm.svm_node[]> vx = new ArrayList<libsvm.svm_node[]>();

        while(true)
        {
                String line = fp.readLine();
                if(line == null) break;

                StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
                Integer y = Integer.parseInt(st.nextToken());
                vy.add((double)y);
                int m = st.countTokens()/2;
                libsvm.svm_node[] x = new libsvm.svm_node[m];
                for(int j=0;j<m;j++)
                {
                        x[j] = new libsvm.svm_node();
                        x[j].index = Integer.parseInt(st.nextToken());
                        x[j].value = Double.parseDouble(st.nextToken());
                }
                vx.add(x);
        }

        libsvm.svm_problem prob = new libsvm.svm_problem();
        prob.l = vy.size();
        prob.x = new libsvm.svm_node[prob.l][];
        for(int i=0;i<prob.l;i++)
            prob.x[i] = vx.get(i);
        prob.y = new double[prob.l];
        for(int i=0;i<prob.l;i++)
            prob.y[i] = vy.get(i);

        fp.close();
        return prob ;
    }

    /**
     * read data set from a file.
     * @param path file path. it must be not null.
     * @return data set
     * @throws IOException
     */
    static List< Pair<BinaryFeatureVector, Integer> > read_dataset(String path) throws IOException{
        return convert(read_problem(path));
    }

    static boolean equals(libsvm.svm_problem problem, List< Pair<BinaryFeatureVector, Integer> > dataset){
        if (problem == null || dataset == null)
            throw new IllegalArgumentException("svm_problem is null, or dataset is null.");

        if (problem.l != dataset.size())
            return false;

        if (problem.y.length != dataset.size())
            return false;

        if (problem.x.length != dataset.size())
            return false;

        for(int l = 0 ; l < problem.l; l++){
            Pair<BinaryFeatureVector, Integer> e = dataset.get(l);
            double y = problem.y[l];
            libsvm.svm_node[] x  = problem.x[l];
            if (y != (double)e.y_)
                return false;

            if(x.length != e.x_.size())
                return false;

            for(int i = 0; i < x.length ; i++){
                if (x[i].index != e.x_.getFeatureID(i))
                    return false;
                if (x[i].value != 1.0)
                    return false;
            }

        }
        return true;
    }


    /**
     * Convert libsvm's data set set to sagittarius's data set.
     * @param problem libsvm's data set. it must be not null.
     * @return sagittarius's data set <br>
     * throw IllegalStateException if the feature space is not binary feature space.
     */
    static List< Pair<BinaryFeatureVector, Integer> > convert(libsvm.svm_problem problem){
        BinaryFeatureVector.Buffer buffer = new BinaryFeatureVector.Buffer();
        List< Pair<BinaryFeatureVector, Integer> > dataset = new ArrayList< Pair<BinaryFeatureVector, Integer > >();
        for(int l = 0; l < problem.l; l++){
            Pair<BinaryFeatureVector, Integer> e = new Pair<BinaryFeatureVector, Integer>();
            e.y_ = (int) problem.y[l];
            buffer.clear();
            libsvm.svm_node[] fv = problem.x[l];
            for(int i = 0 ; i < fv.length; i++){
                if (fv[i].value != 1.0)
                    throw new IllegalStateException("it's not binary feature space.");

                buffer.add(fv[i].index);
            }
            e.x_ = new BinaryFeatureVector();
            e.x_.reset(buffer);
            dataset.add(e);
        }
        return dataset ;
    }
}
