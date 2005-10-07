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

/**
 * LaserElementProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class LaserElementProperties extends ElementProperties {

    protected LaserElementProperties( String name, double[] energyLevels, EnergyEmissionStrategy energyEmissionStrategy, EnergyAbsorptionStrategy energyAbsorptionStrategy, double meanStateLifetime ) {
        super( name, energyLevels, energyEmissionStrategy, energyAbsorptionStrategy, meanStateLifetime );
    }

    public void setMeanStateLifetime( double meanStateLifetime ) {
        super.setMeanStateLifetime( meanStateLifetime );
    }

    public AtomicState getGroundState() {
        return getStates()[0];
    }

    public AtomicState getMiddleEnergyState() {
        return getStates()[1];
    }

    abstract public AtomicState getHighEnergyState();
}
