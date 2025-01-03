// =============================================================================
//
//   RepulsiveNodeNodeForce.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RepulsiveNodeNodeForce.java 1289 2006-06-12 05:52:18Z matzeder $

package org.graffiti.plugins.algorithms.labeling.forces;

import java.util.Collection;

import org.graffiti.plugins.algorithms.labeling.FRLabelNode;
import org.graffiti.plugins.algorithms.labeling.SELabelingAlgorithmParameters;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;
import org.graffiti.plugins.algorithms.springembedderFR.GridSquare;

/**
 * Class for calculation the repulsive force between nodes.
 * 
 * @author matzeder
 * @version $Revision: 1289 $ $Date: 2006-06-12 07:52:18 +0200 (Mon, 12 Jun
 *          2006) $
 */
public class RepulsiveNodeNodeForce extends AbstractSEForce {

    /**
     * Creates a force for node-node repulsion.
     * 
     * @param p
     *            Parameters of the algorithms.
     */
    public RepulsiveNodeNodeForce(SELabelingAlgorithmParameters p) {
        super(p);
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.springembedderFR.AbstractSEForces#
     * calculateForce()
     */
    @Override
    public void calculateForce() {
        // calculate the repulsive forces, with or without grid
        if (parameters.isGridVariant) {
            calcRepulsiveForcesWithGrid();
        } else {
            calcRepulsiveForcesWithoutGrid();
        }
    }

    /**
     * Calculation of the repulsive forces with the grid variant. Important is,
     * the grid variant cannot be used with big nodes.
     */
    private void calcRepulsiveForcesWithGrid() {

        // all nodes of the graph
        for (FRNode currNode1 : parameters.fRGraph.getFRNodes()) {

            // only calculate the forces onto the node if movable
            if (currNode1.isMovable()) {

                // the neighboured grid squares of currNode1 (8 neighbours and
                // the
                // one, the node is in)
                Collection<GridSquare> gridSquares = parameters.grid
                        .getNeighbourGrids(currNode1).values();

                // all neighboured grid squares
                for (GridSquare currGridSquare : gridSquares) {
                    Collection<FRNode> nodesInGridSquare = currGridSquare
                            .getNodes();

                    // the nodes in the actual grid square
                    for (FRNode currNode2 : nodesInGridSquare) {

                        // has to be another one
                        if (currNode1 != currNode2) {
                            // currNode1 and currNode2 are onto the same
                            // coordinate,
                            // one of the two should be moved a little bit
                            // (here currNode2 is moved)
                            while (currNode1.getXPos() == currNode2.getXPos()
                                    && currNode1.getYPos() == currNode2
                                            .getYPos()) {
                                // move a little bit in a random direction
                                moveRandom(currNode2);
                            }

                            // vector between currNode1 and currNode2
                            GeometricalVector delta = new GeometricalVector(
                                    currNode1, currNode2);

                            // x, y - dist between currNode1 and currNode2
                            double x = delta.getX();
                            double y = delta.getY();

                            // if dist smaller than radius, then force is
                            // calculated
                            // (square is faster calculated as sqrt)
                            if ((x * x + y * y) < parameters.grid
                                    .getGridLength()
                                    * parameters.grid.getGridLength()) {

                                // the new force vector for currNode1 and
                                // currNode2
                                GeometricalVector newForceVector = calculateRepulsiveForceVector(
                                        currNode1, currNode2, delta);

                                // the existing force vector for the repulsive
                                // force
                                GeometricalVector existingForceVector = currNode1
                                        .getForce(REP_FORCE);

                                // adding the new force vector to the existing
                                // force
                                // vector
                                currNode1.setForces(REP_FORCE,
                                        GeometricalVector.add(
                                                existingForceVector,
                                                newForceVector));

                            } // end if (< radius)

                        } // end if (currNode1 != currNode2)

                    } // end while (nodesInGridSquareIt)

                } // end for (gridSquares)

            } // end while (gridSquaresIt)

        } // end while (nodesIt)

    }

    /**
     * Calculation of the repulsive forces (without the grid variant). All pairs
     * of nodes are considered.
     */
    protected void calcRepulsiveForcesWithoutGrid() {

        // all nodes of the graph
        for (FRNode currNode1 : parameters.fRGraph.getFRNodes()) {
            // the force for currNode1 only has to be calculated, if the
            // currNode1 is movable
            if (currNode1.isMovable()) {

                // run through all nodes of the graph
                for (FRNode currNode2 : parameters.fRGraph.getFRNodes()) {
                    if (currNode1 != currNode2) {

                        // if currNode1 and currNode2 are at the same coordinate
                        while (currNode1.getXPos() == currNode2.getXPos()
                                && currNode1.getYPos() == currNode2.getYPos()) {
                            moveRandom(currNode2);
                        }

                        GeometricalVector delta;

                        // if phase is the quenching phase, then do not
                        // calculate with the sizes of the nodes, but calculate
                        // trivially
                        if (parameters.phase == QUENCHING_PHASE) {
                            delta = new GeometricalVector(currNode1, currNode2);
                        } else {
                            delta = calculationDistanceVector(currNode1,
                                    currNode2);

                        }
                        // the new force vector of currNode1
                        GeometricalVector newForceVector;

                        if (parameters.isCalculationWangMiyamoto) {
                            newForceVector = calculateRepulsiveForceVectorWang(
                                    currNode1, currNode2, delta);
                        } else {
                            newForceVector = calculateRepulsiveForceVector(
                                    currNode1, currNode2, delta);
                        }

                        // existing force vector of currNode1
                        GeometricalVector existingForceVector = currNode1
                                .getForce(REP_FORCE);

                        // adding the new force vector to the existing force
                        // vector
                        // of currNode1
                        currNode1.setForces(REP_FORCE, GeometricalVector.add(
                                existingForceVector, newForceVector));

                        // System.out.println("Repulsion force on "
                        // + currNode1.getLabel() + " from "
                        // + currNode2.getLabel() + ": ("
                        // + newForceVector.getX() + ", "
                        // + newForceVector.getY() + ")");

                    } // end if (currNode1 != currNode2)

                } // end while (nodesIt2)

            } // end if (movable)

        } // end while (nodesIt)

    }

    /**
     * Calculates the force vector for the repulsive force between two nodes,
     * with distance vector delta. If big nodes intersect, then disance vector
     * is (0,0).
     * 
     * @param currNode1
     *            First given node.
     * @param currNode2
     *            Second given node.
     * @param delta
     *            Distance between nodes.
     * @return The calculated force vector.
     */
    protected GeometricalVector calculateRepulsiveForceVector(FRNode currNode1,
            FRNode currNode2, GeometricalVector delta) {

        double intersectionConstant = 1d;
        // nodes intersect (then delta simulates a very small distance
        // between
        // the nodes
        if (delta.getX() == 0.0 && delta.getY() == 0.0d) {
            // System.out.println("rep force overlap");
            delta = new GeometricalVector(currNode1, currNode2);

            intersectionConstant = 50d;
        }

        double x = delta.getX();
        double y = delta.getY();
        double repForce;

        // calculation 2x, once with sqrt, and once without (2nd possibility
        // is
        // faster)
        if ((currNode1 instanceof FRLabelNode)
                || (currNode2 instanceof FRLabelNode)) {
            if ((currNode1 instanceof FRLabelNode)
                    && (currNode2 instanceof FRLabelNode)) {
                if (!parameters.isCalculationWithForster) {

                    // euklid. distance between currNode1 and currNode2
                    double deltaLength = GeometricalVector.getLength(delta);
                    repForce = calcRepForceNotForsterBothLabels(deltaLength);
                    delta = GeometricalVector.div(delta, deltaLength);

                } else {
                    repForce = calcRepForceBothLabels(new GeometricalVector(x,
                            y));
                }
            } else {
                if (!parameters.isCalculationWithForster) {

                    // euklid. distance between currNode1 and currNode2
                    double deltaLength = GeometricalVector.getLength(delta);
                    repForce = calcRepForceNotForsterToLabel(deltaLength);
                    delta = GeometricalVector.div(delta, deltaLength);

                } else {
                    repForce = calcRepForceToLabel(new GeometricalVector(x, y));
                }
            }
        } else {
            if (!parameters.isCalculationWithForster) {

                // euklid. distance between currNode1 and currNode2
                double deltaLength = GeometricalVector.getLength(delta);
                repForce = calcRepForceNotForster(deltaLength);
                delta = GeometricalVector.div(delta, deltaLength);

            } else {
                repForce = calcRepForce(new GeometricalVector(x, y));
            }
        }

        repForce *= intersectionConstant;

        // System.out.println("repForce: " + repForce);
        return GeometricalVector.mult(delta, repForce);

    }

    /**
     * Calculates the force for the given distance vector. This method doesn't
     * use the calculation of the square root (Forster).
     * 
     * @param distVector
     *            The given distance vector.
     * @return The force value.
     */
    protected double calcRepForce(GeometricalVector distVector) {

        return parameters.repulsionConstantNodeNode
                * parameters.optimalNodeDistance
                * parameters.optimalNodeDistance
                * parameters.optimalNodeDistance
                / ((distVector.getX() * distVector.getX() + distVector.getY()
                        * distVector.getY()));

    }

    protected double calcRepForceToLabel(GeometricalVector distVector) {
        return parameters.repulsionConstantNodeToLabel
                * parameters.optimalNodeDistanceToLabel
                * parameters.optimalNodeDistanceToLabel
                * parameters.optimalNodeDistanceToLabel
                / ((distVector.getX() * distVector.getX() + distVector.getY()
                        * distVector.getY()));
    }

    protected double calcRepForceBothLabels(GeometricalVector distVector) {
        return parameters.repulsionConstantBothLabels
                * parameters.optimalNodeDistanceToLabel
                * parameters.optimalNodeDistanceToLabel
                * parameters.optimalNodeDistanceToLabel
                / ((distVector.getX() * distVector.getX() + distVector.getY()
                        * distVector.getY()));
    }

    /**
     * Calculates the force for the given distance.
     * 
     * @param deltaLength
     *            The given distance.
     * @return The force value.
     */
    protected double calcRepForceNotForster(double deltaLength) {

        return parameters.repulsionConstantNodeNode
                * parameters.optimalNodeDistance
                * parameters.optimalNodeDistance
                * parameters.optimalNodeDistance / (deltaLength);

    }

    protected double calcRepForceNotForsterToLabel(double deltaLength) {

        return parameters.repulsionConstantNodeToLabel
                * parameters.optimalNodeDistanceToLabel
                * parameters.optimalNodeDistanceToLabel
                * parameters.optimalNodeDistanceToLabel / (deltaLength);

    }

    protected double calcRepForceNotForsterBothLabels(double deltaLength) {

        return parameters.repulsionConstantBothLabels
                * parameters.optimalNodeDistanceToLabel
                * parameters.optimalNodeDistanceToLabel
                * parameters.optimalNodeDistanceToLabel / (deltaLength);

    }

    /**
     * Calculates the force vector with the method of Wang and Miyamoto.
     * 
     * @param currNode1
     *            First given node.
     * @param currNode2
     *            Second given node.
     * @param distVector
     *            The distance vector.
     * @return The calculated force vector.
     */
    protected GeometricalVector calculateRepulsiveForceVectorWang(
            FRNode currNode1, FRNode currNode2, GeometricalVector distVector) {
        double overlapConstant = 1.0;
        // overlap
        if (distVector.getX() == 0.0 && distVector.getY() == 0.0d) {
            // System.out.println("wang rep force overlap");
            distVector = new GeometricalVector(currNode1, currNode2);
            overlapConstant = 50.0d;

        }
        double x = distVector.getX();
        double y = distVector.getY();
        double repForce;

        repForce = calcRepForce(new GeometricalVector(x, y));
        repForce *= overlapConstant;
        return GeometricalVector.mult(distVector, repForce);

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
