// =============================================================================
//
//   TestPlugin1.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestPlugin1.java 5773 2010-05-07 18:50:34Z gleissner $

package tests.graffiti.pluginmgr;

import javax.swing.ImageIcon;

import org.graffiti.plugin.GenericPlugin;

/**
 * Represents another plugin for the plugin manager test cases.
 * 
 * @version $Revision: 5773 $
 */
public class TestPlugin1 extends TestPlugin implements GenericPlugin {

    /**
     * Constructor for TestPlugin.
     */
    public TestPlugin1() {
        super();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public ImageIcon getIcon() {
        return iBundle.getIcon("icon.plugin.test");
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
