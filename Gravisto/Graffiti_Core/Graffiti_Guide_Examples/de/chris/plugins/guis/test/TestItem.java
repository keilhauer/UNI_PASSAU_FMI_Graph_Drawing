// =============================================================================
//
//   TestItem.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestItem.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.guis.test;

import javax.swing.ImageIcon;

import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.gui.GraffitiMenuItem;

/**
 * This is a example for a menu item.
 * 
 * @author chris
 */
public class TestItem extends GraffitiMenuItem {

    /**
     * 
     */
    private static final long serialVersionUID = 2729180815574359297L;

    /**
     * Creates a new TestItem object.
     */
    public TestItem() {
        super(TestMenu.ID, new TestMenuItemAction("Test Action"));

        // super("menu.file", new TestMenuItemAction("Test Action"));
        setText(((GraffitiAction) getAction()).getName()); // default
        setMnemonic('T');
        setIcon(new ImageIcon(getClass().getResource("smiley.gif")));
        setToolTipText("This is a test tooltip for the test menu item.");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
