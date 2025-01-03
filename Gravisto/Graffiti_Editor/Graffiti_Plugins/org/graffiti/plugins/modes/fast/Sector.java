// =============================================================================
//
//   Sector.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.modes.fast;

import java.util.Arrays;
import java.util.List;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.view.interactive.SlotEditableEnum;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public enum Sector implements SlotEditableEnum {
    IGNORE("sector.ignore"), LOW("sector.low"), CENTER("sector.center"), HIGH(
            "sector.high");

    private final String bundleString;

    private Sector(String bundleString) {
        this.bundleString = bundleString;
    }

    public String getName(Bundle bundle) {
        return bundle.getString(bundleString);
    }

    public List<? extends SlotEditableEnum> getValues() {
        return Arrays.asList(values());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
