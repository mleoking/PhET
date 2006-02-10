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

import edu.colorado.phet.quantum.model.Photon;

import java.util.Random;

/**
 * MetalEnergyAbsorptionStrategy
 * <p/>
 * Provides a simplified model of how electrons are kicked off a metal by photons. All electrons are
 * considered to be in the lowest sub-level of the highest energy band.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleEnergyAbsorptionStrategy extends MetalEnergyAbsorptionStrategy {
    private static Random random = new Random( System.currentTimeMillis() );

    public SimpleEnergyAbsorptionStrategy( double workFunction ) {
        super( workFunction );
        this.workFunction = workFunction;
    }

    public double energyAfterPhotonCollision( Photon photon ) {
        double e = random.nextInt( NUM_SUB_LEVELS ) != 0 ? Double.NEGATIVE_INFINITY : photon.getEnergy() - workFunction;
        return e;
    }
}
