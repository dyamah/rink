package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.ml.svm.Score;

import org.junit.Test;

public class ScoreTest {

    /**
     * [0] num >  1: No Exception
     * [1] num =  1: No Exception
     * [2] num <= 0: IllegalArgumentException
     **/
    @Test
    public void testPairwiseScore(){
        { // [0]
            Score score = new Score.PairwiseScore(2);
            assertEquals(2, score.getNumberOfLabels());
        }

        { // [1]
            Score score = new Score.PairwiseScore(1);
            assertEquals(1, score.getNumberOfLabels());
        }

        {
            try {
                new Score.PairwiseScore(0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the number of classes is 0 and fewer.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

            try {
                new Score.PairwiseScore(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the number of classes is 0 and fewer.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }
    }

    /**
     * [0] call after Constructor: IllegalStateException
     * [1] call after addJudge(): No Exception, return high scored label
     * [2] call after clear():   IllegalStateException
     * [3] tie case:  No Exception, return the smallest label in tie scoring classes.
     */
    @Test
    public void testGetBestLabel() {
        { // [0]
            Score score = new Score.PairwiseScore(3);
            try {
                score.getBestLabelID();
                fail("");
            } catch (IllegalStateException e){
                assertEquals("No judge has been added.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // [1]
            Score score = new Score.PairwiseScore(3);
            score.addJudge(0, 1,  1.0);
            score.addJudge(0, 2,  0.0001);
            score.addJudge(1, 2, -1.0);
            assertEquals(0, score.getBestLabelID());
        }

        { // [2]
            Score score = new Score.PairwiseScore(3);
            score.addJudge(0, 1,  1.0);
            score.addJudge(0, 2,  0.0001);
            score.addJudge(1, 2, -1.0);
            assertEquals(0, score.getBestLabelID());
            score.clear();
            try {
                score.getBestLabelID();
                fail("");
            } catch (IllegalStateException e){
                assertEquals("No judge has been added.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        {
            Score score = new Score.PairwiseScore(4);

            // 0:1, 1:2, 2:2, 3:1
            score.addJudge(1, 2,  1.0); // 1 > 2   1
            score.addJudge(1, 3,  1.0); // 1 > 3   1
            score.addJudge(2, 3,  1.0); // 2 > 3   2
            score.addJudge(0, 1,  1.0); // 0 > 1   0
            score.addJudge(0, 2, -1.0); // 0 < 2   2
            score.addJudge(0, 3, -1.0); // 0 < 3   3

            assertEquals(1, score.getBestLabelID());
        }

        {
            Score score = new Score.PairwiseScore(2);

            score.addJudge(0, 1,  0.0);
            assertEquals(1, score.getBestLabelID());
        }
    }

    @Test
    public void testClear() {
        Score score = new Score.PairwiseScore(3);

        score.addJudge(0, 1,  -1.0);
        score.addJudge(0, 2,  -1.0);
        score.addJudge(1, 2,   1.0);

        score.clear();

        score.addJudge(0, 1,  1.0);
        score.addJudge(0, 2, -1.0);
        score.addJudge(1, 2, -1.0);

        assertEquals(2, score.getBestLabelID());

        score.clear();
        try {
            score.getBestLabelID();
            fail("");
        } catch (IllegalStateException e){
            assertEquals("No judge has been added.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
    }


    /**
     * [0] pos >= 0,  pos < neg  : No Exception
     * [1] pos < 0    : IllegalArgumentException
     * [2] neg < 0    : IllegalArgumentException
     * [3] pos == neg : IllegalArgumentException
     * [4] pos > neg  : IllegalArgumentException
     */
    @Test
    public void testAddJudge() {
        Score score = new Score.PairwiseScore(3);
        { // [0]
            score.addJudge(0, 1, 1.0);
        }

        { // [1]
            try {
                score.addJudge(-1, 0, 0.0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the label ID is negative.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // [2]
            try {
                score.addJudge(0, -1, 0.0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the label ID is negative.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // [3]
            try {
                score.addJudge(0, 0, 0.0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the negative label ID is same to the positive label ID and fewer.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        { // [4]
            try {
                score.addJudge(1, 0, 1.0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the negative label ID is same to the positive label ID and fewer.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }
    }

}
