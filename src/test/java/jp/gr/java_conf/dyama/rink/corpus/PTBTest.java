package jp.gr.java_conf.dyama.rink.corpus;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import jp.gr.java_conf.dyama.rink.corpus.Common;
import jp.gr.java_conf.dyama.rink.corpus.PTB;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PTBTest {

    @Before
    public void setUp() throws Exception {


    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testUniqueID() {

        {
            Set<Integer> ids = new HashSet<Integer>();
            PTB.PhrasalCategory[]  tags = PTB.PhrasalCategory.values();
            assertEquals(true, tags.length > 0);
            for(PTB.PhrasalCategory pc : tags){
                ids.add(pc.getID());
                assertNotNull(pc.getDescription());
            }
            assertEquals(tags.length, ids.size());
        }

        {
            Set<Integer> ids = new HashSet<Integer>();
            PTB.POS[]  tags = PTB.POS.values();
            assertEquals(true, tags.length > 0);
            for(PTB.POS pos : tags){
                ids.add(pos.getID());
                assertNotNull(pos.getDescription());
            }
            assertEquals(tags.length, ids.size());
        }
    }

    @Test
    public void testParseIntString(){
        {
            PTB.PhrasalCategory[]  tags = PTB.PhrasalCategory.values();
            assertEquals(true, tags.length > 0);
            for(PTB.PhrasalCategory pc : tags){
                assertEquals(pc,  PTB.PhrasalCategory.parseInt(pc.getID()));
                assertEquals(pc,  PTB.PhrasalCategory.parseString(pc.toString()));
            }

            for(int i = -2; i < 10; i++)
                assertEquals(PTB.PhrasalCategory.UNDEFINED, PTB.PhrasalCategory.parseInt(i));

            int m = tags[tags.length - 1].getID();
            for(int i = m + 1; i < m + 5; i++)
                assertEquals(PTB.PhrasalCategory.UNDEFINED, PTB.PhrasalCategory.parseInt(i));

            assertEquals(PTB.PhrasalCategory.UNDEFINED, PTB.PhrasalCategory.parseString(null));
            assertEquals(PTB.PhrasalCategory.UNDEFINED, PTB.PhrasalCategory.parseString("Si"));

        }

        {
            PTB.POS[]  tags = PTB.POS.values();
            assertEquals(true, tags.length > 0);
            for(PTB.POS pos : tags){
                assertEquals(pos,  PTB.POS.parseInt(pos.getID()));
                assertEquals(pos,  PTB.POS.parseString(pos.toString()));
            }

            for(int i = -2; i < 101; i++)
                assertEquals(Common.POS.UNDEFINED, PTB.POS.parseInt(i));

            int m = tags[tags.length - 1].getID();
            for(int i = m + 1; i < m + 5; i++)
                assertEquals(Common.POS.UNDEFINED, PTB.POS.parseInt(i));

            assertEquals(Common.POS.UNDEFINED, PTB.POS.parseString(null));
            assertEquals(Common.POS.UNDEFINED, PTB.POS.parseString("NNs"));
        }
    }

}
