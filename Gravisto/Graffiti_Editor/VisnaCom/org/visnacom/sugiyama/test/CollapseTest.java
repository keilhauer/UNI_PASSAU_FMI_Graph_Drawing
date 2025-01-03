/*==============================================================================
*
*   CollapseTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: CollapseTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.LinkedList;

import org.visnacom.model.*;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.eval.DummyPicture;

import junit.framework.TestCase;

/**
 *
 */
public class CollapseTest extends TestCase {
    //~ Methods ================================================================

    /**
     *
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(CollapseTest.class);
    }

    /**
     *
     */
    public void testcollapseLayout2() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2x = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(n3);
        Node n5 = c.newLeaf(n3);
        Node n6 = c.newLeaf(n3);
        Node n7 = c.newLeaf(n6);
        Node n8 = c.newLeaf(n6);
        Node n9 = c.newLeaf(c.getRoot());
        Node n10 = c.newLeaf(c.getRoot());
        c.newEdge(n1, n3);
        c.newEdge(n7, n8);

        Edge d1 = c.newEdge(n1, n2x);
        Edge d2 = c.newEdge(n2x, n10);
        c.newEdge(n9, n2x);
        c.newEdge(n4, n6);

        SugiyamaDrawingStyle sds =
            new SugiyamaDrawingStyle(c, SugiyamaDrawingStyle.FINAL_STYLE);
        sds.drawImpl();
        DummyPicture.show(sds.s);

        //sds.write(getName());
        Node n11 = c.newLeaf(n2x);
        Node n12 = c.newLeaf(n2x);
        Node n13 = c.newLeaf(n2x);
        Node n14 = c.newLeaf(n2x);

        Edge e1 = c.newEdge(n1, n12);
        Edge e2 = c.newEdge(n1, n11);
        Edge e3 = c.newEdge(n11, n14);
        Edge e4 = c.newEdge(n14, n13);
        Edge e5 = c.newEdge(n14, n12);
        Edge e6 = c.newEdge(n13, n10);

        c.deleteEdge(d2);

        ActionExpand action = new ActionExpand(n2x);
        action.contDerEdges = new LinkedList();
        action.contDerEdges.add(e3);
        action.contDerEdges.add(e4);
        action.contDerEdges.add(e5);

        LinkedList l = new LinkedList();
        l.add(e1);
        l.add(e2);
        action.addMapping(d1, l, false);
        l = new LinkedList();
        l.add(e6);
        action.addMapping(d2, l, true);

        sds.expandImpl(c, action);
        DummyPicture.show(sds.s);
        //sds.write(getName()+"expanded");

        /*collapse */
        c.deleteEdge(e1);
        c.deleteEdge(e2);
        c.deleteEdge(e3);
        c.deleteEdge(e4);
        c.deleteEdge(e5);
        c.deleteEdge(e6);

        c.deleteLeaf(n11);
        c.deleteLeaf(n12);
        c.deleteLeaf(n13);
        c.deleteLeaf(n14);

        d2 = c.newEdge(n2x, n10);

