// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jmolphet;

import org.jmol.api.JmolViewer;

//TODO why is this here? Either rename to JmolMolecule, or move to common/chemistry/src/.../model

/**
 * Interface for a molecule that is viewable with Jmol, intended for use with JmolPanel and JmolDialog.
 *
 * @author Sam Reid
 */
public interface Molecule {

    /**
     * Gets the user-visible name of the molecule.
     *
     * @return String
     */
    String getDisplayName();

    /**
     * Gets the molecule description.
     * Jmol supports a large number of formats, and auto detects the format of the String.
     * For a list of supported formats, see http://jmol.svn.sourceforge.net/viewvc/jmol/trunk/Jmol-datafiles/
     *
     * @return molecule description
     */
    String getData();

    /**
     * This is intended to be a hook for substituting custom colors for things related
     * to the molecule. Since you're given a handle to the viewer, you could be abusive
     * and use this to execute any script.  But you wouldn't do that, would you?
     * <p/>
     * TODO provide an example of how to change atom colors
     *
     * @param viewer
     */
    void fixJmolColors( JmolViewer viewer );
}
