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
     * 
     * @param atom the atom
     * @param alphaParticle the alpha particle
     * @param dt the time step
     * @param D the constant D
     */
    public static void move( AbstractHydrogenAtom atom, AlphaParticle alphaParticle, final double dt, final double D ) {
        
        final double atomX = atom.getX();
        final double atomY = atom.getY();

        /*
         * Model has +y down, algorithm has +y up.
         * So we'll be doing some sign flipping for y coordinates, as noted.
         */
        final double x = alphaParticle.getX() - atomX;
        final double y = -( alphaParticle.getY() - atomY ); // flip y sign from model to algorithm
        
        final double b = alphaParticle.getInitialPosition().getX() - atomX;
        final double r = Math.sqrt( ( x * x ) + ( y * y ) );
        final double phi = Math.atan( -x / y );
        final double v = alphaParticle.getSpeed();
        final double v0 = alphaParticle.getInitialSpeed();

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
        
        double vNew = v0 * Math.sqrt( 1 - ( D / rNew ) );
        
        double xNew = ( rNew * Math.sin( phiNew ) ) + atomX;
        double yNew = ( -rNew * Math.cos( phiNew ) ) + atomY;
        
        alphaParticle.setPosition( xNew, -yNew );  // flip y sign from algorithm to model
        alphaParticle.setSpeed( vNew );
    }
}
