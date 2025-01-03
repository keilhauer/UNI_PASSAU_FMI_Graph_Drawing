// =============================================================================
//
//   FordFulkersonAlgorithmTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FordFulkersonAlgorithmTest.java 5776 2010-05-07 18:57:14Z gleissner $

package tests.graffiti.plugins.algorithms.fordfulkerson;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugins.algorithms.fordfulkerson.FordFulkersonAlgorithm;
import org.graffiti.selection.Selection;

/**
 * A test case for the ford fulkerson algorithm.
 * 
 * @version $Revision: 5776 $
 */
public class FordFulkersonAlgorithmTest extends TestCase {
    /** DOCUMENT ME! */
    private Edge testEdge;

    /** The algorithm. */
    private FordFulkersonAlgorithm algo;

    /** A test graph. */
    private Graph graph;

    /** Two test nodes. */
    private Node sinkNode;

    /** Two test nodes. */
    private Node sourceNode;

    /** DOCUMENT ME! */
    private Selection selection;

    /** DOCUMENT ME! */
    private Parameter<?>[] parameters;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public FordFulkersonAlgorithmTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FordFulkersonAlgorithmTest.class);
    }

    /**
     * DOCUMENT ME!
     */
    public void testCheckEdgesCapacitiesExists() {
        testEdge.removeAttribute(".capacity");
        ((SelectionParameter) parameters[FordFulkersonAlgorithm.SELECTION_PARAM])
                .setSelection(selection);
        ((BooleanParameter) parameters[FordFulkersonAlgorithm.ONLY_SELECTION_PARAM])
                .setValue(new Boolean(true));
        ((StringParameter) parameters[FordFulkersonAlgorithm.SOURCE_PARAM])
                .setValue("q");
        ((StringParameter) parameters[FordFulkersonAlgorithm.SINK_PARAM])
                .setValue("s");
        algo.setParameters(parameters);

        try {
            algo.check();
            assertTrue("Didn't recognize that not all edges have "
                    + "the attribute 'capacity'.", false);
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testCheckEdgesCapacityNotNegative() {
        testEdge.setString(".capacity.label", "-5");
        ((SelectionParameter) parameters[FordFulkersonAlgorithm.SELECTION_PARAM])
                .setSelection(selection);
        ((BooleanParameter) parameters[FordFulkersonAlgorithm.ONLY_SELECTION_PARAM])
                .setValue(new Boolean(true));
        ((StringParameter) parameters[FordFulkersonAlgorithm.SOURCE_PARAM])
                .setValue("q");
        ((StringParameter) parameters[FordFulkersonAlgorithm.SINK_PARAM])
                .setValue("s");
        algo.setParameters(parameters);

        try {
            algo.check();
            assertTrue("Didn't recognize that there is an edge with negative "
                    + "capacity value.", false);
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testCheckEdgesUndirected() {
        testEdge.setDirected(false);
        ((SelectionParameter) parameters[FordFulkersonAlgorithm.SELECTION_PARAM])
                .setSelection(selection);
        ((BooleanParameter) parameters[FordFulkersonAlgorithm.ONLY_SELECTION_PARAM])
                .setValue(new Boolean(true));
        ((StringParameter) parameters[FordFulkersonAlgorithm.SOURCE_PARAM])
                .setValue("q");
        ((StringParameter) parameters[FordFulkersonAlgorithm.SINK_PARAM])
                .setValue("s");
        algo.setParameters(parameters);

        try {
            algo.check();
            assertTrue("Didn't recognize that not all edges are directed.",
                    false);
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testCheckSelectionChosen() {
        selection = null;
        ((SelectionParameter) parameters[FordFulkersonAlgorithm.SELECTION_PARAM])
                .setSelection(selection);
        ((BooleanParameter) parameters[FordFulkersonAlgorithm.ONLY_SELECTION_PARAM])
                .setValue(new Boolean(true));
        ((StringParameter) parameters[FordFulkersonAlgorithm.SOURCE_PARAM])
                .setValue("q");
        ((StringParameter) parameters[FordFulkersonAlgorithm.SINK_PARAM])
                .setValue("s");
        algo.setParameters(parameters);

        try {
            algo.check();
            assertTrue("Didn't recognize incorrect input of selection.", false);
        } catch (PreconditionException e) {
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testCheckSinkNode() {
        ((SelectionParameter) parameters[FordFulkersonAlgorithm.SELECTION_PARAM])
                .setSelection(selection);
        ((BooleanParameter) parameters[FordFulkersonAlgorithm.ONLY_SELECTION_PARAM])
                .setValue(new Boolean(true));
        ((StringParameter) parameters[FordFulkersonAlgorithm.SOURCE_PARAM])
                .setValue("q");
        ((StringParameter) parameters[FordFulkersonAlgorithm.SINK_PARAM])
                .setValue("1");
        algo.setParameters(parameters);

        try {
            algo.check();
            assertTrue("Didn't recognize incorrect input of sink node.", false);
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testCheckSourceAndSinkAreDiconnected() {
        graph.deleteEdge(testEdge);
        ((SelectionParameter) parameters[FordFulkersonAlgorithm.SELECTION_PARAM])
                .setSelection(selection);
        ((BooleanParameter) parameters[FordFulkersonAlgorithm.ONLY_SELECTION_PARAM])
                .setValue(new Boolean(true));
        ((StringParameter) parameters[FordFulkersonAlgorithm.SOURCE_PARAM])
                .setValue("q");
        ((StringParameter) parameters[FordFulkersonAlgorithm.SINK_PARAM])
                .setValue("s");
        algo.setParameters(parameters);
        algo.attach(graph);

        try {
            algo.check();
            assertTrue(
                    "Didn't recognize that source and sink nodes are diconnected.",
                    false);
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testCheckSourceEqualsSink() {
        ((SelectionParameter) parameters[FordFulkersonAlgorithm.SELECTION_PARAM])
                .setSelection(selection);
        ((BooleanParameter) parameters[FordFulkersonAlgorithm.ONLY_SELECTION_PARAM])
                .setValue(new Boolean(true));
        ((StringParameter) parameters[FordFulkersonAlgorithm.SOURCE_PARAM])
                .setValue("q");
        ((StringParameter) parameters[FordFulkersonAlgorithm.SINK_PARAM])
                .setValue("q");
        algo.setParameters(parameters);

        try {
            algo.check();
            assertTrue(
                    "Didn't recognize that source and sink nodes are the same.",
                    false);
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testCheckTooManySourceNodes() {
        Node node1 = graph.addNode();
        node1.addAttribute(new LabelAttribute("label", "q"), "");
        ((SelectionParameter) parameters[FordFulkersonAlgorithm.SELECTION_PARAM])
                .setSelection(selection);
        ((BooleanParameter) parameters[FordFulkersonAlgorithm.ONLY_SELECTION_PARAM])
                .setValue(new Boolean(true));
        ((StringParameter) parameters[FordFulkersonAlgorithm.SOURCE_PARAM])
                .setValue("q");
        ((StringParameter) parameters[FordFulkersonAlgorithm.SINK_PARAM])
                .setValue("s");
        algo.setParameters(parameters);
        algo.attach(graph);

        try {
            algo.check();
            assertTrue(
                    "Didn't recognize that there are two nodes have the same "
                            + "label value so that the selection of one of either as source node is"
                            + " ambiguous.", false);
        } catch (PreconditionException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testExecute() {
        selection.remove(testEdge);
        graph.deleteEdge(testEdge);

        Node node1 = graph.addNode();
        Node node2 = graph.addNode();
        Node node3 = graph.addNode();
        Node node4 = graph.addNode();

        node1.addAttribute(new LabelAttribute("label", "1"), "");
        node2.addAttribute(new LabelAttribute("label", "2"), "");
        node3.addAttribute(new LabelAttribute("label", "3"), "");
        node4.addAttribute(new LabelAttribute("label", "4"), "");

        LabelAttribute label1 = new LabelAttribute("capacity", "16");
        LabelAttribute label2 = new LabelAttribute("capacity", "13");
        LabelAttribute label3 = new LabelAttribute("capacity", "4");
        LabelAttribute label4 = new LabelAttribute("capacity", "10");
        LabelAttribute label5 = new LabelAttribute("capacity", "12");
        LabelAttribute label6 = new LabelAttribute("capacity", "14");
        LabelAttribute label7 = new LabelAttribute("capacity", "9");
        LabelAttribute label8 = new LabelAttribute("capacity", "7");
        LabelAttribute label9 = new LabelAttribute("capacity", "20");
        LabelAttribute label10 = new LabelAttribute("capacity", "4");

        Edge e1 = graph.addEdge(sourceNode, node1, true);
        e1.addAttribute(label1, "");

        Edge e2 = graph.addEdge(sourceNode, node3, true);
        e2.addAttribute(label2, "");

        Edge e3 = graph.addEdge(node3, node1, true);
        e3.addAttribute(label3, "");

        Edge e4 = graph.addEdge(node1, node3, true);
        e4.addAttribute(label4, "");

        Edge e5 = graph.addEdge(node1, node2, true);
        e5.addAttribute(label5, "");

        Edge e6 = graph.addEdge(node3, node4, true);
        e6.addAttribute(label6, "");

        Edge e7 = graph.addEdge(node2, node3, true);
        e7.addAttribute(label7, "");

        Edge e8 = graph.addEdge(node4, node2, true);
        e8.addAttribute(label8, "");

        Edge e9 = graph.addEdge(node2, sinkNode, true);
        e9.addAttribute(label9, "");

        Edge e10 = graph.addEdge(node4, sinkNode, true);
        e10.addAttribute(label10, "");

        algo.attach(graph);
        algo.setSourceNode(sourceNode);
        algo.setSinkNode(sinkNode);
        algo.execute();
        assertEquals(23, algo.getNetworkFlow());
    }

    /**
     * DOCUMENT ME!
     */
    public void testExecuteWithSpecialCase() {
        selection.remove(testEdge);
        graph.deleteEdge(testEdge);

        Node node1 = graph.addNode();
        Node node2 = graph.addNode();
        Node node3 = graph.addNode();
        Node node4 = graph.addNode();
        Node node5 = graph.addNode();
        Node node6 = graph.addNode();

        node1.addAttribute(new LabelAttribute("label", "1"), "");
        node2.addAttribute(new LabelAttribute("label", "2"), "");
        node3.addAttribute(new LabelAttribute("label", "3"), "");
        node4.addAttribute(new LabelAttribute("label", "4"), "");
        node5.addAttribute(new LabelAttribute("label", "5"), "");
        node6.addAttribute(new LabelAttribute("label", "6"), "");

        LabelAttribute label1 = new LabelAttribute("capacity", "40");
        LabelAttribute label2 = new LabelAttribute("capacity", "30");
        LabelAttribute label3 = new LabelAttribute("capacity", "10");
        LabelAttribute label4 = new LabelAttribute("capacity", "22");
        LabelAttribute label5 = new LabelAttribute("capacity", "20");
        LabelAttribute label6 = new LabelAttribute("capacity", "17");
        LabelAttribute label7 = new LabelAttribute("capacity", "32");
        LabelAttribute label8 = new LabelAttribute("capacity", "54");
        LabelAttribute label9 = new LabelAttribute("capacity", "50");
        LabelAttribute label10 = new LabelAttribute("capacity", "37");

        Edge e1 = graph.addEdge(sourceNode, node1, true);
        e1.addAttribute(label1, "");

        Edge e2 = graph.addEdge(node1, node2, true);
        e2.addAttribute(label2, "");

        Edge e3 = graph.addEdge(node2, node3, true);
        e3.addAttribute(label3, "");

        Edge e4 = graph.addEdge(node3, sinkNode, true);
        e4.addAttribute(label4, "");

        Edge e5 = graph.addEdge(sourceNode, node4, true);
        e5.addAttribute(label5, "");

        Edge e6 = graph.addEdge(node6, node4, true);
        e6.addAttribute(label6, "");

        Edge e7 = graph.addEdge(node6, sinkNode, true);
        e7.addAttribute(label7, "");

        Edge e8 = graph.addEdge(node4, node5, true);
        e8.addAttribute(label8, "");

        Edge e9 = graph.addEdge(node5, node3, true);
        e9.addAttribute(label9, "");

        Edge e10 = graph.addEdge(node2, node6, true);
        e10.addAttribute(label10, "");

        algo.attach(graph);
        algo.setSourceNode(sourceNode);
        algo.setSinkNode(sinkNode);
        algo.execute();
        assertEquals(50, algo.getNetworkFlow());
    }

    /**
     * DOCUMENT ME!
     */
    public void testExecuteWithSpecialCase2() {
        selection.remove(testEdge);
        graph.deleteEdge(testEdge);

        Node node1 = graph.addNode();
        Node node2 = graph.addNode();

        node1.addAttribute(new LabelAttribute("label", "1"), "");
        node2.addAttribute(new LabelAttribute("label", "2"), "");

        LabelAttribute label6 = new LabelAttribute("capacity", "6");
        LabelAttribute label7 = new LabelAttribute("capacity", "7");
        LabelAttribute label10 = new LabelAttribute("capacity", "10");
        LabelAttribute label3 = new LabelAttribute("capacity", "3");
        LabelAttribute label4 = new LabelAttribute("capacity", "4");
        LabelAttribute label8 = new LabelAttribute("capacity", "8");

        Edge e6 = graph.addEdge(sourceNode, node1, true);
        e6.addAttribute(label6, "");

        Edge e7 = graph.addEdge(sourceNode, node2, true);
        e7.addAttribute(label7, "");

        Edge e10 = graph.addEdge(node2, node1, true);
        e10.addAttribute(label10, "");

        Edge e3 = graph.addEdge(node1, node2, true);
        e3.addAttribute(label3, "");

        Edge e4 = graph.addEdge(node1, sinkNode, true);
        e4.addAttribute(label4, "");

        Edge e8 = graph.addEdge(node2, sinkNode, true);
        e8.addAttribute(label8, "");

        algo.attach(graph);
        algo.setSourceNode(sourceNode);
        algo.setSinkNode(sinkNode);
        algo.execute();
        assertEquals(5, Integer
                .parseInt(((LabelAttribute) e6.getAttribute(Attribute.SEPARATOR
                        + FordFulkersonAlgorithm.FLOW)).getLabel()));
        assertEquals(7, Integer
                .parseInt(((LabelAttribute) e7.getAttribute(Attribute.SEPARATOR
                        + FordFulkersonAlgorithm.FLOW)).getLabel()));

        try {
            assertEquals(5, Integer.parseInt(((LabelAttribute) e10
                    .getAttribute(Attribute.SEPARATOR
                            + FordFulkersonAlgorithm.FLOW)).getLabel()));
            assertTrue(
                    "An error has oocured while calculating the flow of this"
                            + " edge, because it should'nt have any flow and therefore there"
                            + " should'nt be any attribute \"flow\" at this edge",
                    false);
        } catch (NumberFormatException e) {
        } catch (AttributeNotFoundException e) {
        }

        assertEquals(1, Integer
                .parseInt(((LabelAttribute) e3.getAttribute(Attribute.SEPARATOR
                        + FordFulkersonAlgorithm.FLOW)).getLabel()));
        assertEquals(4, Integer
                .parseInt(((LabelAttribute) e4.getAttribute(Attribute.SEPARATOR
                        + FordFulkersonAlgorithm.FLOW)).getLabel()));
        assertEquals(8, Integer
                .parseInt(((LabelAttribute) e8.getAttribute(Attribute.SEPARATOR
                        + FordFulkersonAlgorithm.FLOW)).getLabel()));
        assertEquals(12, algo.getNetworkFlow());
    }

    /**
     * Initializes a StringAttribute for all test cases
     */
    protected void setUp() {
        algo = new FordFulkersonAlgorithm();
        graph = new AdjListGraph();
        sourceNode = graph.addNode();
        sinkNode = graph.addNode();
        sourceNode.addAttribute(new LabelAttribute("label", "q"), "");
        sinkNode.addAttribute(new LabelAttribute("label", "s"), "");
        testEdge = graph.addEdge(sourceNode, sinkNode, true);

        LabelAttribute label = new LabelAttribute("capacity", "1");
        testEdge.addAttribute(label, "");
        selection = new Selection();
        selection.add(sourceNode);
        selection.add(sinkNode);
        selection.add(testEdge);
        parameters = algo.getParameters();
        algo.attach(graph);
    }

    /*
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        algo = null;
        graph = null;
        sourceNode = null;
        sinkNode = null;
        testEdge = null;
        selection = null;
        parameters = null;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
