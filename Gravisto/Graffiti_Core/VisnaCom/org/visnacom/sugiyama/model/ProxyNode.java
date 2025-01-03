/*==============================================================================
*
*   ProxyNode.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: ProxyNode.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import java.util.*;

import org.visnacom.model.Node;


/**
 * used in DerivedGraph during Hierarchization
 */
public class ProxyNode extends SugiNode {
    //~ Instance fields ========================================================

    private List originalNodes = new LinkedList();

    //~ Constructors ===========================================================

    /**
     * Creates a new ProxyNode object.
     *
     * @param id DOCUMENT ME!
     */
    public ProxyNode(int id) {
        super(id);
    }

    /**
     * Creates a new ProxyNode object.
     */
    public ProxyNode() {
        super();
    }

    //~ Methods ================================================================

    /**
     * @see SugiNode#setClev(CompoundLevel)
     */
    public void setClev(CompoundLevel clev) {
        super.setClev(clev);
        for(Iterator it = originalNodes.iterator(); it.hasNext();) {
            Node n = (Node) it.next();
            ((SugiNode) n).setClev(clev);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodes DOCUMENT ME!
     */
    public void setOriginalNodes(List nodes) {
        originalNodes = new LinkedList(nodes);
        scc = this;
        assert nodes.size() > 1;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String result = "Proxy" + super.toString() + " contains ";
        for(Iterator it = originalNodes.iterator(); it.hasNext();) {
            result += it.next() + ", ";
        }

        return result;
    }
}
