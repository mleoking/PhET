/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import edu.colorado.phet.hydrogenatom.HAConstants;

/**
 * RutherfordScattering is the algorthm for computing the alpha particle trajectories
 * for Plum Pudding, Bohr, deBroglie and Schrodinger hydrogen atom models.
 * The only difference between models is the value of the constant D.
 * <p>
 * This algorithm was specified by Sam McKagan.
 * See the file data/Rutherford_Scattering.pdf ("Trajectories for Rutherford Scattering"). 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RutherfordScattering {

    private RutherfordScattering() {}
    
    /**
     * Moves an alpha particle under the influence of a hydrogen atom.
     * <p>
     * NOTE: Our model has +y down, and this algorithm has +y up.
     * So we'll be flipping the sign on the y coordinate (see inline comments).
     *
     * @param atom the atom
     * @param alphaParticle the alpha particle
     * @param dt the time step
     * @param D the constant D
     */
    public static void move( AbstractHydrogenAtom atom, AlphaParticle alphaParticle, final double dt, final double D ) {
        
        // initial distance between alpha particle and the y-axis
        final double b = Math.abs( alphaParticle.getInitialPosition().getX() - atom.getX() );
        // alpha particle speed
        final double v = alphaParticle.getSpeed();
        // alpha particle initial speed
        final double v0 = alphaParticle.getInitialSpeed();

        // alpha particle's current position, adjusted for atom position
        final double x = alphaParticle.getX() - atom.getX();
        final double y = -( alphaParticle.getY() - atom.getY() ); // flip y sign from model to algorithm
        
        // convert current position to Polar coordinates
        final double r = Math.sqrt( ( x * x ) + ( y * y ) );
        final double phi = Math.atan( -x / y );
        System.out.println( "current: (" + x + "," + y + ") (" + r + "," + Math.toDegrees(phi) + ")" );//XXX

        // new position in Polar coordinates
        double phiNew = 0;
        {
            double t1 = ( b * v * dt );
            double t2 = ( ( b * Math.cos( phi ) ) - ( ( D / 2 ) * Math.sin( phi ) ) );
            double t3 = ( r * r * t2 * t2 );
            phiNew = phi + ( t1 / ( r * Math.sqrt( b + t3 ) ));
        }
        double rNew = 0;
        {
            double t1 = ( b * Math.sin( phiNew ) );
            double t2 = ( ( D / 2 ) * ( Math.cos( phiNew ) - 1 ) );
            rNew = ( b * b ) / ( t1 + t2 );
        }
        
        // convert new position to Cartesian coordinates, adjusted for atom position
        double xNew = ( rNew * Math.sin( phiNew ) ) + atom.getX();
        double yNew = ( -rNew * Math.cos( phiNew ) ) + atom.getY();
        System.out.println( "new: (" + xNew + "," + yNew + ") (" + rNew + "," + Math.toDegrees(phiNew) + ")" );//XXX

        // new velocity
        double vNew = v0 * Math.sqrt( 1 - ( D / rNew ) );
        
        alphaParticle.setPosition( xNew, -yNew ); // flip y sign from algorithm to model
        alphaParticle.setSpeed( vNew );
    }
}
