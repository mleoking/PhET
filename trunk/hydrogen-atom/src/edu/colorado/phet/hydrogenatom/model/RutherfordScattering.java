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

import java.text.DecimalFormat;

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

    // Prints debugging output
    private static boolean _debugOutputEnabled = false;
    
    // Formatter, for debug output
    private static final DecimalFormat F = new DecimalFormat( "0.00" );
    
    /* Not intended for instantiation */
    private RutherfordScattering() {}

    /**
     * Moves an alpha particle under the influence of a hydrogen atom.
     * <p>
     * NOTE: The algorithm assumes that the atom is located at (0,0).
     * This is not the case in our model. So coordindates are adjusted 
     * as described in the comments.
     * <p>
     * NOTE: The algorithm assumes that +y is up.  Our model has +y down.
     * So we'll be adjusting the sign on y coordinates, as described
     * in the comments.
     *
     * @param atom the atom
     * @param alphaParticle the alpha particle
     * @param dt the time step
     * @param D the constant D
     */
    public static void moveParticle( AbstractHydrogenAtom atom, AlphaParticle alphaParticle, final double dt, final double D ) {

        // This algorithm assumes that alpha particles oriented parallel to the y axis.
        assert( alphaParticle.getOrientation() == Math.toRadians( -90 ) );
        
        // initial distance between particle and the y-axis
        final double b = Math.abs( alphaParticle.getInitialPosition().getX() - atom.getX() );
        // particle initial speed
        final double v0 = alphaParticle.getInitialSpeed();

        // particle's current position and speed
        double x = alphaParticle.getX();
        double y = alphaParticle.getY(); 
        double v = alphaParticle.getSpeed();
        
        // adjust for atom position
        x -= atom.getX();
        y -= atom.getY();
        
        // flip y sign from model to algorithm
        y *= -1;
        
        // convert current position to Polar coordinates, measured counterclockwise from the -y axis
        final double r = Math.sqrt( ( x * x ) + ( y * y ) );
        final double phi = Math.atan( -x / y );

        // new position (in Polar coordinates) and speed
        final double t1 = ( r / b );
        final double t2 = ( ( b * Math.cos( phi ) ) - ( ( D / 2 ) * Math.sin( phi ) ) );
        final double phiNew = phi + ( ( b * v * dt ) / ( r * Math.sqrt( ( b * b ) + ( t1 * t1 * t2 * t2 ) ) ) );
        final double rNew = Math.abs( ( b * b ) / ( ( b * Math.sin( phiNew ) ) + ( ( D / 2 ) * ( Math.cos( phiNew ) - 1 ) ) ) );
        final double vNew = v0 * Math.sqrt( 1 - ( D / rNew ) );
        
        // convert new position to Cartesian coordinates
        double xNew = rNew * Math.sin( phiNew );
        double yNew = -rNew * Math.cos( phiNew );
        
        // Debugging output, in coordinates relative to atom's center
        if ( _debugOutputEnabled ) {
            System.out.println( "RutherfordScattering.moveParticle" );
            System.out.println( "  particle id=" + alphaParticle.getId() );
            System.out.println( "  atom type=" + atom.getClass().getName() );
            System.out.println( "  constants:" );
            System.out.println( "    L=" + F.format( HAConstants.ANIMATION_BOX_SIZE.height ) );
            System.out.println( "    dt=" + F.format( dt ) );
            System.out.println( "    v0=" + F.format( v0 ) );
            System.out.println( "    b=" + F.format( b ) );
            System.out.println( "    D=" + F.format( D ) );
            System.out.println( "  current state:" );
            System.out.println( "    x=" + F.format( x ) );
            System.out.println( "    y=" + F.format( y ) );
            System.out.println( "    r=" + F.format( r ) );
            System.out.println( "    phi=" + F.format( phi ) + " (" + F.format( Math.toDegrees( phi ) ) + " degrees)" );
            System.out.println( "    v=" + F.format( v ) );
            System.out.println( "  new state:" );
            System.out.println( "    rNew=" + F.format( rNew ) );
            System.out.println( "    phiNew=" + F.format( phiNew ) + " (" + F.format( Math.toDegrees( phiNew ) ) + " degrees)" );
            System.out.println( "    xNew=" + F.format( xNew ) );
            System.out.println( "    yNew=" + F.format( yNew ) );
            System.out.println( "    vNew=" + F.format( vNew ) );
        }
        
        // adjust for atom position
        xNew += atom.getX();
        yNew += atom.getY();
        
        // flip y sign from algorithm to model
        y *= -1;
        
        alphaParticle.setPosition( xNew, yNew );
        alphaParticle.setSpeed( vNew );
    }
    
    /**
     * Enables debugging output.
     * Used by Developer Controls dialog.
     * 
     * @param enabled true or false
     */
    public static void setDebugOutputEnabled( boolean enabled ) {
        _debugOutputEnabled = enabled;
    }
    
    /**
     * Is debugging output enabled?
     * @return true or false
     */
    public static boolean isDebugOutputEnabled() {
        return _debugOutputEnabled;
    }
}
