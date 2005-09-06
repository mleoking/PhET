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

    protected ElementProperties( String name, double[] energyLevels ) {
        this.name = name;
        this.energyLevels = energyLevels;
    }

    protected void setEnergyLevels(  double[] energyLevels ) {
        this.energyLevels = energyLevels;
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
        // Copy the energies into a new array, sort and normalize them
        double[] energies = new double[energyLevels.length];
        for( int i = 0; i < energies.length; i++ ) {
            energies[i] = energyLevels[i];
        }
        Arrays.sort( energies );

        AtomicState[] states = new AtomicState[energies.length];
        states[0] = new GroundState();
        states[0].setEnergyLevel( energies[0] );
        double eBlue = PhysicsUtil.wavelengthToEnergy( Photon.BLUE );
        for( int i = 1; i < states.length; i++ ) {
            states[i] = new AtomicState();
            double energy = ( energies[i] );
            states[i].setEnergyLevel( energy );
            states[i].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
        }
        AtomicState.linkStates( states );
        return states;
    }

    public String toString() {
        return name;
    }
}
