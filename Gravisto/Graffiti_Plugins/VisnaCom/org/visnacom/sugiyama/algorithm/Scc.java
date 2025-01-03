/*==============================================================================
*
*   Scc.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: Scc.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.model.Node;
import org.visnacom.sugiyama.model.*;


/**
 * specialization of the depth-first search to compute the strongly connected
 * Components. the attribute scc of class SugiNode used.
 * the algorithm is taken from Brandes Methoden der netzwerkanalyse(SS03)
 */
public class Scc extends DepthFirstSearch {
    //~ Instance fields ========================================================

    HashMap iterators = new HashMap();
    List listOfComponents = new LinkedList();
    Stack C = new Stack();
    Stack S_V = new Stack();
    private DerivedGraph derivedGraph;

    //~ Methods ================================================================

    /**
     * computes the strongly connected components of a graph uses Brandes,
     * Methoden der Netzwerkanalyse(SS2003) Satz 1.33.  sets the scc attribute
     * in the SugiNodes.
     *
     * @param graph the graph to process. it must contain SugiNode-objects and
     *        DerivedEdge-objects
     * @param subgraph a list containing SugiNode object that are also
     *        contained in "graph"
     *
     * @return a list of lists of SugiNode objects. Each contained list
     *         represents a scc.
     */
    public List findScc(DerivedGraph graph, List subgraph) {
        assert Hierarchization.testConsistenceOfSubgraph(graph, subgraph);
        this.derivedGraph = graph;
        super.depthFirstSearch(subgraph);

        assert (S_V.empty());
        assert (C.empty());

        List temp = listOfComponents;
        listOfComponents = new LinkedList();
        return temp;
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#getNextUnmarkedEdge(org.visnacom.sugiyama.model.DFSNode)
     */
    protected DFSEdge getNextUnmarkedEdge(DFSNode n) {
        //the for loop is only traversed to the first suitable element
        for(Iterator it = (Iterator) iterators.get(n); it.hasNext();) {
            DerivedEdge e = (DerivedEdge) it.next();

            //added "if" because of restriction to subgraph
            if(e.isIntern()) {
                return e;
            }
        }

        return null;
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#backtrack(org.visnacom.sugiyama.model.DFSNode,
     *      org.visnacom.sugiyama.model.DFSNode)
     */
    protected void backtrack(DFSNode w2, DFSNode v) {
        SugiNode w = (SugiNode) w2;

        // System.out.println("backtrack: w=" + w + " v=" + v);
        if(w == C.peek()) {
            //then w is the next representative
            C.pop();
            w.setScc(w);

            List l = new LinkedList();

            //l.add(w);
            SugiNode u;
            while(true) {
                u = (SugiNode) S_V.pop();
                u.setScc(w);
                l.add(u);

                //until (u==w)
                if(u == w) {
                    break;
                }
            }

            //System.out.println("scc found: " + l);
            listOfComponents.add(l);
        }
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#initializeNewNodeInStack(org.visnacom.sugiyama.model.DFSNode)
     */
    protected void initializeNewNodeInStack(DFSNode n) {
        iterators.put(n, derivedGraph.getOutEdgesIterator((Node) n));
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#root(org.visnacom.sugiyama.model.DFSNode)
     */
    protected void root(DFSNode s) {
        // System.out.println("root:" + s);
        S_V.push(s);
        C.push(s);
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#traverse(org.visnacom.sugiyama.model.DFSNode,
     *      org.visnacom.sugiyama.model.DFSEdge, org.visnacom.sugiyama.model.DFSNode)
     */
    protected void traverse(DFSNode v, DFSEdge e, DFSNode w) {
        assert (v != w);
        // System.out.println("traverse: v=" + v + " e=" + e + " w=" + w);
        //if e is tree-edge
        if(!marked.contains(w)) {
            S_V.push(w);
            C.push(w);
        } else {
            assert marked.contains(w);
        }

        /*if e is back-edge or "quer" with w in S_V
         * back-edge: w marked and w <= v and w in S
         * "quer": w marked and w < v and w not in S
         * (<=> w marked and w < v) and ((w in s) or (w notin S and w in S_V))
         * (loops are forbidden)
         */
        if(marked.contains(w) && w.getDfsNumber() < v.getDfsNumber()) {
            if(!(!S.contains(w) && !S_V.contains(w))) {
                while(w.getDfsNumber() < ((SugiNode) C.peek()).getDfsNumber()) {
                    C.pop();
                }
            }
        }
    }
}
