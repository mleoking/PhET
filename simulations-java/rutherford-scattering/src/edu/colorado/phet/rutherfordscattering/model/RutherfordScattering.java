/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.Dimension;
import java.text.DecimalFormat;

import edu.colorado.phet.rutherfordscattering.RSConstants;

/**
 * RutherfordScattering is the algorthm for computing the alpha particle trajectories
 * for the Rutherford Atom model.
 * <p>
 * This algorithm was specified by Sam McKagan.
 * See the files doc/trajectories.pdf and doc/rutherford-scattering.pdf for details.
 * (Those documents are not necessarily up to date.)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RutherfordScattering {

    // Prints debugging output
    public static boolean DEBUG_OUTPUT_ENABLED = true;
    
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
     * @param dt the time step
     * @param alphaParticle the alpha particle
     * @param atom the atom
     * @param boxSize the size of the box that alpha particles are moving in
     */
    public static void moveParticle( final double dt, AlphaParticle alphaParticle, RutherfordAtom atom, Dimension boxSize ) {

        assert( dt > 0 );
        assert( boxSize.getWidth() == boxSize.getHeight() ); // box must be square!

        // particle properties
        double x = alphaParticle.getX();
        double y = alphaParticle.getY();
        final double s = alphaParticle.getSpeed();
        final double s0 = alphaParticle.getInitialSpeed();
        final double sd = alphaParticle.getDefaultSpeed();
        
        // atom properties
        final int p = atom.getNumberOfProtons();
        final int pd = atom.getDefaultNumberOfProtons();
        
        // Calculate D
        final double L = boxSize.getWidth();
        final double D = ( L / 16 ) * ( p / (double)pd ) * ( ( sd * sd ) / ( s0 * s0 ) );
        
        // Alpha particle's initial position, relative to the atom's center.
        double x0 = Math.abs( alphaParticle.getInitialPosition().getX() - atom.getX() );
        if ( x0 == 0 ) {
            x0 = X_MIN; // algorithm fails for x0=0
        }
        assert( x0 > 0 );
        double y0 = alphaParticle.getInitialPosition().getY() - atom.getY();
        y0 *= -1; // flip y0 sign from model to algorithm

        // b, horizontal distance to atom's center at y == negative infinity
        final double b1 = Math.sqrt( ( x0 * x0 ) + ( y0 * y0 ) );
        final double b = 0.5 * ( x0 + Math.sqrt( ( -2 * D * b1 ) - ( 2 * D * y0 ) + ( x0 * x0 ) ) );

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
        final double phiNew = phi + ( ( b * b * s * dt ) / ( r * Math.sqrt( Math.pow( b, 4 ) + ( r * r * t1 * t1 ) ) ) );
        final double rNew = Math.abs( ( b * b ) / ( ( b * Math.sin( phiNew ) ) + ( ( D / 2 ) * ( Math.cos( phiNew ) - 1 ) ) ) );
        double sNew = s0 * Math.sqrt( 1 - ( D / rNew ) );
        
        // convert new position to Cartesian coordinates
        double xNew = rNew * Math.sin( phiNew );
        double yNew = -rNew * Math.cos( phiNew );
        
        // Adjust the sign of x.
        xNew *= sign;
        
        // flip y sign from algorithm to model
        yNew *= -1;
        
        // adjust for atom position
        xNew += atom.getX();
        yNew += atom.getY();
        
        // Handle algoritm failures gracefully.
        {
            boolean error = false;
            
            if ( !( b > 0 ) ) {
                System.err.println( "ERROR: b=" + b );
                error = true;
            }
            
            if ( !( sNew > 0 ) ) {
                System.err.println( "ERROR: newSpeed=" + sNew + ", reverting to " + s );
                sNew = s;
                error = true;
            }
            
            // Debugging output, in coordinates relative to atom's center
            if ( DEBUG_OUTPUT_ENABLED && error ) {
                System.err.println( "RutherfordScattering.moveParticle" );
                System.err.println( "  particle id=" + alphaParticle.getId() );
                System.err.println( "  constants:" );
                System.err.println( "    dt=" + F.format( dt ) );
                System.err.println( "    b=" + b );
                System.err.println( "    L=" + F.format( L ) );
                System.err.println( "    D=" + D );
                System.err.println( "    (x0,y0)=(" + F.format( x0 ) + "," + F.format( y0 ) + ")" );
                System.err.println( "    s0=" + F.format( s0 ) );
                System.err.println( "    sd=" + F.format( sd ) );
                System.err.println( "    p=" + p );
                System.err.println( "    pd=" + pd );
                System.err.println( "  current state:" );
                System.err.println( "    (x,y)=(" + F.format( x ) + "," + F.format( y ) + ")" );
                System.err.println( "    (r,phi)=(" + F.format( r ) + "," + F.format( Math.toDegrees( phi ) ) + ")" );
                System.err.println( "    s=" + F.format( s ) );
                System.err.println( "  new state:" );
                System.err.println( "    (x,y)=(" + F.format( xNew ) + "," + F.format( yNew ) + ")" );
                System.err.println( "    (r,phi)=(" + F.format( rNew ) + "," + F.format( Math.toDegrees( phiNew ) ) + ")" );
                System.err.println( "    s=" + sNew );
            }
        }
        
        alphaParticle.setPosition( xNew, yNew );
        alphaParticle.setSpeed( sNew );
        alphaParticle.setOrientation( phiNew );
    }
}