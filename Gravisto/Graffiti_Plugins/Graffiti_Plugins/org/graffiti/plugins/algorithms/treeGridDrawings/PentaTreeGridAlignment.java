// =============================================================================
//
//   PentaTreeGrid.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeGridDrawings;

import java.util.Iterator;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;

/**
 * @author Tom
 * @version $Revision$ $Date$
 */
public class PentaTreeGridAlignment extends AbstractAlgorithm {

    /** chooses the method, which is used to draw the tree */
    private StringSelectionParameter drawingMethod;

    // the depth of the processed tree
    private int depth;

    private Node root = null;

    /**
     * Constructs a new instance.
     */
    public PentaTreeGridAlignment() {
        String[] methods = { "NAIVE", "MODIFIED" };
        drawingMethod = new StringSelectionParameter(methods,
                "Drawing Method:", "<html><p>Naive</p>" + "<p>Modified</p>"
                        + "</html>");
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {

        this.root = GraphChecker.checkTree(this.graph, 5);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if (root == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        this.graph.getListenerManager().transactionStarted(this);

        // creates the visited attribute on all nodes (needed for the
        // calculation of the depth)
        visit(root);
        depth = calculateTreeDepth(root, 1);

        removeBends(root);

        FirstPentaWalk fw = new FirstPentaWalk(depth);

        HexaNode rootHexaNode = rootHandler(root);

        if (drawingMethod.getSelectedValue() == "NAIVE") {
            fw.firstPentaWalk(rootHexaNode, 0, 1);

            SecondWalk sw = new SecondWalk();

            sw.secondWalk(rootHexaNode, 0, 0);
        } else if (drawingMethod.getSelectedValue() == "MODIFIED") {
            fw.firstPentaWalkMod(rootHexaNode, 0, 1);

            SecondWalk sw = new SecondWalk();

            sw.secondWalk(rootHexaNode, 0, 0);
        }

        this.graph.getListenerManager().transactionFinished(this);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "PentaTreeGridAlignment";
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { drawingMethod };
    }

    /*
     * @see
     * org.graffiti.plugin.algorithm.Algorithm#setParameters(org.graffiti.plugin
     * .parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

    /**
     * calculates the tree depth recursively
     * 
     * @param n
     *            a node
     * @param level
     *            current depth
     * @return the depth of the subtree under n
     */
    private int calculateTreeDepth(Node n, int level) {
        n.setBoolean("visited", true);
        int maxDepth = level;
        for (Node x : n.getNeighbors()) {

            if (!x.getBoolean("visited")) {

                maxDepth = Math.max(maxDepth, calculateTreeDepth(x, level + 1));
            }
        }
        return maxDepth;
    }

    private void visit(Node n) {
        n.setBoolean("visited", false);
        for (Node x : n.getAllOutNeighbors()) {
            visit(x);
        }

    }

    /*
     * removes all bends in the tree under root
     */
    private void removeBends(Node root) {

        Iterator<Edge> edgeIt = root.getAllOutEdges().iterator();

        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) e
                    .getAttribute("graphics");
            if (edgeAttr.getNumberOfBends() > 0) {
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        "bends");
                edgeAttr.setBends(bends);
            }

            Node n = e.getTarget();
            removeBends(n);

        }

    }

    /*
     * creates a hexanode for root and invokes nodehandler to (recursively)
     * create HexaNodes for subsequently all nodes in the tree
     */
    private HexaNode rootHandler(Node root) {
        HexaNode rootHexaNode = new HexaNode(root);

        if (rootHexaNode.getNumberOfChildren() >= 1) {
            for (int i = 0; i < rootHexaNode.getNumberOfChildren(); i++) {
                HexaNode hexaNode = new HexaNode((Node) rootHexaNode
                        .getChildren().get(i));
                rootHexaNode.getChildren().set(i, hexaNode);
                nodeHandler(hexaNode);
            }
        }

        return rootHexaNode;
    }

    /*
     * Creates a HexaNode for the children of hexaNode and invokes itself
     * recursively until all nodes in that subtree are covered
     */
    private void nodeHandler(HexaNode hexaNode) {
        if (hexaNode == null)
            return;
        else if (hexaNode.getNumberOfChildren() >= 1) {
            for (int j = 0; j < hexaNode.getChildren().size(); j++) {
                HexaNode n = new HexaNode((Node) hexaNode.getChildren().get(j));
                hexaNode.getChildren().set(j, n);
                nodeHandler(n);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
