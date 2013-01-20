package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.parser.core.FeatureImpl;
import jp.gr.java_conf.dyama.rink.parser.core.FeatureImpl.POSITION;
import jp.gr.java_conf.dyama.rink.parser.core.FeatureImpl.RELATION;
import jp.gr.java_conf.dyama.rink.parser.core.FeatureImpl.TYPE;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FeatureImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFeatureImpl() {
        FeatureImpl feature = new FeatureImpl();
        assertEquals(0, feature.encode());
    }

    @Test
    public void testSet() {
        FeatureImpl feature = new FeatureImpl();
        try {
            feature.set(null, RELATION.CHILD, TYPE.POS, 1);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the position is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            feature.set(POSITION.L0, null, TYPE.POS, 1);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the relation is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            feature.set(POSITION.L0, RELATION.CHILD, null, 1);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the type is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            feature.set(POSITION.L1, RELATION.CHILD, TYPE.POS, -1);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the value is out of range.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
        feature.set(POSITION.L0, RELATION.CHILD, TYPE.POS, 0);
        feature.set(POSITION.L0, RELATION.CHILD, TYPE.POS, 0);
        feature.set(POSITION.L0, RELATION.CHILD, TYPE.POS, (int) (Math.pow(2, 21) - 1));
        try {
            feature.set(POSITION.L0, RELATION.CHILD, TYPE.POS, (int) (Math.pow(2, 21)));
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the value is out of range.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void testGetPosition() {
        FeatureImpl feature = new FeatureImpl();
        assertEquals(POSITION.L7, feature.getPosition());
        feature.set(POSITION.L0, RELATION.CHILD, TYPE.POS, 0);
        assertEquals(POSITION.L0, feature.getPosition());
        feature.set(POSITION.L1, RELATION.CHILD, TYPE.POS, 0);
        assertEquals(POSITION.L1, feature.getPosition());
        feature.set(POSITION.R7, RELATION.CHILD, TYPE.POS, 0);
        assertEquals(POSITION.R7, feature.getPosition());
    }

    @Test
    public void testGetRelation() {
        FeatureImpl feature = new FeatureImpl();
        assertEquals(RELATION.SELF, feature.getRelation());
        feature.set(POSITION.L0, RELATION.CHILD, TYPE.POS, 0);
        assertEquals(RELATION.CHILD, feature.getRelation());
        feature.set(POSITION.L0, RELATION.GRANPA, TYPE.POS, 0);
        assertEquals(RELATION.GRANPA, feature.getRelation());
    }

    @Test
    public void testGetType() {
        FeatureImpl feature = new FeatureImpl();
        assertEquals(TYPE.LEXCON, feature.getType());
        feature.set(POSITION.L0, RELATION.CHILD, TYPE.POS, 0);
        assertEquals(TYPE.POS, feature.getType());
        feature.set(POSITION.L0, RELATION.CHILD, TYPE.LEAF, 0);
        assertEquals(TYPE.LEAF, feature.getType());
    }

    @Test
    public void testGetValue() {
        FeatureImpl feature = new FeatureImpl();
        assertEquals(0, feature.getValue());
        feature.set(POSITION.L0, RELATION.CHILD, TYPE.LEAF, 7);
        assertEquals(7, feature.getValue());
        feature.set(POSITION.L0, RELATION.CHILD, TYPE.LEAF, 197194);
        assertEquals(197194, feature.getValue());
    }

    @Test
    public void testEncodeDecode() {
        {
            FeatureImpl f0 = new FeatureImpl();
            assertEquals(POSITION.L7,   f0.getPosition());
            assertEquals(RELATION.SELF, f0.getRelation());
            assertEquals(TYPE.LEXCON,   f0.getType());
            assertEquals(          0,   f0.getValue());
            assertEquals(0, f0.encode());

            FeatureImpl f1 = new FeatureImpl();
            f1.decode(0);
            assertEquals(POSITION.L7,   f1.getPosition());
            assertEquals(RELATION.SELF, f1.getRelation());
            assertEquals(TYPE.LEXCON,   f1.getType());
            assertEquals(          0,   f1.getValue());
        }

        {
            FeatureImpl f0 = new FeatureImpl();
            f0.set(POSITION.L0, RELATION.PARENT, TYPE.POS, 7);
            int expect = (7 << 27) | (1 << 23) | (1 << 21) | 7 ;
            assertEquals(expect, f0.encode());

            FeatureImpl f1 = new FeatureImpl();
            f1.decode(expect);
            assertEquals(POSITION.L0,   f1.getPosition());
            assertEquals(RELATION.PARENT, f1.getRelation());
            assertEquals(TYPE.POS,   f1.getType());
            assertEquals(          7,   f1.getValue());
        }

        {
            FeatureImpl f0 = new FeatureImpl();
            f0.set(POSITION.R3, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291);
            int expect = (11 << 27) | (11 << 23) | (3 << 21) | 118291 ;
            assertEquals(expect, f0.encode());

            FeatureImpl f1 = new FeatureImpl();
            f1.decode(expect);
            assertEquals(POSITION.R3,   f1.getPosition());
            assertEquals(RELATION.LEFT_GRANDCHILD, f1.getRelation());
            assertEquals(TYPE.OTHERS,   f1.getType());
            assertEquals(      118291,   f1.getValue());
        }

        {
            FeatureImpl f0 = new FeatureImpl();
            FeatureImpl f1 = new FeatureImpl();
            int code = 0 ;

            // POSITION
            f0.set(POSITION.L7, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.L7, f1.getPosition());
            f0.set(POSITION.L6, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.L6, f1.getPosition());
            f0.set(POSITION.L5, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.L5, f1.getPosition());
            f0.set(POSITION.L4, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.L4, f1.getPosition());
            f0.set(POSITION.L3, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.L3, f1.getPosition());
            f0.set(POSITION.L2, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.L2, f1.getPosition());
            f0.set(POSITION.L1, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.L1, f1.getPosition());
            f0.set(POSITION.L0, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.L0, f1.getPosition());
            f0.set(POSITION.R0, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.R0, f1.getPosition());
            f0.set(POSITION.R1, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.R1, f1.getPosition());
            f0.set(POSITION.R2, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.R2, f1.getPosition());
            f0.set(POSITION.R3, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.R3, f1.getPosition());
            f0.set(POSITION.R4, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.R4, f1.getPosition());
            f0.set(POSITION.R5, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.R5, f1.getPosition());
            f0.set(POSITION.R6, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.R6, f1.getPosition());
            f0.set(POSITION.R7, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(POSITION.R7, f1.getPosition());

            // RELATION
            f0.set(POSITION.R7, RELATION.SELF, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.SELF, f1.getRelation());
            f0.set(POSITION.R7, RELATION.PARENT, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.PARENT, f1.getRelation());
            f0.set(POSITION.R7, RELATION.LEFT_PARENT, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.LEFT_PARENT, f1.getRelation());
            f0.set(POSITION.R7, RELATION.RIGHT_PARENT, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.RIGHT_PARENT, f1.getRelation());
            f0.set(POSITION.R7, RELATION.GRANPA, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.GRANPA, f1.getRelation());
            f0.set(POSITION.R7, RELATION.LEFT_GRANPA, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.LEFT_GRANPA, f1.getRelation());
            f0.set(POSITION.R7, RELATION.RIGHT_GRANPA, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.RIGHT_GRANPA, f1.getRelation());
            f0.set(POSITION.R7, RELATION.CHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.CHILD, f1.getRelation());
            f0.set(POSITION.R7, RELATION.LEFT_CHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.LEFT_CHILD, f1.getRelation());
            f0.set(POSITION.R7, RELATION.RIGHT_CHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.RIGHT_CHILD, f1.getRelation());
            f0.set(POSITION.R7, RELATION.GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.GRANDCHILD, f1.getRelation());
            f0.set(POSITION.R7, RELATION.LEFT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.LEFT_GRANDCHILD, f1.getRelation());
            f0.set(POSITION.R7, RELATION.RIGHT_GRANDCHILD, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.RIGHT_GRANDCHILD, f1.getRelation());
            f0.set(POSITION.R7, RELATION.SIBLING, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.SIBLING, f1.getRelation());
            f0.set(POSITION.R7, RELATION.LEFT_SIBLING, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.LEFT_SIBLING, f1.getRelation());
            f0.set(POSITION.R7, RELATION.RIGHT_SIBLING, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(RELATION.RIGHT_SIBLING, f1.getRelation());

            // TYPE
            f0.set(POSITION.R7, RELATION.RIGHT_SIBLING, TYPE.LEXCON, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(TYPE.LEXCON, f1.getType());
            f0.set(POSITION.R7, RELATION.RIGHT_SIBLING, TYPE.POS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(TYPE.POS, f1.getType());
            f0.set(POSITION.R7, RELATION.RIGHT_SIBLING, TYPE.LEAF, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(TYPE.LEAF, f1.getType());
            f0.set(POSITION.R7, RELATION.RIGHT_SIBLING, TYPE.OTHERS, 118291); code = f0.encode(); f1.decode(code);
            assertEquals(TYPE.OTHERS, f1.getType());

            // VALUE
            f0.set(POSITION.R7, RELATION.RIGHT_SIBLING, TYPE.OTHERS, 0); code = f0.encode(); f1.decode(code);
            assertEquals(0, f1.getValue());

            f0.set(POSITION.R7, RELATION.RIGHT_SIBLING, TYPE.OTHERS, 1); code = f0.encode(); f1.decode(code);
            assertEquals(1, f1.getValue());

            f0.set(POSITION.R7, RELATION.RIGHT_SIBLING, TYPE.OTHERS, 999991); code = f0.encode(); f1.decode(code);
            assertEquals(999991, f1.getValue());

        }



    }
}