        ActionContract actionC = new ActionContract(n2x);
        l = new LinkedList();
        l.add(e1);
        l.add(e2);
        actionC.addMapping(l,d1, false);
        l = new LinkedList();
        l.add(e6);
        actionC.addMapping(l, d2, true);
        sds.contractImpl(c, actionC);
        DummyPicture.show(sds.s);
    }

    /**
     *
     */
    public void testcollapseVerticalLayout1() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5x = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(c.getRoot());
        Node n6_1 = c.newLeaf(n6);
        Node n6_2 = c.newLeaf(n6);
        Node n6_3 = c.newLeaf(n6);
        Node n9 = c.newLeaf(c.getRoot());
        Node n10 = c.newLeaf(c.getRoot());
        Node n11 = c.newLeaf(c.getRoot());
        c.newEdge(n1, n3);

        Edge d2 = c.newEdge(n1, n5x);
        Edge d3 = c.newEdge(n2, n5x);
        c.newEdge(n2, n4);
        c.newEdge(n9, n10);
        c.newEdge(n10, n5x);
        c.newEdge(n10, n11);
        c.newEdge(n6_1, n6_3);

        c.newEdge(n3, n6);

        SugiyamaDrawingStyle sds =
            new SugiyamaDrawingStyle(c, SugiyamaDrawingStyle.FINAL_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);

        //sds.write(getName());

        /*expand*/
        Node n13 = c.newLeaf(n5x);
        Node n14 = c.newLeaf(n5x);
        Node n15 = c.newLeaf(n5x);
        Edge e1 = c.newEdge(n1, n15);
        Edge e2 = c.newEdge(n1, n14);
        Edge e3 = c.newEdge(n2, n14);
        Edge e4 = c.newEdge(n2, n13);

        Edge e5 = c.newEdge(n14, n15);

        c.deleteEdge(d3);

        ActionExpand action = new ActionExpand(n5x);
        action.contDerEdges = new LinkedList();
        action.contDerEdges.add(e5);

        LinkedList l = new LinkedList();
        l.add(e1);
        l.add(e2);
        action.addMapping(d2, l, false);
        l = new LinkedList();
        l.add(e3);
        l.add(e4);
        action.addMapping(d3, l, true);

        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
        //sds.write(getName()+"expanded");

        /* collapsing */
        c.deleteEdge(e1);
        c.deleteEdge(e2);
        c.deleteEdge(e3);
        c.deleteEdge(e4);
        c.deleteEdge(e5);
        d3 = c.newEdge(n2, n5x);

        c.deleteLeaf(n13);
        c.deleteLeaf(n14);
        c.deleteLeaf(n15);

        ActionContract actionC = new ActionContract(n5x);
        l = new LinkedList();
        l.add(e1);
        l.add(e2);
        actionC.addMapping(l,d2, false);
        l = new LinkedList();
        l.add(e3);
        l.add(e4);
        actionC.addMapping(l,d3, true);
        sds.contractImpl(c, actionC);
           DummyPicture.show(sds.s);
    }

    public void testsplitdummynodesonpath1() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(n1);
        Node n3 = c.newLeaf(n2);
        Node n4 = c.newLeaf(n3);
        Node v = c.newLeaf(n4);
        Node n6 = c.newLeaf(c.getRoot());
        Node n7 = c.newLeaf(n6);
        Node n8 = c.newLeaf(n7);
        Node n9 = c.newLeaf(n8);
        Node u = c.newLeaf(n9);
        Node n11 = c.newLeaf(c.getRoot());

        //edge under test
        Edge d1 = c.newEdge(v, u);

        c.newEdge(n1, n6);
        c.newEdge(n1, n11);
        c.newEdge(n11, n6);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        //sds.write(getName());
        
        /*expand */
        Node n12 = c.newLeaf(v);
        Node n13 = c.newLeaf(v);
        Edge e1 = c.newEdge(n12, u);
        Edge e2 = c.newEdge(n13, u);

        c.deleteEdge(d1);

        ActionExpand action = new ActionExpand(v);
        action.contDerEdges = new LinkedList();

        LinkedList l = new LinkedList();
        l.add(e1);
        l.add(e2);
        action.addMapping(d1, l, true);
        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
        //sds.write(getName()+"expanded");
        
        /*collapse*/
       c.deleteLeaf(n12);
       c.deleteLeaf(n13);
       
       d1 = c.newEdge(v,u);
       ActionContract actionC = new ActionContract(v);
       l = new LinkedList();
       l.add(e1);
       l.add(e2);
       actionC.addMapping(l,d1, true);
       sds.contractImpl(c, actionC);
          DummyPicture.show(sds.s);
    }
    public void testSmallWithAllUpStatic() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(n1);
        Node n4 = c.newLeaf(n2);
        Node n5 = c.newLeaf(n1);
        Node n6 = c.newLeaf(n1);

        c.newEdge(n5, n3);
        c.newEdge(n6, n5);

        Edge d1 = c.newEdge(n4, n3);
        Edge d2 = c.newEdge(n6, n3);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        /*expand*/
        c.deleteEdge(d1);
        c.deleteEdge(d2);

        Node n9 = c.newLeaf(n3);
        Node n10 = c.newLeaf(n3);
        Node n11 = c.newLeaf(n3);
        Node n12 = c.newLeaf(n3);
        Edge e1 = c.newEdge(n4, n10);
        Edge e2 = c.newEdge(n6, n10);
        Edge e7 = c.newEdge(n4, n9);
        Edge e8 = c.newEdge(n4, n11);
        Edge e9 = c.newEdge(n6, n9);
        Edge e10 = c.newEdge(n6, n12);

        Edge e3 = c.newEdge(n9, n10);
        Edge e4 = c.newEdge(n9, n11);
        Edge e5 = c.newEdge(n10, n12);
        Edge e6 = c.newEdge(n12, n9);
        LinkedList l = new LinkedList();
        l.add(e1);
        l.add(e7);
        l.add(e8);

        ActionExpand action = new ActionExpand(n3);
        action.contDerEdges = new LinkedList();
        action.contDerEdges.add(e3);
        action.contDerEdges.add(e4);
        action.contDerEdges.add(e5);
        action.contDerEdges.add(e6);
        action.addMapping(d1, l, true);
        l = new LinkedList();
        l.add(e2);
        l.add(e9);
        l.add(e10);

        action.addMapping(d2, l, true);
        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
        //sds.write(getName());
        /*collapse*/
        d1 = c.newEdge(n4, n3);
        d2 = c.newEdge(n6, n3);
        c.deleteLeaf(n9);
        c.deleteLeaf(n10);
        c.deleteLeaf(n11);
        c.deleteLeaf(n12);
        
        ActionContract actionC = new ActionContract(n3);
        l = new LinkedList();
        l.add(e1);
        l.add(e7);
        l.add(e8);
        actionC.addMapping(l,d1,true);
        l = new LinkedList();
        l.add(e2);
        l.add(e9);
        l.add(e10);
        actionC.addMapping(l,d2, true);
        sds.contractImpl(c, actionC);
           DummyPicture.show(sds.s);
    }
    
    public void testGrandChildren(){
        
    }
}
