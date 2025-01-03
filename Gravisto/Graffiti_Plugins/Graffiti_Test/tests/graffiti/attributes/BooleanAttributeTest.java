// =============================================================================
//
//   BooleanAttributeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BooleanAttributeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class BooleanAttributeTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public BooleanAttributeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(BooleanAttributeTest.class);
    }

    /**
     * Tests if the comfortable methods correspond to the basic methods.
     */
    public void testAttributesAccessBoolean2() {
        Graph g = new AdjListGraph();
        boolean v = true;
        g.getAttributes().getAttributable().setBoolean(
                "Hallo" + Attribute.SEPARATOR + "Test", v);

        boolean d = ((Boolean) (g.getAttributes().getAttribute("Hallo")
                .getAttributable().getAttribute(
                        "Hallo" + Attribute.SEPARATOR + "Test").getValue()))
                .booleanValue();
        assertEquals(v, d);
    }

    /**
     * Tests the copy method.
     */
    public void testCopy() {
        BooleanAttribute b = new BooleanAttribute("b");
        b.setBoolean(true);

        BooleanAttribute clone = (BooleanAttribute) (b.copy());
        assertEquals(b.getBoolean(), clone.getBoolean());
    }

    /**
     * Test the getBoolean() and setBoolean() methods.
     */
    public void testGetSetBoolean() {
        BooleanAttribute b = new BooleanAttribute("b");
        b.setBoolean(true);
        assertEquals(true, b.getBoolean());
    }

    /**
     * Test the getValue() and setValue() methods.
     */
    public void testGetSetValue() {
        BooleanAttribute b = new BooleanAttribute("b");
        b.setValue(new Boolean(true));
        assertEquals(true, ((Boolean) b.getValue()).booleanValue());
    }

    /**
     * Tests the constructor that sets the value
     */
    public void testValueConstructor() {
        BooleanAttribute b = new BooleanAttribute("b", true);
        assertEquals(true, b.getBoolean());
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
