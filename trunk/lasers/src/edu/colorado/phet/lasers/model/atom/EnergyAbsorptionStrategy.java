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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.lasers.model.PhysicsUtil;

import java.util.Random;

/**
 * EnergyAbsorptionStrategy
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class EnergyAbsorptionStrategy {

    abstract public void collideWithElectron( Atom atom, Electron electron );

    /**
     * Returns the kinetic energy of an electron at the time it collides with an atom. It does this
     * by computing the actual time of collision, then the speed of the electron at that time.
     *
     * @param atom
     * @param electron
     * @return
     */
    public static double getElectronEnergyAtCollision( Atom atom, Electron electron ) {
        double energy = 0;
        double prevDistSq = electron.getPositionPrev().distanceSq( atom.getPosition() );
        double atomRadSq = ( atom.getRadius() + electron.getRadius() ) * ( atom.getRadius() + electron.getRadius() );
        double collisionDist = Math.sqrt( prevDistSq ) - Math.sqrt( atomRadSq );
        double a = electron.getAcceleration().getMagnitude() / 2;
        double b = electron.getVelocityPrev().getMagnitude();

        double c = -collisionDist;
        double[] roots = MathUtil.quadraticRoots( a, b, c );
        double t = roots[0] >= 0 ? roots[0] : roots[1];
        if( t < 0 || Double.isNaN( t ) || Double.isInfinite( t ) ) {
            energy = 0;
        }
        else {
            double v = electron.getVelocityPrev().getMagnitude() + electron.getAcceleration().getMagnitude() * t;
            energy = DischargeLampsConfig.PIXELS_PER_NM * DischargeLampsConfig.PIXELS_PER_NM * v * v * electron.getMass() / 2 * PhysicsUtil.EV_PER_JOULE;
//            energy = electron.getEnergy();
        }
        return energy;
    }


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
