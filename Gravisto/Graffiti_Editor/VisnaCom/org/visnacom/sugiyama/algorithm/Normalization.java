/*==============================================================================
*
*   Normalization.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: Normalization.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.sugiyama.model.*;


/**
 * provides static methods for normalization in static layout
 */
public class Normalization {
    //~ Methods ================================================================

    /**
     * makes the edges proper by inserting dummy nodes
     *
     * @param sugigraph DOCUMENT ME!
     */
    public static void normalize(SugiCompoundGraph sugigraph) {
        sugigraph.deactivateAllLHs();
        sugigraph.setWantedNodeType(SugiCompoundGraph.DUMMY_NODE);
        for(Iterator it = sugigraph.getAllEdges().iterator(); it.hasNext();) {
            SugiEdge e = (SugiEdge) it.next();
            normalizeEdge(sugigraph, e);
        }
    }

    /**
     * recursively creates a nested path of dummy nodes that is inserted in the
     * nearest common ancestor
     *
     * @param sugigraph DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param u DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static SugiNode createParentDummyNode(SugiCompoundGraph sugigraph,
        SugiNode v, SugiNode u) {
        SugiNode paV = (SugiNode) sugigraph.getParent(v);
        SugiNode paU = (SugiNode) sugigraph.getParent(u);
        SugiNode paP;
        if(paV == paU) {
            paP = paV;
        } else {
            paP = createParentDummyNode(sugigraph, paV, paU);
        }

        SugiNode p = sugigraph.newDummyLeaf(paP, v.getClev().getTail(),DummyNode.HORIZONTAL);
        return p;
    }

    /**
     * inserts a path of dummy nodes between two nodes which can be already
     * dummy nodes. the original edge is assumed to be deleted, and it is
     * assumed that there is in fact a need for at least one dummy node. the created
     * edges are appended to the given list
     *
     * @param s
     * @param v the source node
     * @param u the target node
     * @param parent the node where to insert the dummy nodes. Needs not to be
     *        parent of v and u
     * @param list the list to fill
     */
    private static void insertLocalDummyNodePath(SugiCompoundGraph s,
        SugiNode v, SugiNode u, SugiNode parent, List list) {
        assert v.getClev().getTail() < u.getClev().getTail() - 1;

        SugiNode d_iminus1 = v;
        DummyNode d_i = null; //used in loop and needed afterwards
        for(int i = v.getClev().getTail() + 1; i <= u.getClev().getTail() - 1;
            i++) {
            d_i = s.newDummyLeaf(parent, i,DummyNode.NORMAL);

            SugiEdge newEdge = (SugiEdge) s.newEdge(d_iminus1, d_i);
            list.add(newEdge);
            d_iminus1 = d_i;
        }

        //special action at the end:
        //close last gap
        assert u.getClev().isSuccessorOf(d_i.getClev());

        SugiEdge ne = (SugiEdge) s.newEdge(d_i, u);
        list.add(ne);
    }

