package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.core.ActionImpl;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyRelations;
import jp.gr.java_conf.dyama.rink.parser.core.SentenceImpl;
import jp.gr.java_conf.dyama.rink.parser.core.State;
import jp.gr.java_conf.dyama.rink.parser.core.WordImpl;
import jp.gr.java_conf.dyama.rink.parser.core.Action.Type;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StateTest {
    static final double E = 0.0000000001;
    WordImpl.Generator word_generator_ ;
    SentenceImpl sentence0_;
    SentenceImpl sentence_ ;
    @Before
    public void setUp() throws Exception {
        word_generator_ = new WordImpl.Generator(new IDConverterImpl.MutableIDConverter());

        sentence0_ = new SentenceImpl(word_generator_, null);
        sentence_       = new SentenceImpl(word_generator_, null);
        sentence_.addWord(word_generator_.generate("I",          0,  1, PTB.POS.PRP,  1, null));
        sentence_.addWord(word_generator_.generate("saw",        2,  5, PTB.POS.VBD, -1, null));
        sentence_.addWord(word_generator_.generate("a",          6,  7, PTB.POS.DT,   3, null));
        sentence_.addWord(word_generator_.generate("girl",       8, 12, PTB.POS.NN,   1, null));
        sentence_.addWord(word_generator_.generate("with",      13, 17, PTB.POS.IN,   3, null));
        sentence_.addWord(word_generator_.generate("a",         18, 19, PTB.POS.DT,   6, null));
        sentence_.addWord(word_generator_.generate("telescope", 20, 29, PTB.POS.NN,   4, null));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStatePool(){
        {
            State.StatePool pool = new State.StatePool(0);
            State state0 = pool.create();
            State state1 = pool.create();
            assertEquals(false, state0 == state1);

            pool.release(state0);
            pool.release(state1);

            State state2 = pool.create();
            assertEquals(true, state0 == state2);
            assertEquals(false, state1 == state2);
        }

        {
            State.StatePool pool = new State.StatePool(1);
            State state0 = pool.create();
            State state1 = pool.create();
            assertEquals(false, state0 == state1);

            pool.release(state0);
            pool.release(state1);

            State state2 = pool.create();
            assertEquals(true, state0 == state2);
            assertEquals(false, state1 == state2);
        }

        {
            State.StatePool pool = new State.StatePool(2);
            State state0 = pool.create();
            State state1 = pool.create();
            assertEquals(false, state0 == state1);

            pool.release(state0);
            pool.release(state1);

            State state2 = pool.create();
            State state3 = pool.create();
            assertEquals(false, state2 == state3);
            assertEquals(true, state0 == state3);
            assertEquals(true, state1 == state2);
        }



    }

    @Test
    public void testState() {
        State state = new State();
        assertEquals(0, state.getPosition());
        assertEquals(true, state.isComplete());
        assertEquals(null, state.getLastAction());
        assertEquals(0, state.size());
        assertEquals(true, state.isEOS());

    }

    @Test
    public void testSetup() {
        {
            State state = new State();
            try {
                state.setup(null);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("the sentence is null.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
        }

        {
            State state = new State();
            state.setup(sentence0_);
            assertEquals(0, state.getPosition());
            assertEquals(true, state.isComplete());
            assertEquals(null, state.getLastAction());
            assertEquals(0, state.size());
            assertEquals(true, state.isEOS());
            assertEquals(true, state.getDependencies() != null);
            assertEquals(0, state.getDependencies().size());
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(0, state.getPosition());
            assertEquals(false, state.isComplete());
            assertEquals(null, state.getLastAction());
            assertEquals(7, state.size());
            assertEquals(false, state.isEOS());
            assertEquals(true, state.getDependencies() != null);
            assertEquals(7, state.getDependencies().size());
        }

    }

    @Test
    public void testCopy(){
        {
            State state0 = new State();

            try {
                state0.copy(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the source state is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

        }

        {
            State state0 = new State();
            State state1 = new State();

            assertEquals(0, state0.size());
            assertEquals(0, state0.getPosition());
            assertEquals(true, state0.isComplete());
            assertEquals(true, state0.isEOS());
            assertEquals(0.0, state0.getScore(), E);
            assertEquals(null, state0.getLastAction());
            {
                DependencyRelations deps0 = state0.getDependencies();
                assertEquals(0, deps0.size());
            }


            assertEquals(0, state1.size());
            assertEquals(0, state1.getPosition());
            assertEquals(true, state1.isComplete());
            assertEquals(true, state1.isEOS());
            assertEquals(0.0, state1.getScore(), E);
            assertEquals(null, state1.getLastAction());
            {
                DependencyRelations deps1 = state1.getDependencies();
                assertEquals(0, deps1.size());
            }


            state0.copy(state1);

            assertEquals(0, state0.size());
            assertEquals(0, state0.getPosition());
            assertEquals(true, state0.isComplete());
            assertEquals(true, state0.isEOS());
            assertEquals(0.0, state0.getScore(), E);
            assertEquals(null, state0.getLastAction());

            {
                DependencyRelations deps0 = state0.getDependencies();
                assertEquals(0, deps0.size());
            }


            assertEquals(0, state1.size());
            assertEquals(0, state1.getPosition());
            assertEquals(true, state1.isComplete());
            assertEquals(true, state1.isEOS());
            assertEquals(0.0, state1.getScore(), E);
            assertEquals(null, state1.getLastAction());
            {
                DependencyRelations deps1 = state1.getDependencies();
                assertEquals(0, deps1.size());
            }
        }

        {
            State state0 = new State();
            State state1 = new State();

            assertEquals(0, state0.size());
            assertEquals(0, state0.getPosition());
            assertEquals(true, state0.isComplete());
            assertEquals(true, state0.isEOS());
            assertEquals(0.0, state0.getScore(), E);
            assertEquals(null, state0.getLastAction());
            {
                DependencyRelations deps = state0.getDependencies();
                assertEquals(0, deps.size());
            }

            state1.setup(sentence_);

            state1.apply(new ActionImpl(Action.Type.SHIFT));
            ActionImpl left = new ActionImpl(Action.Type.LEFT);
            left.setScore(1.12);
            state1.apply(left);
            state1.apply(new ActionImpl(Action.Type.WAIT));

            assertEquals(6, state1.size());

            assertEquals(    1, state1.getPosition());
            assertEquals(false, state1.isComplete());
            assertEquals(false, state1.isEOS());
            assertEquals( 1.12, state1.getScore(), E);
            assertEquals(Action.Type.WAIT, state1.getLastAction().getType());
            {
                DependencyRelations deps = state1.getDependencies();
                assertEquals( 7, deps.size());
                assertEquals(-1, deps.getParentID(0));
                assertEquals(-1, deps.getParentID(1));
                assertEquals( 1, deps.getParentID(2));
                assertEquals(-1, deps.getParentID(3));
                assertEquals(-1, deps.getParentID(4));
                assertEquals(-1, deps.getParentID(5));
                assertEquals(-1, deps.getParentID(6));
            }

            state0.copy(state1);

            assertEquals(    6, state0.size());
            assertEquals(    1, state0.getPosition());
            assertEquals(false, state0.isComplete());
            assertEquals(false, state0.isEOS());
            assertEquals(1.12, state0.getScore(), E);
            assertEquals(Action.Type.WAIT, state0.getLastAction().getType());
            {
                DependencyRelations deps = state0.getDependencies();
                assertEquals( 7, deps.size());
                assertEquals(-1, deps.getParentID(0));
                assertEquals(-1, deps.getParentID(1));
                assertEquals( 1, deps.getParentID(2));
                assertEquals(-1, deps.getParentID(3));
                assertEquals(-1, deps.getParentID(4));
                assertEquals(-1, deps.getParentID(5));
                assertEquals(-1, deps.getParentID(6));
            }

            assertEquals(    6, state1.size());
            assertEquals(    1, state1.getPosition());
            assertEquals(false, state1.isComplete());
            assertEquals(false, state1.isEOS());
            assertEquals(1.12, state1.getScore(), E);
            assertEquals(Action.Type.WAIT, state0.getLastAction().getType());

            {
                DependencyRelations deps = state1.getDependencies();
                assertEquals( 7, deps.size());
                assertEquals(-1, deps.getParentID(0));
                assertEquals(-1, deps.getParentID(1));
                assertEquals( 1, deps.getParentID(2));
                assertEquals(-1, deps.getParentID(3));
                assertEquals(-1, deps.getParentID(4));
                assertEquals(-1, deps.getParentID(5));
                assertEquals(-1, deps.getParentID(6));
            }
        }

        {

        }

    }

    @Test
    public void testApply() {

        {
            State state = new State();
            try {
                state.apply(null);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the action is null.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

        }

        {

            State state = new State();
            assertEquals(false, state.apply( new ActionImpl(Type.SHIFT)));
            assertEquals(null, state.getLastAction());
            assertEquals(false, state.apply( new ActionImpl(Type.WAIT)));
            assertEquals(null, state.getLastAction());
            assertEquals(false, state.apply( new ActionImpl(Type.LEFT)));
            assertEquals(null, state.getLastAction());
            assertEquals(false, state.apply( new ActionImpl(Type.RIGHT)));
            assertEquals(null, state.getLastAction());

        }

        {
            State state = new State();
            state.setup(sentence0_);
            assertEquals(false, state.apply( new ActionImpl(Type.SHIFT)));
            assertEquals(null, state.getLastAction());
            assertEquals(false, state.apply( new ActionImpl(Type.WAIT)));
            assertEquals(null, state.getLastAction());
            assertEquals(false, state.apply( new ActionImpl(Type.LEFT)));
            assertEquals(null, state.getLastAction());
            assertEquals(false, state.apply( new ActionImpl(Type.RIGHT)));
            assertEquals(null, state.getLastAction());
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(0, state.getPosition());
            assertEquals(7, state.size());

            ActionImpl action = new ActionImpl(Type.SHIFT);
            assertEquals(true, state.apply(action));
            assertEquals(1, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(7, state.size());

            action = new ActionImpl(Type.WAIT);
            assertEquals(true, state.apply( action ));
            assertEquals(2, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(7, state.size());

            action = new ActionImpl(Type.LEFT);
            assertEquals(true, state.apply( action));
            assertEquals(1, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(2, action.getLeftTarget());
            assertEquals(3, action.getRightTarget());
            assertEquals(6, state.size());
            {
                int nodeID = state.getRightTarget();
                DependencyRelations deps = state.getDependencies();
                assertEquals(2, nodeID);
                assertEquals(1, deps.getNumberOfChildren(nodeID));
                int childID = deps.getChildID(nodeID, 0);
                assertEquals(true, deps.getParentID(childID) == nodeID) ;
                assertEquals(3, childID);
            }

            action = new ActionImpl(Type.RIGHT);
            assertEquals(true, state.apply(action));
            assertEquals(0, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(1, action.getLeftTarget());
            assertEquals(2, action.getRightTarget());

            assertEquals(5, state.size());
            {
                int nodeID = state.getRightTarget();
                DependencyRelations deps = state.getDependencies();
                assertEquals(2, nodeID);
                assertEquals(2, deps.getNumberOfChildren(nodeID));
                int childID = deps.getChildID(nodeID, 0);
                assertEquals(true, deps.getParentID(childID) == nodeID) ;
                assertEquals(1, childID);

                childID = deps.getChildID(nodeID, 1);
                assertEquals(true, deps.getParentID(childID) == nodeID) ;
                assertEquals(3, childID);
            }
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(0, state.getPosition());
            assertEquals(7, state.size());

            ActionImpl action = new ActionImpl(Type.LEFT);
            assertEquals(true, state.apply( action ));
            assertEquals(0, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(0, action.getLeftTarget());
            assertEquals(1, action.getRightTarget());
            assertEquals(6, state.size());
            {
                int nodeID = state.getLeftTarget();
                DependencyRelations deps = state.getDependencies();
                assertEquals(0, nodeID);
                assertEquals(1, deps.getNumberOfChildren(nodeID));
                int childID = deps.getChildID(nodeID, 0);
                assertEquals(true, deps.getParentID(childID) == nodeID) ;
                assertEquals(1, childID);
            }

            action = new ActionImpl(Type.LEFT);
            assertEquals(true, state.apply(action));
            assertEquals(0, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(0, action.getLeftTarget());
            assertEquals(2, action.getRightTarget());
            assertEquals(5, state.size());
            {
                int nodeID = state.getLeftTarget();
                DependencyRelations deps = state.getDependencies();
                assertEquals(0, nodeID);
                assertEquals(2, deps.getNumberOfChildren(nodeID));
                int childID = deps.getChildID(nodeID, 0);
                assertEquals(true, deps.getParentID(childID) == nodeID) ;
                assertEquals(1, childID);

                childID = deps.getChildID(nodeID, 1);
                assertEquals(true, deps.getParentID(childID) == nodeID) ;
                assertEquals(2, childID);
            }

        }

        {
            State state = new State();
            state.setup(sentence_);
            state.setPosition(5);
            assertEquals(5, state.getPosition());
            assertEquals(7, state.size());

            ActionImpl action = new ActionImpl(Type.RIGHT);
            assertEquals(true, state.apply(action));
            assertEquals(4, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(5, action.getLeftTarget());
            assertEquals(6, action.getRightTarget());

            assertEquals(6, state.size());
            {
                int nodeID = state.getRightTarget();
                DependencyRelations deps = state.getDependencies();
                assertEquals(6, nodeID);
                assertEquals(1, deps.getNumberOfChildren(nodeID));
                int childID = deps.getChildID(nodeID, 0);
                assertEquals(true, deps.getParentID(childID) == nodeID) ;
                assertEquals(5, childID);
            }

            action = new ActionImpl(Type.RIGHT);
            assertEquals(true, state.apply(action));
            assertEquals(3, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(4, action.getLeftTarget());
            assertEquals(6, action.getRightTarget());
            assertEquals(5, state.size());
            {
                int nodeID = state.getRightTarget();
                DependencyRelations deps = state.getDependencies();
                assertEquals(6, nodeID);
                assertEquals(2, deps.getNumberOfChildren(nodeID));
                int childID = deps.getChildID(nodeID, 0);
                assertEquals(true, deps.getParentID(childID) == nodeID) ;
                assertEquals(4, childID);

                childID = deps.getChildID(nodeID, 1);
                assertEquals(true, deps.getParentID(childID) == nodeID) ;
                assertEquals(5, childID);
            }
            state.setPosition(state.size() - 1);

            assertEquals(false, state.apply(new ActionImpl(Type.RIGHT)));
            assertEquals(action, state.getLastAction());
            assertEquals(4, action.getLeftTarget());
            assertEquals(6, action.getRightTarget());

            action = new ActionImpl(Type.SHIFT);
            assertEquals(true, state.apply( action ));
            assertEquals(5, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(6, action.getLeftTarget());
            assertEquals(7, action.getRightTarget());

            assertEquals(false, state.apply( new ActionImpl(Type.WAIT) ));
            assertEquals(5, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(6, action.getLeftTarget());
            assertEquals(7, action.getRightTarget());
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(7, state.size());
            assertEquals(0, state.getPosition());

            assertEquals(true, state.apply( new ActionImpl(Type.LEFT)));
            assertEquals(6, state.size());
            assertEquals(0, state.getPosition());

            assertEquals(true, state.apply( new ActionImpl(Type.LEFT)));
            assertEquals(5, state.size());
            assertEquals(0, state.getPosition());

            assertEquals(true, state.apply( new ActionImpl(Type.LEFT)));
            assertEquals(4, state.size());
            assertEquals(0, state.getPosition());

            assertEquals(true, state.apply( new ActionImpl(Type.LEFT)));
            assertEquals(3, state.size());
            assertEquals(0, state.getPosition());

            assertEquals(true, state.apply( new ActionImpl(Type.LEFT)));
            assertEquals(2, state.size());
            assertEquals(0, state.getPosition());

            assertEquals(true, state.apply( new ActionImpl(Type.LEFT)));
            assertEquals(1, state.size());
            assertEquals(0, state.getPosition());

            assertEquals(false, state.apply( new ActionImpl(Type.LEFT)));
            assertEquals(0, state.getPosition());

            assertEquals(false, state.apply( new ActionImpl(Type.RIGHT)));
            assertEquals(0, state.getPosition());

            ActionImpl action =  new ActionImpl(Type.SHIFT);
            assertEquals(true, state.apply( action ));
            assertEquals(1, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(0, action.getLeftTarget());
            assertEquals(7, action.getRightTarget());

            state.setPosition(0);
            action = new ActionImpl(Type.WAIT);
            assertEquals(true, state.apply(action));
            assertEquals(1, state.getPosition());
            assertEquals(action, state.getLastAction());
            assertEquals(0, action.getLeftTarget());
            assertEquals(7, action.getRightTarget());

            assertEquals(false, state.apply( new ActionImpl(Type.SHIFT)));
            assertEquals(false, state.apply( new ActionImpl(Type.WAIT)));

            assertEquals(true, state.isEOS());
        }

    }

    @Test
    public void testIsComplete() {

        {
            State state = new State();
            assertEquals(true, state.isComplete() );
        }

        {
            State state = new State();
            assertEquals(true, state.isComplete() );
            state.setup(sentence0_);
            assertEquals(true, state.isComplete() );
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(0, state.getPosition());
            assertEquals(false, state.isComplete() );
            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(1, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(2, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.WAIT));
            assertEquals(3, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.WAIT));
            assertEquals(4, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(5, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(6, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(7, state.getPosition());
            assertEquals(true, state.isEOS());
            assertEquals(true, state.isComplete() );
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(0, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(1, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(2, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.WAIT));
            assertEquals(3, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.WAIT));
            assertEquals(4, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.LEFT));
            assertEquals(3, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(4, state.getPosition());
            assertEquals(false, state.isComplete() );

            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(5, state.getPosition());
            assertEquals(false, state.isComplete() );


            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(6, state.getPosition());
            assertEquals(true, state.isEOS());
            assertEquals(false, state.isComplete() );


            state.setPosition(0);
            assertEquals(6, state.size());
            assertEquals(0, state.getPosition());
            assertEquals(false, state.isComplete());
            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(false, state.isComplete());
            state.apply( new ActionImpl(Type.WAIT));
            assertEquals(false, state.isComplete());
            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(false, state.isComplete());
            state.apply( new ActionImpl(Type.WAIT));
            assertEquals(false, state.isComplete());
            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(false, state.isComplete());
            state.apply( new ActionImpl(Type.WAIT));
            assertEquals(6, state.getPosition());
            assertEquals(true, state.isEOS());
            assertEquals(true, state.isComplete());
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(7, state.size());
            state.apply( new ActionImpl(Type.LEFT));
            state.apply( new ActionImpl(Type.LEFT));
            state.apply( new ActionImpl(Type.LEFT));
            state.apply( new ActionImpl(Type.LEFT));
            state.apply( new ActionImpl(Type.LEFT));
            state.apply( new ActionImpl(Type.SHIFT));
            assertEquals(2, state.size());
            assertEquals(1, state.getPosition());
            assertEquals(false, state.isComplete());
            state.setPosition(0);
            state.apply( new ActionImpl(Type.RIGHT));
            assertEquals(1, state.size());
            assertEquals(true, state.isComplete());
        }
    }

    @Test
    public void testGetLeftRightNode() {
        {
            State state = new State();
            assertEquals(0, state.getPosition());
            assertEquals(0, state.size());
            assertEquals(-1, state.getLeftTarget());
            assertEquals(-1, state.getRightTarget());

            assertEquals(-1, state.getIDofLeftNode(-2));
            assertEquals(-1, state.getIDofLeftNode(-1));
            assertEquals(-1, state.getIDofLeftNode(0));
            try {
                state.getIDofLeftNode(1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is greater than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                state.getRightNode(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is less than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            assertEquals(-1, state.getRightNode(0));
            assertEquals(-1, state.getRightNode(1));
            assertEquals(-1, state.getRightNode(2));
        }

        {
            State state = new State();
            state.setup(sentence0_);
            assertEquals(0, state.getPosition());
            assertEquals(0, state.size());

            assertEquals(-1, state.getLeftTarget());
            assertEquals(-1, state.getRightTarget());

            assertEquals(-1, state.getIDofLeftNode(-2));
            assertEquals(-1, state.getIDofLeftNode(-1));
            assertEquals(-1, state.getIDofLeftNode(0));
            try {
                state.getIDofLeftNode(1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is greater than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                state.getRightNode(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is less than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            assertEquals(-1, state.getRightNode(0));
            assertEquals(-1, state.getRightNode(1));
            assertEquals(-1, state.getRightNode(2));
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(0, state.getPosition());
            assertEquals(7, state.size());

            assertEquals(   0, state.getLeftTarget());
            assertEquals(   1, state.getRightTarget());

            assertEquals(-1, state.getIDofLeftNode(-2));
            assertEquals(-1, state.getIDofLeftNode(-1));
            assertEquals( 0, state.getIDofLeftNode(0));
            try {
                state.getIDofLeftNode(1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is greater than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                state.getRightNode(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is less than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            assertEquals(1, state.getRightNode(0));
            assertEquals(2, state.getRightNode(1));
            assertEquals(3, state.getRightNode(2));


            state.setPosition(1);
            assertEquals(   1, state.getLeftTarget());
            assertEquals(   2, state.getRightTarget());

            assertEquals(-1, state.getIDofLeftNode(-2));
            assertEquals( 0, state.getIDofLeftNode(-1));
            assertEquals( 1, state.getIDofLeftNode(0));
            try {
                state.getIDofLeftNode(1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is greater than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                state.getRightNode(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is less than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            assertEquals(2, state.getRightNode(0));
            assertEquals(3, state.getRightNode(1));
            assertEquals(4, state.getRightNode(2));


            state.setPosition(4);
            assertEquals(   4, state.getLeftTarget());
            assertEquals(   5, state.getRightTarget());

            assertEquals(2, state.getIDofLeftNode(-2));
            assertEquals(3, state.getIDofLeftNode(-1));
            assertEquals(4, state.getIDofLeftNode(0));
            try {
                state.getIDofLeftNode(1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is greater than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                state.getRightNode(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is less than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            assertEquals(5, state.getRightNode(0));
            assertEquals(6, state.getRightNode(1));
            assertEquals(-1, state.getRightNode(2));

            state.setPosition(6);
            assertEquals(    6, state.getLeftTarget());
            assertEquals( -1, state.getRightTarget());

            assertEquals(4, state.getIDofLeftNode(-2));
            assertEquals(5, state.getIDofLeftNode(-1));
            assertEquals(6, state.getIDofLeftNode(0));
            try {
                state.getIDofLeftNode(1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is greater than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }

            try {
                state.getRightNode(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the relative position is less than 0.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            assertEquals(-1, state.getRightNode(0));
            assertEquals(-1, state.getRightNode(1));
            assertEquals(-1, state.getRightNode(2));
        }
    }



    @Test
    public void testGetLastAction() {

        State state = new State();
        state.setup(sentence_);
        assertEquals(null, state.getLastAction());
        ActionImpl action = new ActionImpl(Type.SHIFT);
        state.apply(action);
        assertEquals(action, state.getLastAction());

        action = new ActionImpl(Type.WAIT);
        state.apply(action);
        assertEquals(action, state.getLastAction());

        action = new ActionImpl(Type.LEFT);
        state.apply(action);
        assertEquals(action, state.getLastAction());

        action = new ActionImpl(Type.RIGHT);
        state.apply(action);
        assertEquals(action, state.getLastAction());
    }

    @Test
    public void testSetGetPosition() {
        {
            State state = new State();
            assertEquals(0, state.size());

            try{
                state.setPosition(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the position is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

            state.setPosition(0);
            assertEquals(0, state.getPosition());

            try{
                state.setPosition(1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the position is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        {
            State state = new State();
            state.setup(sentence0_);
            assertEquals(0, state.size());

            try{
                state.setPosition(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the position is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

            state.setPosition(0);
            assertEquals(0, state.getPosition());

            try{
                state.setPosition(1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the position is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(7, state.size());

            try{
                state.setPosition(-1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the position is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }

            state.setPosition(0);
            assertEquals(0, state.getPosition());
            state.setPosition(1);
            assertEquals(1, state.getPosition());
            state.setPosition(2);
            assertEquals(2, state.getPosition());
            state.setPosition(3);
            assertEquals(3, state.getPosition());
            state.setPosition(4);
            assertEquals(4, state.getPosition());
            state.setPosition(5);
            assertEquals(5, state.getPosition());
            state.setPosition(6);
            assertEquals(6, state.getPosition());
            state.setPosition(7);
            assertEquals(7, state.getPosition());

            try{
                state.setPosition(8);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the position is out of range.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }
    }

    @Test
    public void testSize() {

        State state = new State();
        assertEquals(0, state.size() );

        state.setup(sentence0_);
        assertEquals(0, state.size() );

        state.setup(sentence_);
        assertEquals(7, state.size() );

        state.apply(new ActionImpl(Type.LEFT));
        assertEquals(6, state.size() );

        state.apply(new ActionImpl(Type.LEFT));
        assertEquals(5, state.size() );

        state.apply(new ActionImpl(Type.LEFT));
        assertEquals(4, state.size() );

        state.apply(new ActionImpl(Type.LEFT));
        assertEquals(3, state.size() );

        state.apply(new ActionImpl(Type.LEFT));
        assertEquals(2, state.size() );

        state.apply(new ActionImpl(Type.LEFT));
        assertEquals(1, state.size() );
    }

    @Test
    public void testIsEOS() {
        {
            State state = new State();
            assertEquals(true, state.isEOS());
        }

        {
            State state = new State();
            state.setup(sentence0_);
            assertEquals(true, state.isEOS());
        }

        {
            State state = new State();
            state.setup(sentence_);
            assertEquals(7, state.size());
            assertEquals(false, state.isEOS());
            state.setPosition(0);
            assertEquals(false, state.isEOS());
            state.setPosition(1);
            assertEquals(false, state.isEOS());
            state.setPosition(2);
            assertEquals(false, state.isEOS());
            state.setPosition(3);
            assertEquals(false, state.isEOS());
            state.setPosition(4);
            assertEquals(false, state.isEOS());
            state.setPosition(5);
            assertEquals(false, state.isEOS());
            state.setPosition(6);
            assertEquals(false, state.isEOS());
            state.setPosition(7);
            assertEquals(true, state.isEOS());

        }

    }

    @Test
    public void testGetDependencies() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field deps = State.class.getDeclaredField("deps_");
        deps.setAccessible(true);
        {
            State state = new State();
            DependencyRelations expect = (DependencyRelations) deps.get(state);
            assertEquals(true, state.getDependencies()  == expect);
        }

        {
            State state = new State();
            state.setup(sentence0_);
            DependencyRelations expect = (DependencyRelations) deps.get(state);
            assertEquals(true, state.getDependencies()  == expect);
        }

        {
            State state = new State();
            state.setup(sentence_);
            DependencyRelations expect = (DependencyRelations) deps.get(state);
            assertEquals(true, state.getDependencies()  == expect);
        }

    }

    @Test
    public void testCompareTo(){
        ActionImpl action = new ActionImpl(Action.Type.LEFT);

        State state0 = new State(); state0.setup(sentence_);

        State state1 = new State(); state1.setup(sentence_);
        State state2 = new State(); state2.setup(sentence_);

        action.setScore(1.0);
        state0.apply(action);

        action.setScore(10.1);
        state1.apply(action);

        assertEquals(true, state0.compareTo(null)   <  0);
        assertEquals(true, state0.compareTo(state0) == 0);
        assertEquals(true, state0.compareTo(state1) >  0);
        assertEquals(true, state0.compareTo(state2) <  0);

        assertEquals(true, state1.compareTo(null)   <  0);
        assertEquals(true, state1.compareTo(state0) <  0);
        assertEquals(true, state1.compareTo(state1) == 0);
        assertEquals(true, state1.compareTo(state2) <  0);

        assertEquals(true, state2.compareTo(null)   <  0);
        assertEquals(true, state2.compareTo(state0) >  0);
        assertEquals(true, state2.compareTo(state1) >  0);
        assertEquals(true, state2.compareTo(state2) == 0);
    }
}
