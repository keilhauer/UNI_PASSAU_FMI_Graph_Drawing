/*==============================================================================
*
*   ViewTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: ViewTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.Visnacom;
import org.visnacom.controller.ViewPanel;
import org.visnacom.model.*;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;

import junit.framework.TestCase;

/**
 * some tests using the editor.
 */
public class ViewTest extends TestCase {
    //~ Instance fields ========================================================

    private View view;
    private ViewPanel viewPanel;

    //~ Methods ================================================================

    /**
     *
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ViewTest.class);
    }

    /**
     *
     */
    public void testexternaldummyNode() {
        Node n1 = view.newLeaf(view.getRoot());
        Node n2 = view.newLeaf(view.getRoot());
        Node n3 = view.newLeaf(n1);
        Node n4 = view.newLeaf(n2);
        Node n5 = view.newLeaf(n3);
        Edge e2 = view.newEdge(n5, n4);
        view.contract(n3);
        redraw();
        view.expand(n3);
    }

    /**
     *
     */
    public void testinternaldummyNode() {
        Node n1 = view.newLeaf(view.getRoot());
        Node n2 = view.newLeaf(n1);
        Node n3 = view.newLeaf(n1);
        Node n4 = view.newLeaf(n2);
        Node n5 = view.newLeaf(n2);
        Edge e1 = view.newEdge(n4, n3);
        Edge e2 = view.newEdge(n5, n3);
        view.contract(n2);
        redraw();
        view.expand(n2);
    }

    /**
     *
     */
    public void testnoedges() {
        Node n1 = view.newLeaf(view.getRoot());
        Node n2 = view.newLeaf(n1);
        Node n3 = view.newLeaf(n1);
        Node n4 = view.newLeaf(n1);
        Node n5 = view.newLeaf(n1);
        Edge e1 = view.newEdge(n2, n3);
        Edge e2 = view.newEdge(n2, n4);
        Edge e3 = view.newEdge(n3, n5);
        view.contract(n1);
        redraw();
        view.expand(n1);
    }

    /**
     *
     */
    public void testoldedgeremains1() {
        Node n1 = view.newLeaf(view.getRoot());
        Node n2 = view.newLeaf(view.getRoot());
        Node n3 = view.newLeaf(n1);
        Edge e1 = view.newEdge(n3, n2);
        Edge e2 = view.newEdge(n1, n2);
        view.contract(n1);
        redraw();
        view.expand(n1);
    }

    /**
     *
     */
    public void testupedge() {
        Node n1 = view.newLeaf(view.getRoot());
        Node n2 = view.newLeaf(view.getRoot());
        Node n3 = view.newLeaf(view.getRoot());
        Node n4 = view.newLeaf(n1);
        Node n5 = view.newLeaf(n2);
        Node n6 = view.newLeaf(n3);
        Edge e1 = view.newEdge(n4, n5);
        Edge e2 = view.newEdge(n5, n6);
        Edge e3 = view.newEdge(n6, n4);
        view.contract(n1);
        view.contract(n3);
        redraw();
        view.expand(n1);
        view.expand(n3);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        viewPanel = Visnacom.main_test();
        view = viewPanel.getView();
    }

    /**
     *
     */
    private void redraw() {
        viewPanel.getPrefs().algorithm = "sugi";
        viewPanel.getGeometry().setDrawingStyle(new SugiyamaDrawingStyle(viewPanel.getGeometry()));
        viewPanel.getPrefs().curveType = "polyline";
        viewPanel.redraw();
    }
}
