// =============================================================================
//
//   EditComponentTestPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditComponentTestPlugin.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.editcomponents.test;

import org.graffiti.plugin.EditorPluginAdapter;

/**
 * DOCUMENT ME!
 * 
 * @author chris
 * @version $Revision: 5769 $ $Date: 2006-01-04 10:21:57 +0100 (Mi, 04 Jan 2006)
 *          $
 */
public class EditComponentTestPlugin extends EditorPluginAdapter {

    /**
     * Creates a new AttributeTestPlugin object.
     */
    public EditComponentTestPlugin() {
        this.attributes = new Class[1];
        this.attributes[0] = TestAttribute.class;

        this.valueEditComponents.put(TestAttribute.class,
                TestEditComponent.class);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
