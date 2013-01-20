package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import jp.gr.java_conf.dyama.rink.ml.svm.BinaryFeatureVector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataSetReaderTest {
    static final double E = 0.000001;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRead_problem() throws IOException {
        new DataSetReader();
        {
            String path = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/bin.txt";
            libsvm.svm_problem prob = DataSetReader.read_problem(path);
            assertEquals(2, prob.l);
            assertEquals(2, prob.y.length);
            assertEquals(2, prob.x.length);

            assertEquals( 1.0, prob.y[0], E);
            assertEquals(-1.0, prob.y[1], E);

            assertEquals(3, prob.x[0].length);
            assertEquals(  1, prob.x[0][0].index);
            assertEquals(1.0, prob.x[0][0].value, E);

            assertEquals(  2, prob.x[0][1].index);
            assertEquals(1.0, prob.x[0][1].value, E);

            assertEquals(  3, prob.x[0][2].index);
            assertEquals(1.0, prob.x[0][2].value, E);

            assertEquals(2, prob.x[1].length);
            assertEquals(  1, prob.x[1][0].index);
            assertEquals(1.0, prob.x[1][0].value, E);
            assertEquals(  4, prob.x[1][1].index);
            assertEquals(1.0, prob.x[1][1].value, E);
        }

        {
            String path = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/real.txt";
            libsvm.svm_problem prob = DataSetReader.read_problem(path);
            assertEquals(2, prob.l);
            assertEquals(2, prob.y.length);
            assertEquals(2, prob.x.length);

            assertEquals( 1.0, prob.y[0], E);
            assertEquals(-1.0, prob.y[1], E);

            assertEquals(3, prob.x[0].length);
            assertEquals(  1, prob.x[0][0].index);
            assertEquals(1.0, prob.x[0][0].value, E);

            assertEquals(  2, prob.x[0][1].index);
            assertEquals(1.001, prob.x[0][1].value, E);

            assertEquals(  3, prob.x[0][2].index);
            assertEquals(1.0, prob.x[0][2].value, E);

            assertEquals(2, prob.x[1].length);
            assertEquals(  1, prob.x[1][0].index);
            assertEquals(1.0, prob.x[1][0].value, E);
            assertEquals(  4, prob.x[1][1].index);
            assertEquals(1.0, prob.x[1][1].value, E);
        }

    }


    @Test
    public void testEqualsSvm_problemListOfPairOfBinaryFeatureVectorInteger() throws IOException {

        String path1 = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/bin.txt";
        String path2 = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/real.txt";
        {
            libsvm.svm_problem prob = DataSetReader.read_problem(path1);
            List< DataSetReader.Pair<BinaryFeatureVector, Integer> > dataset = DataSetReader.read_dataset(path1);

            try {
                DataSetReader.equals(prob, null);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("svm_problem is null, or dataset is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

            try {
                DataSetReader.equals(null, dataset);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("svm_problem is null, or dataset is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

            try {
                DataSetReader.equals(null, null);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("svm_problem is null, or dataset is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        {
            libsvm.svm_problem prob = DataSetReader.read_problem(path1);
            List< DataSetReader.Pair<BinaryFeatureVector, Integer> > dataset = DataSetReader.read_dataset(path1);

            assertEquals(true, DataSetReader.equals(prob, dataset));

            prob.l = 1;
            assertEquals(false, DataSetReader.equals(prob, dataset));

            prob.l = 2;
            prob.y = new double[1];

            assertEquals(false, DataSetReader.equals(prob, dataset));
            prob.y = new double[2]; prob.y[0] = 1.0; prob.y[1] = -2.0;
            assertEquals(false, DataSetReader.equals(prob, dataset));
            prob.y[0] = 1.0; prob.y[1] = -1.0;

            libsvm.svm_node[][] x = prob.x;
            prob.x  = new libsvm.svm_node[1][];
            assertEquals(false, DataSetReader.equals(prob, dataset));
            prob.x = x ;

            libsvm.svm_node[] fv = prob.x[1] ;
            prob.x[1] = new libsvm.svm_node[1];
            assertEquals(false, DataSetReader.equals(prob, dataset));

            prob.x[1] = fv ;
            prob.x[1][0].index = 3 ;
            assertEquals(false, DataSetReader.equals(prob, dataset));

            prob.x[1] = fv ;
            prob.x[1][0].index = 1 ;
            prob.x[1][1].value = 1.1;
            assertEquals(false, DataSetReader.equals(prob, dataset));
        }

        {
            libsvm.svm_problem prob = DataSetReader.read_problem(path2);
            List< DataSetReader.Pair<BinaryFeatureVector, Integer> > dataset = DataSetReader.read_dataset(path1);
            assertEquals(false, DataSetReader.equals(prob, dataset));


        }

        {
            String train = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/train.txt";
            String test  = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/test.txt";

            {
                libsvm.svm_problem prob = DataSetReader.read_problem(train);
                List< DataSetReader.Pair<BinaryFeatureVector, Integer> > dataset = DataSetReader.read_dataset(train);
                assertEquals(true, DataSetReader.equals(prob, dataset));
            }

            {
                libsvm.svm_problem prob = DataSetReader.read_problem(test);
                List< DataSetReader.Pair<BinaryFeatureVector, Integer> > dataset = DataSetReader.read_dataset(test);
                assertEquals(true, DataSetReader.equals(prob, dataset));
            }
        }

    }

    @Test
    public void testConvert() throws IOException {
        {
            String path = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/bin.txt";
            libsvm.svm_problem prob = DataSetReader.read_problem(path);
            List< DataSetReader.Pair<BinaryFeatureVector, Integer> > dataset =  DataSetReader.convert(prob);

            assertEquals(2, dataset.size());
            {
                DataSetReader.Pair<BinaryFeatureVector, Integer> e = dataset.get(0);
                assertEquals(1, (int) e.y_);
                assertEquals(3, e.x_.size());
                assertEquals(1, e.x_.getFeatureID(0));
                assertEquals(2, e.x_.getFeatureID(1));
                assertEquals(3, e.x_.getFeatureID(2));
            }

            {
                DataSetReader.Pair<BinaryFeatureVector, Integer> e = dataset.get(1);
                assertEquals(-1, (int) e.y_);
                assertEquals(2, e.x_.size());
                assertEquals(1, e.x_.getFeatureID(0));
                assertEquals(4, e.x_.getFeatureID(1));
            }

        }

        {
            String path = "src/test/java/jp/gr/java_conf/dyama/rink/ml/svm/testcases/real.txt";
            libsvm.svm_problem prob = DataSetReader.read_problem(path);

            try {
                DataSetReader.convert(prob);
                fail("");
            } catch (IllegalStateException e){
                assertEquals("it's not binary feature space.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }


        }
    }

}
