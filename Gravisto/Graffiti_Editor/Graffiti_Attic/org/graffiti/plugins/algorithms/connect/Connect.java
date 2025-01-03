// =============================================================================
//
//   Connect.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Connect.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.connect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;
import org.graffiti.util.Queue;

/**
 * An implementation of the Connect algorithm.
 * 
 * @version $Revision: 5772 $
 */
public class Connect extends AbstractAlgorithm {

    /** DOCUMENT ME! */
    private Node sourceNode = null;

    /** DOCUMENT ME! */
    private Selection selection;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Connect";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "Connect will start with the only selected node.");

        return new Parameter[] { selParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#attach(Graph)
     */
    @Override
    public void attach(Graph g) {
        this.graph = g;
    }

    // /**
    // * Constructs a new Connect algorithm instance.
    // */
    // public Connect() {
    // // does nothing
    // }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run Connect.");

        if ((selection == null) || (selection.getNodes().size() != 1))
            throw new PreconditionException(
                    "Connect needs exactly one selected node.");

        sourceNode = selection.getNodes().get(0);
    }

    /**
     * The given graph must have at least one node.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if (sourceNode == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        Queue q = new Queue();

        // d contains a mapping from node to an integer, the bfsnum
        Map<Node, Integer> d = new HashMap<Node, Integer>();

        q.addLast(sourceNode);
        d.put(sourceNode, new Integer(0));

        while (!q.isEmpty()) {
            Node v = (Node) q.removeFirst();

            // mark all neighbours and add all unmarked neighbours
            // of v to the queue
            for (Iterator<Node> neighbours = v.getNeighborsIterator(); neighbours
                    .hasNext();) {
                Node neighbour = neighbours.next();

                if (!d.containsKey(neighbour)) {
                    Integer bfsNum = new Integer(d.get(v).intValue() + 1);
                    d.put(neighbour, bfsNum);
                    q.addLast(neighbour);
                }
            }
        }

        Set<Node> result = d.keySet();

        selection.clear();
        selection.addAll(result);

        System.out.println("changed selection to: " + selection.getNodes());

        // g.getListenerManager().transactionStarted(this);
        // for (Iterator nodes = g.getNodesIterator(); nodes.hasNext();) {
        // Node n = (Node) nodes.next();
        // if (d.containsKey(n)) {
        // setLabel(n, d.get(n) + "");
        // }
        // }
        // g.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        selection = null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
