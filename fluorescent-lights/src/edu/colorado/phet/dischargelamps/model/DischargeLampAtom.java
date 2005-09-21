/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.lasers.model.ElementProperties;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;

/**
 * Extends Atom class from the Laser simulation in that it knows how to collide with
 * an electron
 */
public class DischargeLampAtom extends Atom {

    // The time that an atom spends in any one state before dropping to a lower one (except for
    // the ground state)
//    public static final double DEFAULT_STATE_LIFETIME = ( DischargeLampsConfig.DT / DischargeLampsConfig.FPS ) * 300;
    public static final double DEFAULT_STATE_LIFETIME = ( DischargeLampsConfig.DT / DischargeLampsConfig.FPS ) * 100;
    private ElementProperties elementProperties;

////    private EnergyAbsorptionStrategy energyAbsorptionStrategy = new HighestStateAbsorptionStrategy();
    private EnergyEmissionStrategy energyEmissionStrategy = new HydrogenEnergyEmissionStrategy();
////    private EnergyEmissionStrategy energyEmissionStrategy = new NextLowestEnergyEmissionStrategy();
    private EnergyAbsorptionStrategy energyAbsorptionStrategy = new FiftyPercentAbsorptionStrategy();
////    private EnergyEmissionStrategy energyEmissionStrategy = new FiftyPercentEnergyEmissionStrategy();


    /**
     * @param model
     */
    public DischargeLampAtom( LaserModel model, ElementProperties elementProperties ) {
        super( model, elementProperties.getStates().length, true );

        if( elementProperties.getStates().length < 2 ) {
            throw new RuntimeException( "Atom must have at least two states" );
        }
        setStates( elementProperties.getStates() );
        setCurrState( elementProperties.getStates()[0] );
    }

    /**
     * @param model
     * @param states
     * @deprecated
     */
    public DischargeLampAtom( LaserModel model, AtomicState[] states ) {
        super( model, states.length, true );

        if( states.length < 2 ) {
            throw new RuntimeException( "Atom must have at least two states" );
        }
        setStates( states );
        setCurrState( states[0] );
    }

    /**
     * If the electron's energy is greater than the difference between the atom's current energy and one of
     * its higher energy states, the atom absorbs some of the electron's energy and goes to a state higher
     * in energy by the amount it absorbs. Exactly how much energy it absorbs is random.
     *
     * @param electron
     */
    public void collideWithElectron( Electron electron ) {
        energyAbsorptionStrategy.collideWithElectron( this, electron );
    }

    /**
     * Returns the state the atom will be in after it emits a photon. By default, this is the
     * ground state
     *
     * @return
     */
    public AtomicState getEnergyStateAfterEmission() {
        return energyEmissionStrategy.emitEnergy( this );
    }

    public void setElementProperties( ElementProperties elementProperties ) {
        this.elementProperties = elementProperties;
        this.energyAbsorptionStrategy = elementProperties.getEnergyAbsorptionStrategy();
        this.energyEmissionStrategy = elementProperties.getEnergyEmissionStrategy();

        super.setStates( elementProperties.getStates() );
    }
}
