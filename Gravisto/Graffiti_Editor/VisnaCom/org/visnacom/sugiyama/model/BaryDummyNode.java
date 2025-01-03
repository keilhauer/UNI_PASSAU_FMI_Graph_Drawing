/*==============================================================================
*
*   BaryDummyNode.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: BaryDummyNode.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

/**
 * dummy nodes that are inserted for a horizontal edge during barycenter method.
 */
public class BaryDummyNode implements BaryNode {
    //~ Instance fields ========================================================

    //the edge, that caused this dummy node.
    BaryEdge e;
    private float barryCenter = 0.0f;
    private float lambdarho = 0;
    private int LHPosition = -1;
    private int dfsNumber;
    private int localLevel;

    //~ Constructors ===========================================================

    /**
     * Creates a new BaryDummyNode object.
     *
     * @param e DOCUMENT ME!
     * @param level DOCUMENT ME!
     */
    public BaryDummyNode(BaryEdge e, int level) {
        this.e = e;
        this.localLevel = level;
    }

    //~ Methods ================================================================

    /**
     * @see BaryNode#setBarryCenter(float)
     */
    public void setBarryCenter(float barryCenter) {
        this.barryCenter = barryCenter;
    }

    /**
     * @see BaryNode#getBarryCenter()
     */
    public float getBarryCenter() {
        return barryCenter;
    }

    /**
     * @see org.visnacom.sugiyama.model.DFSNode#setDfsNumber(int)
     */
    public void setDfsNumber(int number) {
        assert false;
        System.out.println(
            "Warning: internal error in BaryDummyNode.setDfsNumber");
        //so far in my implementation this methods are never called
        dfsNumber = number;
    }

    /**
     * @see org.visnacom.sugiyama.model.DFSNode#getDfsNumber()
     */
    public int getDfsNumber() {
        assert false;
        System.out.println(
            "Warning: internal error in BaryDummyNode.getDfsNumber");
        //so far in my implementation this methods are never called
        return dfsNumber;
    }

    /**
     * @see BaryNode#setPosition(int)
     */
    public void setPosition(int i) {
        LHPosition = i;
    }

    /**
     * @see BaryNode#getPosition()
     */
    public int getPosition() {
        return LHPosition;
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     */
    public void setLambdaRho(float f) {
        lambdarho = f;
    }

    /**
     * @see org.visnacom.sugiyama.model.BaryNode#getLambdaRho()
     */
    public float getLambdaRho() {
        return lambdarho;
    }

    /**
     * @see org.visnacom.sugiyama.model.BaryNode#getLevel()
     */
    public int getLevel() {
        return localLevel;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String result = "";
        result += "BDNode" + e;
        result += "LR(" + lambdarho + ")";
        result += "p" + getPosition();
        result += "bc" + getBarryCenter();
        //result += "\n";
        return result;
    }
}
