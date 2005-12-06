/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.salt;

import edu.colorado.phet.solublesalts.model.crystal.Lattice;

import java.util.HashMap;

/**
 * Salt
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Salt {
    private Lattice lattice;
    private HashMap components;

    protected Salt( HashMap components, Lattice lattice) {
        this.components = components;
        this.lattice = lattice;
    }

    public HashMap getComponents() {
        return components;
    }

    public Lattice getLattice() {
        return lattice;
    }
}
