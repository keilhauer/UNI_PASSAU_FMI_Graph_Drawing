// =============================================================================
//
//   TestToolButton.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestToolButton.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.tools.test;

import org.graffiti.plugin.gui.ToolButton;
import org.graffiti.plugin.tool.Tool;

/**
 * A smiley botton for TestTool.
 * 
 * @author chris
 */
public class TestToolButton extends ToolButton {

    /**
     * 
     */
    private static final long serialVersionUID = 5438520902930553961L;

    /**
     * Creates a new TestToolButton object.
     * 
     * @param tool
     *            the tool which should be used by pressing this button
     */
    public TestToolButton(Tool<?> tool) {
        super(tool);
        // super(tool, "org.graffiti.plugins.modes.defaultEditMode",
        // new ImageIcon(TestToolButton.class.getResource("smiley.gif")));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
