/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.quantum.model.Electron;
import edu.colorado.phet.dischargelamps.model.EnergyAbsorptionStrategy;
import edu.colorado.phet.quantum.model.Atom;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.quantum.model.*;

import java.util.Random;

/**
 * MetalEnergyAbsorptionStrategy
 * <p/>
 * Models the way that electrons are knocked off a metal when it is hit by a photon.
 * The highest energy level has a number of sub-levels that are evenly spaced. When
 * a photon hits the metal, it hits an electron in a randlomly selected sub-level.
 * The energy required to dislodge an electron is the material's work function plus
 * the energy associated with the depth of the sub-level.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MetalEnergyAbsorptionStrategy extends EnergyAbsorptionStrategy {
    private static Random RANDOM = new Random();
    public static final int NUM_SUB_LEVELS = 20;
    // Total energy depth across all sublevels, in eV
    public static final double TOTAL_ENERGY_DEPTH = 4;

    double workFunction;

    public MetalEnergyAbsorptionStrategy( double workFunction ) {
        this.workFunction = workFunction;
    }

    public double energyAfterPhotonCollision( Photon photon ) {
        // Randomly pick one of the levels
        int level = RANDOM.nextInt( NUM_SUB_LEVELS );
        // Determine the energy of the electron at that level
        double energyRequired = workFunction + ( level * ( TOTAL_ENERGY_DEPTH / NUM_SUB_LEVELS ) );
        return photon.getEnergy() - energyRequired;
    }

    public void collideWithElectron( Atom atom, Electron electron ) {
        throw new RuntimeException( "not implemented" );
    }
}
