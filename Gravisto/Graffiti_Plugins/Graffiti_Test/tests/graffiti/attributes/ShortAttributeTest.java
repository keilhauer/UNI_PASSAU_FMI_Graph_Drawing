// =============================================================================
//
//   ShortAttributeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ShortAttributeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.ShortAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class ShortAttributeTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public ShortAttributeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ShortAttributeTest.class);
    }

    /**
     * Tests if the comfortable methods correspond to the basic methods.
     */
    public void testAttributesAccessShort2() {
        Graph g = new AdjListGraph();
        short v = 10;
        g.getAttributes().getAttributable().setShort(
                "Hallo" + Attribute.SEPARATOR + "Test", v);

        short d = ((Short) (g.getAttributes().getAttribute("Hallo")
                .getAttributable().getAttribute(
                        "Hallo" + Attribute.SEPARATOR + "Test").getValue()))
                .shortValue();
        assertEquals(v, d);
    }

    /**
     * Tests the copy method.
     */
    public void testCopy() {
        ShortAttribute i = new ShortAttribute("i");
        i.setValue(new Short((short) 10));

        ShortAttribute clone = (ShortAttribute) (i.copy());
        assertEquals(((Short) (i.getValue())).intValue(), ((Short) (clone
                .getValue())).intValue());
    }

    /**
     * Tests the get and set methods.
     */
    public void testGetSetShort() {
        ShortAttribute i = new ShortAttribute("i");
        i.setShort((short) 10);
        assertEquals(10, i.getShort());
    }

    /**
     * Tests the get value and set methods.
     */
    public void testGetSetValue() {
        ShortAttribute i = new ShortAttribute("i");
        i.setValue(new Short((short) 10));
        assertEquals(10, ((Short) (i.getValue())).intValue());
    }

    /**
     * Tests the constructor that sets the value
     */
    public void testValueConstructor() {
        ShortAttribute i = new ShortAttribute("i", (short) 7);
        assertEquals(7, i.getShort());
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
