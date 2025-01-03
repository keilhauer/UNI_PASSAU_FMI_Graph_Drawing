// =============================================================================
//
//   ByteAttributeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ByteAttributeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.ByteAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class ByteAttributeTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public ByteAttributeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ByteAttributeTest.class);
    }

    /**
     * Tests if the comfortable methods correspond to the basic methods.
     */
    public void testAttributesAccessByte2() {
        Graph g = new AdjListGraph();
        byte v = 10;
        g.getAttributes().getAttributable().setByte(
                "Hallo" + Attribute.SEPARATOR + "Test", v);

        byte d = ((Byte) (g.getAttributes().getAttribute("Hallo")
                .getAttributable().getAttribute(
                        "Hallo" + Attribute.SEPARATOR + "Test").getValue()))
                .byteValue();
        assertEquals(v, d);
    }

    /**
     * Tests the copy method.
     */
    public void testCopy() {
        ByteAttribute i = new ByteAttribute("i");
        i.setValue(new Byte((byte) 10));

        ByteAttribute clone = (ByteAttribute) (i.copy());
        assertEquals(((Byte) (i.getValue())).intValue(), ((Byte) (clone
                .getValue())).intValue());
    }

    /**
     * Tests the get and set methods.
     */
    public void testGetSetByte() {
        ByteAttribute i = new ByteAttribute("i");
        i.setByte((byte) 10);
        assertEquals(10, i.getByte());
    }

    /**
     * Tests the get value and set methods.
     */
    public void testGetSetValue() {
        ByteAttribute i = new ByteAttribute("i");
        i.setValue(new Byte((byte) 10));
        assertEquals(10, ((Byte) (i.getValue())).intValue());
    }

    /**
     * Tests the constructor that sets the value
     */
    public void testValueConstructor() {
        ByteAttribute i = new ByteAttribute("i", (byte) 7);
        assertEquals(7, i.getByte());
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
