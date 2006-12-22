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

import java.util.Arrays;

/**
 * ElementProperties
 * <p>
 * A specification class that contains the properties for an element that are
 * required by the model.
 * <p>
 * The ElementProperties has an array of AtomicStates, which must have at least one element, which is the
 * ground state. The array of states must be ordered in ascending order of energy. I.e. the ground state
 * must be at [0], and the states ordered upward from there.
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
    private double meanStateLifetime;
    private double workFunction;


    protected ElementProperties( String name, double[] energyLevels,
                                 EnergyEmissionStrategy energyEmissionStrategy ) {
        this( name, energyLevels, energyEmissionStrategy, 0 );
    }

    protected ElementProperties( String name, double[] energyLevels,
                                 EnergyEmissionStrategy energyEmissionStrategy,
                                 double meanStateLifetime ) {
        this.name = name;
        this.energyLevels = energyLevels;
        this.energyEmissionStrategy = energyEmissionStrategy;
        this.meanStateLifetime = meanStateLifetime;
        initStates();
        setEnergyLevels( energyLevels );
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

    public AtomicState getGroundState() {
        return getStates()[0];
    }

    public double getMeanStateLifetime() {
        return meanStateLifetime;
    }

    protected void setMeanStateLifetime( double meanStateLifetime ) {
        this.meanStateLifetime = meanStateLifetime;
    }

    public EnergyEmissionStrategy getEnergyEmissionStrategy() {
        return energyEmissionStrategy;
    }

    public double[] getEnergyLevels() {
        return energyLevels;
    }

    public void setWorkFunction( double workFunction ) {
        this.workFunction = workFunction;
    }

    public double getWorkFunction() {
        return workFunction;
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
            meanStateLifetime = getMeanStateLifetime();
            states[i].setMeanLifetime( meanStateLifetime );
        }
    }
}
