// =============================================================================
//
//   TestAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestAttribute.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.attributecomponents.test;

import org.graffiti.attributes.AbstractAttribute;

/**
 * Contains a String.
 * 
 * @author chris
 * @version $Revision: 5769 $
 */
public class TestAttribute extends AbstractAttribute {

    /** The value of this <code>StringAttribute</code>. */
    private String value;

    /**
     * Constructs a new instance of a <code>StringAttribute</code>.
     * 
     * @param id
     *            the id of the <code>Attribute</code>.
     */
    public TestAttribute(String id) {
        super(id);
        setDescription("This is an example attribute"); // tooltip
    }

    /**
     * Constructs a new instance of a <code>StringAttribute</code> with the
     * given value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the <code>Attribute</code>.
     */
    public TestAttribute(String id, String value) {
        super(id);
        this.value = value;
        setDescription("This is an example attribute"); // tooltip
    }

    /**
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
        value = "";
    }

    /**
     * Returns the value of this object.
     * 
     * @return the value of this object.
     */
    public String getString() {
        return value;
    }

    /**
     * Returns the value of this attribute, i.e. contained Sting object.
     * 
     * @return the value of the attribute, i.e. contained String object.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns a deep copy of this instance.
     * 
     * @return a deep copy of this instance.
     */
    public Object copy() {
        return new TestAttribute(this.getId(), this.value);
    }

    /**
     * @see org.graffiti.attributes.Attribute#toString(int)
     */
    @Override
    public String toString(int n) {
        return getSpaces(n) + getId() + " = \"" + value + "\"";
    }

    /**
     * @see org.graffiti.plugin.Displayable#toXMLString()
     */
    @Override
    public String toXMLString() {
        return getStandardXML(value);
    }

    /**
     * Sets the value of the <code>Attribute</code>. The
     * <code>ListenerManager</code> is informed by the method
     * <code>setValue()</code>.
     * 
     * @param o
     *            the new value of the attribute.
     * 
     * @exception IllegalArgumentException
     *                if the parameter has not the appropriate class for this
     *                <code>Attribute</code>.
     */
    @Override
    protected void doSetValue(Object o) throws IllegalArgumentException {
        assert o != null;

        try {
            value = (String) o;
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("Invalid value type.");
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
