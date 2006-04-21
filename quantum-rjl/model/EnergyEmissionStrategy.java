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
 * EnergyAbsorptionStrategy
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface EnergyEmissionStrategy {
    public AtomicState emitEnergy( Atom atom );
}