    /**
     * the original edge is assumed to be deleted, and there has to be really
     * the need for at least one dummy node.  the edge path from up to down is
     * appended to the given list. No List.remove Operation is done in this method
     *
     * @param sugigraph DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param u DOCUMENT ME!
     * @param down DOCUMENT ME!
     * @param l DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static SugiEdge insertOutSideDummyNodePath(
        SugiCompoundGraph sugigraph, SugiNode v, SugiNode u, boolean down,
        LinkedList l) {
        SugiNode paV = (SugiNode) sugigraph.getParent(v);
        SugiNode paU = (SugiNode) sugigraph.getParent(u);
        int alpha = v.getClev().getLengthOfCommonPart(u.getClev());

        for(int i = (down ? v : u).getClev().getLength() - alpha; i >= 2;
            i--) {
            SugiNode p_i;
            SugiNode c;
            if(down) {
                p_i = sugigraph.newDummyLeaf((SugiNode) sugigraph.getParent(paV),
                        paV.getClev().getTail(),DummyNode.LOCAL_OR_EXTERNAL);
                c = sugigraph.newDummyLeaf(p_i, v.getClev().getTail() + 1, DummyNode.UNKNOWN);
            } else {
                p_i = sugigraph.newDummyLeaf((SugiNode) sugigraph.getParent(paU),
                        paU.getClev().getTail(), DummyNode.LOCAL_OR_EXTERNAL);
                c = sugigraph.newDummyLeaf(p_i, u.getClev().getTail() - 1, DummyNode.UNKNOWN);
            }

            if(down) {
                SugiEdge newEdge = (SugiEdge) sugigraph.newEdge(v, c);
                v = p_i;
                paV = (SugiNode) sugigraph.getParent(v);
                l.addLast(newEdge);
            } else {
                SugiEdge newEdge = (SugiEdge) sugigraph.newEdge(c, u);
                u = p_i;
                paU = (SugiNode) sugigraph.getParent(u);
                l.addFirst(newEdge);
            }
        }

        //special action at the end:
        SugiEdge se = (SugiEdge) sugigraph.newEdge(v, u);
        if(down) {
            l.addLast(se);
        } else {
            l.addFirst(se);
        }

        return se;
    }

    /**
     *
     * @param sugigraph the sugi compound graph containing the edge
     * @param e the edge to normalize
     *
     * @return the edge path
     */
    static List normalizeEdge(SugiCompoundGraph sugigraph, SugiEdge e) {
        SugiEdge remainingEdge = e;
        SugiNode v = (SugiNode) remainingEdge.getSource();
        SugiNode u = (SugiNode) remainingEdge.getTarget();
        
        //already proper
        if(u.getClev().isSuccessorOf(v.getClev())) {
            List l = new LinkedList();
            l.add(e);
            return l;
        }

        SugiNode paV = (SugiNode) sugigraph.getParent(v);
        SugiNode paU = (SugiNode) sugigraph.getParent(u);

        LinkedList firstPart = new LinkedList();
        List middlePart = new LinkedList();
        LinkedList lastPart = new LinkedList();

        firstPart.add(remainingEdge);
        //case 3
        if(paV != paU && !paV.getClev().equals(paU.getClev())) {
            int alpha = v.getClev().getLengthOfCommonPart(u.getClev());

            //there has to be something to make proper
            assert v.getClev().getLength() - alpha > 1
            || u.getClev().getLength() - alpha > 1;

            if(v.getClev().getLength() - alpha > 1) {
                sugigraph.deleteEdge(remainingEdge);

                SugiEdge se = (SugiEdge) firstPart.remove(0);
                assert se == remainingEdge;
                remainingEdge = insertOutSideDummyNodePath(sugigraph, v, u,
                        true, firstPart);
                v = (SugiNode) remainingEdge.getSource();
                assert u == remainingEdge.getTarget();
                assert remainingEdge == firstPart.get(firstPart.size() - 1);
            }

            if(u.getClev().getLength() - alpha > 1) {
                sugigraph.deleteEdge(remainingEdge);

                SugiEdge se = (SugiEdge) firstPart.remove(firstPart.size() - 1);
                assert se == remainingEdge;
                remainingEdge = insertOutSideDummyNodePath(sugigraph, v, u,
                        false, lastPart);
                u = (SugiNode) remainingEdge.getTarget();
                assert v == remainingEdge.getSource();
            }

            paV = (SugiNode) sugigraph.getParent(v);
            paU = (SugiNode) sugigraph.getParent(u);
        }

        //case 1 & 2
        assert (paV.getClev().equals(paU.getClev()));
        if(v.getClev().getTail() < u.getClev().getTail() - 1) {
            SugiNode parent;
            if(paV == paU) {
                //case 1
                parent = paV;
            } else {
                //case 2
                parent = createParentDummyNode(sugigraph, paV, paU);
            }

            sugigraph.deleteEdge(remainingEdge);
            assert (lastPart.isEmpty() && remainingEdge == firstPart.getLast())
            || remainingEdge == lastPart.get(0);
            if(lastPart.isEmpty()) {
                firstPart.removeLast();
            } else {
                lastPart.removeFirst();
            }

            insertLocalDummyNodePath(sugigraph, v, u, parent, middlePart);
        }

        //end
        firstPart.addAll(middlePart);
        firstPart.addAll(lastPart);

        sugigraph.establishEdgeMapping(e.getOriginalEdge(), firstPart);
        
        assert sugigraph.checkEdgeMapping(firstPart,1,true);
        
        return firstPart;
    }
}
