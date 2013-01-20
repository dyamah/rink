package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.gr.java_conf.dyama.rink.parser.core.DependencyGraph.Edge;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyGraph.Node;

public class EdgeTest {
    static final double E = 0.0000001;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEdge() {
        Edge edge = new Edge() ;
        assertEquals(null, edge.getParent());
        assertEquals(null, edge.getChild());
        assertEquals(0.0, edge.getScore(), E);
        assertEquals(false, edge.isActive());
    }

    @Test
    public void testGetChildParent() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Edge edge = new Edge() ;
        assertEquals(null, edge.getParent());
        assertEquals(null, edge.getChild());
        assertEquals(0.0, edge.getScore(), E);
        assertEquals(false, edge.isActive());

        Node node0 = new Node();
        Node node1 = new Node();
        assertEquals(true, node0 != node1);

        Field parent = Edge.class.getDeclaredField("parent_");
        Field child = Edge.class.getDeclaredField("child_");
        parent.setAccessible(true);
        child.setAccessible(true);
        parent.set(edge, node0);
        child.set(edge,node1);
        assertEquals(true, edge.getParent() == node0);
        assertEquals(true, edge.getChild() == node1);
    }

    @Test
    public void testGetSetScore() {
        Edge edge = new Edge() ;
        assertEquals(0.0, edge.getScore(), E);
        edge.setScore(1.11);
        assertEquals(1.11, edge.getScore(), E);

        edge.setScore(-90.18);
        assertEquals(-90.18, edge.getScore(), E);
    }

    @Test
    public void testIsActive() {
        Edge edge = new Edge() ;
        assertEquals(false, edge.isActive());
        edge.enable();
        assertEquals(true, edge.isActive());
        edge.disable();
        assertEquals(false, edge.isActive());
    }

    @Test
    public void testClear() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Edge edge = new Edge() ;
        assertEquals(null, edge.getParent());
        assertEquals(null, edge.getChild());
        assertEquals( 0.0, edge.getScore(), E);
        assertEquals(false, edge.isActive());

        edge.clear();

        assertEquals(null, edge.getParent());
        assertEquals(null, edge.getChild());
        assertEquals( 0.0, edge.getScore(), E);
        assertEquals(false, edge.isActive());

        Node node0 = new Node();
        Node node1 = new Node();
        assertEquals(true, node0 != node1);
        Field parent = Edge.class.getDeclaredField("parent_");
        Field child = Edge.class.getDeclaredField("child_");
        parent.setAccessible(true);
        child.setAccessible(true);
        parent.set(edge, node0);
        child.set(edge,node1);
        assertEquals(true, edge.getParent() == node0);
        assertEquals(true, edge.getChild() == node1);
        edge.setScore(39.01);
        assertEquals(39.01, edge.getScore(), E);
        edge.enable();
        assertEquals(true, edge.isActive());

        edge.clear();

        assertEquals(null, edge.getParent());
        assertEquals(null, edge.getChild());
        assertEquals( 0.0, edge.getScore(), E);
        assertEquals(false, edge.isActive());

    }

}
