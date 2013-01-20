package jp.gr.java_conf.dyama.rink.corpus;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import jp.gr.java_conf.dyama.rink.corpus.Common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommonTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testParsePOS() {
        Common.POS[] tags = Common.POS.values();
        assertEquals(true, tags.length > 0);

        assertEquals(Common.POS.UNDEFINED, Common.POS.parseInt(-1));

        Set<Integer> ids = new HashSet<Integer>();
        for(Common.POS pos : tags){
            ids.add(pos.getID());
            assertNotNull(pos.getDescription());
            assertEquals(pos, Common.POS.parseInt(pos.getID()));
            assertEquals(pos, Common.POS.parseString(pos.toString()));
        }


        assertEquals(Common.POS.UNDEFINED, Common.POS.parseInt( 0));

        assertEquals(Common.POS.BOS,       Common.POS.parseInt( 1));
        assertEquals(Common.POS.EOS,       Common.POS.parseInt( 2));
        assertEquals(Common.POS.NONE,     Common.POS.parseInt( 3));
        assertEquals(Common.POS.BLANK,     Common.POS.parseInt( 4));
        assertEquals(Common.POS.UNDEFINED, Common.POS.parseInt( 5));

        assertEquals(Common.POS.UNDEFINED, Common.POS.parseString(null));
        assertEquals(Common.POS.UNDEFINED, Common.POS.parseString("UNDEFINED"));
        assertEquals(Common.POS.BLANK,     Common.POS.parseString("BLANK"));
        assertEquals(Common.POS.BOS,       Common.POS.parseString("BOS"));
        assertEquals(Common.POS.EOS,       Common.POS.parseString("EOS"));
        assertEquals(Common.POS.NONE,       Common.POS.parseString("NONE"));
        assertEquals(Common.POS.UNDEFINED, Common.POS.parseString("bos"));

        assertEquals(0, Common.POS.UNDEFINED.getID());
        assertEquals(1, Common.POS.BOS.getID());
        assertEquals(2, Common.POS.EOS.getID());
        assertEquals(3, Common.POS.NONE.getID());
        assertEquals(4, Common.POS.BLANK.getID());


        assertEquals(true, Common.POS.UNDEFINED.getDescription() != null);
        assertEquals(true, Common.POS.BLANK.getDescription() != null);
        assertEquals(true, Common.POS.BOS.getDescription() != null);
        assertEquals(true, Common.POS.EOS.getDescription() != null);
        assertEquals(true, Common.POS.NONE.getDescription() != null);
    }

}
