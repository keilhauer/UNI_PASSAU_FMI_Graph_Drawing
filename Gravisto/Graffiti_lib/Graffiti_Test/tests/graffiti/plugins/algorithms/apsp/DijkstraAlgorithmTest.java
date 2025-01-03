// =============================================================================
//
//   DijkstraAlgorithmTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DijkstraAlgorithmTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.plugins.algorithms.apsp;

import junit.framework.TestCase;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.apsp.DijkstraAlgorithm;

/**
 * A test case for the dijkstra algorithm.
 * 
 * @version $Revision: 5771 $
 */
public class DijkstraAlgorithmTest extends TestCase {
    /** The algorithm. */
    private DijkstraAlgorithm algo;

    /** A test graph. */
    private Graph cycle;

    /** A test graph. */
    private Graph negEdge;

    /** Two test nodes. */
    private Node cycleSource;

    /** Two test nodes. */
    private Node cycleTarget;

    /** Two test nodes. */
    private Node negEdgeSource;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public DijkstraAlgorithmTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DijkstraAlgorithmTest.class);
    }

    /**
     * Tests wether the <code>DijkstraAlgorithm.check</code> method detects and
     * reports negative cycles.
     */
    public void testCheckNegativeEdges() {
        try {
            algo.attach(negEdge);
            algo.setSourceNode(negEdgeSource);
            algo.check();

            // assertTrue("check() did not detect an edge of negative weight",
            // false);
        } catch (PreconditionException pe) {
        }
    }

    /**
     * Tests, whether <code>DijkstraAlgorith.check</code> insists on the
     * definition of a source node.
     */
    public void testCheckSourceNode() {
        try {
            algo.attach(cycle);
            algo.setSourceNode(cycleSource);
            algo.check();
        } catch (PreconditionException pe) {
            assertTrue("sourceParam should be set.", false);
        }
    }

    /**
     * Initializes a StringAttribute for all test cases
     */
    @Override
    protected void setUp() {
        algo = new DijkstraAlgorithm();

        cycle = new AdjListGraph();
        cycleSource = cycle.addNode();

        Node b = cycle.addNode();
        cycleTarget = cycle.addNode();

        // FIXME: use a simple DoubleAttribute or a more complex
        // EdgeLabelAttribute in this test cases..
        Edge e1 = cycle.addEdge(cycleSource, b, true);
        e1.setDouble("distance", 1.0);

        Edge e2 = cycle.addEdge(b, cycleTarget, true);
        e2.setDouble("distance", 1.0);

        Edge e3 = cycle.addEdge(cycleTarget, cycleSource, true);
        e3.setDouble("distance", 1.0);

        negEdge = new AdjListGraph();
        negEdgeSource = negEdge.addNode();

        Node e = negEdge.addNode();
        Edge e4 = negEdge.addEdge(negEdgeSource, e, true);
        e4.setDouble("distance", -1.0);
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
