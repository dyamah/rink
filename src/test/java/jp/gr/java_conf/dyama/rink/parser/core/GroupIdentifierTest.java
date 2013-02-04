package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.IOException;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.corpus.Common;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GroupIdentifierTest {

    GroupIdentifier uni_ ;
    GroupIdentifier pos_ ;
    GroupIdentifier ext_ ;
    IDConverter idconverter_ ;
    SimpleSentenceReader reader_ ;
    SampleImpl sample_ ;
    @Before
    public void setUp() throws Exception {
        uni_ = new GroupIdentifier.UniGroupIdentifier();
        pos_ = new GroupIdentifier.POSGroupIdentifier();
        ext_ = new GroupIdentifier.ExtPOSGroupIdentifier();

        idconverter_ = new IDConverterImpl.MutableIDConverter();
        reader_ = new SimpleSentenceReader(new WordImpl.Generator(idconverter_));

        reader_.addWord("I", 0, 1, PTB.POS.PRP, 1);
        reader_.addWord("saw", 0, 1, PTB.POS.VBD, -1);
        reader_.addWord("a", 0, 1, PTB.POS.DT, 3);
        reader_.addWord("girl", 0, 1, PTB.POS.NN, 1);
        reader_.addWord("with", 0, 1, PTB.POS.IN, 3);
        reader_.addWord("a", 0, 1, PTB.POS.DT, 6);
        reader_.addWord("telescope", 0, 1, PTB.POS.NN, 4);
        sample_ = new SampleImpl(reader_, idconverter_);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetGroupID() throws IOException {
        try {
            uni_.getGroupID(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the sample is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            pos_.getGroupID(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the sample is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }


        try {
            ext_.getGroupID(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the sample is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            assertEquals(0, uni_.getGroupID(sample_));
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            pos_.getGroupID(sample_);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the index is out of range.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            assertEquals(Common.POS.EOS.getID(), ext_.getGroupID(sample_));
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        sample_.read();
        State state = sample_.getState();

        state.setPosition(0);
        assertEquals(0, uni_.getGroupID(sample_));
        assertEquals(PTB.POS.PRP.getID(), pos_.getGroupID(sample_));
        assertEquals(PTB.POS.PRP.getID(), ext_.getGroupID(sample_));

        state.setPosition(1);
        assertEquals(0, uni_.getGroupID(sample_));
        assertEquals(PTB.POS.VBD.getID(), pos_.getGroupID(sample_));
        assertEquals(PTB.POS.VBD.getID(), ext_.getGroupID(sample_));

        state.setPosition(2);
        assertEquals(0, uni_.getGroupID(sample_));
        assertEquals(PTB.POS.DT.getID(), pos_.getGroupID(sample_));
        assertEquals(PTB.POS.DT.getID(), ext_.getGroupID(sample_));

        state.setPosition(3);
        assertEquals(0, uni_.getGroupID(sample_));
        assertEquals(PTB.POS.NN.getID(), pos_.getGroupID(sample_));
        assertEquals(PTB.POS.NN.getID(), ext_.getGroupID(sample_));

        state.setPosition(4);
        assertEquals(0, uni_.getGroupID(sample_));
        assertEquals(PTB.POS.IN.getID(), pos_.getGroupID(sample_));
        assertEquals(PTB.POS.IN.getID(), ext_.getGroupID(sample_));

        state.setPosition(5);
        assertEquals(0, uni_.getGroupID(sample_));
        assertEquals(PTB.POS.DT.getID(), pos_.getGroupID(sample_));
        assertEquals(Common.POS.EOS.getID(), ext_.getGroupID(sample_));

        state.setPosition(6);
        assertEquals(0, uni_.getGroupID(sample_));
        assertEquals(PTB.POS.NN.getID(), pos_.getGroupID(sample_));
        assertEquals(Common.POS.EOS.getID(), ext_.getGroupID(sample_));
    }

    @Test
    public void testGetString() {
        assertEquals("", uni_.getString(0));
        assertEquals("", uni_.getString(1));
        assertEquals("", uni_.getString(2));
        assertEquals("", uni_.getString(3));

        assertEquals(PTB.POS.VBD.toString(), pos_.getString(PTB.POS.VBD.getID()));
        assertEquals(PTB.POS.VBD.toString(), ext_.getString(PTB.POS.VBD.getID()));

        assertEquals(PTB.POS.TO.toString(), pos_.getString(PTB.POS.TO.getID()));
        assertEquals(PTB.POS.TO.toString(), ext_.getString(PTB.POS.TO.getID()));

    }

}
