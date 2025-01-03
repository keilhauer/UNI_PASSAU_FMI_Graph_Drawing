// =============================================================================
//
//   AttributeEventTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeEventTest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import junit.framework.TestCase;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Tests class AttributeEvent.
 * 
 * @version $Revision: 5771 $
 */
public class AttributeEventTest extends TestCase {

    /** DOCUMENT ME! */
    private TestAttrListener attrListener;

    /**
     * Constructs a test case with the given name.
     * 
     * @param name
     *            The name of the test case.
     */
    public AttributeEventTest(String name) {
        super(name);
    }

    /**
     * Main method for running all the test cases of this test class.
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AttributeEventTest.class);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventAddString1() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        CollectionAttribute coll = new HashMapAttribute("hashid");
        StringAttribute str = new StringAttribute("strid", "value");
        n.addAttribute(coll, "");
        coll.add(str);

        assertEquals("Didn't call the attr listerner's method four times.", 4,
                attrListener.called);

        check("preAttributeAdded", coll);
        check("postAttributeAdded", coll);
        check("preAttributeAdded", str);
        check("postAttributeAdded", str);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventAddString2() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        CollectionAttribute coll = new HashMapAttribute("hashid");
        StringAttribute str = new StringAttribute("strid", "value");

        // different order than in testAttributeEventAddString1:
        coll.add(str);
        n.addAttribute(coll, "");

        assertEquals("Didn't call the attr listerner's method two times.", 2,
                attrListener.called);

        check("preAttributeAdded", coll);
        check("postAttributeAdded", coll);

        n.getAttribute("hashid.strid").setValue("otherValue");

        assertEquals("Didn't call the attr listeners another two times: "
                + attrListener, 4, attrListener.called);

        check("preAttributeChanged");
        check("postAttributeChanged");
    }

    /**
     * Tests the number of times an attribute event method has been called.
     */
    public void testAttributeEventCalled() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setInteger("not" + Attribute.SEPARATOR + "existing"
                + Attribute.SEPARATOR + "path", 1);

        assertEquals("Didn't call the attribute listerner's method two times.",
                2, attrListener.called);

        Attribute attr = n.getAttribute("not");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventCalledString2() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setString("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "x", "x-value");
        n.setString("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "y", "y-value");

        assertEquals("Didn't call the attr listerner's method four times.", 4,
                attrListener.called);

        Attribute attr = n.getAttribute("graphics");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);

        attr = n.getAttribute("graphics.coords.y");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventCalledTwoTimesBoolean() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setBoolean("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "x", true);

        assertEquals("Didn't call the attr listerner's method two times.", 2,
                attrListener.called);

        Attribute attr = n.getAttribute("graphics");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventCalledTwoTimesByte() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setByte("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "x", (byte) 10);

        assertEquals("Didn't call the attr listerner's method two times.", 2,
                attrListener.called);

        Attribute attr = n.getAttribute("graphics");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventCalledTwoTimesDouble() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setDouble("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "x", 1.1);

        assertEquals("Didn't call the attr listerner's method two times.", 2,
                attrListener.called);

        Attribute attr = n.getAttribute("graphics");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventCalledTwoTimesFloat() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setFloat("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "x", 1.1f);

        assertEquals("Didn't call the attr listerner's method two times.", 2,
                attrListener.called);

        Attribute attr = n.getAttribute("graphics");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    // pph: braucht erst Entscheidung was passiert bei
    // setXXX(unknown path) Aufrufen:

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventCalledTwoTimesInteger() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setInteger("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "x", 1);

        assertEquals("Didn't call the attr listerner's method two times: "
                + attrListener, 2, attrListener.called);

        Attribute attr = n.getAttribute("graphics");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventCalledTwoTimesLong() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setLong("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "x", 10);

        assertEquals("Didn't call the attr listerner's method two times.", 2,
                attrListener.called);

        Attribute attr = n.getAttribute("graphics");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventCalledTwoTimesShort() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setShort("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "x", (short) 10);

        assertEquals("Didn't call the attr listerner's method two times.", 2,
                attrListener.called);

        Attribute attr = n.getAttribute("graphics");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    /**
     * Tests the number of times, an attribute event method has been called.
     */
    public void testAttributeEventCalledTwoTimesString() {
        Graph g = new AdjListGraph();
        g.getListenerManager().addStrictAttributeListener(attrListener);

        Node n = g.addNode();
        n.setString("graphics" + Attribute.SEPARATOR + "coords"
                + Attribute.SEPARATOR + "x", "value");

        assertEquals("Didn't call the attr listerner's method two times.", 2,
                attrListener.called);

        Attribute attr = n.getAttribute("graphics");
        check("preAttributeAdded", attr);
        check("postAttributeAdded", attr);
    }

    /**
     * Tests the attribute event constructor with path as parameter.
     */
    public void testAttributeEventConstructorWithPath() {
        String id = "id";
        String path = "path";
        Attribute attr = new BooleanAttribute(id);
        AttributeEvent event = new AttributeEvent(path, attr);

        assertEquals("Failed to create a correct AttributeEvent."
                + "The attribute reference is not the same", attr, event
                .getAttribute());
        assertEquals("Failed to create a correct AttributeEvent."
                + "The path is not the same", path, event.getPath());
    }

    /**
     * Tests the attribute event constructor without path.
     */
    public void testAttributeEventConstructorWithoutPath() {
        String id = "id";
        Attribute attr = new BooleanAttribute(id);
        AttributeEvent event = new AttributeEvent(attr);

        assertEquals("Failed to create a correct AttributeEvent."
                + "The attribute reference is not the same", attr, event
                .getAttribute());
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() {
        attrListener = new TestAttrListener();
    }

    /**
     * Helper function. Checks that the next elements in
     * attrListener.methodsCalled and attrListener.events correspond to method
     * and attrEvent.
     * 
     * @param method
     *            the method name
     */
    private void check(String method) {
        assertEquals("Didn't call correct method (or in wrong order): "
                + attrListener, method, attrListener.methodsCalled
                .removeFirst());
    }

    /**
     * Helper function. Checks that the next elements in
     * attrListener.methodsCalled and attrListener.events correspond to method
     * and attrEvent.
     * 
     * @param method
     *            the method name
     * @param attr
     *            the Attribute Event belonging to the method
     */
    private void check(String method, Attribute attr) {
        check(method);
        assertEquals("Didn't pass correct event (or in wrong order): "
                + attrListener, attr, ((AttributeEvent) attrListener.events
                .removeFirst()).getAttribute());
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
