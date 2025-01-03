// =============================================================================
//
//   InspectorContainer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: InspectorContainer.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.inspectors.defaults;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JTabbedPane;

import org.graffiti.editor.MainFrame;
import org.graffiti.plugin.gui.GraffitiContainer;
import org.graffiti.plugin.inspector.InspectorTab;

/**
 * Represents the central gui component of the inspector plugin.
 * 
 * @version $Revision: 5772 $
 */
public class InspectorContainer extends JTabbedPane implements
        GraffitiContainer {

    /** The tabbed pane for the edge-, node- and graph-tab. */

    /**
     * 
     */
    private static final long serialVersionUID = 5030057443247692360L;

    // private JTabbedPane tabbedPane;
    /** The list of the inspector's tabs. */
    private List<InspectorTab> tabs;

    /**
     * The reference to the MainFrame. It is necessary for access to the
     * UndoSupport needed by edit panels of particular tabs
     */
    private MainFrame mainFrame;

    /** The id of this GraffitiContainer. */
    private String id = "inspector";

    /** DOCUMENT ME! */
    private String name = "Inspector";

    /** The id of the component this component wants to be added to. */
    private String preferredComponent = "pluginPanel";

    /**
     * Creates a new InspectorContainer object.
     */
    public InspectorContainer() {
        tabs = new LinkedList<InspectorTab>();

        // tabbedPane = new JTabbedPane();
        setSize(Inspector.DEFAULT_WIDTH, 400);
        setMinimumSize(new java.awt.Dimension(Inspector.DEFAULT_WIDTH, 400));
        setPreferredSize(new java.awt.Dimension(Inspector.DEFAULT_WIDTH, 400));
        setMaximumSize(new java.awt.Dimension(Inspector.DEFAULT_WIDTH, 400));

        // tabbedPane.setVisible(true);
        // add(tabbedPane);
        this.validate();
    }

    /**
     * @see org.graffiti.plugin.gui.GraffitiContainer#getId()
     */
    public String getId() {
        return id;
    }

    /**
     * @see org.graffiti.plugin.gui.GraffitiComponent#setMainFrame(org.graffiti.editor.MainFrame)
     */
    public void setMainFrame(MainFrame mf) {
        this.mainFrame = mf;
    }

    /**
     * @see org.graffiti.plugin.gui.GraffitiComponent#getPreferredComponent()
     */
    public String getPreferredComponent() {
        return preferredComponent;
    }

    // public JTabbedPane getTabbedPane() {
    // return this.tabbedPane;
    // }
    public List<InspectorTab> getTabs() {
        return this.tabs;
    }

    // public Component getComponent() {
    // return this;
    // }
    public String getTitle() {
        return this.name; // TODO I18N
    }

    /**
     * Adds a tab to the inspector.
     * 
     * @param tab
     *            the tab to add to the inspector.
     */
    public void addTab(InspectorTab tab) {
        this.tabs.add(tab);

        if (tab.getEditPanel() != null) {
            tab.getEditPanel().setUndoSupport(mainFrame.getUndoSupport());
        }

        // this.tabbedPane.addTab(tab.getTitle(), tab);
        this.addTab(tab.getTitle(), tab);

        // // tab.setVisible(true);
        // tab.revalidate();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param g
     *            DOCUMENT ME!
     */
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        setPreferredSize(getParent().getSize());

        // setPreferredSize(new Dimension((int)getParent().getSize().getWidth(),
        // (int)getParent().getSize().getHeight()-100));
        revalidate();
    }

    /**
     * Removes a tab from the inspector.
     * 
     * @param tab
     *            the tab to remove from the inspector.
     */
    public void removeTab(InspectorTab tab) {
        this.tabs.remove(tab);

        // this.tabbedPane.remove(tab);
        this.remove(tab);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
