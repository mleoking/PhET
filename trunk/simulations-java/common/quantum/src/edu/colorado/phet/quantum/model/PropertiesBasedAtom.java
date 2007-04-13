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
 * PropertiesBasedAtom
 * <p>
 * An Atom that gets its model-dependent specification from an ElementProperties object
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PropertiesBasedAtom extends Atom {

    private EnergyEmissionStrategy energyEmissionStrategy;

    /**
     * @param model
     */
    public PropertiesBasedAtom( QuantumModel model, ElementProperties elementProperties ) {
        super( model, elementProperties.getStates().length, true );

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
    public PropertiesBasedAtom( QuantumModel model, AtomicState[] states ) {
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
     * @return the state the atom will be in after it emits a photon
     */
    public AtomicState getEnergyStateAfterEmission() {
        return energyEmissionStrategy.emitEnergy( this );
    }

    public void setElementProperties( ElementProperties elementProperties ) {
        this.energyEmissionStrategy = elementProperties.getEnergyEmissionStrategy();
        super.setStates( elementProperties.getStates() );
    }
}

