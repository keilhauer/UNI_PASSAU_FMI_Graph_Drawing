// =============================================================================
//
//   TestToolbar.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestToolbar.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.guis.test;

import org.graffiti.plugin.gui.GraffitiContainer;
import org.graffiti.plugin.gui.GraffitiToolbar;

/**
 * DOCUMENT ME!
 * 
 * @author chris
 */
public class TestToolbar extends GraffitiToolbar implements GraffitiContainer {

    // to avoid collisions let ID be package name + ".toolbars." + name of
    // toolbar

    /**
     * 
     */
    private static final long serialVersionUID = 4394415573094595122L;
    /** DOCUMENT ME! */
    public static final String ID = "de.chris.plugins.guis.test.toolbars.Testtoolbar";

    /**
     * Creates a new TestToolbar object.
     */
    public TestToolbar() {
        super(ID);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public String getId() {
        return ID;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
