// =============================================================================
//
//   AdjListEdgeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FastEdgeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.graph;

import java.util.Iterator;

import junit.framework.TestCase;

import org.graffiti.graph.Edge;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;

/**
 * Contains test cases for the adjacency list implementation of the
 * <code>org.graffiti.graph.Edge</code> interface.
 */
public class FastEdgeTest extends TestCase {
    /** The graph for the test cases. */
    private Graph g = new FastGraph();

    /** The edge on which the methods will be tested. */
    private Edge e;

    /** The one node required to build a test edge. */
    private Node n1;

    /** The other node required to build a test edge. */
    private Node n2;

    /**
     * Constructs a new test case for the <code>AdjListGraph</code> class.
     * 
     * @param name
     *            the name for the test case.
     */
    public FastEdgeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FastEdgeTest.class);
    }

    /**
     * Tests if the direction is properly set.
     */
    public void testIsDirected() {
        e.setDirected(Edge.DIRECTED);
        assertEquals("edge is directed.", Edge.DIRECTED, e.isDirected());
        e.reverse();
        assertEquals("edge is directed.", Edge.DIRECTED, e.isDirected());
        e.setDirected(Edge.UNDIRECTED);
        assertEquals("edge is undirected.", Edge.UNDIRECTED, e.isDirected());
    }

    /**
     * Tests if source and target are properly swapped when the edge is
     * reversed.
     */
    public void testReverse() {
        Node sourceBefore = e.getSource();
        Node targetBefore = e.getTarget();
        boolean directed = e.isDirected();
        e.reverse();
        assertTrue("e is still directed.", e.isDirected() == directed);
        assertTrue("former source is now target", e.getTarget() == sourceBefore);
        assertTrue("the edge is not in the proper list", e.getTarget()
                .getDirectedInEdges().contains(e));
        assertEquals("the new target contains not the correct number of edges",
                1, e.getTarget().getEdges().size());
        assertEquals("the new source contains no edge", 1, e.getSource()
                .getEdges().size());
        assertTrue("the edge is not in the outEdges list", e.getSource()
                .getDirectedOutEdges().contains(e));
        assertTrue("former target is now source", e.getSource() == targetBefore);
    }

    /**
     * Tests the setDirected method.
     */
    public void testSetDirected() {
        e.setDirected(false);
        assertEquals("The number of undirected edges in node n1 is wrong.", 1,
                n1.getUndirectedEdges().size());
        assertEquals("The number of undirected edges in node n2 is wrong.", 1,
                n2.getUndirectedEdges().size());
        assertEquals("The number of directed outedges in node n1 is wrong.", 0,
                n1.getDirectedOutEdges().size());
        assertEquals("The number of directed inedges in node n2 is wrong.", 0,
                n2.getDirectedInEdges().size());

        e.reverse();

        e.setDirected(Edge.DIRECTED);
        assertEquals("The number of undirected edges in node n1 is wrong.", 0,
                n1.getUndirectedEdges().size());
        assertEquals("The number of undirected edges in node n2 is wrong.", 0,
                n2.getUndirectedEdges().size());
        assertEquals("The number of directed outedges in node n1 is wrong.", 1,
                n1.getDirectedInEdges().size());
        assertEquals("The number of directed inedges in node n2 is wrong.", 1,
                n2.getDirectedOutEdges().size());
    }

    /**
     * Test if GraphElementNotFoundException will be thrown when setting the
     * source to a node that is not in the same graph as the edge.
     */
    public void testSetSource() {
        Graph g2 = new FastGraph();
        Node wrongNode = g2.addNode();

        try {
            e.setSource(wrongNode);
            fail("GraphElementNotFoundException not thrown.");
        } catch (GraphElementNotFoundException genf) {
        }

        Node n = g.addNode();

        try {
            e.setSource(n);

            boolean foundEdge = false;

            for (Iterator<Node> itr = g.getNodesIterator(); itr.hasNext();) {
                Node nn = itr.next();

                if (nn == n) {
                    assertTrue("e is connected to n.", nn.getEdges()
                            .contains(e));
                    foundEdge = true;
                }
            }

            assertTrue("edge was found from the node.", foundEdge);

            boolean foundNode = false;

            for (Iterator<Edge> itr = g.getEdgesIterator(); itr.hasNext();) {
                Edge ee = itr.next();

                if (ee == e) {
                    assertTrue("e has correct source.", ee.getSource() == n);
                    foundNode = true;
                }

                assertTrue("node was found from the edge.", foundNode);
            }
        } catch (GraphElementNotFoundException gnfe) {
            fail("GraphElementNotFoundException thrown.");
        } catch (IllegalArgumentException iae) {
            fail("IllegalArgumentException thrown.");
        }
    }

    /**
     * Tests if IllegalArgumentException will be thrown when setting the target.
     */
    public void testSetTarget() {
        Graph wrongGraph = new FastGraph();
        Node wrongNode = wrongGraph.addNode();

        try {
            e.setTarget(wrongNode);
            fail("GraphElementNotFoundException not thrown.");
        } catch (GraphElementNotFoundException genf) {
        }

        Node n = g.addNode();

        try {
            e.setTarget(n);

            boolean foundEdge = false;

            for (Iterator<Node> itr = g.getNodesIterator(); itr.hasNext();) {
                Node nn = itr.next();

                if (nn == n) {
                    assertTrue("e is connected to n.", nn.getEdges()
                            .contains(e));
                    foundEdge = true;
                }
            }

            assertTrue("edge was found from the node.", foundEdge);

            boolean foundNode = false;

            for (Iterator<Edge> itr = g.getEdgesIterator(); itr.hasNext();) {
                Edge ee = itr.next();

                if (ee == e) {
                    assertTrue("e has correct target.", ee.getTarget() == n);
                    foundNode = true;
                }

                assertTrue("node was found from the edge.", foundNode);
            }
        } catch (GraphElementNotFoundException gnfe) {
            fail("GraphElementNotFoundException thrown.");
        } catch (IllegalArgumentException iae) {
            fail("IllegalArgumentException thrown.");
        }
    }

    /**
     * Initializes a new graph for every test case.
     */
    @Override
    protected void setUp() {
        g = new FastGraph();
        n1 = g.addNode();
        n2 = g.addNode();

        try {
            e = g.addEdge(n1, n2, Edge.DIRECTED);
        } catch (GraphElementNotFoundException ge) {
            fail("GraphElementNotFoundException illegally thrown.");
        }
    }

    /**
     * Resets the attributes of the test case.
     */
    @Override
    protected void tearDown() {
        g.clear();
        e = null;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
