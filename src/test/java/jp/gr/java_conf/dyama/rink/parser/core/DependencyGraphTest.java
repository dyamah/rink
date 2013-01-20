package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyGraph;
import jp.gr.java_conf.dyama.rink.parser.core.SentenceImpl;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DependencyGraphTest {

    SentenceImpl sentence0_;
    SentenceImpl sentence1_;
    SentenceImpl sentence_;
    WordImpl.Generator generator_;

    @Before
    public void setUp() throws Exception {
        generator_ = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());
        sentence0_ = new SentenceImpl(generator_, null);
        sentence1_ = new SentenceImpl(generator_, null);
        sentence1_.addWord(generator_.generate("Good", 0, 4, PTB.POS.RB, -1, null));

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
    public void testDependencyGraph() {
        DependencyGraph deps = new DependencyGraph();
        assertEquals(0, deps.size());
        assertEquals(null, deps.getNode(-1));
        assertEquals(null, deps.getNode(0));
        assertEquals(null, deps.getNode(1));
        assertEquals(null, deps.getRoot());
        for (int i = -1; i < 101; i++) {
            for (int j = -1; j < 101; j++) {
                assertEquals(false, deps.hasDependencyRelation(i, j));
            }
        }

    }

    @Test
    public void testSetup() {
        DependencyGraph deps = new DependencyGraph();
        assertEquals(0, deps.size());
        assertEquals(null, deps.getNode(-1));
        assertEquals(null, deps.getNode(0));
        assertEquals(null, deps.getNode(1));
        assertEquals(null, deps.getRoot());

        {
            deps.setup(sentence0_);
            assertEquals(0, deps.size());
            assertEquals(null, deps.getNode(-1));
            assertEquals(null, deps.getNode(0));
            assertEquals(null, deps.getNode(1));
            assertEquals(null, deps.getRoot());
            for (int i = -1; i < 101; i++) {
                for (int j = -1; j < 101; j++) {
                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
        }

        {
            deps.setup(sentence1_);
            assertEquals(1, deps.size());
            assertEquals(null, deps.getNode(-1));
            assertEquals(true, deps.getNode(0) != null);
            assertEquals(0, deps.getNode(0).getID());
            assertEquals(null, deps.getNode(1));
            assertEquals(null, deps.getRoot());
            for (int i = -1; i < 101; i++) {
                for (int j = -1; j < 101; j++) {
                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
        }

        {
            deps.setup(sentence_);
            assertEquals(7, deps.size());
            assertEquals(null, deps.getNode(-1));
            assertEquals(true, deps.getNode(0) != null);
            assertEquals(0, deps.getNode(0).getID());

            assertEquals(true, deps.getNode(1) != null);
            assertEquals(1, deps.getNode(1).getID());

            assertEquals(true, deps.getNode(2) != null);
            assertEquals(2, deps.getNode(2).getID());

            assertEquals(true, deps.getNode(3) != null);
            assertEquals(3, deps.getNode(3).getID());

            assertEquals(true, deps.getNode(4) != null);
            assertEquals(4, deps.getNode(4).getID());

            assertEquals(true, deps.getNode(5) != null);
            assertEquals(5, deps.getNode(5).getID());

            assertEquals(true, deps.getNode(6) != null);
            assertEquals(6, deps.getNode(6).getID());

            assertEquals(null, deps.getNode(7));
            assertEquals(null, deps.getRoot());

            for (int i = -1; i < 101; i++) {
                for (int j = -1; j < 101; j++) {
                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
        }
    }

    @Test
    public void testBuildDependencies() {
        DependencyGraph deps = new DependencyGraph();
        assertEquals(0, deps.size());
        assertEquals(null, deps.getNode(-1));
        assertEquals(null, deps.getNode(0));
        assertEquals(null, deps.getNode(1));
        assertEquals(null, deps.getRoot());

        {
            deps.buildDependencies(sentence0_);
            assertEquals(0, deps.size());
            assertEquals(null, deps.getNode(-1));
            assertEquals(null, deps.getNode(0));
            assertEquals(null, deps.getNode(1));
            assertEquals(null, deps.getRoot());
            for (int i = -1; i < 101; i++) {
                for (int j = -1; j < 101; j++) {
                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
        }

        {
            deps.buildDependencies(sentence1_);
            assertEquals(1, deps.size());
            assertEquals(null, deps.getNode(-1));
            assertEquals(true, deps.getNode(0) != null);
            assertEquals(0, deps.getNode(0).getID());
            assertEquals(null, deps.getNode(1));
            assertEquals(true, deps.getRoot() != null);
            assertEquals(0, deps.getRoot().getID());

            for (int i = -1; i < 101; i++) {
                for (int j = -1; j < 101; j++) {
                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
        }

        {
            deps.buildDependencies(sentence_);
            assertEquals(7, deps.size());
            assertEquals(null, deps.getNode(-1));
            assertEquals(true, deps.getNode(0) != null);
            assertEquals(0, deps.getNode(0).getID());

            assertEquals(true, deps.getNode(1) != null);
            assertEquals(1, deps.getNode(1).getID());

            assertEquals(true, deps.getNode(2) != null);
            assertEquals(2, deps.getNode(2).getID());

            assertEquals(true, deps.getNode(3) != null);
            assertEquals(3, deps.getNode(3).getID());

            assertEquals(true, deps.getNode(4) != null);
            assertEquals(4, deps.getNode(4).getID());

            assertEquals(true, deps.getNode(5) != null);
            assertEquals(5, deps.getNode(5).getID());

            assertEquals(true, deps.getNode(6) != null);
            assertEquals(6, deps.getNode(6).getID());

            assertEquals(null, deps.getNode(7));
            assertEquals(true, deps.getRoot() != null);
            assertEquals(1, deps.getRoot().getID());

            assertEquals(true, deps.hasDependencyRelation(1, 0));
            assertEquals(true, deps.hasDependencyRelation(1, 3));
            assertEquals(true, deps.hasDependencyRelation(1, 4));
            assertEquals(true, deps.hasDependencyRelation(3, 2));
            assertEquals(true, deps.hasDependencyRelation(6, 5));
            assertEquals(true, deps.hasDependencyRelation(4, 6));

            for (int i = -1; i < 101; i++) {
                for (int j = -1; j < 101; j++) {
                    if ((i == 1 && j == 0) || (i == 1 && j == 3) || (i == 1 && j == 4) || (i == 3 && j == 2) || (i == 6 && j == 5) || (i == 4 && j == 6))
                        continue;

                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
        }

    }

    @Test
    public void testDepend() {
        DependencyGraph deps = new DependencyGraph();
        assertEquals(0, deps.size());
        assertEquals(null, deps.getNode(-1));
        assertEquals(null, deps.getNode(0));
        assertEquals(null, deps.getNode(1));
        assertEquals(null, deps.getRoot());
        deps.setup(sentence1_);

        try {
            deps.depend(0, 0, 1.0);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the parent ID is same to the child ID.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        try {
            deps.depend(-1, 0, 1.0);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the parent ID is out of range.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        try {
            deps.depend(1, 0, 1.0);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the parent ID is out of range.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        try {
            deps.depend(0, -1, 1.0);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the child ID is out of range.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        try {
            deps.depend(0, 1, 1.0);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the child ID is out of range.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

        deps.setup(sentence_);
        for (int i = -1; i < 101; i++) {
            for (int j = -1; j < 101; j++) {
                assertEquals(false, deps.hasDependencyRelation(i, j));
            }
        }

        deps.depend(2, 3, 1.0);
        for (int i = -1; i < 101; i++) {
            for (int j = -1; j < 101; j++) {
                if (i == 2 && j == 3) {
                    assertEquals(true, deps.hasDependencyRelation(i, j));
                } else {
                    assertEquals(false, deps.hasDependencyRelation(i, j));
                }
            }
        }
    }

    @Test
    public void testGetNode() {
        DependencyGraph deps = new DependencyGraph();
        assertEquals(0, deps.size());

        deps.setup(sentence_);
        assertEquals(7, deps.size());
        assertEquals(null, deps.getNode(-2));
        assertEquals(null, deps.getNode(-1));
        assertEquals(true, deps.getNode(0) != null);
        assertEquals(0, deps.getNode(0).getID());

        assertEquals(true, deps.getNode(1) != null);
        assertEquals(1, deps.getNode(1).getID());

        assertEquals(true, deps.getNode(2) != null);
        assertEquals(2, deps.getNode(2).getID());

        assertEquals(true, deps.getNode(3) != null);
        assertEquals(3, deps.getNode(3).getID());

        assertEquals(true, deps.getNode(4) != null);
        assertEquals(4, deps.getNode(4).getID());

        assertEquals(true, deps.getNode(5) != null);
        assertEquals(5, deps.getNode(5).getID());

        assertEquals(true, deps.getNode(6) != null);
        assertEquals(6, deps.getNode(6).getID());

        assertEquals(null, deps.getNode(7));
        assertEquals(null, deps.getNode(8));
        assertEquals(null, deps.getRoot());

        for (int i = -1; i < 101; i++) {
            for (int j = -1; j < 101; j++) {
                assertEquals(false, deps.hasDependencyRelation(i, j));
            }
        }

    }

    @Test
    public void testSize() {

        DependencyGraph deps = new DependencyGraph();
        assertEquals(0, deps.size());

        deps.setup(sentence0_);
        assertEquals(0, deps.size());

        deps.setup(sentence1_);
        assertEquals(1, deps.size());

        deps.setup(sentence_);
        assertEquals(7, deps.size());
    }

}
