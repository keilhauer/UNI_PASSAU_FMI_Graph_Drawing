/*==============================================================================
*
*   BaryEdge.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: BaryEdge.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

/**
 * interface for barycenter ordering
 */
public interface BaryEdge extends DFSEdge,SortableEdge {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param source
     */
    public void setBSource(BaryNode source);

    /**
     * Returns the source of this edge
     *
     * @return DOCUMENT ME!
     */
    public BaryNode getBSource();

    /**
     * DOCUMENT ME!
     *
     * @param target
     */
    public void setBTarget(BaryNode target);

    /**
     * Returns the target of this edge
     *
     * @return DOCUMENT ME!
     */
    public BaryNode getBTarget();

    /**
     * stores the left edge that is inserted in the upper/lower level, if this
     * edge is horizontal
     *
     * @param dummyEdgeLef DOCUMENT ME!
     */
    public void setDummyEdgeLeft(BaryEdge dummyEdgeLef);

    /**
     * Returns the stored left edge
     *
     * @return DOCUMENT ME!
     */
    public BaryEdge getDummyEdgeLeft();

    /**
     * stores the right edge that is inserted in the upper/lower level, if this
     * edge is horizontal
     *
     * @param dummyEdgeRight DOCUMENT ME!
     */
    public void setDummyEdgeRight(BaryEdge dummyEdgeRight);

    /**
     * Returns the stored right edge
     *
     * @return DOCUMENT ME!
     */
    public BaryEdge getDummyEdgeRight();

    /**
     * stores the node that is inserted in the upper/lower level, if this edge
     * is horizontal
     *
     * @param dummyNode DOCUMENT ME!
     */
    public void setDummyNode(BaryNode dummyNode);

    /**
     * Returns the stored node
     *
     * @return DOCUMENT ME!
     */
    public BaryNode getDummyNode();
}
