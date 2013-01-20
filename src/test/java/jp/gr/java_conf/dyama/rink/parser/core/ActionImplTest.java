package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.parser.core.Action;
import jp.gr.java_conf.dyama.rink.parser.core.ActionImpl;
import jp.gr.java_conf.dyama.rink.parser.core.Action.Type;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActionImplTest {
    static final double E = 0.0000001;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testActionType(){
        assertEquals(1, Type.SHIFT.getID());
        assertEquals(2, Type.WAIT.getID());
        assertEquals(3, Type.LEFT.getID());
        assertEquals(4, Type.RIGHT.getID());

        assertEquals(null, Type.parseInt(-1));
        assertEquals(null, Type.parseInt( 0));
        assertEquals(Type.SHIFT, Type.parseInt( 1));
        assertEquals(Type.WAIT,  Type.parseInt( 2));
        assertEquals(Type.LEFT,  Type.parseInt( 3));
        assertEquals(Type.RIGHT, Type.parseInt( 4));
        assertEquals(null, Type.parseInt( 5));
        assertEquals(null, Type.parseInt( 6));
    }

    @Test
    public void testActionImpl() {
        try {
            new ActionImpl(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the type of action is null.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        {
            Action action = new ActionImpl(Action.Type.LEFT);
            assertEquals(Action.Type.LEFT, action.getType());
            assertEquals(-1, action.getLeftTarget());
            assertEquals(-1, action.getRightTarget());
            assertEquals(0.0, action.getScore(), E);
        }

        {
            Action action = new ActionImpl(Action.Type.RIGHT);
            assertEquals(Action.Type.RIGHT, action.getType());
            assertEquals(-1, action.getLeftTarget());
            assertEquals(-1, action.getRightTarget());
            assertEquals(0.0, action.getScore(), E);
        }

        {
            Action action = new ActionImpl(Action.Type.WAIT);
            assertEquals(Action.Type.WAIT, action.getType());
            assertEquals(-1, action.getLeftTarget());
            assertEquals(-1, action.getRightTarget());
            assertEquals(0.0, action.getScore(), E);
        }

        {
            Action action = new ActionImpl(Action.Type.SHIFT);
            assertEquals(Action.Type.SHIFT, action.getType());
            assertEquals(-1, action.getLeftTarget());
            assertEquals(-1, action.getRightTarget());
            assertEquals(0.0, action.getScore(), E);
        }

    }

    @Test
    public void testGetSetScore() {
        ActionImpl action = new ActionImpl(Action.Type.LEFT);
        assertEquals(0.0, action.getScore(), E);

        action.setScore(0.0);
        assertEquals(0.0, action.getScore(), E);

        action.setScore(1.1);
        assertEquals(1.1, action.getScore(), E);

        action.setScore(-12.3);
        assertEquals(-12.3, action.getScore(), E);

        action.setScore(6.3);
        assertEquals(6.3, action.getScore(), E);
    }

    @Test
    public void testGetSetTarget() {
        ActionImpl action = new ActionImpl(Action.Type.LEFT);
        assertEquals(-1, action.getLeftTarget());
        assertEquals(-1, action.getRightTarget());

        try {
            action.setTarget(-1, 0);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the left ID is negative.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            action.setTarget(0, 0);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the left ID is not less than the right ID.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        try {
            action.setTarget(1, 0);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the left ID is not less than the right ID.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        action.setTarget(1, 2);
        assertEquals(1, action.getLeftTarget());
        assertEquals(2, action.getRightTarget());
    }

    @Test
    public void testGetType() {
        {
            Action action = new ActionImpl(Action.Type.LEFT);
            assertEquals(Action.Type.LEFT, action.getType());
        }

        {
            Action action = new ActionImpl(Action.Type.RIGHT);
            assertEquals(Action.Type.RIGHT, action.getType());
        }

        {
            Action action = new ActionImpl(Action.Type.WAIT);
            assertEquals(Action.Type.WAIT, action.getType());
        }

        {
            Action action = new ActionImpl(Action.Type.SHIFT);
            assertEquals(Action.Type.SHIFT, action.getType());
        }
    }
}
