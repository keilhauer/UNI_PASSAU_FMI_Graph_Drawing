// =============================================================================
//
//   StringEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StringEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * <code>StringEditComponent</code> provides an edit component for editing
 * strings. The edit field has just one line.
 * 
 * @version $Revision: 5772 $
 * 
 * @see org.graffiti.plugin.editcomponent.AbstractValueEditComponent
 * @see javax.swing.text.JTextComponent
 * @see TextAreaEditComponent
 */
public class StringEditComponent extends AbstractValueEditComponent {

    /** The text field containing the value of the displayable. */
    protected JTextComponent textComp;

    // /**
    // * The attribute displayed by this component;
    // */
    // private StringAttribute strAttr;

    /**
     * Constructs a new <code>StringEditComponent</code>.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public StringEditComponent(Displayable<?> disp) {
        super(disp);

        // System.out.println("creating StringAttribute");
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

        // textComp.setSize(new Dimension(100, 30));
        return this.textComp;
    }

    /**
     * Sets the current value of the displayable in the corresponding
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispEditFieldValue() {
        if (showEmpty) {
            System.out.println("empty");
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
    protected void setDispValue() {
        String text = this.textComp.getText();

        if (!text.equals(EMPTY_STRING)
                && !this.displayable.getValue().toString().equals(text)) {
            @SuppressWarnings("unchecked")
            Displayable<String> displayable = (Displayable<String>) this.displayable;
            displayable.setValue(text);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
