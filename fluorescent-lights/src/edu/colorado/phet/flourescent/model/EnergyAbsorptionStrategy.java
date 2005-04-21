/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent.model;

import edu.colorado.phet.lasers.model.atom.Atom;

/**
 * EnergyAbsorptionStrategy
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface EnergyAbsorptionStrategy {
    public void collideWithElectron( Atom atom, Electron electron );
}
