// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.quantum.model.Atom;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.quantum.model.Electron;

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
    public static double getElectronEnergyAtCollision( DischargeLampAtom atom, Electron electron ) {
        double energy = 0;
        double prevDistSq = electron.getPositionPrev().distanceSq( atom.getPosition() );
        double atomRadSq = ( atom.getBaseRadius() + electron.getRadius() ) * ( atom.getBaseRadius() + electron.getRadius() );
//        double atomRadSq = ( atom.getRadius() + electron.getRadius() ) * ( atom.getRadius() + electron.getRadius() );
        double prevDist = electron.getPositionPrev().distance( atom.getPosition() ) - electron.getRadius() - atom.getBaseRadius();
        double collisionDist = Math.sqrt( prevDistSq ) - Math.sqrt( atomRadSq );

        collisionDist = prevDist;
        double a = electron.getAcceleration().magnitude() / 2;
        double b = electron.getVelocityPrev().magnitude();

        double c = -collisionDist;
        double[] roots = MathUtil.quadraticRoots( a, b, c );
        double t = roots[0] >= 0 ? roots[0] : roots[1];
        if ( t < 0 || Double.isNaN( t ) || Double.isInfinite( t ) ) {
            energy = 0;
        }
        else {
            double v = electron.getVelocityPrev().magnitude() + electron.getAcceleration().magnitude() * t;
            energy = DischargeLampsConfig.PIXELS_PER_NM * DischargeLampsConfig.PIXELS_PER_NM * v * v * electron.getMass() / 2 * PhysicsUtil.EV_PER_JOULE;
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
            electron.getVelocity().rotate( theta );
        }
    }

}
