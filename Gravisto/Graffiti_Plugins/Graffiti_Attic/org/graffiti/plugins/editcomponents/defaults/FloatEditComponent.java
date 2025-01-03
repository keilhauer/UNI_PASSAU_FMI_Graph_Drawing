// =============================================================================
//
//   FloatEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FloatEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import org.graffiti.attributes.FloatAttribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.NumberEditComponent;

/**
 * An edit component for editing floats. Quite small since NumberEditComponent
 * does most of the work.
 * 
 * @see NumberEditComponent
 */
public class FloatEditComponent extends NumberEditComponent {

    /**
     * Constructs a new <code>FloatEditComponent</code>.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public FloatEditComponent(Displayable<?> disp) {
        super(disp);
    }

    /**
     * Constructs a new <code>FloatEditComponent</code>.
     * 
     * @param attr
     *            the <code>FloatAttribute</code> to be edited.
     */
    public FloatEditComponent(FloatAttribute attr) {
        super(attr);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
