/*==============================================================================
 *
 *   ExpandTest.java
 *
 *   @author Michael Proepster
 *
 *==============================================================================
 * $Id: ExpandTest.java 907 2005-10-19 12:03:31Z raitner $
 */

package org.visnacom.sugiyama.test;

import java.util.LinkedList;

import org.visnacom.model.*;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.eval.DummyPicture;

import junit.framework.TestCase;

/**
 * some tests for expand without using the editor. the action objects are
 * created manually and also the changes on the graph.
 */
public class ExpandTest extends TestCase {
    //~ Instance fields
    // ========================================================

    //~ Methods
    // ================================================================

    public void testupdateVerticalLayout1() {
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
        // Node n12 = c.newLeaf(c.getRoot());
        Edge d1 = c.newEdge(n1, n3);
        Edge d2 = c.newEdge(n1, n5x);
        Edge d3 = c.newEdge(n2, n5x);
        Edge d7 = c.newEdge(n2, n4);
        c.newEdge(n9, n10);
        c.newEdge(n10, n5x);
        c.newEdge(n10, n11);
        //c.newEdge(n11,n12);
        c.newEdge(n6_1, n6_3);

        Edge d4 = c.newEdge(n3, n6);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        //sds.write(getName());
        //die kante (1,5) und (5,8) bleibt
        Node n13 = c.newLeaf(n5x);
        Node n14 = c.newLeaf(n5x);
        Node n15 = c.newLeaf(n5x);
        Edge e1 = c.newEdge(n1, n15);
        Edge e2 = c.newEdge(n1, n14);
        Edge e3 = c.newEdge(n2, n14);
        Edge e4 = c.newEdge(n2, n13);
        Edge e5 = c.newEdge(n14, n15);
        //c.deleteEdge(d2); Remains
        c.deleteEdge(d3);
        //c.deleteEdge(d6); Remains
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

    }

