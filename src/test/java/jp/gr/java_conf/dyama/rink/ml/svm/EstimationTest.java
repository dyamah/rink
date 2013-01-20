package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.ml.svm.Estimation;
import jp.gr.java_conf.dyama.rink.ml.svm.Score;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EstimationTest {
    static final double E = 0.00000001;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEstimation(){
        {
            Estimation estimation = new Estimation(1, 0);
            double[]  dist = estimation.getDistances();
            double[]  double_array = estimation.getDoubleArrayForInnerProducts();
            int[]  int_array = estimation.getIntArrayForInnerProducts();
            Score score = estimation.getScore();

            assertEquals(0, dist.length);
            assertEquals(0, double_array.length);
            assertEquals(0, int_array.length);
            assertEquals(1, score.getNumberOfLabels());
        }

        {
            Estimation estimation = new Estimation(4, 7);
            double[]  dist = estimation.getDistances();
            double[]  double_array = estimation.getDoubleArrayForInnerProducts();
            int[]  int_array = estimation.getIntArrayForInnerProducts();
            Score score = estimation.getScore();

            assertEquals(6, dist.length);
            assertEquals(7, double_array.length);
            assertEquals(7, int_array.length);
            assertEquals(4, score.getNumberOfLabels());
        }
    }

    @Test
    public void testSetup(){

        Estimation estimation = new Estimation(1, 0);

        double[]  dist = estimation.getDistances();
        double[]  double_array = estimation.getDoubleArrayForInnerProducts();
        int[]  int_array = estimation.getIntArrayForInnerProducts();
        Score score = estimation.getScore();

        assertEquals(0, dist.length);
        assertEquals(0, double_array.length);
        assertEquals(0, int_array.length);
        assertEquals(1, score.getNumberOfLabels());

        estimation.setup(4, 7);
        dist = estimation.getDistances();
        double_array = estimation.getDoubleArrayForInnerProducts();
        int_array = estimation.getIntArrayForInnerProducts();
        score = estimation.getScore();


        assertEquals(6, dist.length);
        assertEquals(7, double_array.length);
        assertEquals(7, int_array.length);
        assertEquals(4, score.getNumberOfLabels());

        estimation.setup(1, 0);
        dist = estimation.getDistances();
        double_array = estimation.getDoubleArrayForInnerProducts();
        int_array = estimation.getIntArrayForInnerProducts();
        score = estimation.getScore();

        assertEquals(0, dist.length);
        assertEquals(0, double_array.length);
        assertEquals(0, int_array.length);
        assertEquals(1, score.getNumberOfLabels());

        estimation.setup(2, 8);
        dist = estimation.getDistances();
        double_array = estimation.getDoubleArrayForInnerProducts();
        int_array = estimation.getIntArrayForInnerProducts();
        score = estimation.getScore();

        assertEquals(1, dist.length);
        assertEquals(8, double_array.length);
        assertEquals(8, int_array.length);
        assertEquals(2, score.getNumberOfLabels());

        dist[0] = 1.1;
        for(int i = 0 ; i < 8; i++){
            double_array[i] = 1.1 + i;
            int_array[i] = 10 + i;
        }
        score.addJudge(0, 1, -1.1);
        score.addJudge(0, 1, -1.1);

        assertEquals(true, dist[0] != 0.0);
        for(int i = 0 ; i < 8; i++){
            assertEquals(true, double_array[i] != 0);
            assertEquals(true, int_array[i] != 0);
        }
        assertEquals(1, score.getBestLabelID());

        estimation.setup(2, 8);
        dist = estimation.getDistances();
        double_array = estimation.getDoubleArrayForInnerProducts();
        int_array = estimation.getIntArrayForInnerProducts();
        score = estimation.getScore();

        assertEquals(1, dist.length);
        assertEquals(8, double_array.length);
        assertEquals(8, int_array.length);
        assertEquals(2, score.getNumberOfLabels());

        assertEquals(true, dist[0] == 0.0);
        for(int i = 0 ; i < 8; i++){
            assertEquals(true, double_array[i] == 0);
            assertEquals(true, int_array[i] == 0);
        }
        try {
            score.getBestLabelID();
            fail("");
        } catch (IllegalStateException e) {
            assertEquals("No judge has been added.", e.getMessage());
        } catch (Exception e) {
            fail("");
        }
    }
}
