// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jmolphet;

import org.jmol.api.JmolViewer;

//TODO javadoc

/**
 * @author Sam Reid
 */
public interface Molecule {

    //TODO javadoc
    String getDisplayName();

    //TODO what is this, and what is it used for?
    int getCID();

    //TODO what formats can this be in?
    String getData();

    // TODO what does this do? how is it used?
    void fixJmolColors( JmolViewer viewer );
}
