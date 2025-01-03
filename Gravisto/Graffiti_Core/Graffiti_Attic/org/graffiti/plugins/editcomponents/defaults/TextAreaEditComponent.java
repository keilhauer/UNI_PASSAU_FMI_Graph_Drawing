// =============================================================================
//
//   TextAreaEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TextAreaEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import javax.swing.JTextArea;

import org.graffiti.attributes.StringAttribute;
import org.graffiti.plugin.Displayable;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 5772 $
 */
public class TextAreaEditComponent extends StringEditComponent {

    /**
     * Constructor for TextAreaEditComponent.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public TextAreaEditComponent(Displayable<?> disp) {
        super(disp);
        this.textComp = new JTextArea();
    }

    /**
     * Constructor for TextAreaEditComponent.
     * 
     * @param attr
     *            DOCUMENT ME!
     */
    public TextAreaEditComponent(StringAttribute attr) {
        super(attr);
        this.textComp = new JTextArea(attr.getString());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
