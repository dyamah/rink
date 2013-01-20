package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.parser.core.BinaryTrie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BinaryTrieTest {
    static final double E = 0.000000001;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testGetSet00() {
        BinaryTrie trie = new BinaryTrie();
        int[] x0 = {1, 2, 3};
        int[] x1 = {1, 2, 3, 4};
        int[] x2 = {1, 1, 2, 3};
        int[] x3 = {7, 2, 3};

        assertEquals( 3, trie.set(x0, 1.0));
        assertEquals( 4, trie.set(x1, 2.0));
        assertEquals( 7, trie.set(x2, 3.0));
        assertEquals(10, trie.set(x3, 4.0));


        int[] x4 = {1, 2, 3};
        int[] x5 = {1, 2, 3, 4};
        int[] x6 = {1, 1, 2, 3};
        int[] x7 = {7, 2, 3};
        int[] x8 = {1, 2};
        int[] x9 = {8, 2, 3};


        assertEquals( 1.0, trie.get(x4), E);
        assertEquals( 2.0, trie.get(x5), E);
        assertEquals( 3.0, trie.get(x6), E);
        assertEquals( 4.0, trie.get(x7), E);
        assertEquals( 0.0, trie.get(x8), E);
        assertEquals( 0.0, trie.get(x9), E);

        int[] x10 = {1, 2};
        int[] x11 = {1, 1, 2};

        assertEquals( 2, trie.set(x10, 10.0));
        assertEquals( 6, trie.set(x11, 11.0));

        assertEquals( 1.0, trie.get(x4), E);
        assertEquals( 2.0, trie.get(x5), E);
        assertEquals( 3.0, trie.get(x6), E);
        assertEquals( 4.0, trie.get(x7), E);
        assertEquals(10.0, trie.get(x8), E);
        assertEquals( 0.0, trie.get(x9), E);
        assertEquals(10.0, trie.get(x10), E);
        assertEquals(11.0, trie.get(x11), E);

        int[] x12 = {};
        assertEquals(-1, trie.set(x12, 1.0));
        assertEquals(0.0, trie.get(x12), E);

    }

    @Test
    public void testGetSet01() {
        int[] x0 = new int[10];
        for(int i = 0 ; i < x0.length; i++)
            x0[i] = i + 1;

        int[] x1 = new int[101];
        for(int i = 0 ; i < x1.length; i++)
            x1[i] = i + 1;

        x1[100] = 70;
        BinaryTrie trie = new BinaryTrie();
        assertEquals( 10, trie.set(x0, 1.0));
        assertEquals(101, trie.set(x1, 2.0));
        assertEquals(1.0, trie.get(x0), E);
        assertEquals(2.0, trie.get(x1), E);
    }

    @Test
    public void testGetSet02() {
        BinaryTrie trie = new BinaryTrie();
        int[] x0 = {1, 2, 3, 4};
        int[] x1 = {2, 3, 4, 5};
        int[] x2 = {3, 4, 5, 6};
        int[] x3 = {3, 4, 6, 6};

        assertEquals(  4, trie.set(x0, 1.0));
        assertEquals(  8, trie.set(x1, 2.0));
        assertEquals( 12, trie.set(x2, 3.0));
        assertEquals( 14, trie.set(x3, 4.0));

        assertEquals(4.0, trie.get(x3), E);
        assertEquals(3.0, trie.get(x2), E);
        assertEquals(2.0, trie.get(x1), E);
        assertEquals(1.0, trie.get(x0), E);
    }

    @Test
    public void testUpdate() {
        BinaryTrie trie = new BinaryTrie();
        int[] x0 = {1, 2, 3, 4};
        int[] x1 = {2, 3, 4, 5};
        int[] x2 = {3, 4, 5, 6};
        int[] x3 = {3, 4, 6, 6};

        assertEquals(  4, trie.update(x0, 1.0));
        assertEquals(  8, trie.update(x1, 2.0));
        assertEquals( 12, trie.update(x2, 3.0));
        assertEquals( 14, trie.update(x3, 4.0));

        assertEquals(4.0, trie.get(x3), E);
        assertEquals(3.0, trie.get(x2), E);
        assertEquals(2.0, trie.get(x1), E);
        assertEquals(1.0, trie.get(x0), E);

        assertEquals(  4, trie.update(x0, 10.0));
        assertEquals(  8, trie.update(x1, 20.0));
        assertEquals( 12, trie.update(x2, 30.0));
        assertEquals( 14, trie.update(x3, 40.0));

        assertEquals(44.0, trie.get(x3), E);
        assertEquals(33.0, trie.get(x2), E);
        assertEquals(22.0, trie.get(x1), E);
        assertEquals(11.0, trie.get(x0), E);
    }
}
