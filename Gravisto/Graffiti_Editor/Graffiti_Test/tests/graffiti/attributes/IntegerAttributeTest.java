// =============================================================================
//
//   IntegerAttributeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: IntegerAttributeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class IntegerAttributeTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public IntegerAttributeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(IntegerAttributeTest.class);
    }

    /**
     * Tests if the comfortable methods correspond to the basic methods.
     */
    public void testAttributesAccessInteger2() {
        Graph g = new AdjListGraph();
        int v = 10;
        g.getAttributes().getAttributable().setInteger(
                "Hallo" + Attribute.SEPARATOR + "Test", v);

        int d = ((Integer) (g.getAttributes().getAttribute("Hallo")
                .getAttributable().getAttribute(
                        "Hallo" + Attribute.SEPARATOR + "Test").getValue()))
                .intValue();
        assertEquals(v, d);
    }

    /**
     * Tests the copy method.
     */
    public void testCopy() {
        IntegerAttribute i = new IntegerAttribute("i");
        i.setValue(new Integer(10));

        IntegerAttribute clone = (IntegerAttribute) (i.copy());
        assertEquals(((Integer) (i.getValue())).intValue(), ((Integer) (clone
                .getValue())).intValue());
    }

    /**
     * Tests the get and set methods.
     */
    public void testGetSetInteger() {
        IntegerAttribute i = new IntegerAttribute("i");
        i.setInteger(10);
        assertEquals(10, i.getInteger());
    }

    /**
     * Tests the get value and set methods.
     */
    public void testGetSetValue() {
        IntegerAttribute i = new IntegerAttribute("i");
        i.setValue(new Integer(10));
        assertEquals(10, ((Integer) (i.getValue())).intValue());
    }

    /**
     * Tests the constructor that sets the value
     */
    public void testValueConstructor() {
        IntegerAttribute i = new IntegerAttribute("i", 7);
        assertEquals(7, i.getInteger());
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
