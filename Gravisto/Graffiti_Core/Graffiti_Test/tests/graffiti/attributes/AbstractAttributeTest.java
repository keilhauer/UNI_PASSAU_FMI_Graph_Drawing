// =============================================================================
//
//   AbstractAttributeTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractAttributeTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.attributes;

import junit.framework.TestCase;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FloatAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IllegalIdException;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.AdjListGraph;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5771 $ $Date: 2006-01-10 12:25:10 +0100 (Di, 10 Jan 2006)
 *          $
 */
public class AbstractAttributeTest extends TestCase {

    /** DOCUMENT ME! */
    private BooleanAttribute round;

    /** DOCUMENT ME! */
    private CollectionAttribute coords;

    /** DOCUMENT ME! */
    private CollectionAttribute graphix;

    /** DOCUMENT ME! */
    private CollectionAttribute h;

    /** DOCUMENT ME! */
    private CollectionAttribute shape;

    /** DOCUMENT ME! */
    private IntegerAttribute x;

    /** DOCUMENT ME! */
    private StringAttribute graphixdesc;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public AbstractAttributeTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AbstractAttributeTest.class);
    }

    /**
     * Tests if the constructor rejects ids containing the separator char
     */
    public void testConstructor() {
        // wondering if the id is checked for correctness
        try {
            new AbstractAttributeImpl("such.a.bad" + Attribute.SEPARATOR + "id");
            fail("An id must not contain the SEPARATOR char!");
        } catch (IllegalIdException e) {
        }
    }

    /**
     * Tests the getAttributable() method.
     */
    public void testGetAttributable() {
        AdjListGraph g = new AdjListGraph();
        g.addAttribute(h, "");
        assertEquals(g, h.getAttributable());
        assertEquals(g, graphix.getAttributable());
        assertEquals(g, coords.getAttributable());
        assertEquals(g, shape.getAttributable());
        assertEquals(g, graphixdesc.getAttributable());
        assertEquals(g, x.getAttributable());
        assertEquals(g, round.getAttributable());
    }

    /**
     * Tests the get id method.
     */
    public void testGetId() {
        IntegerAttribute i = new IntegerAttribute("i");
        assertEquals("i", i.getId());

        FloatAttribute fi = new FloatAttribute("i");
        assertEquals("i", fi.getId());

        DoubleAttribute di = new DoubleAttribute("i");
        assertEquals("i", di.getId());

        BooleanAttribute bi = new BooleanAttribute("i");
        assertEquals("i", bi.getId());

        StringAttribute si = new StringAttribute("i");
        assertEquals("i", si.getId());

        HashMapAttribute hmi = new HashMapAttribute("i");
        assertEquals("i", hmi.getId());
    }

    /**
     * Tests the getParent() method.
     */
    public void testGetParent() {
        assertEquals("coords", x.getParent().getId());
        assertEquals("graphix", coords.getParent().getId());
        assertEquals("graphix", shape.getParent().getId());
        assertEquals("graphix", graphixdesc.getParent().getId());
        assertEquals("shape", round.getParent().getId());
        assertEquals("h", graphix.getParent().getId());
        assertEquals(null, h.getParent());
    }

    /**
     * tests the getPath() method.
     */
    public void testGetPath() {
        assertEquals("h", h.getPath());
        assertEquals("h" + Attribute.SEPARATOR + "graphix", graphix.getPath());
        assertEquals("h" + Attribute.SEPARATOR + "graphix"
                + Attribute.SEPARATOR + "coords", coords.getPath());
        assertEquals("h" + Attribute.SEPARATOR + "graphix"
                + Attribute.SEPARATOR + "shape", shape.getPath());
        assertEquals("h" + Attribute.SEPARATOR + "graphix"
                + Attribute.SEPARATOR + "gr properties", graphixdesc.getPath());
        assertEquals("h" + Attribute.SEPARATOR + "graphix"
                + Attribute.SEPARATOR + "coords" + Attribute.SEPARATOR + "x", x
                .getPath());
        assertEquals(
                "h" + Attribute.SEPARATOR + "graphix" + Attribute.SEPARATOR
                        + "shape" + Attribute.SEPARATOR + "round", round
                        .getPath());
    }

    /**
     * Initializes for every TestCase
     */
    @Override
    protected void setUp() {
        // h - graphix - coords - x
        // | L__ shape - round
        // L__graphixdesc
        h = new HashMapAttribute("h");
        graphix = new HashMapAttribute("graphix");
        graphixdesc = new StringAttribute("gr properties", "empty");
        coords = new HashMapAttribute("coords");
        x = new IntegerAttribute("x", 1);
        h.add(graphix);
        coords.add(x);
        graphix.add(coords);
        graphix.add(graphixdesc);

        shape = new HashMapAttribute("shape");
        round = new BooleanAttribute("round", true);
        shape.add(round);
        graphix.add(shape);
    }

    /**
     * Inner class used to provide an (empty) implementation for abstract class
     * AbstractAttribute. To test its constructor.
     */
    class AbstractAttributeImpl extends AbstractAttribute {
        /**
         * Creates a new AbstractAttributeImpl object.
         * 
         * @param id
         *            DOCUMENT ME!
         */
        public AbstractAttributeImpl(String id) {
            super(id);
        }

        /*
         * @see org.graffiti.attributes.Attribute#setDefaultValue()
         */
        public void setDefaultValue() {
            // TODO Auto-generated method stub
        }

        /**
         * DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        public Object getValue() {
            return null;
        }

        /**
         * DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        public Object copy() {
            return null;
        }

        /**
         * DOCUMENT ME!
         * 
         * @param o
         *            DOCUMENT ME!
         */
        @Override
        public void doSetValue(Object o) {
        }

        /**
         * DOCUMENT ME!
         */
        protected void doSetValue() {
        }
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
