// =============================================================================
//
//   LongAttributeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LongAttributeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LongAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class LongAttributeTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public LongAttributeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(LongAttributeTest.class);
    }

    /**
     * Tests if the comfortable methods correspond to the basic methods.
     */
    public void testAttributesAccessLong2() {
        Graph g = new AdjListGraph();
        long v = 10;
        g.getAttributes().getAttributable().setLong(
                "Hallo" + Attribute.SEPARATOR + "Test", v);

        long d = ((Long) (g.getAttributes().getAttribute("Hallo")
                .getAttributable().getAttribute(
                        "Hallo" + Attribute.SEPARATOR + "Test").getValue()))
                .longValue();
        assertEquals(v, d);
    }

    /**
     * Tests the copy method.
     */
    public void testCopy() {
        LongAttribute i = new LongAttribute("i");
        i.setValue(10);

        LongAttribute clone = (LongAttribute) (i.copy());
        assertEquals(((Long) (i.getValue())).intValue(), ((Long) (clone
                .getValue())).intValue());
    }

    /**
     * Tests the get and set methods.
     */
    public void testGetSetLong() {
        LongAttribute i = new LongAttribute("i");
        i.setLong(10);
        assertEquals(10, i.getLong());
    }

    /**
     * Tests the get value and set methods.
     */
    public void testGetSetValue() {
        LongAttribute i = new LongAttribute("i");
        i.setValue(10);
        assertEquals(10, ((Long) (i.getValue())).intValue());
    }

    /**
     * Tests the constructor that sets the value
     */
    public void testValueConstructor() {
        LongAttribute i = new LongAttribute("i", 7);
        assertEquals(7, i.getLong());
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
