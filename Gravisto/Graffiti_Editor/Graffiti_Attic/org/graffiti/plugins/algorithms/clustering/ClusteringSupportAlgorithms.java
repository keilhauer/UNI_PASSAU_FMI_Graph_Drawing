// ==============================================================================
//
//   ClusteringSupportAlgorithms.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ClusteringSupportAlgorithms.java 5772 2010-05-07 18:47:22Z gleissner $

/*
 * Created on 07.06.2004
 */

package org.graffiti.plugins.algorithms.clustering;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.util.Queue;

/**
 * This singleton class is a collection of algorithms that are needed frequently
 * in clustering algorithms.
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */
public class ClusteringSupportAlgorithms {

    /** The base path for storing clustering information on graph elements */
    static final String BASE = "clustering.";

    /**
     * Path for storage of the cut-edge-mark on edges. This mark implies, that
     * the edge belongs to a multiway cut on its graph.
     */
    private static final String MULTIWAY_CUT_EDGE_MARK = BASE + "cutEdgeMark";

    /**
     * Path for storage of the cluster-number on nodes. All Nodes of a graph
     * with equal cluster_number belong to the same cluster in this graph.
     */
    private static final String CLUSTER_NUMBER = BASE + "clusterNumber";

    /** Path for storage of secured capacities */
    private static final String STORED_CAPACITIES = BASE + "storedCapacities";

    /** The path for the storage of the capacity sum on the nodes */
    private static final String CAPACITY_SUM = ClusteringSupportAlgorithms.BASE
            + "capacitySum";

    /**
     * The path for the storage of the temporary terminal mark, a flag that is
     * important for the generation of terminals
     */
    private static final String TEMPORARY_TERMINAL_MARK = BASE
            + "temporaryTerminslMark";

    /** Path for labels on nodes and edges, this path may not be private */
    static final String LABEL_PATH = "label.label";

    /** the path for storing the terminal flag on nodes */
    private static final String TERMINAL_MARK_PATH = BASE + "terminalMarkPath";

    /** Error message */
    private static final String NODE_NOT_IN_GRAPH_ERROR = "This node is not"
            + " an element of the graph.";

    /** Error message */
    private static final String PREFLOW_PUSH_PRECONDITION_ERROR = "Preconditions of the preflow push algorithm are not satisfied.";

    /** Error message */
    private static final String NODE_HAS_NO_CLUSTER_NUMBER_ERROR = "This node has no cluster number";

    /** Error message */
    private static final String NOT_ALL_EDGES_HAVE_BETWEENNESS_ERROR = "Not all edges of the graph have betweenness values.";

    /** Error message */
    private static final String PRECONDITIONS_OF_BETWEENNESS_VIOLATED_ERROR = "The Preconditions of the BetweennessAlgorithm have been violated.";

    /** Error message */
    private static final String RESTORE_WITHOUT_STORE_ERROR = "Tried to restore"
            + "capacity on an edge without a stored capacity";

    /** Error message */
    private static final String CAPACITIES_SET_TWICE_ERROR = "Either trivial"
            + "or inverted betweenness can be used as capacities, but not both";

    /** error message */
    private static final String NO_TERMINALS_SET_OR_FOUND_ERROR = "No "
            + "Terminals have been set";

    /** Error message */
    private static final String GRAPH_HAS_NOT_ENOUGH_CLUSTERS_ERROR = "The graph has not enough clusters";

    /** error message */
    private static final String FOUND_COMPONENT_WITHOUT_TERMINAL_ERROR = "There has to be a at least one terminal in each weakly connected "
            + "component";

    /** the singleton object of this class */
    private static ClusteringSupportAlgorithms csa = null;

    /** The random object for generating random numbers */
    private final Random random = new Random();

    /** The singleton object of <code>BetweennessSupportAlgorithms</code> */
    private BetweennessSupportAlgorithms bsa = BetweennessSupportAlgorithms
            .getBetweennessSupportAlgorithms();

    /** The singleton object of <code> FlowNetworkSupportAlgorithms </code> */
    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    /** The network-flow algorithms used for clustering computations */
    private PreflowPushAlgorithm preflowPush = new PreflowPushAlgorithm();

    /**
     * Constructs the singleton ClusteringSupportAlgorithms Object
     */
    private ClusteringSupportAlgorithms() {
    }

    /**
     * Returns the singleton <code> ClusteringSupportAlgorithms </code> object
     * 
     * @return the singleton
     */
    public static ClusteringSupportAlgorithms getClusteringSupportAlgorithms() {
        if (csa == null) {
            csa = new ClusteringSupportAlgorithms();
        }

        return csa;
    }

    /**
     * Calculates the weakly connected components in the given graph using
     * <code>ignoringBFS</code> ignoring edge directions. Edges marked by method
     * <code> setIgnoredByBFS </code> will be ignored in this algorithm.
     * Component marks will be removed after using them. Returns an array of
     * components or an empty array if the graph contains no nodes.
     * 
     * @param graph
     *            the graph
     * 
     * @return the weakly connected components
     */
    public Collection[] getAllConnectedComponents(Graph graph) {
        return bsa.getAllConnectedComponents(graph);
    }