    public void testupdateLayout2() {
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
        c.newEdge(n7,n8);
        Edge d1 = c.newEdge(n1, n2x);
        Edge d2 = c.newEdge(n2x, n10);
        c.newEdge(n9, n2x);
        c.newEdge(n4, n6);

        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
       // sds.write(getName());
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

    }
    public void testexternaldummynodepath() {
        CompoundGraph s = new Static();
        Node n1 = s.newLeaf(s.getRoot());
        Node n2 = s.newLeaf(s.getRoot());
        Node n3 = s.newLeaf(s.getRoot());
        Node n4 = s.newLeaf(n2);
        Node n5 = s.newLeaf(n3);
        Node n6 = s.newLeaf(n4);
        Node n7 = s.newLeaf(n5);
        Node n8 = s.newLeaf(n6);
        Node n9 = s.newLeaf(n7);
        Node n10 = s.newLeaf(n8);
        Node n11 = s.newLeaf(n9);
        Node n12 = s.newLeaf(n10);
        Node n13 = s.newLeaf(n11);

        Node n14 = s.newLeaf(n10);
        Node n15 = s.newLeaf(n10);
        Node n16 = s.newLeaf(n10);
        Node n17 = s.newLeaf(n10);

        //edge under test
        Edge d1 = s.newEdge(n12, n13);

        s.newEdge(n1, n2);
        s.newEdge(n1, n3);
        s.newEdge(n12, n14);
        s.newEdge(n14, n15);
        s.newEdge(n15, n16);
        s.newEdge(n16, n17);
        s.newEdge(n17, n13);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(s,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        //      sds.write(getName());
        Node n18 = s.newLeaf(n12);
        Node n19 = s.newLeaf(n12);
        Edge e1 = s.newEdge(n18, n13);
        Edge e2 = s.newEdge(n19, n13);
        s.deleteEdge(d1);
        ActionExpand action = new ActionExpand(n12);
        action.contDerEdges = new LinkedList();

        LinkedList l = new LinkedList();
        l.add(e1);
        l.add(e2);
        action.addMapping(d1, l, true);
        sds.expandImpl(s, action);
           DummyPicture.show(sds.s);
        //sds.write(getName()+"expanded");
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
        //sds.write(getName(),"png");
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
    }

    public void testorderinternaloldRemains() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5x = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(c.getRoot());
        Node n7 = c.newLeaf(c.getRoot());
        Node n8 = c.newLeaf(c.getRoot());
        Node n9 = c.newLeaf(c.getRoot());
        Node n10 = c.newLeaf(c.getRoot());
        Node n11 = c.newLeaf(c.getRoot());
        Node n12 = c.newLeaf(c.getRoot());
        Edge d1 = c.newEdge(n1, n3);
        Edge d2 = c.newEdge(n1, n5x);
        Edge d3 = c.newEdge(n2, n5x);
        Edge d7 = c.newEdge(n2, n4);
        c.newEdge(n9, n10);
        c.newEdge(n10, n5x);
        c.newEdge(n10, n11);
        c.newEdge(n11, n12);
        c.newEdge(n12, n7);
        c.newEdge(n12, n8);

        Edge d4 = c.newEdge(n3, n6);
        Edge d5 = c.newEdge(n5x, n7);
        Edge d6 = c.newEdge(n5x, n8);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        //sds.write(getName());
        //die kante (1,5) und (5,8) bleibt
        Node n13 = c.newLeaf(n5x);
        Node n14 = c.newLeaf(n5x);
        Node n15 = c.newLeaf(n5x);
        Edge e1 = c.newEdge(n1, n15);
        Edge e2 = c.newEdge(n1, n14);
        Edge e3 = c.newEdge(n2, n14);
        Edge e4 = c.newEdge(n2, n13);
        Edge e5 = c.newEdge(n14, n15);
        Edge e6 = c.newEdge(n13, n7);
        Edge e7 = c.newEdge(n15, n8);
        //c.deleteEdge(d2); Remains
        c.deleteEdge(d3);
        c.deleteEdge(d5);
        //c.deleteEdge(d6); Remains
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
        l = new LinkedList();
        l.add(e6);
        action.addMapping(d5, l, true);
        l = new LinkedList();
        l.add(e7);
        action.addMapping(d6, l, false);

        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
        //sds.write(getName()+"expanded");
    }

    public void testorderinternal() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5x = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(c.getRoot());
        Node n7 = c.newLeaf(c.getRoot());
        Node n8 = c.newLeaf(c.getRoot());
        Edge d1 = c.newEdge(n1, n3);
        Edge d2 = c.newEdge(n1, n5x);
        Edge d3 = c.newEdge(n2, n5x);
        Edge d7 = c.newEdge(n2, n4);

        Edge d4 = c.newEdge(n3, n6);
        Edge d5 = c.newEdge(n5x, n7);
        Edge d6 = c.newEdge(n5x, n8);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        //sds.write(getName());

        Node n9 = c.newLeaf(n5x);
        Node n10 = c.newLeaf(n5x);
        Node n11 = c.newLeaf(n5x);
        Edge e1 = c.newEdge(n1, n11);
        Edge e2 = c.newEdge(n1, n10);
        Edge e3 = c.newEdge(n2, n10);
        Edge e4 = c.newEdge(n2, n9);
        Edge e5 = c.newEdge(n10, n11);
        Edge e6 = c.newEdge(n9, n7);
        Edge e7 = c.newEdge(n11, n8);
        c.deleteEdge(d2);
        c.deleteEdge(d3);
        c.deleteEdge(d5);
        c.deleteEdge(d6);
        ActionExpand action = new ActionExpand(n5x);
        action.contDerEdges = new LinkedList();
        action.contDerEdges.add(e5);

