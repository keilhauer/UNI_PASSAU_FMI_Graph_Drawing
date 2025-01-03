/*==============================================================================
*
*   TestGraphSugiyama.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: TestGraphSugiyama.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.model.CompoundGraph;
import org.visnacom.model.Node;

/**
 *
 */
public class TestGraphSugiyama {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public static void initialize(CompoundGraph c) {
        Node n0 =  c.getRoot();
        Node n1 =  c.newLeaf(n0);
        Node n2 =  c.newLeaf(n0);
        Node n3 =  c.newLeaf(n0);
        Node n4 =  c.newLeaf(n0);
        Node n5 =  c.newLeaf(n0);
        Node n6 =  c.newLeaf(n0);
        Node n7 =  c.newLeaf(n0);
        Node n8 =  c.newLeaf(n0);
        Node n9 =  c.newLeaf(n0);
        Node n10 =  c.newLeaf(n0);
        Node n11 =  c.newLeaf(n0);
        Node n12 =  c.newLeaf(n0);
        Node n13 =  c.newLeaf(n0);
        Node n14 =  c.newLeaf(n0);
        Node n15 =  c.newLeaf(n1);
        Node n16 =  c.newLeaf(n1);
        Node n17 =  c.newLeaf(n2);
        Node n18 =  c.newLeaf(n2);
        Node n19 =  c.newLeaf(n3);
        Node n20 =  c.newLeaf(n3);
        Node n21 =  c.newLeaf(n4);
        Node n22 =  c.newLeaf(n4);
        Node n23 =  c.newLeaf(n5);
        Node n24 =  c.newLeaf(n5);
        Node n25 =  c.newLeaf(n5);
        Node n26 =  c.newLeaf(n6);
        Node n27 =  c.newLeaf(n8);
        Node n28 =  c.newLeaf(n8);
        Node n29 =  c.newLeaf(n9);
        Node n30 =  c.newLeaf(n9);
        Node n31 =  c.newLeaf(n10);
        Node n32 =  c.newLeaf(n10);
        Node n33 =  c.newLeaf(n10);
        Node n34 =  c.newLeaf(n10);
        Node n35 =  c.newLeaf(n10);
        Node n36 =  c.newLeaf(n10);
        Node n37 =  c.newLeaf(n10);
        Node n38 =  c.newLeaf(n10);
        Node n39 =  c.newLeaf(n10);
        Node n40 =  c.newLeaf(n11);
        Node n41 =  c.newLeaf(n11);
        Node n42 =  c.newLeaf(n12);
        Node n43 =  c.newLeaf(n13);
        Node n45 =  c.newLeaf(n17);
        Node n44 =  c.newLeaf(n14);
        Node n46 =  c.newLeaf(n17);
        Node n47 =  c.newLeaf(n22);
        Node n48 =  c.newLeaf(n22);
        Node n49 =  c.newLeaf(n28);
        Node n50 =  c.newLeaf(n28);
        Node n51 =  c.newLeaf(n29);
        Node n52 =  c.newLeaf(n29);
        Node n53 =  c.newLeaf(n34);
        Node n54 =  c.newLeaf(n34);
        Node n55 =  c.newLeaf(n44);
        Node n56 =  c.newLeaf(n44);

        c.newEdge(n15, n16);
        c.newEdge(n15, n43);
        c.newEdge(n46, n18);
        c.newEdge(n46, n26);
        c.newEdge(n2, n5);
        c.newEdge(n19, n22);
        c.newEdge(n21, n48);
        c.newEdge(n22, n20);
        c.newEdge(n20, n7);
        c.newEdge(n47, n6);
        c.newEdge(n4, n14);
        c.newEdge(n23, n8);
        c.newEdge(n23, n24);
        c.newEdge(n26, n25);
        c.newEdge(n6, n9);
        c.newEdge(n5, n9);
        c.newEdge(n7, n10);
        c.newEdge(n27, n28);
        c.newEdge(n49, n52);
        c.newEdge(n51, n50);
        c.newEdge(n28, n30);
        c.newEdge(n29, n30);
        c.newEdge(n29, n12);
        c.newEdge(n31, n33);
        c.newEdge(n32, n33);
        c.newEdge(n33, n34);
        c.newEdge(n53, n54);
        c.newEdge(n34, n35);
        c.newEdge(n35, n36);
        c.newEdge(n36, n37);
        c.newEdge(n36, n38);
        c.newEdge(n36, n39);
        c.newEdge(n10, n44);
        c.newEdge(n40, n41);
        c.newEdge(n42, n40);
        c.newEdge(n12, n43);
        c.newEdge(n9, n13);
        c.newEdge(n43, n56);
        c.newEdge(n55, n56);
    }
}