    /**
     * Searches an Array for a given element and returns the index of the first
     * hit.
     * 
     * @param array
     *            the array
     * @param element
     *            the element to be searched for
     * 
     * @return the index of the <code>element </code> in the <code>array</code>
     */
    public int getArrayPosition(Node[] array, Node element) {
        int index = -1;

        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                index = i;

                break;
            }
        }

        return index;
    }

    /**
     * Sets the value of the cluster number to a given node. Old values will be
     * overwritten. All node in a graph with identical cluster number belong to
     * the same cluster and nodes with different cluster numbers belong to
     * different clusters.
     * 
     * @param node
     *            the node
     * @param number
     *            the cluster number
     */
    public void setClusterNumber(Node node, int number) {
        removeClusterNumber(node);
        node.setInteger(CLUSTER_NUMBER, number);
    }

    /**
     * Sets the value of the cluster number to the given nodes. Old values will
     * be overwritten. All node in a graph with identical cluster number belong
     * to the same cluster and nodes with different cluster numbers belong to
     * different clusters.
     * 
     * @param cluster
     *            the nodes of one cluster
     * @param number
     *            the cluster number
     */
    public void setClusterNumber(Collection cluster, int number) {
        Node tempNode;

        for (Iterator nodeIt = cluster.iterator(); nodeIt.hasNext();) {
            tempNode = (Node) nodeIt.next();
            setClusterNumber(tempNode, number);
        }
    }

    /**
     * Sets the cluster numbers to an array of clusters, using the array index
     * as cluster number.
     * 
     * @param clusters
     *            the array of clusters
     */
    public void setClusterNumber(Collection[] clusters) {
        for (int i = 0; i < clusters.length; i++) {
            setClusterNumber(clusters[i], i);
        }
    }

    /**
     * Returns the cluster number of the given node. All node in a graph with
     * identical cluster number belong to the same cluster and nodes with
     * different cluster numbers belong to different clusters.
     * 
     * @param node
     *            the node
     * 
     * @return the cluster number
     * 
     * @throws ClusteringException
     *             if the node has no cluster number
     */
    public int getClusterNumber(Node node) {
        int number = -1;

        try {
            number = node.getInteger(CLUSTER_NUMBER);
        } catch (AttributeNotFoundException anfe) {
            throw new ClusteringException(NODE_HAS_NO_CLUSTER_NUMBER_ERROR,
                    anfe);
        }

        return number;
    }

    /**
     * Returns the Collection of nodes in the graph, that are not in the given
     * Component. This method assumes that the given nodes ar all part of the
     * same graph.
     * 
     * @param graph
     *            the graph
     * @param partOfNodes
     *            a set of nodes of the graph
     * 
     * @return the rest of the nodes in the graph
     */
    public Collection getComplementNodes(Graph graph, Collection partOfNodes) {
        Collection otherNodes = new LinkedList();
        Node tempNode;

        for (Iterator nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            tempNode = (Node) nodeIt.next();

            // Do NOT use component mark here!
            if (!partOfNodes.contains(tempNode)) {
                otherNodes.add(tempNode);
            }
        }

        return otherNodes;
    }

    /**
     * If <code> mark </code> is set to true this method sets the component mark
     * to a given graph element. All nodes and edges with this mark must belong
     * to the same component of the same graph. If <code> mark  </code> is set
     * to false, then the mark will be removed.
     * 
     * @param e
     *            the element
     * @param mark
     *            if true, the mark will be set, if false it will be removed
     */
    public void setComponentMark(GraphElement e, boolean mark) {
        bsa.setComponentMark(e, mark);
    }

    /**
     * If <code> mark </code> is set to true this method sets the component mark
     * to the given graph elements. All nodes and edges with this mark must
     * belong to the same component of the same graph. If <code> mark
     * </code> is set to
     * false, then the mark will be removed.
     * 
     * @param elements
     *            the elements
     * @param mark
     *            if true, the mark will be set, if false it will be removed
     */
    public void setComponentMark(Collection elements, boolean mark) {
        bsa.setComponentMark(elements, mark);
    }

    /**
     * Checks if a graph element has a component mark. All Nodes and edges with
     * this mark have to belong to the same component of the same graph.
     * 
     * @param e
     *            the element
     * 
     * @return true, if the node has the mark, false otherwise
     */
    public boolean isComponentMarked(GraphElement e) {
        return bsa.isComponentMarked(e);
    }

    /**
     * Checks, if a given graph is weakly connected.
     * 
     * @param graph
     * 
     * @return true if the graph is weakly connected, false otherwise.
     */
    public boolean isConnected(Graph graph) {
        return (getNumberOfConnectedComponents(graph) <= 1);
    }

    /**
     * If <code> mark </code> is set to <code> true </code> this method sets the
     * multiway cut edge mark to the given edge. All edges with this mark
     * represent a multiway cut in a graph. If <code> mark </code> is set to
     * <code> false </code> the mark is removed from the edge.
     * 
     * @param edge
     *            the edge
     * @param mark
     *            if true the mark is set, if false it is removed
     */
    public void setCutEdgeMark(Edge edge, boolean mark) {
        try {
            edge.removeAttribute(MULTIWAY_CUT_EDGE_MARK);
        } catch (AttributeNotFoundException anfe) {
        }

        if (mark) {
            edge.setBoolean(MULTIWAY_CUT_EDGE_MARK, true);
        }
    }

    /**
     * If <code> mark </code> is set to <code> true </code> this method sets the
     * multiway cut edge mark to the given edges. All edges with this mark
     * represent a multiway cut in a graph. If <code> mark </code> is set to
     * <code> false </code> the mark is removed from the edges.
     * 
     * @param edges
     *            the edges
     * @param mark
     *            if true the mark is set, if false it is removed
     */
    public void setCutEdgeMark(Collection edges, boolean mark) {
        Edge tempEdge;

        for (Iterator edgeIt = edges.iterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();
            setCutEdgeMark(tempEdge, mark);
        }
    }

    /**
     * Checks if an edge has a multiway cut edge mark. All edges with this mark
     * represent a multiway cut in a graph.
     * 
     * @param edge
     *            the edge
     * 
     * @return true if the edge has the mark, false otherwise
     */
    public boolean isCutEdgeMarked(Edge edge) {
        boolean marked = false;

        try {
            marked = edge.getBoolean(MULTIWAY_CUT_EDGE_MARK);
        } catch (AttributeNotFoundException anfe) {
        }

        return marked;
    }

    /**
     * Calculates the cut size when separating one component of the graph from
     * the rest. The cutsize is the sum of the capacities of all cut edges.
     * 
     * @param network
     *            the flow network.
     * @param oneComponent
     *            the set of nodes to be seperated from the rest.
     * 
     * @return the cutsize
     */
    public double getCutSize(Graph network, Collection oneComponent) {
        // assume that alle nodes of oneComponent are in given network
        double cutSize = 0.0;
        Collection edgesToComplement = getEdgesToNodeComplement(oneComponent);
        Edge tempEdge;

        for (Iterator edgeIt = edgesToComplement.iterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();
            cutSize += nsa.getCapacity(tempEdge);
        }

        return nsa.round(cutSize); // gerundet
    }

    /**
     * Collects all edges of a given Component of the graph to nodes that are
     * not in this Component or vice versa. The algorithm assumes, that all
     * nodes given in the nodeList belong to the same graph.
     * 
     * @param nodeList
     *            the Component
     * 
     * @return the Edged from the <code> nodeList </code> Component to the rest
     *         of the graph
     */
    public Collection getEdgesToNodeComplement(Collection nodeList) {
        // mark all Nodes of the given component
        setComponentMark(nodeList, true);

        Node tempNode;

        // Assuming that all Nodes are in the same graph
        Collection edgesToComplement = new LinkedList();

        for (Iterator sourceNodeIt = nodeList.iterator(); sourceNodeIt
                .hasNext();) {
            tempNode = (Node) sourceNodeIt.next();

            // find All Edges from and to complement of source component
            for (Iterator edgesIt = tempNode.getEdgesIterator();

            // on directed graphs this also takes edges directed to tempNode
            edgesIt.hasNext();) {
                Edge tempEdge = (Edge) edgesIt.next();
                Node source = tempEdge.getSource();
                Node target = tempEdge.getTarget();
                boolean sourceMarked = false;
                boolean targetMarked = false;
                sourceMarked = isComponentMarked(source);
                targetMarked = isComponentMarked(target);

                // if either source is in SourceComponent and target not
                // or the other way round
                if ((sourceMarked && !targetMarked)
                        || (!sourceMarked && targetMarked)) {
                    edgesToComplement.add(tempEdge);
                }
            }
        }

        setComponentMark(nodeList, false);

        return edgesToComplement;
    }

    /**
     * Marks an edge to be ignored or not ignored by all following runs of
     * <code>ignoringBFS</code> until the property is set again.
     * 
     * @param edge
     *            the edge
     * @param ignored
     *            if true, the edge will be ignored, if false it will not be
     *            ignored.
     */
    public void setIgnoredByBFS(Edge edge, boolean ignored) {
        bsa.setIgnoredByBFS(edge, ignored);
    }

    /**
     * Marks all given edges to be ignored or not ignored by all following runs
     * of <code>ignoringBFS</code> until the property is set again.
     * 
     * @param edges
     *            the edges
     * @param ignored
     *            if true, the edge will be ignored, if false it will not be
     *            ignored.
     */
    public void setIgnoredByBFS(Collection edges, boolean ignored) {
        bsa.setIgnoredByBFS(edges, ignored);
    }

    /**
     * Checks if a given edge is marked to be ignored by <code>
     * ignoringBFS</code>
     * 
     * @param edge
     *            the edge
     * 
     * @return true if it is set to be ignored, false otherwise
     */
    public boolean isIgnoredByBFS(Edge edge) {
        return bsa.isIgnoredByBFS(edge);
    }

    /**
     * Calculates the number of weakly connected components in the given graph
     * using <code>ignoringBFS</code> ignoring edge directions. Edges marked by
     * method <code> setIgnoredByBFS </code> will be ignored in this algorithm.
     * Component marks will be removed after using them. Returns the number of
     * components or 0 if the graph contains no nodes.
     * 
     * @param graph
     *            the graph
     * 
     * @return the number of weakly connected components
     */
    public int getNumberOfConnectedComponents(Graph graph) {
        int number = 0;
        Node tempNode = null;
        csa.removeComponentMark(graph);

        for (Iterator nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            tempNode = (Node) nodeIt.next();

            if (!isComponentMarked(tempNode)) {
                csa.ignoringBFS(graph, tempNode, true);
                number++;
            }
        }

        csa.removeComponentMark(graph);

        return number;
    }

    /**
     * Creates a random positive int from 0 to i-1 by using an internal <code>
     * Random </code>
     * object.
     * 
     * @param i
     *            the exclusive upper bound for the integers
     * 
     * @return a random int
     */
    public int getRandomPositiveInt(int i) {
        return random.nextInt(i);
    }

    /**
     * Colors the given clusters (<code> Collections </code> of <code> Node
     * </code> Objects)
     * in different colors.
     * 
     * @param clusters
     *            the array of clusters
     */
    public void colorClusters(Collection[] clusters) {
        if (clusters.length > 0) {
            int baseIntensity = 50;
            int maximumIntensity = 255;
            int intensityRange = maximumIntensity - baseIntensity;

            int numberOfClusters = clusters.length;
            int numberOfNuancesPerColor = (int) Math
                    .ceil(numberOfClusters / 6.0);

            int colorIntensityStep = intensityRange / numberOfNuancesPerColor;

            boolean red;
            int redIntensity;
            boolean green;
            int greenIntensity;
            boolean blue;
            int blueIntensity;
            int actualNumberOfColor;
            int actualColorIntensity;

            int actualNuance = -1;

            for (int i = 0; i < numberOfClusters; i++) {
                actualNumberOfColor = i % 6;

                if (actualNumberOfColor == 0) {
                    actualNuance++;
                }

                actualColorIntensity = maximumIntensity
                        - (actualNuance * colorIntensityStep);

                red = false;
                redIntensity = 0;
                green = false;
                greenIntensity = 0;
                blue = false;
                blueIntensity = 0;

                switch (actualNumberOfColor) {
                case 0: {
                    red = true;

                    break;
                }

                case 1: {
                    green = true;

                    break;
                }

                case 2: {
                    blue = true;

                    break;
                }

                case 3: {
                    red = true;
                    green = true;

                    break;
                }

                case 4: {
                    red = true;
                    blue = true;

                    break;
                }

                case 5: {
                    green = true;
                    blue = true;

                    break;
                }
                }

                if (red) {
                    redIntensity = actualColorIntensity;
                }

                if (green) {
                    greenIntensity = actualColorIntensity;
                }

                if (blue) {
                    blueIntensity = actualColorIntensity;
                }

                Node colorNode;

                for (Iterator it = clusters[i].iterator(); it.hasNext();) {
                    colorNode = (Node) it.next();
                    nsa.setFillColor(colorNode, redIntensity, greenIntensity,
                            blueIntensity);
                }
            }
        }
    }

    /**
     * Colors the given clusters (<code> Collections </code> of <code> Node
     * </code> Objects)
     * in different colors and colors the cut edges grey.
     * 
     * @param clusters
     *            the array of clusters
     * @param cutEdges
     *            the cut edges
     */
    public void colorClustersAndCutEdges(Collection[] clusters,
            Collection cutEdges) {
        colorEdgesGrey(cutEdges);
        colorClusters(clusters);
    }

    /**
     * Sets the color of a given <code>Collection</code> of graph edges to a
     * specific value.
     * 
     * @param edges
     *            the edges
     * @param r
     *            red
     * @param g
     *            green
     * @param b
     *            blue
     */
    public void colorEdges(Collection edges, int r, int g, int b) {
        Edge tempEdge;

        for (Iterator edgeIt = edges.iterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();
            nsa.setFrameColor(tempEdge, r, g, b);
            nsa.setFillColor(tempEdge, r, g, b);
        }
    }

    /**
     * Colors the given edges grey
     * 
     * @param edges
     *            the edges
     */
    public void colorEdgesGrey(Collection edges) {
        colorEdges(edges, 150, 150, 150);
    }

    /**
     * Colors the given edges black to their normal black color
     * 
     * @param edges
     *            the edged
     */
    public void colorEdgesToNormal(Collection edges) {
        colorEdges(edges, 0, 0, 0);
    }

    /**
     * Colors all edges of a given graph back to their normal black color
     * 
     * @param graph
     *            the graph
     */
    public void colorEdgesToNormal(Graph graph) {
        colorEdgesToNormal(graph.getEdges());
    }

    /**
     * Sets the color of a given <code>Collection</code> of graph nodes to a
     * specific rgb value.
     * 
     * @param component
     *            the graph component
     * @param r
     *            red
     * @param g
     *            green
     * @param b
     *            blue
     */
    public void colorGraphComponent(Collection component, int r, int g, int b) {
        Node node;

        for (Iterator nodeIt = component.iterator(); nodeIt.hasNext();) {
            node = (Node) nodeIt.next();
            nsa.setFillColor(node, r, g, b);
        }
    }

    /**
     * Colors nodes and edges of a given graph back to their normal colors.
     * 
     * @param graph
     *            the graph
     */
    public void colorGraphToNormal(Graph graph) {
        colorNodesToNormal(graph);
        colorEdgesToNormal(graph);
    }

    /**
     * Colors the nodes of the given <code> Collection </code> to the standard
     * node color.
     * 
     * @param nodes
     *            the nodes
     */
    public void colorNodesToNormal(Collection nodes) {
        // TODO write colorNodesToNormal(Collection nodes)
        // This method should set the color of the nodes back
        // to the standard value
    }

    /**
     * Colors the nodes of the given graph to the standard node color
     * 
     * @param graph
     *            the graph
     */
    public void colorNodesToNormal(Graph graph) {
        colorNodesToNormal(graph.getNodes());
    }

    /**
     * Computes the total cut size of a multiway cut by adding the capacities of
     * all edges of the cut.
     * 
     * @param multiwayCut
     *            the cut
     * 
     * @return the size of the cut
     */
    public double computeMultiwayCutSize(Collection multiwayCut) {
        double cutSize = 0.0;

        for (Iterator it = multiwayCut.iterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            cutSize += nsa.getCapacity(e);
        }
        cutSize = nsa.round(cutSize);

        return cutSize;
    }

    /**
     * Creates capacities on a given graph using the computation of the
     * betweenness of the edges. The capacities are the inverted values of the
     * betweenness values. So bottleneck edges between clusters get little
     * capacities, edges in the center of the graph get medium capacities and
     * edges on the boundary get high capacity values.
     * 
     * @param graph
     *            the graph, for which capacities are created.
     */
    public void generateBetweennessCapacities(Graph graph) {
        BetweennessAlgorithm ba = new BetweennessAlgorithm();

        try {
            computeAdjustedBetweenness(graph, ba);
            makeCapacityFromBetweenness(graph, ba);
            ba.removeBetweenness();
        } catch (Throwable t) {
            ba.removeBetweenness();
            nsa.removeCapacities(graph);
            t.printStackTrace();
        }
    }

    /**
     * Generates the trivial capacity on 1.0 to each edge of a given graoh,
     * overwriting old values.
     * 
     * @param graph
     *            the graph
     */
    public void generateTrivialCapacities(Graph graph) {
        Edge tempEdge;

        for (Iterator edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();
            nsa.setCapacity(tempEdge, 1.0);
        }
    }

    /**
     * Creates a minimum cut in the given network and returns it. The cut is
     * generated with the help of a maximum flow computation with source
     * <code>s</code> and sink <code>t</code>.
     * 
     * @param network
     *            the network
     * @param s
     *            the source for the maximum flow computation
     * @param t
     *            the sink for the maximum flow computation
     * 
     * @return the Collection of edges that defines the cut
     */
    public Collection get_s_t_cut(Graph network, Node s, Node t) {
        // first run a network flow algorithm on (N,s,t)
        runPreflowPush(network, s, t);

        // then get the Komponent of the source by BFS,
        // and get the Edges from it to its Complement
        return getEdgesToNodeComplement(residualNetBFS(network, s));
    }

    /**
     * Runs a breadth-first-search on a graph, ignoring all edges which were set
     * to be ignored by the <code> setIgnoredByBFS </code> method. All found
     * nodes are marked with a <code> componentMark </code> that can be checked
     * by the <code> isComponentMarked </code> method and removed by the
     * <code> removeComponentMarks </code> method. If the boolean parameter is
     * set to true, the direction of the edges will be ignored too. Before a new
     * run of residualNetBFS or ignoringBFS on the same network, the
     * <code> componentMark </code> are removed automaticly.
     * 
     * @param graph
     *            the graph
     * @param startNode
     *            the starting node of the BFS
     * @param ignoreEdgeDirection
     *            if true, the direction of the edges will be ignored while the
     *            BFS
     * 
     * @return the Collection of found nodes
     * 
     * @throws ClusteringException
     *             if the given startNode is not an Element of the given graph
     */
    public Collection ignoringBFS(Graph graph, Node startNode,
            boolean ignoreEdgeDirection) {
        return bsa.ignoringBFS(graph, startNode, ignoreEdgeDirection);
    }

    /**
     * Computes the cut edges between given clusters of a graph. The nodes in
     * the clusters must belong to the given graph. Cluster numbers are stored
     * on the nodes to map a node to its cluster. All edges, that have one
     * endpoint in one cluster and the other in another cluster are cut edges.
     * 
     * @param graph
     *            the graph
     * @param clusters
     *            the given clusters of the graph
     * @return the cut edges
     */
    public Collection computeCutFromClusters(Graph graph, Collection[] clusters) {
        Collection cut = new LinkedList();
        this.setClusterNumber(clusters);
        Edge tempEdge;
        Node source;
        Node target;
        for (Iterator edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();
            source = tempEdge.getSource();
            target = tempEdge.getTarget();
            if (getClusterNumber(source) != getClusterNumber(target)) {
                cut.add(tempEdge);
            }
        }
        removeClusterNumber(graph);
        return cut;
    }

    /**
     * Computes the clusters of a graph with given cut edges. The cut edges must
     * belong to the given graph.
     * 
     * @param graph
     *            the graph
     * @param cut
     *            the cut edges, that will be ignored during the computation of
     *            the clusters
     * @return the array of clusters
     */
    public Collection[] computeClustersFromCut(Graph graph, Collection cut) {
        setIgnoredByBFS(cut, true);
        Collection[] clusters = getAllConnectedComponents(graph);
        setIgnoredByBFS(cut, false);
        return clusters;
    }

    /**
     * Removes all data, that possibly was created by clustering algorithms from
     * a given graph
     * 
     * @param graph
     *            the graph
     * @param removeCapacities
     *            if true, the capacities will be removed to, if false it won't
     * @param removeTerminalMarks
     *            if true, the terminal marks will be removed, if false it won't
     */
    public void removeAllClusteringData(Graph graph, boolean removeCapacities,
            boolean removeTerminalMarks) {
        nsa.removeFlow(graph);
        if (removeCapacities) {
            nsa.removeCapacities(graph);
        }
        removeComponentMark(graph);
        removeClusterNumber(graph);
        removeCutEdgeMark(graph);
        removeIgnoringByBFS(graph);
        removeCapacitySum(graph);
        if (removeTerminalMarks) {
            removeTerminalMark(graph);
        }
    }

    /**
     * Removes the cluster number of a single node.
     * 
     * @param node
     */
    public void removeClusterNumber(Node node) {
        try {
            node.removeAttribute(CLUSTER_NUMBER);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the cluster number of all nodes of a graph.
     * 
     * @param graph
     *            the graph
     */
    public void removeClusterNumber(Graph graph) {
        Node tempNode;

        for (Iterator nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            tempNode = (Node) nodeIt.next();
            removeClusterNumber(tempNode);
        }
    }

    /**
     * Removes the component mark from all elements of the given graph
     * 
     * @param graph
     *            the graph
     */
    public void removeComponentMark(Graph graph) {
        bsa.removeComponentMark(graph);
    }

    /**
     * Removes the multiway cut edge marks from all edges of the given graph.
     * 
     * @param graph
     *            the graph
     */
    public void removeCutEdgeMark(Graph graph) {
        setCutEdgeMark(graph.getEdges(), false);
    }

    /**
     * Removes all marks from the edges of a graph, so none of them will be
     * ignored by ignoringBFS anymore.
     * 
     * @param graph
     *            the graph
     */
    public void removeIgnoringByBFS(Graph graph) {
        Edge tempEdge;

        for (Iterator edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();
            setIgnoredByBFS(tempEdge, false);
        }
    }

    /**
     * Runs a breadth-first-search on the residual network of a given network.
     * All found nodes are marked with a <code> componentMark </code> which can
     * be checked by the <code> isComponentMarked </code> method and removed by
     * the. <code> removeComponentMarks </code> method. Before a new run of
     * residualNetBFS or ignoringBFS on the same network, the
     * <code> BFSMarks </code> are removed automaticly.
     * 
     * @param network
     *            the flow network
     * @param startNode
     *            the starting node of the BFS
     * 
     * @return the Collection of found Nodes
     * 
     * @throws ClusteringException
     *             if the given <code> startNode </code> is not an element of
     *             the given network
     */
    public Collection residualNetBFS(Graph network, Node startNode) {
        removeComponentMark(network);

        if (startNode.getGraph() != network)
            throw new ClusteringException(NODE_NOT_IN_GRAPH_ERROR);

        Collection startNodeComponent = new LinkedList();
        Queue queue = new Queue();

        // mark start-node and put it into the queue
        queue.addLast(startNode);
        setComponentMark(startNode, true);

        while (!queue.isEmpty()) {
            Node sourceNode = (Node) queue.removeFirst();

            // get all undirected and directed out edges
            Collection outEdges = sourceNode.getAllOutEdges();

            for (Iterator edgeIt = outEdges.iterator(); edgeIt.hasNext();) {
                Edge tempEdge = (Edge) edgeIt.next();
                Node targetNode = nsa.getOtherEdgeNode(sourceNode, tempEdge);

                // get residual Capacity for the actual Edge
                double capacity = nsa.getCapacity(tempEdge);
                double flow = nsa.getFlow(tempEdge);
                double totalResidualCapacity = capacity - flow;

                // if positive residual capacity and not yet marked
                if ((totalResidualCapacity > 0)
                        && !isComponentMarked(targetNode)) {
                    // mark and add to queue
                    queue.addLast(targetNode);
                    setComponentMark(targetNode, true);
                }
            }
        }

        for (Iterator it = network.getNodesIterator(); it.hasNext();) {
            Node node = (Node) it.next();

            if (isComponentMarked(node)) {
                startNodeComponent.add(node);
            }
        }

        return startNodeComponent;
    }

    /**
     * Restores the capacities stored by the <code> storeCapacities </code>
     * method on a given graph.
     * 
     * @param graph
     *            the graph
     * 
     * @throws ClusteringException
     *             if there is an edge with no stored capacity
     */
    public void restoreCapacities(Graph graph) {
        Edge tempEdge;
        double cap;
        nsa.removeCapacities(graph);

        for (Iterator edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();

            try {
                cap = tempEdge.getDouble(STORED_CAPACITIES);

                if (cap > 0) {
                    nsa.setCapacity(tempEdge, cap);
                }
            } catch (AttributeNotFoundException anfe) {
                nsa.removeCapacities(graph);
                throw new ClusteringException(RESTORE_WITHOUT_STORE_ERROR, anfe);
            }
        }
    }

    /**
     * Executes a <code> PreflowPushAlgorithm </code> on the given flow network,
     * source and sink.
     * 
     * @param network
     *            the flow network
     * @param source
     *            the source of the network flow
     * @param sink
     *            the sink of the network flow
     * 
     * @throws ClusteringException
     *             if a <code>PreconditionException</code> occurs in the
     *             <code> PreflowPushAlgorithm </code>
     */
    public void runPreflowPush(Graph network, Node source, Node sink) {
        preflowPush.reset();
        preflowPush.setAll(network, source, sink, false, true);

        try {
            preflowPush.check();
        } catch (PreconditionException pe) {
            throw new ClusteringException(PREFLOW_PUSH_PRECONDITION_ERROR, pe);
        }
        preflowPush.execute();
    }

    /**
     * Stores capacities of a given graph. After capacities have been changed,
     * the stored values can be restored by the <code> restoreCapacities
     * </code> method.
     * 
     * @param graph
     */
    public void storeCapacities(Graph graph) {
        Edge tempEdge;
        double cap;

        for (Iterator edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();
            cap = nsa.getCapacity(tempEdge);
            tempEdge.setDouble(STORED_CAPACITIES, cap);
        }
    }

    /**
     * Stores the old capacity values an generates new ones. The old values can
     * be restored by the <code> restoreCapacities </code> method. The newly
     * generated capacities depend on the boolean method parameters.
     * 
     * @param graph
     *            the graph for which capacities are created.
     * @param trivial
     *            trivial capacity values of 1.0 for each edge are created on
     *            the graph
     * @param invertedBetweenness
     *            capacity values equal to the inverted betweenness values of
     *            the edges are generated.
     * 
     * @throws ClusteringException
     *             if both methods for the creation of capacites were used
     */
    public void storeOldAndGenerateNewCapacities(Graph graph, boolean trivial,
            boolean invertedBetweenness) {
        if (trivial && invertedBetweenness)
            throw new ClusteringException(CAPACITIES_SET_TWICE_ERROR);
        else {
            storeCapacities(graph);

            if (trivial) {
                generateTrivialCapacities(graph);
            }

            if (invertedBetweenness) {
                generateBetweennessCapacities(graph);
            }
        }
    }

    /**
     * Computes the betweenness of a given graph and adjusts it, so the inverted
     * betweenness values can be used as capacities for the graph. This method
     * also computes candidates for terminals for the use of a
     * terminal-speparation-algorithm for clustering.
     * 
     * @param graph
     *            the graph
     * @param ba
     *            The <code> BetweennessAlgorithm </code> used for the
     *            computation
     * 
     * @throws ClusteringException
     *             if the preconditions of the <code>BetweennessAlgorithm</code>
     *             have not been not satisfied
     */
    private void computeAdjustedBetweenness(Graph graph, BetweennessAlgorithm ba) {
        ba.reset();

        // scaling the maximum betweenness, so preflow push accepts the inverted
        // value.
        ba.setAll(graph, false, true, false, true, 1);

        try {
            ba.check();
        } catch (PreconditionException pe) {
            throw new ClusteringException(
                    PRECONDITIONS_OF_BETWEENNESS_VIOLATED_ERROR, pe);
        }

        // compute the betweenness of the graph
        ba.execute();

        // scaling the minimum value, so preflow push acceps the inverted value
        Edge edge;

        for (Iterator edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            edge = (Edge) edgeIt.next();

            if (ba.getBetweenness(edge) < 0.0000002) {
                ba.setBetweenness(edge, 0.0000002);
            }
        }
    }

    /**
     * Filters out null clusters of a given cluster array. This is a submethod
     * of <code> computeAdjustedBetweenness </code>
     * 
     * @param clusters
     *            the clusters array with possible null values
     * 
     * @return a clusters array without null values
     */
    private Collection[] filterOutNullClusters(Collection[] clusters) {
        // init lastNonNull to the last element
        int lastNonNull = clusters.length - 1;

        // make sure, lastNonNull ist the last non null array value
        while (clusters[lastNonNull] == null) {
            lastNonNull--;
        }

        // swap null values to the end of the array decrementing lastNonNull
        for (int i = 0; i <= lastNonNull; i++) {
            if (clusters[i] == null) {
                clusters[i] = clusters[lastNonNull];
                clusters[lastNonNull] = null;
                lastNonNull--;
            }
        }

        Collection[] result = new Collection[lastNonNull + 1];
        System.arraycopy(clusters, 0, result, 0, lastNonNull + 1);

        return result;
    }

    /**
     * Creates capacity values on a Graph taking for each nodes the invers of
     * its betweenness value (calculated by <code> BetweennessAlgorithm
     * </code>). Old capacity values will
     * be overwritten.
     * 
     * @param graph
     *            the graph for which capacities will be created.
     * @param ba
     *            the BetweennessAlgorithm
     * 
     * @throws ClusteringException
     *             if an Exception occurs
     */
    private void makeCapacityFromBetweenness(Graph graph,
            BetweennessAlgorithm ba) {
        Edge tempEdge;

        for (Iterator edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            tempEdge = (Edge) edgeIt.next();

            try {
                makeCapacityFromBetweenness(tempEdge, ba);
            } catch (ClusteringException ce) {
                // if an exception happens -> remove again all capacities
                nsa.removeCapacities(graph);
                throw new ClusteringException(
                        NOT_ALL_EDGES_HAVE_BETWEENNESS_ERROR, ce);
            }
        }
    }

    /**
     * Sets the capacity of an edge to the inverse of its betweenness value
     * (calculated by <code> BetweennessAlgorithm  </code>). An old capacity
     * value will be overwritten.
     * 
     * @param edge
     *            the edge
     * @param ba
     *            the algorithm that has computed the betweenness.
     */
    private void makeCapacityFromBetweenness(Edge edge, BetweennessAlgorithm ba) {
        double betweenness = ba.getBetweenness(edge);
        double capacity = 1.0 / betweenness;
        System.out.println("Edge    Cap : " + capacity);
        nsa.setCapacity(edge, Math.round(nsa.round(capacity)));
    }

    /**
     * Removes the capacity sum of a given node.
     * 
     * @param node
     *            the node
     */
    private void removeCapacitySum(Node node) {
        try {
            node.removeAttribute(CAPACITY_SUM);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the capacity sums of all nodes of the graph.
     * 
     * @param graph
     *            the ggraph
     */
    public void removeCapacitySum(Graph graph) {
        Node node;
        for (Iterator nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            node = (Node) nodeIt.next();
            removeCapacitySum(node);
        }
    }

    /**
     * Returns the capacity sum of a given node (which is the sum of the
     * capacities of all edges incident to this node).
     * 
     * @param node
     *            the node
     * @return the capacity sum
     */
    public double getCapacitySum(Node node) {
        double capSum = 0.0;
        try {
            capSum = node.getDouble(CAPACITY_SUM);
        } catch (AttributeNotFoundException anfe) {
        }
        return capSum;
    }

    /**
     * Sets the capacity sum of a node to the given value.
     * 
     * @param node
     *            the node
     * @param capSum
     *            the new value of the capacity sum
     */
    private void setCapacitySum(Node node, double capSum) {
        removeCapacitySum(node);
        node.setDouble(CAPACITY_SUM, capSum);
    }

    /**
     * Computes the capacity sums of a whole graph.
     * 
     * @param graph
     *            the graph
     */
    public void computeCapacitySum(Graph graph) {
        computeCapacitySum(graph.getNodes().toArray(new Node[0]));
    }

    /**
     * Computes the capacity sums of the given nodes.
     * 
     * @param nodes
     *            the nodes
     */
    public void computeCapacitySum(Node[] nodes) {
        Node node;
        Edge edge;
        Collection edges;
        double capacitySum;

        for (int i = 0; i < nodes.length; i++) {
            // get the actual node, it's incident edges and reset the
            // capacitySum
            node = nodes[i];
            edges = node.getEdges();
            capacitySum = 0.0;

            // sum up the capacities of all incident edges (ignoring loops)
            for (Iterator edgeIt = edges.iterator(); edgeIt.hasNext();) {
                edge = (Edge) edgeIt.next();
                if (edge.getSource() != edge.getTarget()) {
                    capacitySum += nsa.getCapacity(edge);
                }
            }
            setCapacitySum(node, capacitySum);
        }
    }

    /**
     * Removes the temporary mark on a terminal.
     * 
     * @param node
     *            the node.
     */
    public void removeTemporaryTerminalMark(Node node) {
        try {
            node.removeAttribute(TEMPORARY_TERMINAL_MARK);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the temporary marks on the terminals. This method must be called,
     * when all Terminals have been generated.
     * 
     * @param nodes
     *            the nodes, from which the mark will be removed.
     */
    public void removeTemporaryTerminalMark(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            removeTemporaryTerminalMark(nodes[i]);
        }
    }

    /**
     * Sets a node to be a terminal.
     * 
     * @param node
     *            the node to be marked
     */
    public void setTemporaryTerminalMark(Node node) {
        node.setBoolean(TEMPORARY_TERMINAL_MARK, true);
    }

    /**
     * Sets all given nodes to be terminals.
     * 
     * @param nodes
     */
    public void setTemporaryTerminalMark(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            setTemporaryTerminalMark(nodes[i]);
        }
    }

    /**
     * Checks if a node is marked with the temporary terminal mark.
     * 
     * @param node
     *            the node
     * @return true, if the node is marked, false otherwise
     */
    public boolean isTemporaryTerminalMarked(Node node) {
        boolean mark = false;
        try {
            mark = node.getBoolean(TEMPORARY_TERMINAL_MARK);
        } catch (AttributeNotFoundException anfe) {
        }
        return mark;
    }

    /**
     * Checks if terminals are set to the graph. Checks if there is a weakly
     * connected component in the graph which has no terminal in it. In this
     * case the algorithm cannot guaranty that there is one weakly connected
     * component for each terminal after its execution.
     * 
     * @param graph
     *            the graph
     * @param terminals
     *            the terminal array
     * @param pe
     *            the <code>PreconditionException</code> to which errors are
     *            added.
     * 
     * @return true, if there is more then one terminal, false otherwise
     */
    public boolean checkTerminals(Graph graph, Node[] terminals,
            PreconditionException pe) {
        boolean noError = true;
        int number;

        // Check if there are no terminals set
        if (terminals == null) {

            pe.add(NO_TERMINALS_SET_OR_FOUND_ERROR);
            noError = false;

        } else {

            if (terminals.length == 0) {
                pe.add(NO_TERMINALS_SET_OR_FOUND_ERROR);
                noError = false;
            }
        }

        // Check if there is not at least one terminal per connected component
        int numberOfTerminalsInComp;
        Collection[] components = csa.getAllConnectedComponents(graph);
        for (int i = 0; i < components.length; i++) {
            numberOfTerminalsInComp = findTerminalsByMark(components[i]).size();
            if (numberOfTerminalsInComp == 0) {
                pe.add(FOUND_COMPONENT_WITHOUT_TERMINAL_ERROR);
                noError = false;
            }
        }
        return noError;
    }

    /**
     * Finds the terminals out of an array of nodes.
     * 
     * @param nodes
     *            the nodes to be searched
     * @return the found terminals
     */
    private Collection findTerminalsByMark(Collection nodes) {
        Collection found = new LinkedList();
        Node node;
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            node = (Node) it.next();
            if (isTerminalMarked(node)) {
                found.add(node);
            }
        }
        return found;
    }

    /**
     * Randomly chooses a given number of nodes from the array of nodes. The
     * method assumes that the given number is between 1 and the number of nodes
     * in the array.
     * 
     * @param nodes
     *            the nodes of the graph
     * @param number
     *            the number of nodes to choose
     * @return the chosen random nodes
     */
    public Node[] getRandomNodes(Node[] nodes, int number) {

        Node[] randomNodes = new Node[number];
        int randomIndex;
        int maxChooseIndex = nodes.length - 1;

        for (int i = 0; i < number; i++) {
            Node tempNode;
            do {
                randomIndex = csa.getRandomPositiveInt(maxChooseIndex + 1);

                tempNode = nodes[randomIndex];

                nodes[randomIndex] = nodes[maxChooseIndex];
                nodes[maxChooseIndex] = tempNode;
                maxChooseIndex--;
                // if this node is already a terminal -> choose another one
            } while (isTemporaryTerminalMarked(tempNode));
            // found a non terminal -> take it and mark it!
            randomNodes[i] = tempNode;
            setTemporaryTerminalMark(tempNode);
        }
        return randomNodes;
    }

    /**
     * Searches for the nodes with the greatest values of capacity sum and
     * returns them. The capacity sum has to be computed before the execution of
     * this method by the <code>computeCapacitySum</code> method. The given
     * array will be modified by the algorithm, so a copy should be given to it.
     * The method assumes that the given number is between 1 and the number of
     * nodes in the array.
     * 
     * @param nodes
     *            the array of nodes
     * @param number
     *            the wanted number of nodes
     * @return the nodes with greatest capacity sum
     */
    public Node[] getNodesWithGreatestCapacitySum(Node[] nodes, int number) {
        Comparator capSumComp = getCapacitySumComparator();

        Node[] greatest = new Node[number];

        // sorts the array by the comparator
        Arrays.sort(nodes, capSumComp);

        // takes the 'number' greatest nodes out
        Node node;
        int skips = 0;

        for (int i = 0; i < number; i++) {
            // take next terminal candidate
            node = nodes[nodes.length - 1 - i - skips];

            // if it is a already marked, take the next one and raise the skips
            while (isTemporaryTerminalMarked(node)) {
                skips++;
                node = nodes[nodes.length - 1 - i - skips];
            }
            // if it is not marked -> take it as terminal and mark it
            greatest[i] = node;
            setTemporaryTerminalMark(node);
        }
        return greatest;
    }

    /**
     * Sets the terminal mark to a node or removes it.
     * 
     * @param node
     *            the node
     */
    public void setTerminalMark(Node node) {
        removeTerminalMark(node);
        node.setBoolean(TERMINAL_MARK_PATH, true);
    }

    /**
     * Checks if a node has the terminal mark.
     * 
     * @param n
     *            the node
     * 
     * @return true, if the node has the terminal mark, false otherwise
     */
    public boolean isTerminalMarked(Node n) {
        boolean isTerminal = false;
        try {
            isTerminal = n.getBoolean(TERMINAL_MARK_PATH);
        } catch (AttributeNotFoundException anfe) {
        }
        return isTerminal;
    }

    /**
     * Removes the terminal mark from all nodes of the given graph .
     * 
     * @param graph
     *            the graph
     */
    public void removeTerminalMark(Graph graph) {
        removeTerminalMark(graph.getNodes().toArray(new Node[0]));
    }

    /**
     * Removes the terminal mark from all given nodes
     * 
     * @param nodes
     *            the nodes
     */
    public void removeTerminalMark(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            removeTerminalMark(nodes[i]);
        }
    }

    /**
     * Removes the terminal mark from a given node.
     * 
     * @param node
     *            the node
     */
    public void removeTerminalMark(Node node) {
        try {
            node.removeAttribute(TERMINAL_MARK_PATH);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Constructs an Object of an anonymous capacity-sum-Comparator-class and
     * returns it.
     * 
     * @return the Comparator
     */
    private Comparator getCapacitySumComparator() {
        Comparator capSumComp = new Comparator() {
            public int compare(Object o1, Object o2) {
                Node n1 = (Node) o1;
                Node n2 = (Node) o2;
                double capSum1 = 0.0;
                double capSum2 = 0.0;
                capSum1 = getCapacitySum(n1);
                capSum2 = getCapacitySum(n2);
                if ((capSum1 - capSum2) < 0)
                    return -1;
                else
                    return +1;
            }
        };
        return capSumComp;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
