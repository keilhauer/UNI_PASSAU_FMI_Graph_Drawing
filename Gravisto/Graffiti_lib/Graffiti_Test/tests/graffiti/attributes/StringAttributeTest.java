// =============================================================================
//
//   StringAttributeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StringAttributeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.IllegalIdException;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class StringAttributeTest extends TestCase {

    /** StringAttribute for the test cases */
    StringAttribute s = new StringAttribute("s");

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public StringAttributeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(StringAttributeTest.class);
    }

    /**
     * Tests if the comfortable methods correspond to the basic methods.
     */
    public void testAttributesAccessString2() {
        Graph g = new AdjListGraph();
        String v = "10";
        g.getAttributes().getAttributable().setString(
                "Hallo" + Attribute.SEPARATOR + "Test", v);

        String d = ((String) (g.getAttributes().getAttribute("Hallo")
                .getAttributable().getAttribute(
                        "Hallo" + Attribute.SEPARATOR + "Test").getValue()));
        assertEquals(v, d);
    }

    /**
     * Tests if the constructor rejects ids containing the separator char
     */
    public void testConstructor() {
        // wondering if the id is checked for correctness
        try {
            new StringAttribute("such.a.bad" + Attribute.SEPARATOR + "id");
            fail("An id must not contain the SEPARATOR char!");
        } catch (IllegalIdException e) {
        }
    }

    /**
     * Tests the copy method.
     */
    public void testCopy() {
        StringAttribute s = new StringAttribute("s");
        s.setValue("s");

        StringAttribute clone = (StringAttribute) (s.copy());
        assertEquals(s.getValue(), clone.getValue());
    }

    /**
     * Test the getString() and setString() methods.
     */
    public void testGetSetString() {
        s.setString("Test");
        assertEquals("Test", s.getString());
    }

    /**
     * Tests the getValue() and setValue() methods.
     */
    public void testGetSetValue() {
        s.setValue("test");
        assertEquals("test", s.getValue());
    }

    /**
     * Tests the constructor that sets the value.
     */
    public void testValueConstructor() {
        StringAttribute sa = new StringAttribute("sa", "test");
        assertEquals("test", sa.getString());
    }

    /**
     * Initializes a StringAttribute for all test cases
     */
    @Override
    protected void setUp() {
        s = new StringAttribute("s");
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
