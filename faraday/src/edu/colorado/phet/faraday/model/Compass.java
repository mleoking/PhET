/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.math.AbstractVector2D;

/**
 * Compass is the model of a compass.
 * <p>
 * The behavior of the compass needle attempts to be physically accurate
 * with respect to friction, inertia, etc. Instead of jumping to a specific
 * direction, the needle will overshoot, then gradually reach equillibrium.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Compass extends AbstractCompass {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MOMENT_OF_INERTIA = 1.0;
    private static final double MAGNET_MOMENT = 1.0;
    private static final double DRAG_COEFFICIENT = 1.0;
    
    private static final double FRICTION = 1.0;  // (magnet moment)/(moment of inertia)
    private static final double SNAPPINESS = 1.0; // (drag coefficient)/(moment of inertia)
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Angle of needle deflection (in radians)
    protected double _theta;
    
    // Change in theta per clock (in radians per clock tick)
    protected double _angularVelocity;
    
    // Change in angular velocity per elapsed time
    protected double _angularAcceleration; 
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param magnetModel the magnet that the compass is observing
     */
    public Compass( IMagnet magnetModel ) {
        super( magnetModel );
        
        _theta = Math.toRadians( super.getDirection() );
        _angularVelocity = 0.0;
        _angularAcceleration = calcAngularAcceleration( _angularVelocity, _theta );
        
        System.out.println( "Compass.init: " + toString() );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Algorithm courtesy of Michael Dubson (dubson@spot.colorado.edu).
     * 
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {

        double previousTheta = _theta;
        
        // Step 1: theta
        _theta = _theta + (_angularVelocity * dt) + (0.5 * _angularAcceleration * Math.pow( dt, 2 ));
        
        // Step 2: angular velocity
        _angularVelocity = _angularVelocity + (_angularAcceleration * dt);
        
        // Step 3: angular accelaration
        _angularAcceleration = calcAngularAcceleration( _angularVelocity, _theta );
            
        // If the angle has changed, then set the needle direction.
        if ( _theta != previousTheta ) {
          setDirection( Math.toDegrees( _theta ) );
        }
    }
    
    /**
     * Calculates the angular acceleration.
     * This method uses two "fudge factors" -
     * the amount of friction and the "snappiness" of the needle.
     * 
     * @param angularVelocity angular velocity of the compass needle
     * @param theta angle of the compass needle, in radians
     * @return the angular acceleration of the compass needle
     */
    private double calcAngularAcceleration( double angularVelocity, double theta ) {
        AbstractVector2D B = super.getFieldStrength();
        double Bx = Math.abs( B.getX() );
        double By = Math.abs( B.getY() );
        return (SNAPPINESS * ((Math.cos(theta) * By) - (Math.sin(theta) * Bx))) - (FRICTION * angularVelocity);
    }
    
    /**
     * Calculates the angular acceleration.
     * This method uses three "fudge factors" -
     * the magnet moment, moment of intertia, and drag coefficient.
     * 
     * @param angularVelocity angular velocity of the compass needle
     * @param theta angle of the compass needle, in radians
     * @return the angular acceleration of the compass needle
     */
    private double calcAngularAcceleration2( double angularVelocity, double theta ) {
        double ux = MAGNET_MOMENT * Math.cos( theta );
        double uy = MAGNET_MOMENT * Math.sin( theta );
        double Bx = Math.abs( super.getFieldStrength().getX() );
        double By = Math.abs( super.getFieldStrength().getY() );
        double b = DRAG_COEFFICIENT;
        double w = angularVelocity;
        double I = MOMENT_OF_INERTIA;
        return ( ( (ux * By) - (uy * Bx) ) - (b * w) ) / I;
    }
    
    public static void main( String[] args ) {
        
        BarMagnet magnet = new BarMagnet();
        magnet.setLocation( 400, 400 );
        magnet.setStrength( 200 );
        magnet.setDirection( 0 );
        
        System.out.println( "magnet: " + 
                " location=" + magnet.getLocation() +
                " strength=" + magnet.getStrength() +
                " direction=" + magnet.getDirection() );
        
        Compass compass = new Compass( magnet );
        compass.setLocation( 400, 500 );
        System.out.println( "compass: " + 
                " location=" + compass.getLocation() );

        for ( int i = 0; i < 5; i++ ) {
            System.out.println( "compass: " + 
                    " direction=" + compass.getDirection() +
                    " B=" + compass.getFieldStrength().getMagnitude() +
                    " Bx=" + compass.getFieldStrength().getX() +
                    " By=" + compass.getFieldStrength().getY() +
                    " B0=" + Math.toDegrees( compass.getFieldStrength().getAngle() ) );
            compass.stepInTime( 1 );
        }
    }
    
    public String toString() {
        return super.toString() +
                " Compass.init=[ " +
                " 0=" + Math.toDegrees(_theta) + "(" + _theta + ")" +
                " w=" + _angularVelocity +
                " a=" + _angularAcceleration +
                " ]";
    }
}
