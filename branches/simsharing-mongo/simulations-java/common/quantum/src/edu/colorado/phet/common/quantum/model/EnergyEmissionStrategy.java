// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.quantum.model;


/**
 * EnergyAbsorptionStrategy
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface EnergyEmissionStrategy {
    public AtomicState emitEnergy( Atom atom );
}
