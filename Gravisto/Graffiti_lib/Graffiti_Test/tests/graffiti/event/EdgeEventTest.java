// =============================================================================
//
//   EdgeEventTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeEventTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import junit.framework.TestCase;

import org.graffiti.event.EdgeEvent;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Tests class EdgeEvent.
 * 
 * @version $Revision: 5771 $
 */
public class EdgeEventTest extends TestCase {

    /** DOCUMENT ME! */
    private TestEdgeListener edgeListener;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public EdgeEventTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(EdgeEventTest.class);
    }

    /**
     * Test the number of times, a node event method has been called and the
     * order of methods invocation.
     */
    public void testEdgeEventCalled() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictEdgeListener(edgeListener);

        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Edge e = g.addEdge(n1, n2, Edge.DIRECTED);

        e.reverse();
        assertEquals("Didn't call the edge listener 6 times (two times for "
                + "pre/postSourceNodeChanged, two times for "
                + "pre/postTargetNodeChanged and two times for "
                + "pre/postEdgeReversed.", 6, edgeListener.called);

        assertEquals("Did not call preEdgeReversed first.", "preEdgeReversed",
                edgeListener.methodsCalled.get(0));

        assertEquals("Did not call preSourceNodeChanged after "
                + "preEdgeReversed.", "preSourceNodeChanged",
                edgeListener.methodsCalled.get(1));

        assertEquals("Did not call postSourceNodeChanged after "
                + "preSourceNodeChanged.", "postSourceNodeChanged",
                edgeListener.methodsCalled.get(2));

        assertEquals("Did not call postUndirectedEdgeAdded after "
                + "postSourceNodeChanged.", "preTargetNodeChanged",
                edgeListener.methodsCalled.get(3));

        assertEquals("Did not call postUndirectedEdgeAdded after "
                + "preTargetNodeChanged.", "postTargetNodeChanged",
                edgeListener.methodsCalled.get(4));

        assertEquals("Did not call postUndirectedEdgeAdded after "
                + "postTargetNodeChanged.", "postEdgeReversed",
                edgeListener.methodsCalled.get(5));

        e.setDirected(false);

        assertEquals("Didn't call the edge listener another 2 times.", 8,
                edgeListener.called);

        assertEquals("Did not call preDirectedChanged first",
                "preDirectedChanged", edgeListener.methodsCalled.get(6));

        assertEquals("Did not call postUndirectedEdgeAdded after "
                + "preDirectedChanged.", "postDirectedChanged",
                edgeListener.methodsCalled.get(7));
    }

    /**
     * Tests the EdgeEvent constructor.
     */
    public void testEdgeEventConstructor() {
        Graph graph = new AdjListGraph();

        Node node = graph.addNode();
        Edge edge = graph.addEdge(node, node, Edge.DIRECTED);

        EdgeEvent event = new EdgeEvent(edge);

        assertEquals("Failed to create a correct EdgeEvent."
                + "The edge reference is not the same", edge, event.getEdge());
    }

    /**
     * Sets up the text fixture. Called before every test case method.
     */
    @Override
    protected void setUp() {
        edgeListener = new TestEdgeListener();
    }

    /**
     * Tears down the text fixture. Called after all test case methods hava run.
     */
    @Override
    protected void tearDown() {
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