        LinkedList l = new LinkedList();
        l.add(e1);
        l.add(e2);
        action.addMapping(d2, l, true);
        l = new LinkedList();
        l.add(e3);
        l.add(e4);
        action.addMapping(d3, l, true);
        l = new LinkedList();
        l.add(e6);
        action.addMapping(d5, l, true);
        l = new LinkedList();
        l.add(e7);
        action.addMapping(d6, l, true);

        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
        //sds.write(getName()+"expanded");
    }

    public void testorderexternal() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(n1);
        Node n5 = c.newLeaf(n1);
        Node n6 = c.newLeaf(n1);
        Node n7 = c.newLeaf(n2);
        Node n8 = c.newLeaf(n2);
        Node n9 = c.newLeaf(n2);
        Node n10x = c.newLeaf(n2);
        Node n11 = c.newLeaf(n2);
        Node n12 = c.newLeaf(n2);
        Node n13 = c.newLeaf(n3);
        Node n14 = c.newLeaf(n3);
        Node n15 = c.newLeaf(n3);
        Edge d1 = c.newEdge(n4, n10x);
        Edge d3 = c.newEdge(n13, n10x);
        Edge d4 = c.newEdge(n10x, n6);
        Edge d5 = c.newEdge(n10x, n15);

        c.newEdge(n7, n10x);
        c.newEdge(n4, n5);
        c.newEdge(n5, n6);
        c.newEdge(n9, n5);
        c.newEdge(n9, n6);
        c.newEdge(n8, n11);
        c.newEdge(n8, n12);
        c.newEdge(n7, n9);
        c.newEdge(n12, n14);
        c.newEdge(n12, n15);
        c.newEdge(n13, n14);
        c.newEdge(n14, n15);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        //sds.write(getName(),"png");

        Node n16 = c.newLeaf(n10x);
        Node n17 = c.newLeaf(n10x);
        Node n18 = c.newLeaf(n10x);
        Node n19 = c.newLeaf(n10x);
        //interne kanten
        Edge e1 = c.newEdge(n16, n19);
        Edge e2 = c.newEdge(n17, n18);

        //(4,10)
        Edge e3 = c.newEdge(n4, n16);
        //(13,10)
        Edge e4 = c.newEdge(n13, n17);
        Edge e5 = c.newEdge(n13, n18);
        //(10,6)
        Edge e6 = c.newEdge(n16, n6);
        Edge e7 = c.newEdge(n17, n6);
        //(10,15)
        Edge e8 = c.newEdge(n17, n15);
        Edge e9 = c.newEdge(n19, n15);

        //delete (10,6) and (13,10)
        c.deleteEdge(d3);
        c.deleteEdge(d4);
        ActionExpand action = new ActionExpand(n10x);
        action.contDerEdges = new LinkedList();
        action.contDerEdges.add(e1);
        action.contDerEdges.add(e2);

        LinkedList l = new LinkedList();
        l.add(e3);
        action.addMapping(d1, l, false);
        l = new LinkedList();
        l.add(e4);
        l.add(e5);
        action.addMapping(d3, l, true);
        l = new LinkedList();
        l.add(e6);
        l.add(e7);
        action.addMapping(d4, l, true);
        l = new LinkedList();
        l.add(e8);
        l.add(e9);
        action.addMapping(d5, l, false);

        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
        //sds.write(getName()+"expanded");
    }

    public void testinternaldummyNodeStatic() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(n1);
        Node n3 = c.newLeaf(n1);
        Edge d1 = c.newEdge(n2, n3);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        //sds.write(getName());
        Node n4 = c.newLeaf(n2);
        Node n5 = c.newLeaf(n2);
        Edge e1 = c.newEdge(n4, n3);
        Edge e2 = c.newEdge(n5, n3);
        c.deleteEdge(d1);

        ActionExpand action = new ActionExpand(n2);
        action.contDerEdges = new LinkedList();

        LinkedList l = new LinkedList();
        l.add(e1);
        l.add(e2);
        action.addMapping(d1, l, true);
        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
        //sds.write(getName()+"expanded");
    }

    public void testexternaldummyNodeStatic() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(n1);
        Node n4 = c.newLeaf(n2);
        Edge d1 = c.newEdge(n3, n4);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);

        Node n5 = c.newLeaf(n3);
        c.deleteEdge(d1);

        Edge e1 = c.newEdge(n5, n4);
        LinkedList l = new LinkedList();
        l.add(e1);

        ActionExpand action = new ActionExpand(n3);
        action.contDerEdges = new LinkedList();
        action.addMapping(d1, l, true);

        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
        //        sds.write(getName());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ExpandTest.class);
    }

    /**
     *  
     */
    public void testEmptyGraphStatic() {
        CompoundGraph c = new Static();
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);

        ActionExpand action = new ActionExpand(c.getRoot());
        action.contDerEdges = new LinkedList();
        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
    }

    public void testexpandRoot() {
        CompoundGraph c = new Static();
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        c.newLeaf(c.getRoot());
        c.newLeaf(c.getRoot());
        ActionExpand action = new ActionExpand(c.getRoot());
        action.contDerEdges = new LinkedList();
        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
    }

    /**
     *  
     */
    public void testSmallWithAllStatic() {
        CompoundGraph s = new Static();
        Node n1 = s.newLeaf(s.getRoot());
        Node n2 = s.newLeaf(s.getRoot());
        Node n3 = s.newLeaf(n1);
        Node n4 = s.newLeaf(n2);
        Node n5 = s.newLeaf(n1);
        Node n6 = s.newLeaf(n1);
        Node n7 = s.newLeaf(n2);
        Node n8 = s.newLeaf(n2);
        s.newEdge(n7, n8);
        s.newEdge(n8, n4);
        s.newEdge(n3, n5);
        s.newEdge(n5, n6);

        Edge d1 = s.newEdge(n3, n4);
        Edge d2 = s.newEdge(n3, n6);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(s,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);

        s.deleteEdge(d1);
        s.deleteEdge(d2);

        Node n9 = s.newLeaf(n3);
        Node n10 = s.newLeaf(n3);
        Node n11 = s.newLeaf(n3);
        Node n12 = s.newLeaf(n3);
        Edge e1 = s.newEdge(n10, n4);
        Edge e2 = s.newEdge(n10, n6);
        Edge e7 = s.newEdge(n9, n4);
        Edge e8 = s.newEdge(n11, n4);
        Edge e9 = s.newEdge(n9, n6);
        Edge e10 = s.newEdge(n12, n6);

        Edge e3 = s.newEdge(n9, n10);
        Edge e4 = s.newEdge(n9, n11);
        Edge e5 = s.newEdge(n10, n12);
        Edge e6 = s.newEdge(n12, n9);
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
        sds.expandImpl(s, action);
           DummyPicture.show(sds.s);
       // DummyPicture.write(sds.s, getName(),"png");
    }

    /**
     *  
     */
    public void testSmallWithAllUpStatic() {
        CompoundGraph s = new Static();
        Node n1 = s.newLeaf(s.getRoot());
        Node n2 = s.newLeaf(s.getRoot());
        Node n3 = s.newLeaf(n1);
        Node n4 = s.newLeaf(n2);
        Node n5 = s.newLeaf(n1);
        Node n6 = s.newLeaf(n1);

        //Node n7 = s.newLeaf(n2);
        //Node n8 = s.newLeaf(n2);
        //s.newEdge(n7, n8);
        //s.newEdge(n8, n4);
        s.newEdge(n5, n3);
        s.newEdge(n6, n5);

        Edge d1 = s.newEdge(n4, n3);
        Edge d2 = s.newEdge(n6, n3);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(s,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);

        s.deleteEdge(d1);
        s.deleteEdge(d2);

        Node n9 = s.newLeaf(n3);
        Node n10 = s.newLeaf(n3);
        Node n11 = s.newLeaf(n3);
        Node n12 = s.newLeaf(n3);
        Edge e1 = s.newEdge(n4, n10);
        Edge e2 = s.newEdge(n6, n10);
        Edge e7 = s.newEdge(n4, n9);
        Edge e8 = s.newEdge(n4, n11);
        Edge e9 = s.newEdge(n6, n9);
        Edge e10 = s.newEdge(n6, n12);

        Edge e3 = s.newEdge(n9, n10);
        Edge e4 = s.newEdge(n9, n11);
        Edge e5 = s.newEdge(n10, n12);
        Edge e6 = s.newEdge(n12, n9);
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
        sds.expandImpl(s, action);
           DummyPicture.show(sds.s);
        //sds.write(getName());
    }

    /**
     *  
     */
    public void testexternaldummyNodeUpStatic() {
        CompoundGraph c = new Static();
        Node n2 = c.newLeaf(c.getRoot());
        Node n1 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(n1);
        Node n4 = c.newLeaf(n2);
        Edge d1 = c.newEdge(n4, n3);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);
        //sds.write(getName());
        Node n5 = c.newLeaf(n3);
        c.deleteEdge(d1);

        Edge e1 = c.newEdge(n4, n5);
        LinkedList l = new LinkedList();
        l.add(e1);

        ActionExpand action = new ActionExpand(n3);
        action.contDerEdges = new LinkedList();
        action.addMapping(d1, l, true);

        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
        //sds.write(getName()+"expanded");
        /*
         * der 6er ist rechts, weil der 3er vorher lambda=1 hatte. wegen user's
         * mental map veraendere ich den wert nichtd.h. ich bewege ihn nicht.
         * damit kann es passieren, dass der 6er rechts von ihm hinkommt. das
         * problem gibt es aber auch schon beim static layout
         *  
         */
    }

    /**
     *  
     */
    public void testlocalandexternalStatic() {
        CompoundGraph s = new Static();
        Node n1 = s.newLeaf(s.getRoot());
        Node n2 = s.newLeaf(s.getRoot());
        Node n3 = s.newLeaf(n1);
        Node n4 = s.newLeaf(n2);
        Node n5 = s.newLeaf(n1);
        Node n6 = s.newLeaf(n1);
        Node n7 = s.newLeaf(n2);
        Node n8 = s.newLeaf(n2);
        s.newEdge(n7, n8);
        s.newEdge(n8, n4);
        s.newEdge(n3, n5);
        s.newEdge(n5, n6);

        Edge d1 = s.newEdge(n3, n4);
        Edge d2 = s.newEdge(n3, n6);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(s,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);

        s.deleteEdge(d1);
        s.deleteEdge(d2);

        Node n10 = s.newLeaf(n3);
        Edge e1 = s.newEdge(n10, n4);
        Edge e2 = s.newEdge(n10, n6);
        LinkedList l = new LinkedList();
        l.add(e1);

        ActionExpand action = new ActionExpand(n3);
        action.contDerEdges = new LinkedList();
        action.addMapping(d1, l, true);
        l = new LinkedList();
        l.add(e2);
        action.addMapping(d2, l, true);
        sds.expandImpl(s, action);
           DummyPicture.show(sds.s);
        //        sds.write(getName());
    }

    /**
     * same as testnoedges, but without using franz' code at all.
     */
    public void testnoedgesStatic() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
                SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
           DummyPicture.show(sds.s);

        Node n2 = c.newLeaf(n1);
        Node n3 = c.newLeaf(n1);
        Node n4 = c.newLeaf(n1);
        Node n5 = c.newLeaf(n1);
        Edge e1 = c.newEdge(n2, n3);
        Edge e2 = c.newEdge(n2, n4);
        Edge e3 = c.newEdge(n3, n5);
        Edge e4 = c.newEdge(n5, n2);
        Edge e5 = c.newEdge(n4, n2);
        ActionExpand action = new ActionExpand(n1);
        action.contDerEdges = new LinkedList();
        action.contDerEdges.add(e1);
        action.contDerEdges.add(e2);
        action.contDerEdges.add(e3);
        action.contDerEdges.add(e4);
        action.contDerEdges.add(e5);
        sds.expandImpl(c, action);
           DummyPicture.show(sds.s);
    }

}