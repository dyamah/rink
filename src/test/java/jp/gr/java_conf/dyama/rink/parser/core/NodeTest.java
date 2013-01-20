package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.gr.java_conf.dyama.rink.parser.core.DependencyGraph;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyGraph.Edge;
import jp.gr.java_conf.dyama.rink.parser.core.DependencyGraph.Node;

public class NodeTest {

    Field id_ ;
    Field parents_ ;
    Field parent_ ;
    Field children_ ;
    Field childrenIDs_ ;
    Field numOfChildren_ ;
    Field size_ ;

    @Before
    public void setUp() throws Exception {
        assertEquals(100, DependencyGraph.DEFAULT_CAPACITY);

        id_              = Node.class.getDeclaredField("id_");
        id_.setAccessible(true);
        parents_         = Node.class.getDeclaredField("parents_");
        parents_.setAccessible(true);
        parent_          = Node.class.getDeclaredField("parent_");
        parent_.setAccessible(true);
        children_        = Node.class.getDeclaredField("children_");
        children_.setAccessible(true);
        childrenIDs_     = Node.class.getDeclaredField("childrenIDs_");
        childrenIDs_.setAccessible(true);
        numOfChildren_   = Node.class.getDeclaredField("numOfChildren_");
        numOfChildren_.setAccessible(true);
        size_            = Node.class.getDeclaredField("size_");
        size_.setAccessible(true);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testNode() {
        Node node = new Node();
        assertEquals(-1, node.getID());
        assertEquals(null, node.getParent());
        assertEquals(0, node.getNumOfChildren());
    }

    @Test
    public void testSetup() throws IllegalArgumentException, IllegalAccessException {
        {
            Node node = new Node();
            assertEquals(-1, node.getID());
            assertEquals(null, node.getParent());
            assertEquals(0, node.getNumOfChildren());
            assertEquals(0, size_.getInt(node));

            node.setup(3, 10);
            assertEquals(3, node.getID());
            assertEquals(null, node.getParent());
            assertEquals(0, node.getNumOfChildren());
            assertEquals(10, size_.getInt(node));
        }

        {
            Node node = new Node();
            assertEquals(-1, node.getID());
            assertEquals(null, node.getParent());
            assertEquals(0, node.getNumOfChildren());
            assertEquals(0, size_.getInt(node));
            {
                Edge[] parents = (Edge[])parents_.get(node) ;
                assertEquals(100, parents.length);
                Edge[] children = (Edge[])children_.get(node) ;
                assertEquals(100, children.length);
                int[] childrenIDs = (int[])childrenIDs_.get(node);
                assertEquals(100, childrenIDs.length);
            }


            node.setup(2, 100);
            assertEquals(2, node.getID());
            assertEquals(null, node.getParent());
            assertEquals(0, node.getNumOfChildren());
            assertEquals(100, size_.getInt(node));
            {
                Edge[] parents = (Edge[])parents_.get(node) ;
                assertEquals(100, parents.length);
                Edge[] children = (Edge[])children_.get(node) ;
                assertEquals(100, children.length);
                int[] childrenIDs = (int[])childrenIDs_.get(node);
                assertEquals(100, childrenIDs.length);
            }

            node.setup(4,  101);
            assertEquals(4, node.getID());
            assertEquals(null, node.getParent());
            assertEquals(0, node.getNumOfChildren());
            assertEquals(101, size_.getInt(node));
            {
                Edge[] parents = (Edge[])parents_.get(node) ;
                assertEquals(101, parents.length);
                Edge[] children = (Edge[])children_.get(node) ;
                assertEquals(101, children.length);
                int[] childrenIDs = (int[])childrenIDs_.get(node);
                assertEquals(101, childrenIDs.length);
            }

            node.setup(5,  501);
            assertEquals(5, node.getID());
            assertEquals(null, node.getParent());
            assertEquals(0, node.getNumOfChildren());
            assertEquals(501, size_.getInt(node));
            {
                Edge[] parents = (Edge[])parents_.get(node) ;
                assertEquals(501, parents.length);
                Edge[] children = (Edge[])children_.get(node) ;
                assertEquals(501, children.length);
                int[] childrenIDs = (int[])childrenIDs_.get(node);
                assertEquals(501, childrenIDs.length);
            }

            node.setup(6,  70);
            assertEquals(6, node.getID());
            assertEquals(null, node.getParent());
            assertEquals(0, node.getNumOfChildren());
            assertEquals(70, size_.getInt(node));
            {
                Edge[] parents = (Edge[])parents_.get(node) ;
                assertEquals(100, parents.length);
                Edge[] children = (Edge[])children_.get(node) ;
                assertEquals(100, children.length);
                int[] childrenIDs = (int[])childrenIDs_.get(node);
                assertEquals(100, childrenIDs.length);
            }
        }
    }

    @Test
    public void testIsSameChildern() throws IllegalArgumentException, IllegalAccessException {
        {
            Node node0 = new Node();
            Node node1 = new Node();
            assertEquals(true, node0 != node1);
            assertEquals(true, node0.isSameChildren(node1));
            assertEquals(false, node0.isSameChildren(null));
            assertEquals(false, node1.isSameChildren(null));
        }

        {
            Node node0 = new Node(); node0.setup(2, 3);
            Node node1 = new Node(); node1.setup(3, 3);
            assertEquals(false, node0.isSameChildren(node1));
        }

        {
            Node node0 = new Node(); node0.setup(3, 3);
            Node node1 = new Node(); node1.setup(3, 3);
            int[] childrenIDs0 = (int[])childrenIDs_.get(node0);
            int[] childrenIDs1 = (int[])childrenIDs_.get(node1);
            childrenIDs0[0] = 1 ; childrenIDs0[1] = 2 ; childrenIDs0[2] = 3 ;
            childrenIDs1[0] = 1 ; childrenIDs1[1] = 2 ; childrenIDs1[2] = 3 ;
            numOfChildren_.setInt(node0, 2);
            numOfChildren_.setInt(node1, 3);
            assertEquals(false, node0.isSameChildren(node1));
            assertEquals(true, node0.isSameChildren(node0));
            assertEquals(true, node1.isSameChildren(node1));
            numOfChildren_.setInt(node0, 3);
            assertEquals(true, node0.isSameChildren(node1));
        }

    }

    @Test
    public void testParent() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
        Node node = new Node(); node.setup(3, 3);
        Node parent = new Node(); parent.setup(1, 3);
        Edge p0 = new Edge(); p0.enable();
        Edge p1 = new Edge(); p1.disable();
        Edge[] parents = (Edge[]) parents_.get(node);
        parents[1] = p0;
        parents[2] = p1;
        parents[3] = p0;


        Field x = Edge.class.getDeclaredField("parent_");
        x.setAccessible(true);
        x.set(p0, parent);
        x.set(p1, parent);

        parent_.setInt(node, -1);
        assertEquals(null, node.getParent());

        parent_.setInt(node,  0);
        assertEquals(null, node.getParent());

        parent_.setInt(node,  1);
        assertEquals(true, node.getParent() == parent);

        parent_.setInt(node,  2);
        assertEquals(null, node.getParent());

        parent_.setInt(node,  3);
        assertEquals(null, node.getParent());
    }

