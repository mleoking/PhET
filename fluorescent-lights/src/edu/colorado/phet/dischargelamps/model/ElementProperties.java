/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.PhysicsUtil;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.util.Arrays;

/**
 * ElementProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElementProperties {
    private double[] energyLevels;
    private String name;
    private boolean levelsMovable;
    private AtomicState[] states;
    private EnergyEmissionStrategy energyEmissionStrategy;
    private EnergyAbsorptionStrategy energyAbsorptionStrategy;

    protected ElementProperties( String name, double[] energyLevels, 
                                 EnergyEmissionStrategy energyEmissionStrategy,
                                 EnergyAbsorptionStrategy energyAbsorptionStrategy ) {
        this.name = name;
        this.energyLevels = energyLevels;
        this.energyEmissionStrategy = energyEmissionStrategy;
        this.energyAbsorptionStrategy = energyAbsorptionStrategy;
        initStates();
    }

    protected void setEnergyLevels( double[] energyLevels ) {
        if( energyLevels.length != states.length ) {
            this.energyLevels = energyLevels;
            initStates();
        }
        else {
            this.energyLevels = energyLevels;
            updateStates();
        }
    }

    public EnergyEmissionStrategy getEnergyEmissionStrategy() {
        return energyEmissionStrategy;
    }

    public EnergyAbsorptionStrategy getEnergyAbsorptionStrategy() {
        return energyAbsorptionStrategy;
    }

    public double[] getEnergyLevels() {
        return energyLevels;
    }

    public boolean isLevelsMovable() {
        return levelsMovable;
    }

    public void setLevelsMovable( boolean levelsMovable ) {
        this.levelsMovable = levelsMovable;
    }

    public AtomicState[] getStates() {
        return states;
    }

    public String toString() {
        return name;
    }

    private void initStates() {
        states = new AtomicState[energyLevels.length];
        states[0] = new GroundState();
        states[0].setEnergyLevel( energyLevels[0] );
        for( int i = 1; i < states.length; i++ ) {
            states[i] = new AtomicState();
            states[i].setEnergyLevel( 0 );
        }
        AtomicState.linkStates( states );
        updateStates();
    }

    private void updateStates() {
        // Copy the energies into a new array, sort and normalize them
        double[] energies = new double[energyLevels.length];
        for( int i = 0; i < energies.length; i++ ) {
            energies[i] = energyLevels[i];
        }
        Arrays.sort( energies );

        states[0].setEnergyLevel( energies[0] );
        for( int i = 1; i < states.length; i++ ) {
            double energy = ( energies[i] );
            states[i].setEnergyLevel( energy );
            states[i].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
        }
    }
}
