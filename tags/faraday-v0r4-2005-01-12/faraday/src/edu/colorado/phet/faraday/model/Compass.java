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
    
    private static final double SNAPPINESS = 1E-3; // (drag coefficient)/(moment of inertia)
    private static final double FRICTION = 0.2;  // (magnet moment)/(moment of inertia)

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Angle of needle orientation (in radians)
    protected double _theta;
    
    // Angular velocity, the change in angle over time.
    protected double _omega;
    
    // Angular accelaration, the change in angular velocity over time.
    protected double _alpha; 
    
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
        
        _theta = 0.0;
        _omega = 0.0;
        _alpha = 0.0;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
   
    public void stepInTime( double dt ) {
        stepInTime3( dt );
    }
    
    /**
     * Revised version of Ron's WaterMolecule algorithm.
     * 
     * @param dt
     */
    public void stepInTime3( double dt ) {

        // Constants
        final double S_C = 1E-3;
        final double S_B = S_C * 200;
        
        // Initialize local variables.
        double thetaOld = _theta;
        double omegaOld = _omega;
        double alphaOld = _alpha;
        double phi = 0.0;
        double magnitude = 0.0;
        
        // B field.
        AbstractVector2D B = getFieldStrength();
        magnitude = B.getMagnitude();
        phi = (( magnitude == 0 ) ? 0.0 : ( B.getAngle() - thetaOld ));
        
        double alpha = S_C * Math.sin( phi ) * magnitude - S_B * omegaOld;
        
        // Step 1: orientation
        _theta = ( thetaOld + ( omegaOld * dt ) + ( alpha * dt * dt / 2 ) );
        
        // Step 2: angular accelaration
        _alpha = S_C * Math.sin( phi ) * B.getMagnitude() - S_B * ( omegaOld + ( alpha * dt ) );
            
        // Step 3: angular velocity
        _omega = omegaOld + ( ( _alpha + alpha ) / 2 ) * dt;
        
        // If the angle has changed, then set the needle direction.
        if ( _theta != thetaOld ) {
          setDirection( Math.toDegrees( _theta ) );
        }
    }
    
    /**
     * Algorithm cannibalized from edu.colorado.phet.microwave.model.WaterMolecule
     * in Ron LeMaster's "microwaves" simulation.
     * 
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime2( double dt ) {

        // Constants
        final double S_C = 1E-3;
        final double S_B = S_C * 200;
        
        // Initialize local variables.
        double thetaOld = _theta;
        double omegaOld = _omega;
        double alphaOld = _alpha;
        double phi = 0.0;
        double magnitude = 0.0;
        
        // B field.
        AbstractVector2D B = getFieldStrength();
        magnitude = B.getMagnitude();
        phi = (( magnitude == 0 ) ? 0.0 : ( B.getAngle() - thetaOld ));
        
        double alpha = S_C * Math.sin( phi ) * magnitude - S_B * omegaOld;
        
        // Step 1: orientation
        _theta = ( thetaOld + ( omegaOld * dt ) + ( alpha * dt * dt / 2 ) ) % ( Math.PI * 2 );
        
        // Step 2: temporary angular velocity
        double omegaTemp = omegaOld + ( alpha * dt );
        
        // Step 3: angular accelaration
        _alpha = S_C * Math.sin( phi ) * B.getMagnitude() - S_B * omegaTemp;
            
        // Step 4: angular velocity
        _omega = omegaOld + ( ( _alpha + alpha ) / 2 ) * dt;
        
        // If the angle has changed, then set the needle direction.
        if ( _theta != thetaOld ) {
          setDirection( Math.toDegrees( _theta ) );
        }
    }
    
    /**
     * Algorithm courtesy of Michael Dubson (dubson@spot.colorado.edu).
     * 
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime1( double dt ) {

        double thetaN = _theta;
        
        // Step 1: theta
        _theta = ( _theta + ( _omega * dt ) + ( 0.5 * _alpha * dt * dt ) ) % ( Math.PI * 2 );
        
        // Step 2: angular velocity
        _omega = _omega + ( _alpha * dt );
        
        // Step 3: angular accelaration
        _alpha = calcAngularAcceleration2( _omega, _theta );
            
        // If the angle has changed, then set the needle direction.
        if ( _theta != thetaN ) {
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
    private double calcAngularAcceleration2( double angularVelocity, double theta ) {
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
    private double calcAngularAcceleration1( double angularVelocity, double theta ) {
        double ux = MAGNET_MOMENT * Math.cos( theta );
        double uy = MAGNET_MOMENT * Math.sin( theta );
        double Bx = Math.abs( super.getFieldStrength().getX() );
        double By = Math.abs( super.getFieldStrength().getY() );
        double b = DRAG_COEFFICIENT;
        double w = angularVelocity;
        double I = MOMENT_OF_INERTIA;
        return ( ( (ux * By) - (uy * Bx) ) - (b * w) ) / I;
    }
    
    //----------------------------------------------------------------------------
    // Object overrides
    //----------------------------------------------------------------------------
  
    /**
     * Provides a string representation of this object.
     * Do not write code that relies on the format of this string.
     * 
     * @return string representation
     */
    public String toString() {
        return " Compass=[" +
                "theta=" + Math.toDegrees(_theta) +
                " omega=" + _omega +
                " alpha=" + _alpha +
                super.toString() +
                "]";
    }
}