    @Test
    public void testGetNumOfChildren() throws IllegalArgumentException, IllegalAccessException {
        Node node = new Node();
        numOfChildren_.setInt(node, 0);
        assertEquals(0, node.getNumOfChildren());

        numOfChildren_.setInt(node, 1);
        assertEquals(1, node.getNumOfChildren());

        numOfChildren_.setInt(node, 4);
        assertEquals(4, node.getNumOfChildren());
    }

    @Test
    public void testGetChild() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
        Node node = new Node(); node.setup(3, 3);
        Node child = new Node(); child.setup(1, 3);
        Edge p0 = new Edge(); p0.enable();
        Edge p1 = new Edge(); p1.disable();
        Edge[] children = (Edge[]) children_.get(node);
        children[0] = null;
        children[1] = p0;
        children[2] = p1;
        children[3] = p0;
        numOfChildren_.setInt(node, 3);
        int[] childrenIDs = (int[]) childrenIDs_.get(node);
        childrenIDs[0] = 0;
        childrenIDs[1] = 1;
        childrenIDs[2] = 2;
        childrenIDs[3] = 3;

        Field x = Edge.class.getDeclaredField("child_");
        x.setAccessible(true);
        x.set(p0, child);
        x.set(p1, child);

        assertEquals(null, node.getChild(-1));
        assertEquals(null, node.getChild( 0));
        assertEquals(true, node.getChild( 1) == child);
        assertEquals(null, node.getChild( 2));
        assertEquals(null, node.getChild( 3));
    }
}
