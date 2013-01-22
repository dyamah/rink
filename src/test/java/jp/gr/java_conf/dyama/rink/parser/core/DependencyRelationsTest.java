package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyRelations;
import jp.gr.java_conf.dyama.rink.parser.core.SentenceImpl;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DependencyRelationsTest {
    SentenceImpl sentence_;
    WordImpl.Generator generator_;

    @Before
    public void setUp() throws Exception {
        generator_ = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());

        sentence_ = new SentenceImpl(generator_, null);
        sentence_.addWord(generator_.generate("I", 0, 1, PTB.POS.PRP, 1, null));
        sentence_.addWord(generator_.generate("saw", 1, 4, PTB.POS.VBD, -1, null));
        sentence_.addWord(generator_.generate("a", 5, 6, PTB.POS.DT, 3, null));
        sentence_.addWord(generator_.generate("girl", 7, 11, PTB.POS.NN, 1, null));
        sentence_.addWord(generator_.generate("with", 12, 16, PTB.POS.IN, 1, null));
        sentence_.addWord(generator_.generate("a", 17, 18, PTB.POS.DT, 6, null));
        sentence_.addWord(generator_.generate("telescope", 19, 28, PTB.POS.NN, 4, null));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDependencyRelations() {
        DependencyRelations deps = new DependencyRelations();
        assertEquals(0, deps.size());
    }

    @Test
    public void testSetup() {


        {
            DependencyRelations deps = new DependencyRelations();
            try {
                deps.setup(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the sentence is null.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            SentenceImpl sentence1023 = new SentenceImpl(generator_, null);
            SentenceImpl sentence1024 = new SentenceImpl(generator_, null);
            for(int i = 0; i < 1023; i++){
                int parent = 0;
                if (i == 0)
                    parent = -1;
                sentence1023.addWord(generator_.generate("I", i, i+1, PTB.POS.PRP, parent, null));
            }

            for(int i = 0; i < 1024; i++){
                int parent = 0;
                if (i == 0)
                    parent = -1;
                sentence1024.addWord(generator_.generate("I", i, i+1, PTB.POS.PRP, parent, null));
            }
            deps.setup(sentence1023);
            assertEquals(1023, deps.size());
            try {
                deps.setup(sentence1024);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the size of sentence is over the capacity.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
        }
        {
            DependencyRelations deps = new DependencyRelations();
            assertEquals(0, deps.size());
            deps.setup(sentence_);
            assertEquals(7, deps.size());
            assertEquals(-1, deps.getParentID(0));
            assertEquals(-1, deps.getParentID(1));
            assertEquals(-1, deps.getParentID(2));
            assertEquals(-1, deps.getParentID(3));
            assertEquals(-1, deps.getParentID(4));
            assertEquals(-1, deps.getParentID(5));
            assertEquals(-1, deps.getParentID(6));
        }

    }

    @Test
    public void testBuild() {
        {
            DependencyRelations deps = new DependencyRelations();
            try {
                deps.build(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the sample is null.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            SentenceImpl sentence1023 = new SentenceImpl(generator_, null);
            SentenceImpl sentence1024 = new SentenceImpl(generator_, null);
            for(int i = 0; i < 1023; i++){
                int parent = 0;
                if (i == 0)
                    parent = -1;
                sentence1023.addWord(generator_.generate("I", i, i+1, PTB.POS.PRP, parent, null));
            }

            for(int i = 0; i < 1024; i++){
                int parent = 0;
                if (i == 0)
                    parent = -1;
                sentence1024.addWord(generator_.generate("I", i, i+1, PTB.POS.PRP, parent, null));
            }
            deps.build(sentence1023);
            assertEquals(1023, deps.size());
            try {
                deps.build(sentence1024);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the size of sentence is over the capacity.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
        }
        {
            DependencyRelations deps = new DependencyRelations();
            assertEquals(0, deps.size());
            deps.build(sentence_);
            assertEquals(7, deps.size());
            assertEquals( 1, deps.getParentID(0));
            assertEquals(-1, deps.getParentID(1));
            assertEquals( 3, deps.getParentID(2));
            assertEquals( 1, deps.getParentID(3));
            assertEquals( 1, deps.getParentID(4));
            assertEquals( 6, deps.getParentID(5));
            assertEquals( 4, deps.getParentID(6));
        }
    }

    @Test
    public void testCopy() {
        DependencyRelations deps = new DependencyRelations();
        try {
            deps.copy(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the source is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }
        DependencyRelations src = new DependencyRelations();
        assertEquals(0, deps.size());
        src.setup(sentence_);
        assertEquals(7, src.size());
        deps.copy(src);

        assertEquals(-1, deps.getParentID(0));
        assertEquals(-1, deps.getParentID(1));
        assertEquals(-1, deps.getParentID(2));
        assertEquals(-1, deps.getParentID(3));
        assertEquals(-1, deps.getParentID(4));
        assertEquals(-1, deps.getParentID(5));
        assertEquals(-1, deps.getParentID(6));

        SentenceImpl sentence0 = new SentenceImpl(generator_, null);
        src.setup(sentence0);
        assertEquals(0, src.size());
        assertEquals(7, deps.size());
        deps.copy(src);
        assertEquals(0, deps.size());
        src.build(sentence_);
        assertEquals(7, src.size());
        deps.copy(src);
        assertEquals( 1, deps.getParentID(0));
        assertEquals(-1, deps.getParentID(1));
        assertEquals( 3, deps.getParentID(2));
        assertEquals( 1, deps.getParentID(3));
        assertEquals( 1, deps.getParentID(4));
        assertEquals( 6, deps.getParentID(5));
        assertEquals( 4, deps.getParentID(6));
    }

    @Test
    public void testHasDependencyRelation() {
        {
            DependencyRelations deps = new DependencyRelations();
            try {
                deps.hasDependencyRelation(0, 1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
        }
        {
            DependencyRelations deps = new DependencyRelations();
            deps.setup(sentence_);
            assertEquals(7, deps.size());
            try {
                deps.hasDependencyRelation(-1, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.hasDependencyRelation(7, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.hasDependencyRelation(0, -1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the child ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.hasDependencyRelation(0, 7);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the child ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            for(int i = 0 ; i < 7; i++){
                for(int j = 0 ; j < 7; j++){
                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
        }

        {
            DependencyRelations deps = new DependencyRelations();
            deps.build(sentence_);
            assertEquals(7, deps.size());
            try {
                deps.hasDependencyRelation(-1, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.hasDependencyRelation(7, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.hasDependencyRelation(0, -1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the child ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.hasDependencyRelation(0, 7);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the child ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }


            for(int i = 0 ; i < 7; i++){
                for(int j = 0 ; j < 7; j++){
                    if ((i == 1 && j == 0) ||
                            (i == 1 && j == 3) ||
                            (i == 1 && j == 4) ||
                            (i == 3 && j == 2) ||
                            (i == 6 && j == 5) ||
                            (i == 4 && j == 6)){
                        assertEquals( true, deps.hasDependencyRelation(i, j));
                    } else {
                        assertEquals( false, deps.hasDependencyRelation(i, j));
                    }
                }
            }
        }
    }

    @Test
    public void testDepend() {
        {
            DependencyRelations deps = new DependencyRelations();
            assertEquals(0, deps.size());
            try {
                deps.depend(-1, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            try {
                deps.depend(0, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.depend(1, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

        }

        {
            DependencyRelations deps = new DependencyRelations();
            deps.setup(sentence_);
            assertEquals(7, deps.size());

            try {
                deps.depend(-1, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            try {
                deps.depend(7, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            try {
                deps.depend(0, -1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the child ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            try {
                deps.depend(0, 7);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the child ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.depend(0, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is same to the child's one.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.depend(6, 6);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the parent ID is same to the child's one.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            assertEquals(false, deps.hasDependencyRelation(0, 1));
            deps.depend(0, 1);
            assertEquals(true, deps.hasDependencyRelation(0, 1));

            assertEquals(false, deps.hasDependencyRelation(2, 3));
            deps.depend(2, 3);
            assertEquals(true, deps.hasDependencyRelation(2, 3));

        }



    }

    @Test
    public void testGetChildID() {
        {
            DependencyRelations deps = new DependencyRelations();
            deps.build(sentence_);
            List<Integer> actuals = new ArrayList<Integer>();

            assertEquals(7, deps.size());
            try {
                deps.getChildID(-1, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the node ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.getChildID(7, 0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the node ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }



            {
                actuals.clear();
                int[] expected = {};
                for(int i = 0; i < deps.getNumberOfChildren(0); i++)
                    actuals.add(deps.getChildID(0, i));
                assertEquals(0, expected.length);
                assertEquals(expected.length, actuals.size());
                for(int i = 0; i < expected.length; i++)
                    assertEquals(expected[i], (int)actuals.get(i));
            }

            {
                actuals.clear();
                int[] expected = {0, 3, 4};
                for(int i = 0; i < deps.getNumberOfChildren(1); i++)
                    actuals.add(deps.getChildID(1, i));
                assertEquals(3, expected.length);
                assertEquals(expected.length, actuals.size());
                for(int i = 0; i < expected.length; i++)
                    assertEquals(expected[i], (int)actuals.get(i));
            }

            {
                actuals.clear();
                int[] expected = {};
                for(int i = 0; i < deps.getNumberOfChildren(2); i++)
                    actuals.add(deps.getChildID(2, i));
                assertEquals(0, expected.length);
                assertEquals(expected.length, actuals.size());
                for(int i = 0; i < expected.length; i++)
                    assertEquals(expected[i], (int)actuals.get(i));
            }

            {
                actuals.clear();
                int[] expected = {2};
                for(int i = 0; i < deps.getNumberOfChildren(3); i++)
                    actuals.add(deps.getChildID(3, i));

                assertEquals(1, expected.length);
                assertEquals(expected.length, actuals.size());
                for(int i = 0; i < expected.length; i++)
                    assertEquals(expected[i], (int)actuals.get(i));
            }

            {
                actuals.clear();
                int[] expected = {6};
                for(int i = 0; i < deps.getNumberOfChildren(4); i++)
                    actuals.add(deps.getChildID(4, i));

                assertEquals(1, expected.length);
                assertEquals(expected.length, actuals.size());
                for(int i = 0; i < expected.length; i++)
                    assertEquals(expected[i], (int)actuals.get(i));
            }

            {
                actuals.clear();
                int[] expected = {};
                for(int i = 0; i < deps.getNumberOfChildren(5); i++)
                    actuals.add(deps.getChildID(5, i));

                assertEquals(0, expected.length);
                assertEquals(expected.length, actuals.size());
                for(int i = 0; i < expected.length; i++)
                    assertEquals(expected[i], (int)actuals.get(i));
            }

            {
                actuals.clear();
                int[] expected = {5};
                for(int i = 0; i < deps.getNumberOfChildren(6); i++)
                    actuals.add(deps.getChildID(6, i));

                assertEquals(1, expected.length);
                assertEquals(expected.length, actuals.size());
                for(int i = 0; i < expected.length; i++)
                    assertEquals(expected[i], (int)actuals.get(i));
            }
        }
        {
            DependencyRelations deps = new DependencyRelations();
            deps.setup(sentence_);
            assertEquals(7, deps.size());
            assertEquals(0, deps.getNumberOfChildren(3));

            deps.depend(3, 1);
            deps.depend(3, 0);
            deps.depend(3, 5);
            deps.depend(3, 4);
            deps.depend(3, 6);
            deps.depend(3, 2);

            assertEquals(6, deps.getNumberOfChildren(3));
            assertEquals(0, deps.getChildID(3, 0));
            assertEquals(1, deps.getChildID(3, 1));
            assertEquals(2, deps.getChildID(3, 2));
            assertEquals(4, deps.getChildID(3, 3));
            assertEquals(5, deps.getChildID(3, 4));
            assertEquals(6, deps.getChildID(3, 5));


        }
    }

    @Test
    public void testGetParentID() {
        {
            DependencyRelations deps = new DependencyRelations();
            deps.build(sentence_);
            assertEquals(7, deps.size());
            try {
                deps.getParentID(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the node ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.getParentID(7);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the node ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            assertEquals( 1, deps.getParentID(0));
            assertEquals(-1, deps.getParentID(1));
            assertEquals( 3, deps.getParentID(2));
            assertEquals( 1, deps.getParentID(3));
            assertEquals( 1, deps.getParentID(4));
            assertEquals( 6, deps.getParentID(5));
            assertEquals( 4, deps.getParentID(6));
        }

        {
            DependencyRelations deps = new DependencyRelations();
            deps.setup(sentence_);
            assertEquals(7, deps.size());
            try {
                deps.getParentID(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the node ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                deps.getParentID(7);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the node ID is out of range.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            assertEquals(-1, deps.getParentID(0));
            assertEquals(-1, deps.getParentID(1));
            assertEquals(-1, deps.getParentID(2));
            assertEquals(-1, deps.getParentID(3));
            assertEquals(-1, deps.getParentID(4));
            assertEquals(-1, deps.getParentID(5));
            assertEquals(-1, deps.getParentID(6));
        }
    }

    @Test
    public void testIsSameChildren() {
        DependencyRelations deps0 = new DependencyRelations();
        DependencyRelations deps1 = new DependencyRelations();
        deps1.build(sentence_);
        DependencyRelations deps2 = new DependencyRelations();
        deps2.build(sentence_);
        DependencyRelations deps3 = new DependencyRelations();
        deps3.setup(sentence_);
        deps3.depend(1, 3);
        deps3.depend(1, 4);
        deps3.depend(3, 2);
        deps3.depend(6, 5);

        assertEquals(7, deps1.size());
        try {
            deps1.isSameChildren(-1, deps0);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the node ID is out of range.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        try {
            deps1.isSameChildren(7, deps0);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the node ID is out of range.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        try {
            deps1.isSameChildren(0, null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the dependency relations is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        assertEquals(true, deps1.isSameChildren(0, deps2));
        assertEquals(true, deps1.isSameChildren(1, deps2));
        assertEquals(true, deps1.isSameChildren(2, deps2));
        assertEquals(true, deps1.isSameChildren(3, deps2));
        assertEquals(true, deps1.isSameChildren(4, deps2));
        assertEquals(true, deps1.isSameChildren(5, deps2));
        assertEquals(true, deps1.isSameChildren(6, deps2));

        assertEquals(false, deps1.isSameChildren(0, deps0));
        assertEquals(false, deps1.isSameChildren(1, deps0));
        assertEquals(false, deps1.isSameChildren(2, deps0));
        assertEquals(false, deps1.isSameChildren(3, deps0));
        assertEquals(false, deps1.isSameChildren(4, deps0));
        assertEquals(false, deps1.isSameChildren(5, deps0));
        assertEquals(false, deps1.isSameChildren(6, deps0));

        assertEquals( true, deps1.isSameChildren(0, deps3));
        assertEquals(false, deps1.isSameChildren(1, deps3));
        assertEquals( true, deps1.isSameChildren(2, deps3));
        assertEquals( true, deps1.isSameChildren(3, deps3));
        assertEquals(false, deps1.isSameChildren(4, deps3));
        assertEquals( true, deps1.isSameChildren(5, deps3));
        assertEquals( true, deps1.isSameChildren(6, deps3));
    }

}
