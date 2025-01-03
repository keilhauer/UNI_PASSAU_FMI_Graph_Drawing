// =============================================================================
//
//   ConfluentDrawingAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConfluentDrawingAlgorithm.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.confluentDrawing;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.ProbabilityParameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * 
 */
public class ConfluentDrawingAlgorithm extends AbstractAlgorithm {

    /** DOCUMENT ME! */

    // private Selection selection;
    private static final Logger logger = Logger
            .getLogger(ConfluentDrawingAlgorithm.class.getName());

    /** should the graph be directed ? */
    private BooleanParameter favorParam;

    /** label the edges ? */
    private BooleanParameter labelEdgesParam;

    /** label the nodes ? */
    private BooleanParameter labelNodesParam;

    /** multigraph allowed ? */
    private BooleanParameter makeParam;

    /** an edge's maximum length */
    private DoubleParameter maxLengthParam;

    /** an edge's minimum length */
    private DoubleParameter minLengthParam;

    /** number of edges */
    // private IntegerParameter degreeParam;
    /** an node's minimum label */
    private IntegerParameter firstLabelParam;

    /** number od nodes */
    private IntegerParameter nodesParam;

    /** probability parameter */
    private ProbabilityParameter probabilityParam;

    /** selection */
    private Selection selection;

    private HashMap graphMap;

    private int cSwitches;

    private int bSwitches;

    private ArrayList sortedNode;

    private int confEdges;

    private long cliqueRT;

    private long biCliqueRT;

    private int nrOfrecusions;

    private boolean bicFlag;

    private int maxCliqueSize;

    private int maxBicliqueSize;

