// =============================================================================
//
//   AdjListNodeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FastNodeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.AdjListNode;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;

/**
 * Contains test cases for the adjacency list implementation of the
 * <code>org.graffiti.graph.Node</code> interface.
 * 
 * @version $Revision: 5771 $
 */
public class FastNodeTest extends TestCase {
    /** The graph for the test cases. */
    private AdjListGraph g = new AdjListGraph();

    /**
     * Constructs a new test case for the <code>AdjListGraph</code> class.
     * 
     * @param name
     *            the name for the test case.
     */
    public FastNodeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AdjListEdgeTest.class);
    }

    /**
     * Tests if all directed ingoing edges are contained in the collection.
     */
    public void testGetDirectedInEdges() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Edge e1 = g.addEdge(n1, n2, Edge.DIRECTED);
        g.addEdge(n3, n2, Edge.DIRECTED);
        assertEquals("number of edges in collection is correct", 2, n2
                .getDirectedInEdges().size());
        assertTrue("ingoing edge e1 in collection", n2.getDirectedInEdges()
                .contains(e1));
        assertTrue("outgoing edge not in collection", !n1.getDirectedInEdges()
                .contains(e1));

        Edge e3 = g.addEdge(n4, n2, Edge.UNDIRECTED);
        assertTrue("undirected ingoing edge e3 not in collection", !n2
                .getDirectedOutEdges().contains(e3));
        g.deleteEdge(e1);
        assertEquals("# outgoing edges updated", 1, n2.getDirectedInEdges()
                .size());
    }

    /**
     * Tests if all directed in edges were found.
     */
    public void testGetDirectedInEdgesIterator() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        g.addEdge(n2, n1, Edge.DIRECTED);
        g.addEdge(n3, n1, Edge.DIRECTED);

        try {
            Collection<Edge> coll = new ArrayList<Edge>(g.getEdges());
            int k = 1;

            for (Iterator<Edge> i = n1.getDirectedInEdgesIterator(); i
                    .hasNext(); k++) {
                Edge tmp = i.next();
                assertTrue("edge " + k + " is contained in inEdgesIterator.",
                        coll.contains(tmp));
                coll.remove(tmp);
            }

            assertTrue("no more inEdges left; all inEdges were in iterator.",
                    coll.isEmpty());
        } catch (GraphElementNotFoundException ge) {
            fail("GraphElementNotFoundException illegally thrown.");
        }
    }

    /**
     * Tests if all undirected outgoing edges are contained in the collection.
     */
    public void testGetDirectedOutEdges() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Edge e1 = g.addEdge(n2, n1, Edge.DIRECTED);
        g.addEdge(n2, n3, Edge.DIRECTED);
        assertEquals("correct # outgoing edges in collection", 2, n2
                .getDirectedOutEdges().size());
        assertTrue("outgoing edge e1 in collection", n2.getDirectedOutEdges()
                .contains(e1));
        assertTrue("ingoing edge not in collection", !n1.getDirectedOutEdges()
                .contains(e1));

        Edge e3 = g.addEdge(n2, n4, Edge.UNDIRECTED);
        assertTrue("undirected outgoing edge e3 not in collection", !n2
                .getDirectedOutEdges().contains(e3));
        g.deleteEdge(e1);
        assertEquals("# outgoing edges updated", 1, n2.getDirectedOutEdges()
                .size());
    }

    /**
     * Tests if all undirected out edges were found.
     */
    public void testGetDirectedOutEdgesIterator() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        g.addEdge(n1, n2, Edge.DIRECTED);
        g.addEdge(n1, n3, Edge.DIRECTED);

        try {
            Collection<Edge> coll = new ArrayList<Edge>(g.getEdges());
            int k = 1;

            for (Iterator<Edge> i = n1.getDirectedOutEdgesIterator(); i
                    .hasNext(); k++) {
                Edge tmp = i.next();
                assertTrue("edge " + k + " is contained in outEdgesIterator.",
                        coll.contains(tmp));
                coll.remove(tmp);
            }

            assertTrue("no more outEdges left; all outEdges were in iterator.",
                    coll.isEmpty());
        } catch (GraphElementNotFoundException ge) {
            fail("GraphElementNotFoundException illegally thrown.");
        }
    }

    /**
     * Tests if all in/outgoing, (un)directed edges are in the collection.
     */
    public void testGetEdges() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Edge e1 = g.addEdge(n2, n1, Edge.UNDIRECTED);
        Edge e2 = g.addEdge(n3, n2, Edge.DIRECTED);
        Edge e3 = g.addEdge(n4, n2, Edge.UNDIRECTED);
        assertTrue("undirected outedge found in collection", ((AdjListNode) n2)
                .getEdges().contains(e1));
        assertTrue("undirected inedge found in collection", ((AdjListNode) n2)
                .getEdges().contains(e3));
        assertTrue("directed inedge found in collection", ((AdjListNode) n2)
                .getEdges().contains(e2));
        assertTrue("directed outedge found in collection", ((AdjListNode) n3)
                .getEdges().contains(e2));
        assertTrue("edge from n1 to n2 not found in collection for n4",
                !((AdjListNode) n4).getEdges().contains(e1));
    }

    /**
     * Tests if added edges will be contained in the edges iterator.
     */
    public void testGetEdgesIterator() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();

        try {
            Edge e = g.addEdge(n1, n2, Edge.DIRECTED);
            Iterator<Edge> i = n1.getEdgesIterator();
            assertTrue("e is contained in allEdgesIterator.", i.next() == e);
        } catch (GraphElementNotFoundException ge) {
            fail("GraphElementNotFoundException illegally thrown.");
        }
    }

    /**
     * Tests if the indegree of a node is correct.
     */
    public void testGetInDegree() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        g.addEdge(n2, n1, Edge.UNDIRECTED);
        g.addEdge(n3, n2, Edge.DIRECTED);
        assertTrue("indegree of n2 is correct", 2 == n2.getInDegree());
        assertTrue("indegree of n4 is correct", 0 == n4.getInDegree());
        assertTrue("indegree of n1 is correct", 1 == n1.getInDegree());

        g.addEdge(n4, n2, Edge.UNDIRECTED);
        assertTrue("indegree of n2 is correct", 3 == n2.getInDegree());
        assertTrue("indegree of n4 is correct", 1 == n4.getInDegree());
    }

    /**
     * Tests if collection contains all but only in-neighbors.
     */
    public void testGetInNeighbors() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Node n5 = g.addNode();
        g.addEdge(n2, n1, Edge.DIRECTED);
        g.addEdge(n3, n2, Edge.DIRECTED);
        g.addEdge(n4, n2, Edge.UNDIRECTED);
        g.addEdge(n5, n2, Edge.UNDIRECTED);
        Collection<Node> inList = n2.getInNeighbors();
        assertTrue("in-neighbor n3 found", inList.contains(n3));
        assertTrue("in-neighbor n4 found", inList.contains(n4));
        assertTrue("in-neighbor n5 found", inList.contains(n5));
        assertTrue("out-neighbor not found in in-neighbor list", !inList
                .contains(n1));
    }

    /**
     * Tests if all the in-neighbor-nodes are contained in the iterator.
     */
    public void testGetInNeighborsIterator() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Node n5 = g.addNode();
        g.addEdge(n2, n1, Edge.UNDIRECTED);
        g.addEdge(n3, n2, Edge.DIRECTED);
        g.addEdge(n4, n2, Edge.UNDIRECTED);
        g.addEdge(n5, n3, Edge.DIRECTED);
        Collection<Node> inNeighbors = n2.getInNeighbors();

        for (Iterator<Node> neighborIt = n2.getInNeighborsIterator(); neighborIt
                .hasNext();) {
            Node n = neighborIt.next();
            assertTrue("node found in the iterator.", inNeighbors.contains(n));

            if (!inNeighbors.remove(n)) {
                fail("Unable to remove node.");
            }
        }

        assertTrue("all nodes reached with iterator", inNeighbors.size() == 0);
    }

    /**
     * Tests if collection contains all neighbor nodes.
     */
    public void testGetNeighbors1() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Node n5 = g.addNode();
        g.addEdge(n2, n1, Edge.UNDIRECTED);
        g.addEdge(n3, n2, Edge.DIRECTED);
        g.addEdge(n4, n2, Edge.UNDIRECTED);
        g.addEdge(n5, n3, Edge.DIRECTED);
        Collection<Node> neighbors = n2.getNeighbors();
        assertTrue("neighbor n1 found", neighbors.contains(n1));
        assertTrue("neighbor n3 found", neighbors.contains(n3));
        assertTrue("neighbor n4 found", neighbors.contains(n4));
        assertTrue("no-neighbor not found in neighbor list", !(neighbors
                .contains(n5)));
    }

    /**
     * Tests if all neighbor nodes are in the collection.
     */
    public void testGetNeighbors2() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Node n5 = g.addNode();
        g.addEdge(n2, n1, Edge.UNDIRECTED);
        g.addEdge(n3, n2, Edge.DIRECTED);
        g.addEdge(n4, n2, Edge.UNDIRECTED);
        assertEquals("correct # neighbor nodes in collection", 3,
                ((AdjListNode) n2).getNeighbors().size());
        assertTrue("node n1 in collection", ((AdjListNode) n2).getNeighbors()
                .contains(n1));
        assertTrue("node n3 in collection", ((AdjListNode) n2).getNeighbors()
                .contains(n3));
        assertTrue("node n4 in collection", ((AdjListNode) n2).getNeighbors()
                .contains(n4));

        g.addEdge(n5, n4, Edge.DIRECTED);
        assertTrue("node n5 not in collection", !((AdjListNode) n2)
                .getNeighbors().contains(n5));
        g.deleteNode(n1);
        assertTrue("deleted node n1 no longer in collection",
                !((AdjListNode) n2).getNeighbors().contains(n1));
    }

    /**
     * Tests if all the neighbor nodes are contained in the iterator.
     */
    public void testGetNeighborsIterator() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Node n5 = g.addNode();
        g.addEdge(n2, n1, Edge.UNDIRECTED);
        g.addEdge(n3, n2, Edge.DIRECTED);
        g.addEdge(n4, n2, Edge.UNDIRECTED);
        g.addEdge(n5, n3, Edge.DIRECTED);
        Collection<Node> neighbors = n2.getNeighbors();

        for (Iterator<Node> neighborIt = n2.getNeighborsIterator(); neighborIt
                .hasNext();) {
            Node n = neighborIt.next();
            assertTrue("node found in the iterator.", neighbors.contains(n));

            if (!neighbors.remove(n)) {
                fail("Unable to remove node.");
            }
        }

        assertTrue("all nodes reached with iterator", neighbors.size() == 0);
    }

    /**
     * Tests if the outdegree of a node is correct.
     */
    public void testGetOutDegree() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        g.addEdge(n1, n2, Edge.UNDIRECTED);
        g.addEdge(n2, n3, Edge.DIRECTED);
        assertTrue("outdegree of n2 is correct", 2 == n2.getOutDegree());
        assertTrue("outdegree of n4 is correct", 0 == n4.getOutDegree());
        assertTrue("outdegree of n1 is correct", 1 == n1.getOutDegree());

        g.addEdge(n2, n4, Edge.UNDIRECTED);
        assertTrue("outdegree of n2 is correct", 3 == n2.getOutDegree());
        assertTrue("outdegree of n4 is correct", 1 == n4.getOutDegree());
    }

    /**
     * Tests if the collection contains all but only out-neighbors.
     */
    public void testGetOutNeighbors() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Node n5 = g.addNode();
        g.addEdge(n1, n2, Edge.DIRECTED);
        g.addEdge(n2, n3, Edge.DIRECTED);
        g.addEdge(n2, n4, Edge.UNDIRECTED);
        g.addEdge(n5, n2, Edge.UNDIRECTED);
        Collection<Node> outList = n2.getOutNeighbors();
        assertTrue("out-neighbor n3 found", outList.contains(n3));
        assertTrue("out-neighbor n4 found", outList.contains(n4));
        assertTrue("out-neighbor n5 found", outList.contains(n5));
        assertTrue("in-neighbor not found in collection for out-neighbors",
                !(outList.contains(n1)));
    }

    /**
     * Tests if all the out-neighbor-nodes are contained in the iterator.
     */
    public void testGetOutNeighborsIterator() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Node n5 = g.addNode();
        g.addEdge(n2, n1, Edge.UNDIRECTED);
        g.addEdge(n2, n3, Edge.DIRECTED);
        g.addEdge(n4, n2, Edge.UNDIRECTED);
        g.addEdge(n5, n3, Edge.DIRECTED);
        Collection<Node> outNeighbors = n2.getOutNeighbors();

        for (Iterator<Node> neighborIt = n2.getOutNeighborsIterator(); neighborIt
                .hasNext();) {
            Node n = neighborIt.next();
            assertTrue("node found in the iterator.", outNeighbors.contains(n));

            if (!outNeighbors.remove(n)) {
                fail("Unable to remove node.");
            }
        }

        assertTrue("all nodes reached with iterator", outNeighbors.size() == 0);
    }

    /**
     * Tests if all undirected edges are contained in the collection.
     */
    public void testGetUndirectedEdges() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Edge e1 = g.addEdge(n2, n1, Edge.UNDIRECTED);
        assertEquals("correct # undirected edges in collection "
                + "(for source node)", 1, n2.getUndirectedEdges().size());
        assertEquals("correct # undirected edges in collection "
                + "(for target node)", 1, n1.getUndirectedEdges().size());

        Edge e2 = g.addEdge(n3, n2, Edge.DIRECTED);
        assertTrue("undirected (out)edge is in collection", n2
                .getUndirectedEdges().contains(e1));
        assertTrue("undirected (in)edge is in collection", n1
                .getUndirectedEdges().contains(e1));
        assertTrue("directed (out)edge is not in collection", !n3
                .getUndirectedEdges().contains(e2));
        assertTrue("directed (in)edge is not in collection", !n2
                .getUndirectedEdges().contains(e2));

        Edge e3 = g.addEdge(n4, n2, Edge.UNDIRECTED);
        assertTrue("undirected edge still in collection", n2
                .getUndirectedEdges().contains(e1));
        assertTrue("undirected edge added to collection", n2
                .getUndirectedEdges().contains(e3));
    }

    /**
     * Tests if all undirected edges are contained in the iterator.
     */
    public void testGetUndirectedEdgesIterator() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        Edge e1 = g.addEdge(n2, n1, Edge.UNDIRECTED);
        g.addEdge(n2, n3, Edge.DIRECTED);
        g.addEdge(n2, n4, Edge.DIRECTED);
        Iterator<Edge> itr = n2.getUndirectedEdgesIterator();
        assertTrue("iterator is not empty.", itr.hasNext());

        Edge e = itr.next();
        assertTrue("e1 is in iterator.", e == e1);
        assertTrue("iterator does not contain more elements.", !itr.hasNext());
    }

    /**
     * Tests if collection contains all but only undirected neighbor nodes.
     */
    public void testGetUndirectedNeighbors() {
        Node n1 = g.addNode();
        Node n2 = g.addNode();
        Node n3 = g.addNode();
        Node n4 = g.addNode();
        g.addEdge(n1, n3, Edge.UNDIRECTED);
        g.addEdge(n2, n3, Edge.DIRECTED);
        g.addEdge(n2, n4, Edge.UNDIRECTED);
        Collection<Node> undirList = n2.getUndirectedNeighbors();
        assertTrue("undirected neighbor n4 found", undirList.contains(n4));
        assertTrue("directed neighbor n3 not found", !(undirList.contains(n3)));
        assertTrue("undirected no-neighbor n1 not found", !(undirList
                .contains(n1)));
    }

    /**
     * Initializes a new graph for every test case.
     */
    @Override
    protected void setUp() {
        g = new AdjListGraph();
    }

    /**
     * Actions to be done at tear down.
     */
    @Override
    protected void tearDown() {
        g.clear();
    }

    /**
     * Tests if the copy method properly works.
     */

    // public void testCopy() {
    // Node n = g.addNode();
    // int id = 4711;
    // IntegerAttribute ia = new IntegerAttribute("testid", id);
    // n.addAttribute(ia, "");
    // Node clonedNode = (AdjListNode)n.copy();
    // assertEquals("cloned node has the same id.", id,
    // ((IntegerAttribute)
    // clonedNode.getAttribute("testid")).getInteger());
    // }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
