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

import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.dischargelamps.model.EnergyAbsorptionStrategy;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.Photon;

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

    private double workFunction;
    private int numLevels = 10;
    // Total energy depth across all sublevels, in eV
    private double totalEnergyDepth = 4;

    public MetalEnergyAbsorptionStrategy( double workFunction ) {
        this.workFunction = workFunction;
    }

    public double energyAfterPhotonCollision( Photon photon ) {
        // Randomly pick one of the levels
        int level = RANDOM.nextInt( numLevels );
        // Determine the energy of the electron at that level
        double energyRequired = workFunction + ( level * ( totalEnergyDepth / numLevels ) );
        return photon.getEnergy() - energyRequired;
    }

    public void collideWithElectron( Atom atom, Electron electron ) {
        throw new RuntimeException( "not implemented" );
    }
}
