// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.Dimension;
import java.text.DecimalFormat;

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

    // Reports algorithm failures
    private static boolean REPORT_FAILURES_VERBOSE = false;
    
    // Move problem particles outside of the box so that they'll be remove
    private static boolean CULL_PROBLEM_PARTICLES = true;
    
    // Formatter, for debug output
    private static final DecimalFormat F = new DecimalFormat( "0.00" );
    
    // algorithm fails for x=0, so use this min value
    public static final double X0_MIN = 0.00001;
    
    // Divisor for L used in the calculation of D.
    public static final double L_DIVISOR = 8;
    
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
     * (6) Depending on the parameters supplied, the algorithm will tend
     * to fail as the alpha particle's horizontal position (x) gets closer
     * to zero. So the Gun model is calibrated to fire alpha particles 
     * with some min initial x value.
     *
     * @param dt the time step
     * @param alphaParticle the alpha particle
     * @param atom the atom
     * @param boxSize the size of the box that alpha particles are moving in
     */
    public static void moveParticle( final double dt, AlphaParticle alphaParticle, RutherfordAtom atom, Dimension boxSize ) {

        assert( dt > 0 );
        assert( boxSize.getWidth() == boxSize.getHeight() ); // box must be square!
        
        //-------------------------------------------------------------------------------
        // misc constants that we'll need
        //-------------------------------------------------------------------------------
        
        final double L = boxSize.getWidth();
        
        final int p = atom.getNumberOfProtons(); // protons in the atom's nucleus
        final int pd = atom.getDefaultNumberOfProtons(); // default setting for the sim
        
        final double s = alphaParticle.getSpeed();  // particle's current speed
        final double s0 = alphaParticle.getInitialSpeed(); // speed when it left the gun
        final double sd = alphaParticle.getDefaultSpeed(); // default setting for the sim
        
        //-------------------------------------------------------------------------------
        // (x0,y0) : the alpha particle's initial position, relative to the atom's center.
        //-------------------------------------------------------------------------------
        
        double x0 = Math.abs( alphaParticle.getInitialPosition().getX() - atom.getX() );
        if ( x0 == 0 ) {
            x0 = X0_MIN; // algorithm fails for x0 < X0_MIN
        }

        double y0 = alphaParticle.getInitialPosition().getY() - atom.getY();
        y0 *= -1; // flip y0 sign from model to algorithm
        
        //-------------------------------------------------------------------------------
        // (x,y) : the alpha particle's current position, relative to the atom's center
        //-------------------------------------------------------------------------------
        
        double x = alphaParticle.getX() - atom.getX();
        boolean xWasNegative = false;
        if ( x < 0 ) {
            // This algorithm fails for x < 0, so adjust accordingly.
            x *= -1;
            xWasNegative = true;
        }
        assert ( x >= 0 );

        double y = alphaParticle.getY() - atom.getY();
        y *= -1; // flip y sign from model to algorithm

        //-------------------------------------------------------------------------------
        // calculate D
        //-------------------------------------------------------------------------------
        
        final double D = ( L / L_DIVISOR ) * ( p / (double)pd ) * ( ( sd * sd ) / ( s0 * s0 ) );
        
        //-------------------------------------------------------------------------------
        // calculate new alpha particle position, in Polar coordinates
        //-------------------------------------------------------------------------------
        
        // b, horizontal distance to atom's center at y == negative infinity
        final double b1 = Math.sqrt( ( x0 * x0 ) + ( y0 * y0 ) );
        final double b = 0.5 * ( x0 + Math.sqrt( ( -2 * D * b1 ) - ( 2 * D * y0 ) + ( x0 * x0 ) ) );
        
        // convert current position to Polar coordinates, measured counterclockwise from the -y axis
        final double r = Math.sqrt( ( x * x ) + ( y * y ) );
        final double phi = Math.atan2( x, -y );

        // new position (in Polar coordinates) and speed
        final double t1 = ( ( b * Math.cos( phi ) ) - ( ( D / 2 ) * Math.sin( phi ) ) );
        double phiNew = phi + ( ( b * b * s * dt ) / ( r * Math.sqrt( Math.pow( b, 4 ) + ( r * r * t1 * t1 ) ) ) );
        final double rNew = Math.abs( ( b * b ) / ( ( b * Math.sin( phiNew ) ) + ( ( D / 2 ) * ( Math.cos( phiNew ) - 1 ) ) ) );
        double sNew = s0 * Math.sqrt( 1 - ( D / rNew ) );
        
        //-------------------------------------------------------------------------------
        // convert to Cartesian coordinates
        //-------------------------------------------------------------------------------
        
        double xNew = rNew * Math.sin( phiNew );
        if ( xWasNegative ) {
            xNew *= -1; // restore the sign
        }
        
        double yNew = -rNew * Math.cos( phiNew );
        
        //-------------------------------------------------------------------------------
        // handle failures in the algorithm
        //-------------------------------------------------------------------------------
            
        boolean error = false;
        if ( !( b > 0 ) || !( sNew > 0 ) ) {
            System.err.println( "ERROR: RutherfordScattering.moveParticle from " + "(x,y)=" + pointToString( x, y ) + " to " + "(xNew,yNew)=" + pointToString( xNew, yNew ) + " : " + "b=" + b + " newSpeed=" + sNew );
            error = true;
        }

        // Verbose debug output, in coordinates relative to the atom's center
        if ( error && REPORT_FAILURES_VERBOSE ) {
            System.err.println( "DEBUG: RutherfordScattering.moveParticle [" );
            System.err.println( "  particle id=" + alphaParticle.getId() );
            System.err.println( "  constants:" );
            System.err.println( "    dt=" + dt );
            System.err.println( "    b=" + b );
            System.err.println( "    L=" + L );
            System.err.println( "    D=" + D );
            System.err.println( "    (x0,y0)=" + pointToString( x0, y0 ) );
            System.err.println( "    s0=" + s0 );
            System.err.println( "    sd=" + sd );
            System.err.println( "    p=" + p );
            System.err.println( "    pd=" + pd );
            System.err.println( "  current state:" );
            System.err.println( "    (x,y)=" + pointToString( x, y ) );
            System.err.println( "    (r,phi)=" + pointToString( r, Math.toDegrees( phi ) ) );
            System.err.println( "    s=" + s );
            System.err.println( "  new state:" );
            System.err.println( "    (xNew,yNew)=" + pointToString( xNew, yNew ) );
            System.err.println( "    (rNew,phiNew)=" + pointToString( rNew, Math.toDegrees( phiNew ) ) );
            System.err.println( "    sNew=" + sNew );
            System.err.println( "]" );
        }

        // Move the problem particle outside the box
        if ( error && CULL_PROBLEM_PARTICLES ) {
            xNew = 10 * L;
            yNew = 10 * L;
            sNew = s;
            phiNew = phi;
        }

        //-------------------------------------------------------------------------------
        // adjust position to be absolute
        //-------------------------------------------------------------------------------

        xNew += atom.getX();
        yNew *= -1; // flip y sign from algorithm to model
        yNew += atom.getY();

        //-------------------------------------------------------------------------------
        // set the alpha particle's new properties
        //-------------------------------------------------------------------------------

        alphaParticle.setPosition( xNew, yNew );
        alphaParticle.setSpeed( sNew );
        alphaParticle.setOrientation( phiNew );
    }
    
    /*
     * Converts a point to a String.
     * @param x
     * @param y
     * @return
     */
    private static String pointToString( double x, double y ) {
        return "(" + F.format( x ) + "," + F.format( y ) + ")";
    }
}