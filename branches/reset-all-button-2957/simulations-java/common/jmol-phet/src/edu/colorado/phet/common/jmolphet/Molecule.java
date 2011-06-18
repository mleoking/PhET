// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jmolphet;

import org.jmol.api.JmolViewer;

/**
 * @author Sam Reid
 */
public interface Molecule {
    String getDisplayName();

    int getCID();

    String getCmlData();

    void fixJmolColors( JmolViewer viewer );
}
