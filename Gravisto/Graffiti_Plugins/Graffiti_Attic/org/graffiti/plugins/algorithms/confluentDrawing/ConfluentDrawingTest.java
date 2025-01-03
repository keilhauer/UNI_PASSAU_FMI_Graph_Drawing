// =============================================================================
//
//   ConfluentDrawingTest.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConfluentDrawingTest.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.confluentDrawing;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * @author zhang
 * @version $Revision: 5772 $ $Date: 2007-07-18 16:12:43 +0200 (Mi, 18 Jul 2007)
 *          $
 */
public class ConfluentDrawingTest {

    // main method
    public static void main(String[] args) {
        ConfluentDrawingTest cdTest = new ConfluentDrawingTest();
        // geoTest.graphClass(1);
        // geoTest.graphClass(2);
        // geoTest.graphClass(3);
        // geoTest.graphClassDunne();
        // geoTest.graphClassDunne();
        cdTest.test();
    }

    public int getRandomPos(int length) {
        double random = Math.random();
        Float flo = new Float((length - 1) * random);
        int pos = Math.round(flo.floatValue());
        return pos;
    }

    public void addNodes(int nodeNumber, Graph graph) {
        Node[] node = new Node[nodeNumber];

        HashMap xCoor = new HashMap();
        HashMap yCoor = new HashMap();

        // start a transaction
        graph.getListenerManager().transactionStarted(this);

        // generate nodes and assign coordinates to them
        for (int i = 0; i < nodeNumber; ++i) {
            node[i] = graph.addNode();

            double x = getXYCoordinate(xCoor);
            double y = getXYCoordinate(yCoor);

            HashMapAttribute cc = new HashMapAttribute("graphics");

            node[i].addAttribute(cc, "");

            CoordinateAttribute ca = new CoordinateAttribute(
                    GraphicAttributeConstants.COORDINATE, new Point2D.Double(x,
                            y));

            node[i].addAttribute(ca, GraphicAttributeConstants.GRAPHICS);

        }

        graph.getListenerManager().transactionFinished(this);

    }

    private double getXYCoordinate(HashMap xyCoor) {
        boolean seek = true;
        double result = 0.0;
        while (seek) {
            result = Math.random();
            result = result * 1000;
            Double key = new Double(result);
            if (!xyCoor.containsKey(key)) {
                xyCoor.put(key, null);
                seek = false;
            }
        }
        return result;
    }

    public void addEdge(Graph graph, int edgeNumber) {

        List nodesList = graph.getNodes();
        HashMap adjacentEdge = new HashMap();

        graph.getListenerManager().transactionStarted(this);

        while (edgeNumber > 0) {
            int pos = getRandomPos(nodesList.size());
            Node n1 = (Node) nodesList.get(pos);
            pos = getRandomPos(nodesList.size());
            Node n2 = (Node) nodesList.get(pos);
            if (!n1.getAllInNeighbors().contains(n2) && !n1.equals(n2)) {
                graph.addEdge(n1, n2, false);
                --edgeNumber;
            } else {
                continue;
            }

        }
        graph.setDirected(false);
        graph.getListenerManager().transactionFinished(this);
    }

    private Graph generator(int nodeNr, double procentOfEdge) {

        // add nodes

        Graph graph = new AdjListGraph();

        Node[] nodes = new Node[nodeNr];

        graph.getListenerManager().transactionStarted(this);

        this.addNodes(nodeNr, graph);
        int edgeNr = new Integer((int) Math.round((nodeNr * (nodeNr - 1)) / 2
                * procentOfEdge)).intValue();
        this.addEdge(graph, edgeNr);

        /*
         * for (int i = 0; i < nodeNr; ++i) { nodes[i] = graph.addNode();
         * 
         * double x = (Math.sin((1.0 * i) / (1.0 * nodeNr) * Math.PI * 2.0) *
         * 180.0) + 250.0; double y = (Math.cos((1.0 * i) / (1.0 * nodeNr) *
         * Math.PI * 2.0) * 180.0) + 250.0;
         * 
         * HashMapAttribute cc = new HashMapAttribute("graphics");
         * 
         * nodes[i].addAttribute(cc, "");
         * 
         * CoordinateAttribute ca = new CoordinateAttribute(
         * GraphicAttributeConstants.COORDINATE, new Point2D.Double(x, y));
         * nodes[i].addAttribute(ca, GraphicAttributeConstants.GRAPHICS);
         * 
         * }
         * 
         * // add edges
         * 
         * if (procentOfEdge != 0.0) { double r = 1 / Math.log(1 -
         * procentOfEdge); int v = 1; int w = 1;
         * 
         * while (v < nodeNr) { w += (1 + ((int)(r * Math.log(Math.random()))));
         * 
         * while (w > nodeNr) { v++; w -= (nodeNr - v); }
         * 
         * if (v < nodeNr) { graph.addEdge(nodes[v - 1], nodes[w - 1], false); }
         * } }
         */

        graph.getListenerManager().transactionFinished(this);
        return graph;
    }

