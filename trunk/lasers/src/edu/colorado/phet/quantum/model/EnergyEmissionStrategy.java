/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;




/**
 * EnergyEmissionStrategy
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface EnergyEmissionStrategy {

    /**
     * Given a specified atom, determine its new state after going through a
     * quantum decrease in energy
     *
     * @param atom
     * @return the atom's new state
     */
    public AtomicState emitEnergy( Atom atom );
}