    /**
     * Constructs a new instance.
     */
    public ConfluentDrawingAlgorithm() {
        makeParam = new BooleanParameter(false, "random graph",
                "make it automatic");

        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");

        probabilityParam = new ProbabilityParameter(0.5, "probability",
                "probability of edge generation");

        // degreeParam = new IntegerParameter(new Integer(2), new Integer(0),
        // new Integer(100), "starting degree",
        // "the minimal degree of graph to generate");

        favorParam = new BooleanParameter(false, "Biclique", "in favor ?");
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Confluent drawing";
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
        SelectionParameter selParam = new SelectionParameter("Selection",
                "<html>The selection to work on.<p>If empty, "
                        + "the whole graph is used.</html>");
        selParam.setSelection(new Selection("_temp_"));

        return new Parameter[] { selParam, makeParam, nodesParam,
                probabilityParam, favorParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getInteger().compareTo(new Integer(2)) < 0) {
            errors.add("The number of nodes may not be smaller than two.");
        }

        if (probabilityParam.getProbability().compareTo(new Double(1.0)) > 0) {
            errors.add("The probability may not be greater than 1.0.");
        }

        if (probabilityParam.getProbability().compareTo(new Double(0.0)) < 0) {
            errors.add("The probability may not be less than 0.0.");
        }

        /*
         * if(degreeParam.getInteger().compareTo(new Integer(0)) < 0) {
         * errors.add("The degree may not be smaller than zero."); }
         * 
         * if (favorParam.getBoolean().booleanValue()) { throw new
         * PreconditionException(
         * "the Algorithm work on the undirected graph. Please select nothing");
         * }
         */

        /*
         * if ((selection == null) || !selection.isEmpty()) { throw new
         * PreconditionException( "Please don not select the graph elements"); }
         */

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        nodesParam.setValue(new Integer(5));
        // degreeParam.setValue(new Integer(2));
        probabilityParam.setValue(new Double(0.5));
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if (this.makeParam.getBoolean().booleanValue()) {
            graph.clear();
            graph.getListenerManager().transactionStarted(this);
            generator();

            graph.getListenerManager().transactionFinished(this);
        }

        /*
         * graph.getListenerManager().transactionStarted(this);
         * selection.addAll(graph.getGraphElements()); numberNodes();
         * selection.clear();
         * graph.getListenerManager().transactionFinished(this);
         */

        if (graph.getNodes().size() <= 0)
            return;
        createHashGraph();

        graph.getListenerManager().transactionStarted(this);
        // this.save = new ArrayList();
        long current = System.currentTimeMillis();
        // this.sortedNode = Heapsort.heapsort(this.graph.getNodes(),
        // new DegreeComp(DegreeComp.DES));
        drawUndirected(graph);
        System.out.println("\t\t\t\t the runtime is:  "
                + (System.currentTimeMillis() - current));

        System.out.println("\t\t\t\t the number of the created Switch is:  "
                + (this.bSwitches + this.cSwitches));
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * 
     */
    private void createHashGraph() {
        this.graphMap = new HashMap(graph.getNumberOfNodes());
        Iterator graphItr = graph.getNodesIterator();

        while (graphItr.hasNext()) {
            Node node = (Node) graphItr.next();
            HashMap nGHashMap = new HashMap();
            // Iterator ngItr = node.getNeighborsIterator();
            Iterator ngItr = node.getEdgesIterator();

            while (ngItr.hasNext()) {
                Edge ng = (Edge) ngItr.next();
                // Node ng = (Node)ngItr.next();
                // ArrayList toAdd = new ArrayList();
                // toAdd.add(node);
                // toAdd.add(ng);
                // Edge toAdd = null;
                // Iterator edgeItr = graph.getEdgesIterator();
                // System.out.println(graph.getNumberOfEdges() + "\t" +
                // graph.getEdges().size());
                // while(edgeItr.hasNext()){
                // Edge tmp = (Edge)edgeItr.next();
                if ((ng.getSource().equals(node))) {
                    nGHashMap.put(ng.getTarget(), ng);
                }
                if ((ng.getTarget().equals(node))) {
                    nGHashMap.put(ng.getSource(), ng);
                    // }
                }

                // nGHashMap.put(ng, toAdd);
            }
            this.graphMap.put(node, nGHashMap);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void drawUndirected(Graph g) {

        // if g is palar
        // draw g
        // else if G contains a large clique or biclique subgraph C
        // create a new vertex v
        // obtain a new graph C' by removing edges of C and connecting each
        // vertex of C to v

        // CliqueAlgorithm clique = new CliqueAlgorithm(g);
        // createHashGraph();
        CliqueAlgorithm_bk clique = new CliqueAlgorithm_bk(g, this.graphMap);
        // long current = System.currentTimeMillis();
        // clique.searchClique();
        // System.out.println("\t\t\t\t\t\t the runtime of listing of clique is:
        // "+(System.currentTimeMillis()-current));
        BicliqueAlgorithm biclique = new BicliqueAlgorithm(g, this.graphMap);
        // long current1 = System.currentTimeMillis();
        // biclique.update(this.sortedNode);
        // System.out.println("\t\t\t\t\t\t the runtime of listing of
        // bicliqueis: "+(System.currentTimeMillis()-current1));
        PlanarityAlgorithm planarAlg = new PlanarityAlgorithm();
        planarAlg.attach(g);
        try {
            planarAlg.testPlanarity();
        } catch (Exception e) {
            GmlWriter gw = new GmlWriter();
            File file1 = new File("/home/cip/zhang/diplomarbeit/test/graph_"
                    + System.currentTimeMillis() + ".gml");
            OutputStream os = null;

            try {
                os = new FileOutputStream(file1);
            } catch (FileNotFoundException exp) {
                exp.printStackTrace();
            }

            try {
                gw.write(os, g);
            } catch (IOException exp) {
                exp.printStackTrace();
            }

            return;
        }

        boolean bicliqueFavor;
        // if (favorParam.getBoolean().booleanValue())
        if (this.bicFlag) {
            bicliqueFavor = biclique.hasMaxBiclique() || clique.hasMaxClique();
        } else {
            // bicliqueFavor = biclique.hasMaxBiclique() ||
            // clique.hasMaxClique();
            bicliqueFavor = clique.hasMaxClique() || biclique.hasMaxBiclique();
        }
        this.cliqueRT += clique.getCliqueRT();
        this.biCliqueRT += biclique.getBicliqueRT();
        System.out.println("\t\t\t\t\t\t the runtime of listing of clique is:"
                + this.cliqueRT + "\t the runtime of listing of Biclique is:"
                + this.biCliqueRT);

        if (planarAlg.isPlanar()) {
            logger
                    .log(Level.INFO,
                            "\t >>>>>>>>>> success , because the graph is planar   <<<<<<<<<<");
            return;
        } else if (bicliqueFavor) {
            // LinkedList cliques = (LinkedList)clique.getCliques();
            ArrayList cliqueArray = Heapsort.heapsort(clique.getCliques(),
                    new DegreeComp(DegreeComp.CLIQUES));
            // Iterator cliquesItr = clique.getCliques().iterator();
            Iterator cliquesItr = cliqueArray.iterator();
            System.out.println("size of cliques array is "
                    + clique.getCliques().size());
            while (cliquesItr.hasNext()) {
                Clique cli = (Clique) cliquesItr.next();
                Set maxClique = cli.getClique();
                if (!maxClique.isEmpty() && cli.valid()) {
                    logger.log(Level.INFO,
                            "\t >>>>>>>>>> CLIQUE::: before the deletion of edge, edges of graph are: "
                                    + g.getEdges().size()
                                    + "   vertices of graph are: "
                                    + g.getNodes().size() + " <<<<<<<<<<");

                    if (this.maxCliqueSize < cli.size()) {
                        this.maxCliqueSize = cli.size();
                    }
                    deleteEdges(cli, cli.getEdges().iterator());
                    this.confEdges += (cli.getNrOfEdges() - cli.size());
                    logger.log(Level.INFO,
                            "\t >>>>>>>>>> CLIQUE::: after the deletion of edge, edges of graph are: "
                                    + g.getEdges().size()
                                    + "   vertices of graph are: "
                                    + g.getNodes().size() + " <<<<<<<<<<");

                    this.cSwitches++;
                    this.createCycleForTest(g, maxClique);
                    // createCycle(g, maxClique);
                    logger.log(Level.INFO,
                            "\t >>>>>>>>>> CLIQUE::: after the creating of cycle, edges of graph are: "
                                    + g.getEdges().size()
                                    + "   vertices of graph are: "
                                    + g.getNodes().size() + " <<<<<<<<<<");

                }
            }

            ArrayList cliqueArray1 = Heapsort.heapsort(biclique.getBicliques(),
                    new DegreeComp(DegreeComp.BICLIQUES));
            // Iterator bicliquesItr = biclique.getBicliques().iterator();
            Iterator bicliquesItr = cliqueArray1.iterator();
            System.out.println("size of Bicliques array is "
                    + biclique.getBicliques().size());
            while (bicliquesItr.hasNext()) {
                if (!biclique.isEmpty()) {
                    Biclique bcli = (Biclique) bicliquesItr.next();

                    Set maxBic = bcli.getBiclique();

                    if (!maxBic.isEmpty() && bcli.valid()) {
                        logger.log(Level.INFO,
                                "\t >>>>>>>>>> BICLIQUE::: before the deletion of edge, edges of graph are: "
                                        + g.getEdges().size()
                                        + "   vertices of graph are: "
                                        + g.getNodes().size() + " <<<<<<<<<<");

                        if (this.maxBicliqueSize < bcli.size()) {
                            this.maxBicliqueSize = bcli.size();
                        }
                        deleteEdges(null, bcli.getEdges().iterator());
                        this.confEdges += (bcli.getNrOfEdges() - bcli.size());
                        logger.log(Level.INFO,
                                "\t >>>>>>>>>> BICLIQUE::: after the deletion of edge, edges of graph are: "
                                        + g.getEdges().size()
                                        + "   vertices of graph are: "
                                        + g.getNodes().size() + " <<<<<<<<<<");

                        this.bSwitches++;
                        this.createCycleForTest(g, maxBic);
                        // createCycle(g, maxBic);
                        logger.log(Level.INFO,
                                "\t >>>>>>>>>> BICLIQUE::: after the creating of cycle, edges of graph are: "
                                        + g.getEdges().size()
                                        + "   vertices of graph are: "
                                        + g.getNodes().size() + " <<<<<<<<<<");

                    }
                }
            }
            drawUndirected(g);
        } else {
            // fail to draw confluent
            logger
                    .log(
                            Level.INFO,
                            "\t >>>>>>>>>> failure by drawing confluent, because all of the remaining clique is just at all K3 or few <<<<<<<<<<");
            return;
        }

        /*
         * //if(clique.hasMaxClique()) if(biclique.hasMaxBiclique()) { //print
         * the clique //Set maxClique = clique.getClique(); //Iterator
         * maxCliqueItr = maxClique.iterator(); Set maxClique =
         * biclique.getBiclique(); Iterator maxCliqueItr = maxClique.iterator();
         * //this.setNodeColor(maxCliqueItr);
         * 
         * Iterator edgeItr = graph.getEdgesIterator();
         * 
         * while(edgeItr.hasNext()) { Edge edge = (Edge) edgeItr.next();
         * 
         * if(maxClique.contains(edge.getSource()) &&
         * maxClique.contains(edge.getTarget())) { ColorAttribute colorAtt =
         * (ColorAttribute) edge.getAttribute(GraphicAttributeConstants.GRAPHICS
         * + Attribute.SEPARATOR + GraphicAttributeConstants.FRAMECOLOR);
         * colorAtt.setColor(Color.CYAN); } } }
         */

        /*
         * Iterator maxCliqueItr1 = maxClique.iterator(); while
         * (maxCliqueItr1.hasNext()) { Iterator edgeItr =
         * ((Node)maxCliqueItr1.next()).getEdgesIterator();
         * this.setColor(edgeItr); }
         */

        // replace v by a small traffic circle to get a confluent drawing of g
        // else fail
    }

    private void createNewVertex(Set clique) {
        Random random = new Random();
        int red = random.nextInt(255);
        int blue = random.nextInt(255);
        int green = random.nextInt(255);
        Node node = graph.addNode();

        CoordinateAttribute ca = (CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        ColorAttribute colorAtt = (ColorAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.FILLCOLOR);
        colorAtt.setColor(new Color(red, green, blue));

        Edge toAdd;
        Iterator itr = clique.iterator();
        while (itr.hasNext()) {
            toAdd = graph.addEdge((Node) itr.next(), node, false);
            ColorAttribute colorAtte2 = (ColorAttribute) toAdd
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FRAMECOLOR);
            colorAtte2.setColor(new Color(red, blue, green));
        }

    }

    private void createCycleForTest(Graph g, Collection cycle) {
        Object[] arrObj = cycle.toArray();
        for (int i = 0; i < arrObj.length; i++) {
            // Edge toAdd;
            if (i != arrObj.length - 1) {
                if (!((HashMap) this.graphMap.get(arrObj[i]))
                        .containsKey(arrObj[i + 1])) {
                    Edge toAdd = g.addEdge((Node) arrObj[i],
                            (Node) arrObj[i + 1], false);
                    HashMap aux = (HashMap) this.graphMap.get(arrObj[i]);
                    aux.put(arrObj[i + 1], toAdd);
                    HashMap aux1 = (HashMap) this.graphMap.get(arrObj[i + 1]);
                    aux1.put(arrObj[i], toAdd);
                }
            } else {
                if (!((HashMap) this.graphMap.get(arrObj[i]))
                        .containsKey(arrObj[0])) {
                    Edge toAdd = g.addEdge((Node) arrObj[i], (Node) arrObj[0],
                            false);
                    HashMap aux = (HashMap) this.graphMap.get(arrObj[i]);
                    aux.put(arrObj[0], toAdd);
                    HashMap aux1 = (HashMap) this.graphMap.get(arrObj[0]);
                    aux1.put(arrObj[i], toAdd);
                }
            }
        }
    }

    private void createCycle(Graph g, Collection cycle) {
        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);
        Object[] arrObj = cycle.toArray();
        for (int i = 0; i < arrObj.length; i++) {
            Edge toAdd = null;
            if (i != arrObj.length - 1) {
                if (!((HashMap) this.graphMap.get(arrObj[i]))
                        .containsKey(arrObj[i + 1])) {
                    toAdd = g.addEdge((Node) arrObj[i], (Node) arrObj[i + 1],
                            false);
                    // HashMap aux = new HashMap();
                    HashMap aux = (HashMap) this.graphMap.get(arrObj[i]);
                    aux.put(arrObj[i + 1], toAdd);
                    HashMap aux1 = (HashMap) this.graphMap.get(arrObj[i + 1]);
                    aux1.put(arrObj[i], toAdd);
                    // this.graphMap.put((Node)arrObj[i], aux);
                }
            } else {
                if (!((HashMap) this.graphMap.get(arrObj[i]))
                        .containsKey(arrObj[0])) {
                    toAdd = g
                            .addEdge((Node) arrObj[i], (Node) arrObj[0], false);
                    // HashMap aux = new HashMap();
                    HashMap aux = (HashMap) this.graphMap.get(arrObj[i]);
                    aux.put(arrObj[0], toAdd);
                    HashMap aux1 = (HashMap) this.graphMap.get(arrObj[0]);
                    aux1.put(arrObj[i], toAdd);
                    // this.graphMap.put((Node)arrObj[i], aux);
                }
            }
            if (toAdd != null) {
                ColorAttribute colorAtte2 = (ColorAttribute) toAdd
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FRAMECOLOR);
                colorAtte2.setColor(new Color(red, green, blue));
            }
        }
    }

    private void deleteEdges(Clique cli, Iterator edgItr) {
        // HashSet graphEdges = new HashSet(graph.getEdges());
        // Collection graphEdges = graph.getEdges();
        while (edgItr.hasNext()) {
            Edge toDel = (Edge) edgItr.next();
            // ArrayList edge = (ArrayList)edgItr.next();
            // System.out.println(toDel);
            // if(this.graphMap.get(toDel.getSource()) != null){
            if (this.graphMap.containsKey(toDel.getSource())
                    && ((HashMap) this.graphMap.get(toDel.getSource()))
                            .containsKey(toDel.getTarget())) {
                ((HashMap) this.graphMap.get(toDel.getSource())).remove(toDel
                        .getTarget());
            }

            if (this.graphMap.containsKey(toDel.getTarget())
                    && ((HashMap) this.graphMap.get(toDel.getTarget()))
                            .containsKey(toDel.getSource())) {
                ((HashMap) this.graphMap.get(toDel.getTarget())).remove(toDel
                        .getSource());
            }
            // }

            // if (graph.getEdges().contains(toDel))
            // {

            graph.deleteEdge(toDel);

            // }

        }

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

            CoordinateAttribute ca = (CoordinateAttribute) node[i]
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(x, y));
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

    /**
     * 
     * 
     */
    private void generator() {
        // add nodes
        int n = nodesParam.getInteger().intValue();

        Node[] nodes = new Node[n];

        graph.getListenerManager().transactionStarted(this);

        this.addNodes(n, graph);
        double p = probabilityParam.getProbability().doubleValue();
        int edgeNr = new Integer((int) Math.round((n * (n - 1)) / 2 * p))
                .intValue();
        this.addEdge(graph, edgeNr);

        /*
         * for (int i = 0; i < n; ++i) { nodes[i] = graph.addNode();
         * 
         * CoordinateAttribute ca = (CoordinateAttribute)nodes[i]
         * .getAttribute(GraphicAttributeConstants.GRAPHICS +
         * Attribute.SEPARATOR + GraphicAttributeConstants.COORDINATE);
         * 
         * double x = (Math.sin((1.0 * i) / (1.0 * n) * Math.PI * 2.0) * 180.0)
         * + 250.0; double y = (Math.cos((1.0 * i) / (1.0 * n) * Math.PI * 2.0)
         * * 180.0) + 250.0; ca.setCoordinate(new Point2D.Double(x, y)); }
         * 
         * // add edges double p = probabilityParam.getDouble().doubleValue();
         * 
         * if (p != 0.0) { double r = 1 / Math.log(1 - p); int v = 1; int w = 1;
         * 
         * while (v < n) { w += (1 + ((int)(r * Math.log(Math.random()))));
         * 
         * while (w > n) { v++; w -= (n - v); }
         * 
         * if (v < n) { graph.addEdge(nodes[v - 1], nodes[w - 1], false); } } }
         */

        graph.getListenerManager().transactionFinished(this);

        /*
         * // label the nodes if(nodeLabelParam.getBoolean().booleanValue()) {
         * super.labelNodes(startNumberParam.getInteger().intValue()); } //
         * label the edges if(labelEdgesParam.getBoolean().booleanValue()) {
         * super.labelEdges(labelEdgesParam.getString(),
         * edgeMin.getInteger().intValue(), edgeMax.getInteger().intValue()); }
         */
    }

    /**
     * 
     */
    private void numberNodes() {
        int number = 1;
        NodeLabelAttribute labelAttr;
        Collection nodeList = selection.getNodes();
        Iterator nodeSitr = nodeList.iterator();

        while (nodeSitr.hasNext()) {
            Node auxN = (Node) nodeSitr.next();
            String LABEL_PATH;

            if (GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH.equals("")) {
                LABEL_PATH = GraphicAttributeConstants.LABEL;
            } else {
                LABEL_PATH = GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH
                        + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL;
            }

            try {
                labelAttr = (NodeLabelAttribute) auxN.getAttribute(LABEL_PATH);

                // has label - don't touch it; except it is empty
                if (labelAttr.getLabel().equals("")) {
                    labelAttr.setLabel(String.valueOf(number));
                    number++;
                }
            } catch (AttributeNotFoundException anfe) {
                // no label - associate one
                labelAttr = new NodeLabelAttribute(
                        GraphicAttributeConstants.LABEL, String.valueOf(number));
                auxN.addAttribute(labelAttr,
                        GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);
                number++;
            }
        }
    }

    /**
     * @return the number of confluent edges
     */
    public int getConfEdges() {
        return this.confEdges;
    }

    /**
     * @return the summer time of listing Clique
     */
    public long getCliqueRuntime() {
        return this.cliqueRT;
    }

    /**
     * @return the summer time of listing Biclique
     */
    public long getBiCliqueRuntime() {
        return this.biCliqueRT;
    }

    public void setBicFlag(boolean flag) {
        this.bicFlag = flag;
    }

    public int getMaxCliqueSize() {
        return this.maxCliqueSize;
    }

    public int getMaxBicliqueSize() {
        return this.maxBicliqueSize;
    }

    public int getcSwitches() {
        return this.cSwitches;
    }

    public int getbSwitches() {
        return this.bSwitches;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
