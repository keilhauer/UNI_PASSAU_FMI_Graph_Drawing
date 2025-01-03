/*==============================================================================
*
*   RandomGraphs.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: RandomGraphs.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.eval;

import java.util.*;

import org.visnacom.controller.ViewPanel;
import org.visnacom.model.*;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.view.DefaultDrawingStyle;
import org.visnacom.view.Geometry;


/**
 * provides algorithms for graph creation.
 */
public class RandomGraphs {
    //~ Methods ================================================================


    /**
     * this method creates the edges dependent on the defined mean complexity.
     * 
     *
     * @param cpg DOCUMENT ME!
     * @param absoluteComplexity the wished mean complexity
     * @param numOfEdges wished number of edges
     * @param seed DOCUMENT ME!
     * @param nodes array of nodes for direct access, can be null
     *
     * @throws CreationError DOCUMENT ME!
     */
    public static void addEdgesComplexityDependant(CompoundGraph cpg,
        double absoluteComplexity, int numOfEdges, int seed, Node[] nodes)
        throws CreationError {
        if(nodes == null) {
            nodes =
                (Node[]) cpg.getAllNodes().toArray(new Node[cpg.getNumOfNodes()]);
        }

        //make sure the ids fit.
        for(int i = 0; i < nodes.length; i++) {
            nodes[i].setId(i);
        }

        Entry[][] matrix = new Entry[nodes.length][nodes.length];

        bottom_up(cpg.getRoot(), cpg, matrix);
        top_down(cpg.getRoot(), cpg, matrix);

        //compute the a_i's
        int[] a = new int[nodes.length];
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                if(matrix[i][j].isValid()) {
                    a[matrix[i][j].getValue()]++;
                }
            }
        }

        //determine k
        int k = 2;
        int sumOfAllowedEdges = 0;
        for(; k < a.length; k++) {
            sumOfAllowedEdges += a[k];
            if(a[k] == 0) {
                break;
            }
        }

        k--;
        // is -2, because loops are forbidden and edges to parent, too.
        assert sumOfAllowedEdges < nodes.length * (nodes.length - 2);

        Profiler.set(Profiler.ALLOWED_EDGES, new Integer(sumOfAllowedEdges));
        Profiler.set(Profiler.MAX_COMPLEXITY, new Integer(k));

        if(nodes.length > 2 && k < 2) {
            //the graph is degenerated to a chain. it is impossible to add any
            // edges
            throw new CreationError("unable to create edges");
        }

        double[] c = new double[k - 1];

        //if (absoluteComplexityMode) {
        double complexity = absoluteComplexity;

        Profiler.set(Profiler.MEAN_COMPLEXITY_DEF, new Double(complexity));
        if(k < complexity) {
            //wished complexity cannot be achieved
            assert false;
            throw new CreationError("complexity to big: k=" + k
                + ", complexity=" + complexity);
        }

        if(complexity == 2 || complexity == k) {
            System.out.println("WARNING: complexity=" + complexity
                + " at limit. graph creation degenerates");
        }

        computeBinomialDistribution(numOfEdges, complexity - 2, c, false);

        //} else {

        /*this was a try. not used. the idea was to choose the mean such that 
         * half of the edges connect siblings */
        /*
           //equation: 0.5*numOfEdges == q^k-2*numOfEdges (= c_2)
           double q = Math.pow(relEdgesBetweenSiblings,1.0/((double)k-2));
           double mean = (k-2)*(1-q);
           computeBinomialDistribution(numOfEdges, mean, c, false);
         * */

        //}
        double[] p = new double[k + 1];
        for(int i = 2; i < p.length; i++) {
            p[i] = c[i - 2] / (double) a[i];
        }

        double maxValue = 0;
        for(int i = 2; i < p.length; i++) {
            maxValue = Math.max(maxValue, p[i]);
        }

        if(maxValue > 1) {
            throw new CreationError(
                "wished number of edges could not be reached");
        }

        //        for(int i = 2; i < p.length; i++) {
        //            p[i] /= maxValue;
        //        }
        Random ran = new Random(seed);
        for(int i = 0; i < nodes.length; i++) {
            for(int j = 0; j < nodes.length; j++) {
                if(matrix[i][j].isValid()) {
                    double p_i = p[matrix[i][j].getValue()];
                    assert p_i <= 1;
                    //                        double sureEdges = Math.floor(p_i);
                    //                        for(int l = 1; l <= sureEdges; l++) {
                    //                            cpg.newEdge(nodes[i], nodes[j]);
                    //                        }
                    //                        double newp = p_i - sureEdges;
                    if(ran.nextDouble() <= p_i) {
                        cpg.newEdge(nodes[i], nodes[j]);
                    }
                }
            }
        }
    }

    /**
     * fills the matrix with 'invalid' entries, i.e. between ancestors and
     * descendants
     *
     * @param v DOCUMENT ME!
     * @param c DOCUMENT ME!
     * @param matrix DOCUMENT ME!
     */
    public static void bottom_up(Node v, CompoundGraph c, Entry[][] matrix) {
        for(Iterator it = c.getChildrenIterator(v); it.hasNext();) {
            Node w = (Node) it.next();
            bottom_up(w, c, matrix);

            for(int i = 0; i < matrix[w.getId()].length; i++) {
                if(i != v.getId() && matrix[w.getId()][i] != null) {
                    matrix[v.getId()][i] = matrix[w.getId()][i].add(1, false);
                    matrix[i][v.getId()] = matrix[v.getId()][i];
                }
            }
        }

        matrix[v.getId()][v.getId()] = new Entry(0, false);
    }

    /**
     * originally creation algorithm.
     *
     * @param graph an empty graph to fill
     * @param numNodes DOCUMENT ME!
     * @param density DOCUMENT ME!
     * @param meanDegree DOCUMENT ME!
     * @param seed DOCUMENT ME!
     *
     * @throws CreationError DOCUMENT ME!
     */
    public static void constructRandomGraph(CompoundGraph graph, int numNodes,
        double density, double meanDegree, int seed)
        throws CreationError {
        /*profiling */
        Profiler.set(Profiler.NUM_NODES, new Integer(numNodes));
        Profiler.set(Profiler.DENSITY_DEF, new Double(density));
        //Profiler.set(Profiler.REL_COMPLEXITY_DEF, new Double(rel_complexity));
        Profiler.set(Profiler.MEAN_DEGREE_DEF, new Double(meanDegree));
        Profiler.set(Profiler.SEED, new Integer(seed));

        /*work */
        Node[] nodes =
            constructInclusionTree(graph, numNodes, meanDegree, seed);
        if(numNodes != graph.getNumOfNodes()) {
            throw new CreationError("inclusion tree creation failed");
        }

        //        addEdges(graph, density, rel_complexity, seed, nodes);
        addEdgesUniformly(graph, density, seed, nodes);
        //        addEdges3(graph, rel_complexity, seed, nodes);

        /*profiling*/
        Profiler.set(Profiler.NUM_EDGES_ACT, new Integer(graph.getNumOfEdges()));
        Profiler.set(Profiler.MEAN_COMPLEXITY_ACT,
            new Double(graph.getMeanComplexity()));
        Profiler.set(Profiler.MEAN_DEGREE_ACT, new Double(graph.getMeanDegree()));
        Profiler.set(Profiler.TREE_HEIGHT,
            (new Integer(graph.inclusionHeight(graph.getRoot()).intValue())));
    }

    /**
     * modified creation algorithm.
     *
     * @param numNodes DOCUMENT ME!
     * @param meanDegree DOCUMENT ME!
     * @param numEdges DOCUMENT ME!
     * @param graph DOCUMENT ME!
     * @param absoluteComplexity
     * @param seed DOCUMENT ME!
     *
     * @throws CreationError
     */
    public static void constructRandomGraphComplexitydependant(int numNodes,
        double meanDegree, int numEdges, CompoundGraph graph,
        double absoluteComplexity, int seed)
        throws CreationError {
        /*profiling */

        Profiler.set(Profiler.NUM_NODES, new Integer(numNodes));
        Profiler.set(Profiler.MEAN_DEGREE_DEF, new Double(meanDegree));
        Profiler.set(Profiler.SEED, new Integer(seed));

        /*work */
        Node[] nodes =
            constructInclusionTree(graph, numNodes, meanDegree, seed);
        if(numNodes != graph.getNumOfNodes()) {
            throw new CreationError("inclusion tree creation failed");
        }

        addEdgesComplexityDependant(graph, absoluteComplexity, numEdges, seed,
            nodes);

        /*profiling*/
        Profiler.set(Profiler.NUM_EDGES_ACT, new Integer(graph.getNumOfEdges()));
        Profiler.set(Profiler.MEAN_COMPLEXITY_ACT,
            new Double(graph.getMeanComplexity()));
        Profiler.set(Profiler.MEAN_DEGREE_ACT, new Double(graph.getMeanDegree()));
        Profiler.set(Profiler.TREE_HEIGHT,
            (new Integer(graph.inclusionHeight(graph.getRoot()).intValue())));
    }

    /**
     * third creation algorithm. not used. creates the edges dependant on the depth
     * of the end nodes.
     *
     * @param graph
     * @param numNodes
     * @param meanDegree
     * @param numEdges
     * @param seed
     *
     * @throws CreationError
     */
    public static void constructRandomGraphDepthDependant(CompoundGraph graph,
        int numNodes, double meanDegree, int numEdges, int seed)
        throws CreationError {
        double meanDepthValue = 0.8;

        /*profiling */
        Profiler.set(Profiler.NUM_NODES, new Integer(numNodes));
        //            Profiler.set(Profiler.DENSITY_DEF, new Double(density));
        //            Profiler.set(Profiler.REL_COMPLEXITY_DEF, new Double(rel_complexity));
        Profiler.set(Profiler.MEAN_DEGREE_DEF, new Double(meanDegree));
        Profiler.set(Profiler.SEED, new Integer(seed));

        /*work */
        Node[] nodes =
            constructInclusionTree(graph, numNodes, meanDegree, seed);
        if(numNodes != graph.getNumOfNodes()) {
            throw new CreationError("inclusion tree creation failed");
        }

        addEdgesDepthdependant(graph, numEdges, seed, nodes, meanDepthValue);

        /*profiling*/
        Profiler.set(Profiler.NUM_EDGES_ACT, new Integer(graph.getNumOfEdges()));
        Profiler.set(Profiler.MEAN_COMPLEXITY_ACT,
            new Double(graph.getMeanComplexity()));
        Profiler.set(Profiler.MEAN_DEGREE_ACT, new Double(graph.getMeanDegree()));
        Profiler.set(Profiler.TREE_HEIGHT,
            (new Integer(graph.inclusionHeight(graph.getRoot()).intValue())));
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @throws CreationError DOCUMENT ME!
     */
    public static void main(String[] args)
        throws CreationError {
        ViewPanel viewPanel = new ViewPanel();
        Geometry geo = viewPanel.getGeometry();
        geo.setDrawingStyle(new DefaultDrawingStyle(geo));
        Profiler.reset();
        //        contructRandomGraphDepthDependant(geo.getView(), 50, 3, 20, 0);
        //        constructRandomGraphComplexitydependant(20,3,20,geo.getView(), 0.2, 0);
        geo.setDrawingStyle(new SugiyamaDrawingStyle(geo));
        geo.redraw();
        Profiler.saveTestResult();
        Profiler.printResults();
        DummyPicture.show(geo);
        //        DummyPicture.write(geo, "test" , "pdf");
        //    double[] a = new double[4];
        //    computeBinomialDistribution(200, 3, null, false)
    }

    /**
     * DOCUMENT ME!
     *
     * @param v DOCUMENT ME!
     * @param c DOCUMENT ME!
     * @param matrix DOCUMENT ME!
     */
    public static void top_down(Node v, CompoundGraph c, Entry[][] matrix) {
        for(Iterator it = c.getChildrenIterator(v); it.hasNext();) {
            Node w = (Node) it.next();
            for(int i = 0; i < matrix[w.getId()].length; i++) {
                if(matrix[w.getId()][i] == null) {
                    matrix[w.getId()][i] = matrix[v.getId()][i].add(1, true);
                }
            }

            top_down(w, c, matrix);
        }
    }

    /**
     * edge creation dependent on depth of nodes
     *
     * @param cpg DOCUMENT ME!
     * @param numOfEdges DOCUMENT ME!
     * @param seed DOCUMENT ME!
     * @param nodes DOCUMENT ME!
     * @param relmeanDepthSum
     *
     * @throws CreationError
     */
    private static void addEdgesDepthdependant(CompoundGraph cpg,
        int numOfEdges, int seed, Node[] nodes, double relmeanDepthSum)
        throws CreationError {
        if(nodes == null) {
            nodes =
                (Node[]) cpg.getAllNodes().toArray(new Node[cpg.getNumOfNodes()]);
        }

        //make sure the ids fit.
        for(int i = 0; i < nodes.length; i++) {
            nodes[i].setId(i);
        }

        Entry[][] matrix = new Entry[nodes.length][nodes.length];
        bottom_up(cpg.getRoot(), cpg, matrix);

        //now the matrix is filled with invalid entries, all allowed edges have
        //        "null" entries
        int[] t = new int[nodes.length];
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                if(matrix[i][j] == null) {
                    t[cpg.inclusionDepth(nodes[i]).intValue()
                    + cpg.inclusionDepth(nodes[j]).intValue()]++;
                }
            }
        }

        //determine maxdepth
        int maxDepth = 0;
        for(int i = 0; i < t.length; i++) {
            if(t[i] > 0) {
                maxDepth = i;
            }
        }

        int treeHeight = cpg.inclusionHeight(cpg.getRoot()).intValue();
        assert maxDepth <= treeHeight * 2;
        assert t[0] == 0;
        assert t[1] == 0;

        //compute wished number of edges
        double[] c = new double[maxDepth - 1];
        computeBinomialDistribution(numOfEdges,
            relmeanDepthSum * (maxDepth - 2), c, false);

        //compute probabilites p_i
        double[] p = new double[maxDepth + 1];
        for(int i = 2; i < p.length; i++) {
            p[i] = c[i - 2] / (double) t[i];
        }

        //test, whether the p_i are <= 1
        double maxValue = 0;
        for(int i = 2; i < p.length; i++) {
            maxValue = Math.max(maxValue, p[i]);
        }

        if(maxValue > 1) {
            throw new CreationError(
                "wished number of edges could not be reached");
        }

        Random ran = new Random(seed);
        for(int i = 0; i < nodes.length; i++) {
            for(int j = 0; j < nodes.length; j++) {
                if(matrix[i][j] == null) {
                    double p_i =
                        p[cpg.inclusionDepth(nodes[i]).intValue()
                        + cpg.inclusionDepth(nodes[j]).intValue()];
                    if(ran.nextDouble() <= p_i) {
                        cpg.newEdge(nodes[i], nodes[j]);
                    }
                }
            }
        }
    }

    /**
     * choses the edges uniformly with p=d (density)
     *
     * @param cpg DOCUMENT ME!
     * @param density DOCUMENT ME!
     * @param seed DOCUMENT ME!
     * @param nodes DOCUMENT ME!
     *
     */
    private static void addEdgesUniformly(CompoundGraph cpg, double density,
        int seed, Node[] nodes) {
        if(nodes == null) {
            nodes =
                (Node[]) cpg.getAllNodes().toArray(new Node[cpg.getNumOfNodes()]);
        }

        for(int i = 0; i < nodes.length; i++) {
            nodes[i].setId(i);
        }

        Random ran = new Random(seed);
        for(int i = 0; i < nodes.length; i++) {
            for(int j = 0; j < nodes.length; j++) {
                if(i != j) {
                    if(ran.nextDouble() <= density) {
                        try {
                            cpg.newEdge(nodes[i], nodes[j]);
                        } catch(InvalidEdgeException e) {}
                    }
                }
            }
        }
    }

    /**
     * fills the given array with values c_0,...c_n, such that the sum is the
     * specified sum and the mean value is the specified mean value. all
     * values should be positive.
     *
     * @param sum DOCUMENT ME!
     * @param mean DOCUMENT ME!
     * @param values DOCUMENT ME!
     * @param cumulative DOCUMENT ME!
     */
    private static void computeBinomialDistribution(int sum, double mean,
        double[] values, boolean cumulative) {
        int n = values.length - 1;
        double p = mean / n;
        assert mean <= n;

        long binkoff = 1; //start with n over 0
        for(int k = 0; k <= n / 2; k++) {
            if(k > 0) {
                //in the first loop, the value needs no adjust
                //                assert binkoff * (n - k + 1) % k == 0;
                //                System.out.println((binkoff * (n - k + 1)) % k);
                //computation with doubles or ints should yield the same
                assert Math.round(((double) (n - k + 1) / (double) k) * (double) binkoff) == (binkoff * (n
                - k + 1)) / k && binkoff * (n - k + 1) % k == 0;

                binkoff = (binkoff * (n - k + 1)) / k;
            }

            values[k] =
                (double) sum * (double) binkoff * Math.pow(1.0 - p, n - k) * Math
                .pow(p, k);
            values[(n - k)] =
                (double) sum * (double) binkoff * Math.pow(1.0 - p, k) * Math
                .pow(p, n - k);
        }

        if(cumulative) {
            for(int i = 1; i < values.length; i++) {
                assert values[i] > 0 || mean == 0 || mean == n;
                //values should only be zero, if the mean value is at the extrema.
                values[i] += values[i - 1];
            }
        }
    }

    /**
     * used version. chooses the number of children for each node with certain
     * distribution.
     *
     * @param c DOCUMENT ME!
     * @param numNodes DOCUMENT ME!
     * @param meanDegree DOCUMENT ME!
     * @param seed DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static Node[] constructInclusionTree(CompoundGraph c, int numNodes,
        double meanDegree, int seed) {
        Random ran = new Random(seed);
        Node[] nodes = new Node[numNodes];
        nodes[0] = c.getRoot();

        int actualsize = 1;
        LinkedList queue = new LinkedList();
        queue.add(c.getRoot());

        assert meanDegree >= 1;

        int n = (int) Math.rint(meanDegree * 2);

        //degrees should be between 1 and n
        //values are inside p shifted one entry to the left: 0..n-1
        double[] p = new double[n];
        computeBinomialDistribution(1, meanDegree - 1, p, true);

        while(actualsize < numNodes) {
            Node next = (Node) queue.removeFirst();

            int degree = 0;
            double rannumber = ran.nextDouble();

            //double sum = 0;
            //            int i = 0;
            while(rannumber > p[degree]) {
                degree++;
            }

            degree++; //I want to start at 1

            for(int i = 0; i < degree && actualsize < numNodes; i++) {
                Node newn = (c.newLeaf(next));
                queue.add(newn);
                nodes[actualsize++] = newn;
            }
        }

        return nodes;
    }

    /**
     * unused. number of children is computed out of relation between numLeaves
     * and numNodes. construction bottom up starting with leaves. is taken
     * from forster
     *
     * @param c the compound graph to fill
     * @param numLeaves the wished number of leaves. is reached approximately
     * @param numNodes the wished number of total nodes. is reached exactly
     * @param seed initial value for pseudo random numbers
     */
    private static void constructInclusionTree3(CompoundGraph c, int numLeaves,
        int numNodes, int seed) {
        assert numLeaves < numNodes;

        Random random = new Random(seed);

        for(int i = 0; i < numLeaves; i++) {
            c.newLeaf(c.getRoot());
        }

        List nodes = c.getAllNodes();
        nodes.remove(c.getRoot());

        int numChildren = (numNodes) / (numNodes - numLeaves);

        /* that leads to: */
        /* numChildren - 1 <= childrenRoot <= (numNodes - numLeaves) + (numChildren -1) */

        //int numChildren = (numNodes - 1) / (numNodes - numLeaves);

        /* would lead to:
         * numChildren <= childrenRoot <= (numNodes - numLeaves) + numChildren*/
        while(nodes.size() >= numChildren && numNodes > c.getNumOfNodes()) {
            List children = new ArrayList(numChildren);
            for(int i = 0; i < numChildren; i++) {
                children.add(nodes.remove(random.nextInt(nodes.size())));
            }

            nodes.add(c.split(children));
        }
    }

    /**
     * chooses for each node uniformly its parent. not in use.
     *
     * @param c DOCUMENT ME!
     * @param size DOCUMENT ME!
     * @param seed DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static Node[] constructInclusionTreeOld1(CompoundGraph c, int size,
        int seed) {
        Node[] nodes = new Node[size];
        for(int i = 0; i < nodes.length; i++) {
            nodes[i] = c.newLeaf(c.getRoot());
        }

        Random ran = new Random(seed);
        for(int i = 0; i < nodes.length; i++) {
            int parent;
            int numberOfTries = 0;
            do {
                parent = ran.nextInt(size);
            } while(numberOfTries++ < size
                && (c.isAncestor(nodes[i], nodes[parent])));

            if(numberOfTries < size) {
                c.moveNode(nodes[i], nodes[parent]);
            }
        }

        return nodes;
    }

    //~ Inner Classes ==========================================================

    /**
     *
     */
    public static class CreationError extends Exception {
        /**
         * Creates a new CreationError object.
         *
         * @param arg0 DOCUMENT ME!
         */
        public CreationError(String arg0) {
            super(arg0);
        }
    }

    /**
     * represents an entry in the matrix, see thesis
     */
    public static class Entry {
        private boolean valid;
        private int value;

        /**
         * Creates a new Entry object.
         *
         * @param i DOCUMENT ME!
         * @param b DOCUMENT ME!
         */
        public Entry(int i, boolean b) {
            value = i;
            valid = b;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int getValue() {
            return value;
        }

        /**
         * DOCUMENT ME!
         *
         * @param i DOCUMENT ME!
         * @param b DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Entry add(int i, boolean b) {
            return new Entry(value + i, b);
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String toString() {
            if(valid) {
                return "" + value;
            } else {
                return "X" + value + "X";
            }
        }
    }
}
