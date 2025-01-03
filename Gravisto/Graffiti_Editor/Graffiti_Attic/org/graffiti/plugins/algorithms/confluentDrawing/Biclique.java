// =============================================================================
//
//   Biclique.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Biclique.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.confluentDrawing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.graffiti.graph.Node;

/**
 * the biclique class
 * 
 * @author Xiaolei Zhang
 * @version 1.0 10.09.2005
 */
public class Biclique {

    /** the bipartition of biclique */
    private Set bipartition_X;

    /** the bipartition of biclique */
    private Set bipartition_Y;

    /** the size of biclique */
    private int size;

    private HashMap graphMap;

    private Set edges;

    /**
     * Creates a new Biclique object.
     * 
     * @param length
     *            the size of biclique
     */
    public Biclique(int length, HashMap map) {
        this.bipartition_X = new HashSet(length);
        this.bipartition_Y = new HashSet(length);
        this.edges = new HashSet(length);
        this.graphMap = map;
    }

    /**
     * set the bipartition with a collection
     * 
     * @param bip_X
     *            The bipartition_X to set.
     */
    public void setBipartition_X(Collection bip_X) {
        Iterator itr = bip_X.iterator();

        while (itr.hasNext()) {
            this.bipartition_X.add(itr.next());
        }
    }

    /**
     * return the bipartition
     * 
     * @return Returns the bipartition_X.
     */
    public Set getBipartition_X() {
        return bipartition_X;
    }

    /**
     * set the bipartition with a collection
     * 
     * @param bip_Y
     *            The bipartition_Y to set.
     */
    public void setBipartition_Y(Collection bip_Y) {
        Iterator itr = bip_Y.iterator();

        while (itr.hasNext()) {
            this.bipartition_Y.add(itr.next());
        }
    }

    /**
     * return the bipartition
     * 
     * @return Returns the bipartition_Y.
     */
    public Set getBipartition_Y() {
        return bipartition_Y;
    }

    /**
     * add a single node into the bipartition
     * 
     * @param x
     *            The bipartition_X to set.
     */
    public void addX(Node x) {
        this.bipartition_X.add(x);
    }

    /**
     * remove a single node into the bipartition
     * 
     * @param x
     *            The bipartition_X to set.
     */
    public void removeX(Node x) {
        this.bipartition_X.remove(x);
    }

    /**
     * add a single node into the bipartition
     * 
     * @param y
     *            The bipartition_Y to set.
     */
    public void addY(Node y) {
        this.bipartition_Y.add(y);
    }

    /**
     * remove a single node into the bipartition
     * 
     * @param y
     *            The bipartition_Y to set.
     */
    public void removeY(Node y) {
        this.bipartition_Y.remove(y);
    }

    /**
     * return the size of biclique
     * 
     * @return the size of biclique
     */
    public int size() {
        return this.bipartition_X.size() + this.bipartition_Y.size();
    }

    /**
     * return the clique.
     * 
     * @return Returns the clique.
     */
    public Set getBiclique() {
        Set res = new HashSet();
        Iterator itr = this.bipartition_X.iterator();
        while (itr.hasNext()) {
            res.add(itr.next());
        }
        Iterator itr1 = this.bipartition_Y.iterator();
        while (itr1.hasNext()) {
            res.add(itr1.next());
        }
        return res;
    }

    /**
     * return edges in the clique.
     * 
     * @return Returns edges in the clique.
     */
    public Collection getEdges() {
        /*
         * this.edges = new ArrayList();
         * 
         * while (graphEdgeItr.hasNext()) { Edge edge =
         * (Edge)graphEdgeItr.next(); if
         * (this.getBiclique().contains(edge.getSource()) &&
         * this.getBiclique().contains(edge.getTarget())) {
         * this.edges.add(edge); } }
         */
        Iterator cItr = this.bipartition_X.iterator();
        while (cItr.hasNext()) {
            Node node1 = (Node) cItr.next();
            Iterator cItr1 = this.bipartition_Y.iterator();
            while (cItr1.hasNext()) {
                Node node2 = (Node) cItr1.next();
                if (this.graphMap.containsKey(node1)
                        && ((HashMap) this.graphMap.get(node1))
                                .containsKey(node2)) {
                    // System.out.println(((HashMap)this.graphMap.get(node1)).get(node2));
                    this.edges.add(((HashMap) this.graphMap.get(node1))
                            .get(node2));

                }
                /*
                 * if(this.graphMap.containsKey(node2) &&
                 * ((HashMap)this.graphMap.get(node2)).containsKey(node1)){
                 * this.
                 * edges.add(((HashMap)this.graphMap.get(node2)).get(node1)); }
                 */
            }

        }
        return this.edges;
    }

    public int getNrOfEdges() {
        return this.edges.size();
    }

    /**
     * @return
     */
    public boolean valid() {
        int edgeNr = 0;
        Iterator itr = this.bipartition_X.iterator();
        while (itr.hasNext()) {
            Node vertex = (Node) itr.next();
            Iterator itr1 = this.bipartition_Y.iterator();
            while (itr1.hasNext()) {
                Node vertex2 = (Node) itr1.next();
                if (!isNeighborhood(vertex, vertex2)
                        || !isNeighborhood(vertex2, vertex))
                    return false;
                // edgeNr++;
                // this.edges.add(((HashMap)this.graphMap.get(vertex)).get(vertex2));
            }
        }
        // System.out.println("\t\t edge of biclique is: " + edgeNr + "\t" +
        // (this.bipartition_X.size() * this.bipartition_Y.size()));
        // return edgeNr == (this.bipartition_X.size() *
        // this.bipartition_Y.size());
        return true;
    }

    private boolean isNeighborhood(Node node, Node node2) {
        // Collection neigbors = node.getNeighbors();
        // HashSet ngHash = new HashSet(node.getNeighbors());
        // return ngHash.contains(node2);
        return (this.graphMap.containsKey(node) && ((HashMap) this.graphMap
                .get(node)).containsKey(node2));
    }

    /**
     * to string
     * 
     * @return string value of biclique
     */
    @Override
    public String toString() {
        String res = "";
        Iterator itr1 = this.bipartition_X.iterator();

        while (itr1.hasNext()) {
            res += (itr1.next().toString() + "\t");
        }

        Iterator itr2 = this.bipartition_Y.iterator();

        while (itr2.hasNext()) {
            res += (itr2.next().toString() + "\t");
        }

        return res;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
