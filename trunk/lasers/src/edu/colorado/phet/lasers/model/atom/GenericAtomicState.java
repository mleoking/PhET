/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;

/**
 * GenericAtomicState
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GenericAtomicState extends AtomicState {

    public GenericAtomicState( AtomicState templateState ) {

    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

    }

    public AtomicState getNextLowerEnergyState() {
        return null;
    }

    public AtomicState getNextHigherEnergyState() {
        return null;
    }
}