    private void test() {
        String mainPath = "/home/cip/zhang/diplomarbeit/test/06.04.06/";
        FileWriter fileWriter1;

        PrintWriter writer1;

        try {

            for (int edgeP = 80; edgeP <= 80; edgeP += 5) {
                double edgeProb = edgeP / 100.0;

                fileWriter1 = new FileWriter(mainPath + "vGraph_dick_"
                        + edgeProb + "_RES" + ".dat");

                writer1 = new PrintWriter(new BufferedWriter(fileWriter1));

                writer1.println(" N.Nr\t" + "cS.Nr\t" + "bS.Nr\t" + "E.Nr\t"
                        + "cE.Nr\t" + "lE.Nr\t" + "T.c\t" + "T.b\t" + "T\t"
                        + "maxC\t" + "maxB\t");

                writer1.flush();

                for (int i = 80; i <= 80; i += 10) {
                    writer1.println("<<<<<<<  the graphs with " + i
                            + " vertices >>>>>>>");
                    writer1.flush();

                    writer1.println("<<<<<<<  the graphs with " + (edgeP)
                            + "% dick >>>>>>>");
                    writer1.flush();
                    // for (int j = 0; j < 5; j++)
                    // {

                    Graph graph = this.generator(i, edgeProb);
                    Graph graph1 = (Graph) graph.copy();
                    writerGraph(writer1, graph, true);
                    writerGraph(writer1, graph1, false);

                    // }

                }
                fileWriter1.close();
                writer1.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param writer1
     * @param g
     */
    private void writerGraph(PrintWriter writer1, Graph g, boolean bicFlag) {
        ConfluentDrawingAlgorithm confAlg = new ConfluentDrawingAlgorithm();
        confAlg.setBicFlag(bicFlag);
        confAlg.attach(g);

        try {
            confAlg.check();
        } catch (PreconditionException ex) {
            ex.printStackTrace();
        }

        int cSwitchNumber, bSwitchNumber, confEdgeNumber, oriEdges, maxClique, maxBiclique;
        long runtimeOfC, runtimeOfB, fulltime1;

        System.out.println("graph: " + " node: " + g.getNodes().size()
                + " edge: " + g.getEdges().size());

        oriEdges = g.getEdges().size();
        confAlg.execute();
        cSwitchNumber = confAlg.getcSwitches();
        bSwitchNumber = confAlg.getbSwitches();
        confEdgeNumber = confAlg.getConfEdges();
        runtimeOfC = confAlg.getCliqueRuntime();
        runtimeOfB = confAlg.getBiCliqueRuntime();
        fulltime1 = runtimeOfC + runtimeOfB;
        maxClique = confAlg.getMaxCliqueSize();
        maxBiclique = confAlg.getMaxBicliqueSize();

        writer1.println(g.getNodes().size() + "\t" + cSwitchNumber + "\t"
                + bSwitchNumber + "\t" + oriEdges + "\t" + confEdgeNumber
                + "\t" + (oriEdges - confEdgeNumber) + "\t" + runtimeOfC + "\t"
                + runtimeOfB + "\t" + fulltime1 + "\t" + maxClique + "\t"
                + maxBiclique);
        if (!bicFlag) {
            writer1.println();
        }
        writer1.flush();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
