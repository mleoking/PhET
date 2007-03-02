/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.model;

import java.text.DecimalFormat;

import edu.colorado.phet.rutherfordscattering.RSConstants;

/**
 * RutherfordScattering is the algorthm for computing the alpha particle trajectories
 * for the Rutherford Scattering hydrogen atom models.
 * <p>
 * This algorithm was specified by Sam McKagan.
 * See the file doc/trajectories.pdf ("Trajectories for Rutherford Scattering").
 * The design specification (doc/rutherford-scattering.pdf) also contains some
 * details about how to calculate D.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RutherfordScattering {

    // Prints debugging output
    public static boolean DEBUG_OUTPUT_ENABLED = false;
    
    // Formatter, for debug output
    private static final DecimalFormat F = new DecimalFormat( "0.00" );
    
    // Value of x used when x==0 (this algorithm fails when x==0)
    public static final double X_MIN = 0.01;
    
    /* Not intended for instantiation */
    private RutherfordScattering() {}
    
    /**
     * Moves an alpha particle under the influence of a hydrogen atom.
     * <p>
     * ASSUMPTIONS MADE IN THIS ALGORITHM: 
     * (1) The atom is located at (0,0).
     * This is not the case in our model. So coordindates are adjusted 
     * as described in the comments.
     * (2) +y is up.
     * Our model has +y down. So we'll be adjusting the sign on y 
     * coordinates, as described in the comments.
     * (3) alpha particles are moving from bottom to top
     * (4) x values are positive.
     * The algoritm fails for negative values of x. This is not
     * mentioned in the specification document. So we have to convert
     * to positive values of x, then convert back.
     * (5) Using "phi=arctan(-x,y)" as described in the spec causes
     * particles to jump discontinuously when they go above the y axis.
     * This is fixed by using Math.atan2 instead.
     *
     * @param atom the atom
     * @param alphaParticle the alpha particle
     * @param dt the time step
     * @param D the constant D
     */
    public static void moveParticle( RutherfordAtomModel atom, AlphaParticle alphaParticle, final double dt ) {

        assert( dt > 0 );

        final double D = getD( atom, alphaParticle );
        
        // Alpha particle's initial position, relative to the atom's center.
        final double x0 = getX0( atom, alphaParticle );
        assert( x0 > 0 );
        double y0 = alphaParticle.getInitialPosition().getY() - atom.getY();
        y0 *= -1; // flip y0 sign from model to algorithm

        // b, horizontal distance to atom's center at y == negative infinity
        final double b1 = Math.sqrt( ( x0 * x0 ) + ( y0 * y0 ) );
        final double b = 0.5 * ( x0 + Math.sqrt( ( -2 * D * b1 ) - ( 2 * D * y0 ) + ( x0 * x0 ) ) );
        assert ( b > 0 );

        // particle's current position and speed
        double x = alphaParticle.getX();
        double y = alphaParticle.getY();
        final double v = alphaParticle.getSpeed();
        final double v0 = alphaParticle.getInitialSpeed();
        
        // adjust for atom position
        x -= atom.getX();
        y -= atom.getY();
        
        // This algorithm fails for x < 0, so adjust accordingly.
        int sign = 1;
        if ( x < 0 ) {
            x *= -1;
            sign = -1;
        }
        assert( x >= 0 );
        
        // flip y sign from model to algorithm
        y *= -1;
        
        // convert current position to Polar coordinates, measured counterclockwise from the -y axis
        final double r = Math.sqrt( ( x * x ) + ( y * y ) );
        final double phi = Math.atan2( x, -y );

        // new position (in Polar coordinates) and speed
        final double t1 = ( ( b * Math.cos( phi ) ) - ( ( D / 2 ) * Math.sin( phi ) ) );
        final double phiNew = phi + ( ( b * b * v * dt ) / ( r * Math.sqrt( Math.pow( b, 4 ) + ( r * r * t1 * t1 ) ) ) );
        final double rNew = Math.abs( ( b * b ) / ( ( b * Math.sin( phiNew ) ) + ( ( D / 2 ) * ( Math.cos( phiNew ) - 1 ) ) ) );
        final double vNew = v0 * Math.sqrt( 1 - ( D / rNew ) );
        
        // convert new position to Cartesian coordinates
        double xNew = rNew * Math.sin( phiNew );
        double yNew = -rNew * Math.cos( phiNew );
        
        // Debugging output, in coordinates relative to atom's center
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "RutherfordScattering.moveParticle" );
            System.out.println( "  particle id=" + alphaParticle.getId() );
            System.out.println( "  atom type=" + atom.getClass().getName() );
            System.out.println( "  constants:" );
            System.out.println( "    L=" + F.format( RSConstants.ANIMATION_BOX_SIZE.height ) );
            System.out.println( "    D=" + F.format( D ) );
            System.out.println( "    dt=" + F.format( dt ) );
            System.out.println( "    (x0,y0)=(" + F.format( x0 ) + "," + F.format( y0 ) + ")" );
            System.out.println( "    v0=" + F.format( v0 ) );
            System.out.println( "    b=" + F.format( b ) );
            System.out.println( "  current state:" );
            System.out.println( "    (x,y)=(" + F.format( x ) + "," + F.format( y ) + ")" );
            System.out.println( "    (r,phi)=(" + F.format( r ) + "," + F.format( Math.toDegrees( phi ) ) + ")" );
            System.out.println( "    v=" + F.format( v ) );
            System.out.println( "  new state:" );
            System.out.println( "    (x,y)=(" + F.format( xNew ) + "," + F.format( yNew ) + ")" );
            System.out.println( "    (r,phi)=(" + F.format( rNew ) + "," + F.format( Math.toDegrees( phiNew ) ) + ")" );
            System.out.println( "    v=" + F.format( vNew ) );
        }
        
        // Adjust the sign of x.
        xNew *= sign;
        
        // flip y sign from algorithm to model
        yNew *= -1;
        
        // adjust for atom position
        xNew += atom.getX();
        yNew += atom.getY();
        
        alphaParticle.setPosition( xNew, yNew );
        alphaParticle.setSpeed( vNew );
        alphaParticle.setOrientation( phiNew );
    }

    /*
     * Gets the value x0.
     * This value must be > 0, and is adjusted accordingly.
     * 
     * @param atom
     * @param alphaParticle
     * @return
     */
    private static double getX0( AbstractHydrogenAtom atom, AlphaParticle alphaParticle ) {
        double x0 = Math.abs( alphaParticle.getInitialPosition().getX() - atom.getX() );
        if ( x0 == 0 ) {
            x0 = X_MIN;
        }
        return x0;
    }
    
    /*
     * Gets the constant D.
     * 
     * @param alphaParticle
     * @return double
     */
    private static double getD( RutherfordAtomModel atom, AlphaParticle alphaParticle ) {
        final double L = RSConstants.ANIMATION_BOX_SIZE.height;
        final double D = L / 16;
        return D;
    }
}
