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

import edu.colorado.phet.lasers.model.LaserModel;

/**
 * PropertiesBasedAtom
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PropertiesBasedAtom extends Atom {

    private EnergyEmissionStrategy energyEmissionStrategy;
    private EnergyAbsorptionStrategy energyAbsorptionStrategy;


    /**
     * @param model
     */
    public PropertiesBasedAtom( LaserModel model, ElementProperties elementProperties ) {
        super( model, elementProperties.getStates().length, true );

        this.energyAbsorptionStrategy = elementProperties.getEnergyAbsorptionStrategy();
        this.energyEmissionStrategy = elementProperties.getEnergyEmissionStrategy();
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
    public PropertiesBasedAtom( LaserModel model, AtomicState[] states ) {
        super( model, states.length, true );

        if( states.length < 2 ) {
            throw new RuntimeException( "Atom must have at least two states" );
        }
        setStates( states );
        setCurrState( states[0] );
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
        this.energyAbsorptionStrategy = elementProperties.getEnergyAbsorptionStrategy();
        this.energyEmissionStrategy = elementProperties.getEnergyEmissionStrategy();
        super.setStates( elementProperties.getStates() );
    }
}

