// =============================================================================
//
//   TestEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestEditComponent.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.attributecomponents.test;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5769 $ $Date: 2009-01-29 16:08:13 +0100 (Do, 29 Jan 2009)
 *          $
 */
public class TestEditComponent extends AbstractValueEditComponent {

    /** The text field containing the value of the displayable. */
    protected JTextComponent textComp;

    /**
     * Constructs a new <code>TestEditComponent</code>.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public TestEditComponent(Displayable<?> disp) {
        super(disp);

        textComp = new JTextField(disp.getValue().toString());
    }

    /**
     * Returns the <code>JComponent</code> of this edit component.
     * 
     * @return the <code>JComponent</code> of this edit component.
     */
    public JComponent getComponent() {
        textComp.setMinimumSize(new Dimension(0, 30));
        textComp.setPreferredSize(new Dimension(50, 30));
        textComp.setMaximumSize(new Dimension(2000, 30));

        return this.textComp;
    }

    /**
     * Sets the current value of the displayable in the corresponding
     * <code>JComponent</code>.
     */
    @Override
    public void setDispEditFieldValue() {
        if (showEmpty) {
            this.textComp.setText(EMPTY_STRING);
        } else {
            this.textComp.setText(displayable.getValue().toString());
        }
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>. But only if it is different.
     */
    @Override
    public void setDispValue() {
        String text = this.textComp.getText();

        if (!text.equals(EMPTY_STRING)
                && !this.displayable.getValue().toString().equals(text)) {
            @SuppressWarnings("unchecked")
            Displayable<String> disp = (Displayable<String>) displayable;
            disp.setValue(text);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
