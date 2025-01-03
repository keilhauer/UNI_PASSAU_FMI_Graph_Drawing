// =============================================================================
//
//   KingPlugin.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: KingPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.tournamentkings;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Marek Piorkowski
 * @version $Revision: 5772 $ $Date: 2009-10-19 20:32:39 +0200 (Mo, 19 Okt 2009)
 *          $
 */
public class KingPlugin extends GenericPluginAdapter {
    public KingPlugin() {
        this.algorithms = new Algorithm[] { new King(), new SortedSeqOfKings() };
    }

    @Override
    public String getName() {
        return "Kings in Tournaments Algorithms";
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
