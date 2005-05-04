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

import edu.colorado.phet.lasers.model.atom.Atom;

import java.util.Random;

/**
 * EnergyAbsorptionStrategy
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface EnergyAbsorptionStrategy {
    public void collideWithElectron( Atom atom, Electron electron );

    /**
     * Utility class that sets an electron's direction of travel to a random
     * direction within +/- PI/4 of its current direction
     */
    public static class ElectronRedirector {
        private static Random random = new Random();
        private static double dispersionAngle = Math.PI / 4;

        public static void setElectronDirection( Electron electron ) {
            double theta = random.nextDouble() * dispersionAngle - dispersionAngle / 2;
            System.out.println( "Math.toDegrees( theta ) = " + Math.toDegrees( theta ) );
            electron.getVelocity().rotate( theta );
        }
    }
}
