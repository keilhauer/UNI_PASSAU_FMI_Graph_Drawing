/*==============================================================================
*
*   ContractNormalization.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: ContractNormalization.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.model.ActionContract;
import org.visnacom.model.Edge;
import org.visnacom.sugiyama.model.*;


/**
 * see thesis chapter Contraction.Normalization
 */
public class ContractNormalization {
    //~ Methods ================================================================

    /**
     *  the main method to call
     *
     * @param s DOCUMENT ME!
     * @param action DOCUMENT ME!
     * @param v DOCUMENT ME!
     */
    public static void contract(SugiCompoundGraph s, ActionContract action, SugiNode v) {
        for(Iterator it = action.getMappinsIterator(); it.hasNext();) {
            ActionContract.Mapping m = (ActionContract.Mapping) it.next();
            adjustEdge(s, m, v);
        }

        assert s.checkEdgeMappings(0, true);
        assert s.checkLHs(2, false); //set it to true, if warnings wished
    }

    /**
     * for debug only!
     *
     * @param s DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param v DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static SugiNode getW(SugiCompoundGraph s, ActionContract.Mapping m,
        SugiNode v) {
        SugiNode w = s.getCorrespondingNode(m.newEdge.getSource());
        if(w == v) {
            w = s.getCorrespondingNode(m.newEdge.getTarget());
        } else {
            assert s.getCorrespondingNode(m.newEdge.getTarget()) == v;
        }

        return w;
    }

    /**
     * performs the needed operations on the contracted edge paths.
     *
     * @param s DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param v DOCUMENT ME!
     */
    private static void adjustEdge(SugiCompoundGraph s,
        ActionContract.Mapping m, SugiNode v) {
        List edgepaths = new LinkedList();

        for(Iterator it = m.oldEdges.iterator(); it.hasNext();) {
            List edgePath = s.getCorrespondingEdges((Edge) it.next());
            edgepaths.add(edgePath);
        }

        Iterator it = edgepaths.iterator();

        //special case v_1
        if(it.hasNext()) {
            List edgepath = (List) it.next();
            if(!m.newEdgeInserted) {
                deleteEdgePath(s, edgepath);
                it.remove();
            }
        }

        //for i = 2..n
        while(it.hasNext()) {
            deleteEdgePath(s, (List) it.next());
            it.remove();
        }

        if(m.newEdgeInserted) {
            /* modify edge path */

            //      identify v_iC_i's
            boolean isFromVtoU = false;
            assert edgepaths.size() == 1;

            List edgePath1 = (List) edgepaths.get(0);
            SugiEdge upperSegment = (SugiEdge) edgePath1.get(0);
            SugiEdge lowerSegment =
                (SugiEdge) edgePath1.get(edgePath1.size() - 1);
            if(s.getParent(upperSegment.getSource()) == v) {
                isFromVtoU = true;
                assert lowerSegment.getTarget() == getW(s, m, v);
            } else {
                isFromVtoU = false;
                assert s.getParent(lowerSegment.getTarget()) == v;
                assert upperSegment.getSource() == getW(s, m, v);
            }

            Edge origE = upperSegment.getOriginalEdge();

            if(isFromVtoU) {
                SugiEdge v_1C_1 = (SugiEdge) edgePath1.remove(0);
                SugiEdge p_1U_1 = (SugiEdge) edgePath1.remove(0);
                s.deleteSubTree((SugiNode) p_1U_1.getSource());

                SugiEdge vU = (SugiEdge) s.newEdge(v, p_1U_1.getTarget());
                edgePath1.add(0, vU);
            } else {
                SugiEdge c_1V_1 =
                    (SugiEdge) edgePath1.remove(edgePath1.size() - 1);
                SugiEdge u_1P_1 =
                    (SugiEdge) edgePath1.remove(edgePath1.size() - 1);
                s.deleteSubTree((SugiNode) u_1P_1.getTarget());

                SugiEdge uV = (SugiEdge) s.newEdge(u_1P_1.getSource(), v);
                edgePath1.add(uV);
            }

            s.deleteMapping(origE);
            s.establishEdgeMapping(m.newEdge, edgePath1);
        }
    }

    /**
     * deletes the given edgepath completely
     *
     * @param s DOCUMENT ME!
     * @param edgepath DOCUMENT ME!
     */
    private static void deleteEdgePath(SugiCompoundGraph s, List edgepath) {
        //traversier hoch, dann beim runtergehen bereits LH's l�schen
        //rekursiv alle bl�tter l�schen
        for(Iterator it = edgepath.iterator(); it.hasNext();) {
            SugiEdge edge = (SugiEdge) it.next();
            if(it.hasNext()) {
                //deleteTarget
                SugiNode sn = (SugiNode) edge.getTarget();

                if(!s.getAllNodes().contains(sn)) {
                    continue;
                }

                SugiNode paSn = (SugiNode) s.getParent(sn);
                assert sn.isDummyNode();
                while(paSn.isDummyNode()) {
                    sn = paSn;
                    paSn = (SugiNode) s.getParent(sn);
                }

                s.deleteSubTree(sn);
            }
        }

        s.deleteMapping(((SugiEdge) edgepath.get(0)).getOriginalEdge());
    }
}
