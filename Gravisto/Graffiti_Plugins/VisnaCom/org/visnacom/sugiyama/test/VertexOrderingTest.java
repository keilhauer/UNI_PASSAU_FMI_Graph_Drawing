/*==============================================================================
*
*   VertexOrderingTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: VertexOrderingTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.Iterator;
import java.util.List;

import org.visnacom.model.Edge;
import org.visnacom.model.Node;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.algorithm.*;
import org.visnacom.sugiyama.eval.DummyPicture;
import org.visnacom.sugiyama.model.CompoundLevel;
import org.visnacom.sugiyama.model.SugiCompoundGraph;
import org.visnacom.sugiyama.model.SugiNode;

import junit.framework.TestCase;

/**
 *
 */
public class VertexOrderingTest extends TestCase {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(VertexOrderingTest.class);
    }

    /**
     *
     */
    public final void testInitialize() {
        TestGraph3 tg = new TestGraph3();
        SugiCompoundGraph s = tg.getTestGraph3withoutproxy();
        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        s.activateAllLHs(true);
        s.checkLHs(0);
    }

    /**
     *
     */
    public final void testOrderflatgraph() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        s.setDrawingStyle(SugiyamaDrawingStyle.DEBUG_STYLE);
        Node n1 = s.newLeaf(s.getRoot());
        Node n2 = s.newLeaf(s.getRoot());
        Node n3 = s.newLeaf(s.getRoot());
        Node n4 = s.newLeaf(s.getRoot());
        Node n5 = s.newLeaf(s.getRoot());
        Node n6 = s.newLeaf(s.getRoot());
        Node n7 = s.newLeaf(s.getRoot());
        Node n8 = s.newLeaf(s.getRoot());
        Node n9 = s.newLeaf(s.getRoot());
        Node n10 = s.newLeaf(s.getRoot());
        Node n11 = s.newLeaf(s.getRoot());
        Node n12 = s.newLeaf(s.getRoot());
        Node n13 = s.newLeaf(s.getRoot());
        Node n14 = s.newLeaf(s.getRoot());
        Node n15 = s.newLeaf(s.getRoot());
        Node n16 = s.newLeaf(s.getRoot());
        Node n17 = s.newLeaf(s.getRoot());
        Node n18 = s.newLeaf(s.getRoot());
        Node n19 = s.newLeaf(s.getRoot());
        Node n20 = s.newLeaf(s.getRoot());

        Edge e1 = s.newEdge(n1, n2);
        Edge e2 = s.newEdge(n2, n1);
        Edge e3 = s.newEdge(n2, n3);
        Edge e4 = s.newEdge(n3, n1);
        Edge e5 = s.newEdge(n2, n4);
        Edge e6 = s.newEdge(n3, n4);
        s.newEdge(n5, n14);


        Edge e7 = s.newEdge(n5, n4);
        s.newEdge(n7, n4);


        Edge e8 = s.newEdge(n4, n6);
        Edge e9 = s.newEdge(n5, n6);
        Edge e10 = s.newEdge(n6, n5);
        Edge e11 = s.newEdge(n5, n7);
        Edge e12 = s.newEdge(n8, n6);
        Edge e13 = s.newEdge(n8, n9);
        Edge e14 = s.newEdge(n10, n1);
        Edge e15 = s.newEdge(n11, n1);
        s.newEdge(n11, n3);
        s.newEdge(n11, n9);


        Edge e16 = s.newEdge(n11, n10);
        Edge e17 = s.newEdge(n11, n12);
        Edge e18 = s.newEdge(n11, n13);
        Edge e19 = s.newEdge(n11, n14);
        Edge e20 = s.newEdge(n11, n15);
        Edge e21 = s.newEdge(n12, n13);
        Edge e22 = s.newEdge(n13, n15);
        Edge e23 = s.newEdge(n15, n12);
        Edge e24 = s.newEdge(n14, n10);
        Edge e25 = s.newEdge(n10, n16);
        Edge e26 = s.newEdge(n10, n17);
        Edge e27 = s.newEdge(n10, n18);
        Edge e28 = s.newEdge(n16, n17);
        Edge e29 = s.newEdge(n16, n19);
        Edge e30 = s.newEdge(n17, n19);
        Edge e31 = s.newEdge(n18, n17);
        Edge e32 = s.newEdge(n19, n20);
        Edge e33 = s.newEdge(n20, n18);
        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        for(int i = 0; i <= 4; i++) {
            VertexOrdering.order(s, i);
            MetricLayout.layout(s);
             DummyPicture.show(s);
             //DummyPicture.write(s, getName()+i + "iterations", "png");
        }
    }

    /**
     *
     */
    public final void testOrdertestgraph1() {
        TestGraph1 tg = new TestGraph1();
        SugiCompoundGraph s = tg.getSugiTestGraph1();
        s.setDrawingStyle(SugiyamaDrawingStyle.DEBUG_STYLE);
        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        for(int i = 4; i <= 4; i++) {
            VertexOrdering.order(s, i);
            MetricLayout.layout(s, 4);
            DummyPicture.show(s);
            //DummyPicture.write(s, "testgraph1_" + i + "iterations");
        }
    }

    /**
     *
     */
    public final void testOrdertestgraph3() {
        TestGraph3 tg = new TestGraph3();
        SugiCompoundGraph s = tg.getTestGraph3withoutproxy();
        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, 1);
        MetricLayout.layout(s,4);
         DummyPicture.show(s);
    }

    /*
     * simplified version of testgraph1
     */
    public void testHorizontalEdges() {
        Node[] node = new Node[40];
        SugiCompoundGraph c = new SugiCompoundGraph();
       // node[0] = c.newLeaf(c.getRoot());
        node[1] = c.newLeaf(c.getRoot());
        node[2] = c.newLeaf(c.getRoot());
        node[3] = c.newLeaf(c.getRoot());
        node[4] = c.newLeaf(c.getRoot());
        node[5] = c.newLeaf(c.getRoot());
        node[6] = c.newLeaf(c.getRoot());
        node[7] = c.newLeaf(c.getRoot());
        node[8] = c.newLeaf(c.getRoot());
        node[9] = c.newLeaf(c.getRoot());
        node[10] = c.newLeaf(c.getRoot());
        node[11] = c.newLeaf(c.getRoot());
        node[12] = c.newLeaf(c.getRoot());
        node[13] = c.newLeaf(c.getRoot());
        node[14] = c.newLeaf(node[1]);
        node[15] = c.newLeaf(node[2]);
        node[16] = c.newLeaf(node[3]);
        node[17] = c.newLeaf(node[4]);
        node[18] = c.newLeaf(node[5]);
        node[19] = c.newLeaf(node[6]);
        node[20] = c.newLeaf(node[7]);
        node[21] = c.newLeaf(node[8]);
        node[22] = c.newLeaf(node[9]);
        node[23] = c.newLeaf(node[10]);
        node[24] = c.newLeaf(node[11]);
        node[25] = c.newLeaf(node[12]);
        node[26] = c.newLeaf(node[13]);

        c.newEdge(node[1 + 13], node[4 + 13]);
        c.newEdge(node[4 + 13], node[7 + 13]);
        c.newEdge(node[1 + 13], node[2 + 13]);
        c.newEdge(node[7 + 13], node[9 + 13]);
        c.newEdge(node[1 + 13], node[3 + 13]);
        //c.newEdge(node[7 + 13], node[8 + 13]);
        c.newEdge(node[5 + 13], node[6 + 13]);
        //c.newEdge(node[9 + 13], node[8 + 13]);
        //c.newEdge(node[7 + 13], node[10 + 13]);
        //c.newEdge(node[7 + 13], node[11 + 13]);
       // c.newEdge(node[7 + 13], node[12 + 13]);
        //c.newEdge(node[4 + 13], node[13 + 13]);
        //c.newEdge(node[9 + 13], node[13 + 13]);
        Hierarchization.hierarchize(c);
        Normalization.normalize(c);

        VertexOrdering.order(c, 10);

        MetricLayout.layout(c, 4);
        DummyPicture.show(c);
    }

    /*
     */
    public void testBild1() {
            Node[] node = new Node[40];
            SugiCompoundGraph c = new SugiCompoundGraph();
            node[1] = c.newLeaf(c.getRoot());
            node[2] = c.newLeaf(c.getRoot());
            node[4] = c.newLeaf(c.getRoot());
            node[3] = c.newLeaf(c.getRoot());
            node[5] = c.newLeaf(c.getRoot());
            node[6] = c.newLeaf(c.getRoot());
            node[7] = c.newLeaf(c.getRoot());
            node[9] = c.newLeaf(c.getRoot());
            node[14] = c.newLeaf(node[1]);
            node[15] = c.newLeaf(node[2]);
            node[16] = c.newLeaf(node[3]);
            node[17] = c.newLeaf(node[4]);
            node[18] = c.newLeaf(node[5]);
            node[19] = c.newLeaf(node[6]);
            node[20] = c.newLeaf(node[7]);
            node[22] = c.newLeaf(node[9]);

            c.newEdge(node[1 + 13], node[4 + 13]);
            c.newEdge(node[4 + 13], node[7 + 13]);
            c.newEdge(node[2 + 13], node[3 + 13]);
            c.newEdge(node[7 + 13], node[9 + 13]);
            //c.newEdge(node[1 + 13], node[3 + 13]);
            //c.newEdge(node[7 + 13], node[8 + 13]);
            c.newEdge(node[5 + 13], node[6 + 13]);
            //c.newEdge(node[9 + 13], node[8 + 13]);
            //c.newEdge(node[7 + 13], node[10 + 13]);
            //c.newEdge(node[7 + 13], node[11 + 13]);
           // c.newEdge(node[7 + 13], node[12 + 13]);
            //c.newEdge(node[4 + 13], node[13 + 13]);
            //c.newEdge(node[9 + 13], node[13 + 13]);
            Hierarchization.hierarchize(c);
            Normalization.normalize(c);

            VertexOrdering.order(c, 10);

            MetricLayout.layout(c, 4);
            DummyPicture.show(c);
           // DummyPicture.write(c, "verschraenkteTeilgraphen");
        }

    /**
     *
     */
    public void testImproveOrdering1() {
        System.out.print(getClass());
        System.out.println(getName());


        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode[] node = new SugiNode[28];
        for(int i = 0; i < node.length; i++) {
            node[i] = (SugiNode) s.newLeaf(s.getRoot());
        }


        CompoundLevel rootclev = CompoundLevel.getClevForRoot();
        ((SugiNode) s.getRoot()).setClev(rootclev);
        node[0].setClev(rootclev.getSubLevel(1));
        node[1].setClev(rootclev.getSubLevel(1));
        node[2].setClev(rootclev.getSubLevel(1));
        node[3].setClev(rootclev.getSubLevel(1));
        node[4].setClev(rootclev.getSubLevel(2));
        node[5].setClev(rootclev.getSubLevel(2));
        node[6].setClev(rootclev.getSubLevel(2));
        node[7].setClev(rootclev.getSubLevel(2));
        node[8].setClev(rootclev.getSubLevel(3));
        node[9].setClev(rootclev.getSubLevel(3));
        node[10].setClev(rootclev.getSubLevel(3));
        node[11].setClev(rootclev.getSubLevel(3));
        node[12].setClev(rootclev.getSubLevel(4));
        node[13].setClev(rootclev.getSubLevel(4));
        node[14].setClev(rootclev.getSubLevel(4));
        node[15].setClev(rootclev.getSubLevel(5));
        node[16].setClev(rootclev.getSubLevel(6));
        node[17].setClev(rootclev.getSubLevel(6));
        node[18].setClev(rootclev.getSubLevel(6));
        node[19].setClev(rootclev.getSubLevel(6));
        node[20].setClev(rootclev.getSubLevel(6));
        node[21].setClev(rootclev.getSubLevel(6));
        node[22].setClev(rootclev.getSubLevel(7));
        node[23].setClev(rootclev.getSubLevel(7));
        node[24].setClev(rootclev.getSubLevel(7));
        node[25].setClev(rootclev.getSubLevel(8));
        node[26].setClev(rootclev.getSubLevel(8));
        node[27].setClev(rootclev.getSubLevel(8));

        s.newEdge(node[0], node[4]);
        s.newEdge(node[0], node[4]);
        s.newEdge(node[1], node[13]);
        s.newEdge(node[2], node[12]);
        s.newEdge(node[3], node[5]);
        s.newEdge(node[3], node[6]);
        s.newEdge(node[3], node[14]);
        s.newEdge(node[3], node[7]);
        s.newEdge(node[4], node[8]);
        s.newEdge(node[5], node[9]);
        s.newEdge(node[6], node[11]);
        s.newEdge(node[7], node[10]);
        s.newEdge(node[8], node[12]);
        s.newEdge(node[9], node[12]);
        s.newEdge(node[13], node[15]);
        s.newEdge(node[15], node[18]);
        s.newEdge(node[15], node[19]);
        s.newEdge(node[15], node[20]);
        s.newEdge(node[15], node[21]);
        s.newEdge(node[15], node[26]);
        s.newEdge(node[15], node[27]);
        s.newEdge(node[15], node[27]);
        s.newEdge(node[16], node[22]);
        s.newEdge(node[16], node[23]);
        s.newEdge(node[17], node[23]);
        s.newEdge(node[18], node[24]);
        s.newEdge(node[18], node[26]);
        s.newEdge(node[19], node[24]);
        s.newEdge(node[20], node[24]);
        s.newEdge(node[21], node[24]);
        Normalization.normalize(s);

        VertexOrdering.order(s, 10);

        MetricLayout.layout(s, 4);
        DummyPicture.show(s);
        //DummyPicture.write(s,"improveOrdering1");
        System.out.print("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     * @param pa DOCUMENT ME!
     */
    private void printLHs(SugiCompoundGraph s, SugiNode pa) {
        System.out.println("LH of " + pa);
        System.out.println(pa.getLocalHierarchy());


        //recursive call
        List children = s.getChildren(pa);
        for(Iterator it = children.iterator(); it.hasNext();) {
            SugiNode child = (SugiNode) it.next();
            printLHs(s, child);
        }
    }
}
