/*==============================================================================
*
*   PositionInitializer.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: PositionInitializer.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.sugiyama.model.*;

/**
 * used in vertex ordering to compute initial node positions in a level using
 * depth first search. this is done to seperate independant subgraphs.
 */
public class PositionInitializer extends DepthFirstSearch {
    //~ Instance fields ========================================================

    HashMap iterators = new HashMap();
    List result = new LinkedList();
    Map incomingEdges;
    Map outgoingEdges;

    //~ Methods ================================================================

    /**
     * changes the positions of the nodes in the level so that independent
     * subgraphs are not entangled. uses a undirected depht first search
     *
     * @param level a list of nodes
     * @param outgoingEdges a map containing a list of outgoing edges for each
     *        node.
     * @param incomingEdges a map containing a list of incoming edges for each
     *        node.
     */
    public void order(List level, Map outgoingEdges, Map incomingEdges) {
        if(level.size() <= 1
            || !(new EdgeIteratorOfLevel(level, outgoingEdges)).hasNext()) {
            return;
        }

        this.outgoingEdges = outgoingEdges;
        this.incomingEdges = incomingEdges;
        depthFirstSearch(level);

        Iterator it = result.iterator();
        ListIterator it2 = level.listIterator();
        while(it2.hasNext()) {
            assert it.hasNext();
            it2.next();
            it2.set(it.next());
        }

        if(it.hasNext()) {
            assert false;
            System.err.println("Internal error in PositionInitializer.order");
        }
    }

    /**
     * @see DepthFirstSearch#getNextUnmarkedEdge(org.visnacom.sugiyama.model.DFSNode)
     */
    protected DFSEdge getNextUnmarkedEdge(DFSNode n) {
        Iterator it = (Iterator) iterators.get(n);
        if(it.hasNext()) {
            return (DFSEdge) it.next();
        } else {
            return null;
        }
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#backtrack(org.visnacom.sugiyama.model.DFSNode,
     *      org.visnacom.sugiyama.model.DFSNode)
     */
    protected void backtrack(DFSNode w, DFSNode node) {}

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#initializeNewNodeInStack(SugiNode)
     */
    protected void initializeNewNodeInStack(DFSNode n) {
        iterators.put(n,
            new TwoCollectionsIterator((List) outgoingEdges.get(n),
                (Collection) incomingEdges.get(n)));
        result.add(n);
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#root(org.visnacom.sugiyama.model.DFSNode)
     */
    protected void root(DFSNode node) {
//        result.add(node);
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#traverse(org.visnacom.sugiyama.model.DFSNode,
     *      org.visnacom.sugiyama.model.DFSEdge, org.visnacom.sugiyama.model.DFSNode)
     */
    protected void traverse(DFSNode v, DFSEdge e, DFSNode w) {
//        result.add(w);
    }

    //~ Inner Classes ==========================================================

    /**
     * help-iterator for easy iteration over two collections
     */
    private static class TwoCollectionsIterator implements Iterator {
        boolean firstCompleted = false;
        private Iterator it;
        private Iterator secondIt;

        /**
         * DOCUMENT ME!
         *
         * @param first
         * @param second
         */
        public TwoCollectionsIterator(Collection first, Collection second) {
            this.secondIt = second.iterator();
            it = first.iterator();
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return it.hasNext() || secondIt.hasNext();
        }

        /**
         * @see java.util.Iterator#next()
         */
        public Object next() {
            if(it.hasNext()) {
                return it.next();
            } else {
                return secondIt.next();
            }
        }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
