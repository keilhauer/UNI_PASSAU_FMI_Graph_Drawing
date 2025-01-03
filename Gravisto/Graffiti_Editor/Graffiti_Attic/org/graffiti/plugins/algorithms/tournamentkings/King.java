// =============================================================================
//
//   King.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: King.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.tournamentkings;

import java.awt.Color;

import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * Searches a king in a tournament graph. A king is a node that beats every
 * other node in the tournament graph. <br>
 * A tournament is a complete directed graph, that means there is exact one edge
 * between every node u and every node v.<br>
 * A node u beats a node v, if there is an edge (u, v) or there is a node w with
 * edges (u, w) and (w, v). <br>
 * Every tournament has a king (at least one). The node with the maximal out
 * degree is a king in the tournament.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5772 $ $Date: 2006-04-27 19:34:22 +0200 (Do, 27 Apr 2006)
 *          $
 */
public class King extends AbstractAlgorithm {

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Search a King";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (!Utils.checkIfGraphIsTournament(graph)) {
            errors.add("The given graph is not a tournament.");
        }

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        Node maxOutDegreeNode = Utils.getNodeWithMaxOutDegree(graph.getNodes());
        NodeGraphicAttribute nga = (NodeGraphicAttribute) maxOutDegreeNode
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        ColorAttribute ca = nga.getFillcolor();
        ca.setColor(Color.red);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
