// =============================================================================
//
//   CliqueAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CliqueAlgorithm_bk.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.confluentDrawing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

//import org.graffiti.graphics.NodeLabelAttribute;

/**
 * list all the cliques in graph in O(a(G)m) time per clique, where a(G) is the
 * arboricity of graph and m is the number of edges.
 * 
 * @author Xiaolei Zhang
 * @version 0.0.1 21.08.2005
 */
public class CliqueAlgorithm_bk {

    /** the graph to execute */
    private Graph graph;

    private HashMap graphMap;

    private Collection cliques;

    /** the edges between nodes in clique */
    private Collection edges;

    private long cliqueRuntime;

    /**
     * Creates a new CliqueAlgorithm object.
     * 
     * @param g
     *            the graph
     */
    public CliqueAlgorithm_bk(Graph g, HashMap map) {
        this.graph = g;
        this.graphMap = map;
        this.cliques = new LinkedList();
    }

    /**
     * return the cliques.
     * 
     * @return Returns the cliques.
     */
    public Collection getCliques() {
        return this.cliques;
    }

    /**
     * verify whether the graph contains the clique.
     * 
     * @return <code>True</code> if contains the clique. <code>False</code> if
     *         not.
     */
    public boolean hasMaxClique() {
        long current = System.currentTimeMillis();
        enumerate_cliques(new Clique(this.graphMap), graph.getNodes(),
                new ArrayList());
        // System.out.println("\t\t\t\t\t\t the runtime of listing of clique is:"
        // +(System.currentTimeMillis()-current));
        this.cliqueRuntime = System.currentTimeMillis() - current;
        // System.out.println("\t\t\t\t\t\t the runtime of listing of clique is:"
        // +(System.currentTimeMillis()-current));
        return (this.cliques.size() > 0 && ((Clique) ((LinkedList) this.cliques)
                .get(0)).size() > 3);
    }

    public void searchClique() {
        // ------ to Print and remove it after success
        /*
         * Iterator myNodeItr = this.graph.getNodesIterator(); int
         * myNodeItrLogger = 1;
         * 
         * System.out.println( "unsorted with Format(node | inDegree | outDegree
         * | label):");
         * 
         * while(myNodeItr.hasNext()) { Node toPri = (Node) myNodeItr.next();
         * 
         * System.out.println("\t" + toPri.toString() + " | " +
         * toPri.getInDegree() + " | " + toPri.getOutDegree() + " | " +
         * ((NodeLabelAttribute) toPri.getAttribute("label")).getLabel()); }
         */
        // -------------------
        // ArrayList sList = new ArrayList();
        // this.cliques = new ArrayList();
        // Clique cli = new Clique();
        enumerate_cliques(new Clique(this.graphMap), graph.getNodes(),
                new ArrayList());
        // this.cliques = Heapsort.heapsort(this.cliques, new DegreeComp(
        // DegreeComp.CLIQUES));

        // --- to print and remove it after success
        /*
         * for(int i = 0; i < this.cliques.size(); i++){
         * if(((ArrayList)this.cliques).get(i) != null){
         * System.out.println(((Clique
         * )((ArrayList)this.cliques).get(i)).toString()); } }
         */
        // System.out.println("\t\t\tfinished?????? :-) clique size= " +
        // this.getClique().size());
        // ----------
    }

    private void enumerate_cliques(Clique clique, Collection prepare,
            Collection s) {

        if (prepare.isEmpty()) {
            if (s.isEmpty()) {
                if (clique.size() > 3) {
                    // if(!this.cliques.isEmpty() && (clique.size() >
                    // ((Clique)((LinkedList)this.cliques).get(0)).size()))
                    // ((LinkedList)this.cliques).add(0,clique);
                    // else
                    this.cliques.add(clique);
                }
                return;
            }
        } else {
            // long current1 = System.currentTimeMillis();
            Object[] toArr = prepare.toArray();
            // System.out.println("to Array time: "+(System.currentTimeMillis()-current1));
            Node uT = (Node) toArr[0];

            for (int i = 0; i < toArr.length; i++) {
                if (!isNeighborhood((Node) toArr[i], uT)) {
                    // long current1 = System.currentTimeMillis();
                    prepare.remove(toArr[i]);
                    Clique auxCli = (Clique) clique.clone();
                    auxCli.setClique((Node) toArr[i]);
                    // System.out.println("to Array time: "+(System.currentTimeMillis()-current1));
                    enumerate_cliques(auxCli, insect(prepare, ((Node) toArr[i])
                            .getNeighbors()), insect(s, ((Node) toArr[i])
                            .getNeighbors()));
                    s.add(toArr[i]);
                }

            }
        }
    }

    private Collection insect(Collection set1, Collection set2) {
        ArrayList insect = new ArrayList();
        HashSet set2Hash = new HashSet(set2);
        Iterator set1Itr = set1.iterator();

        while (set1Itr.hasNext()) {
            Node tNode = (Node) set1Itr.next();
            if (set2Hash.contains(tNode)) {
                insect.add(tNode);
            }
        }
        return insect;
    }

    public boolean isNeighborhood(Node node, Node node2) {
        // Collection neigbors = node.getNeighbors();
        // HashSet ngHash = new HashSet(node.getNeighbors());
        return (this.graphMap.containsKey(node) && ((HashMap) this.graphMap
                .get(node)).containsKey(node2));
        // return ngHash.contains(node2);
    }

    /**
     * @return the runtime of listing clique
     */
    public long getCliqueRT() {
        return this.cliqueRuntime;
    }

    // rabbish

    /**
     * return the clique.
     * 
     * @return Returns the clique.
     */
    public Set getClique() {
        if (this.cliques.size() > 0
                && ((Clique) ((LinkedList) this.cliques).get(0)).size() > 3)
            return ((Clique) ((LinkedList) cliques).get(0)).getClique();
        else
            return new HashSet();
    }

    /**
     * return edges in the clique.
     * 
     * @return Returns edges in the clique.
     */
    public Collection getEdges() {
        this.edges = new ArrayList();
        Iterator edgeItr = graph.getEdgesIterator();
        Set cliq = getClique();

        while (edgeItr.hasNext()) {
            Edge edge = (Edge) edgeItr.next();
            if (cliq.contains(edge.getSource())
                    && cliq.contains(edge.getTarget())) {
                this.edges.add(edge);
            }
        }
        return this.edges;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
