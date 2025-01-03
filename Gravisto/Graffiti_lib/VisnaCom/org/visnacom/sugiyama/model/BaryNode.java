/*==============================================================================
*
*   BaryNode.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: BaryNode.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

/**
 * interface for use in vertex ordering
 */
public interface BaryNode extends DFSNode, SortableNode {
    //~ Methods ================================================================

    /**
     * sets the barrycenter value
     *
     * @param barryCenter DOCUMENT ME!
     */
    public void setBarryCenter(float barryCenter);

    /**
     * gets the barry center value
     *
     * @return DOCUMENT ME!
     */
    public float getBarryCenter();

    /**
     * sets the position attribute.
     *
     * @param i DOCUMENT ME!
     */
    public void setPosition(int i);

   
    /**
     * returns the lambda rho value
     *
     * @return the value: (lambda - rho)
     */
    public float getLambdaRho();

    /**
     * DOCUMENT ME!
     *
     * @return the local level of this node
     */
    public int getLevel();
}
