/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.common.util.PhysicsUtil;

import java.util.Random;

public interface InitialElectronSpeedStrategy {

    //-----------------------------------------------------------------
    // Definition of the interface
    //-----------------------------------------------------------------

    double determineNewElectronSpeed( double energy );

    //-----------------------------------------------------------------
    // Concrete implementations of the interface
    //-----------------------------------------------------------------

    /**
     * Returns an initial speed based soley on the energy the electron has. Always returns
     * the same speed for a given energy.
     */
    public class Uniform implements InitialElectronSpeedStrategy {
        private double scaleFactor = 1;

        public Uniform( double scaleFactor ) {
            this.scaleFactor = scaleFactor;
        }

        public double determineNewElectronSpeed( double energy ) {
            double speed = Math.sqrt( 2 * energy / PhysicsUtil.ELECTRON_MASS ) * scaleFactor;
            return speed;
        }
    }

    /**
     * Returns an initial speed that is randomly distributed between the speed the electron
     * would have if its kinetic energy were equal to a specified energy, and a specified minimum speed.
     */
    public class Randomized extends Uniform {
        private Random random = new Random();
        private double minSpeed;

        public Randomized( double scaleFactor, double minSpeed ) {
            super( scaleFactor );
            this.minSpeed = minSpeed;
        }

        public double determineNewElectronSpeed( double energy ) {
            double maxSpeed = super.determineNewElectronSpeed( energy );

            // Speed is randomly distributed between the max speed and a minimum speed.
            double speed = maxSpeed * random.nextDouble();
            speed = Math.max( speed, minSpeed );
            return speed;
        }
    }
}
