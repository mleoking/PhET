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
        _angularAcceleration = 0.0;
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
        _theta = _theta + ( _angularVelocity * dt ) + ( 0.5 * _angularAcceleration * Math.pow( dt, 2 ) );
        
        // Step 2: angular velocity
        _angularVelocity = _angularVelocity + ( _angularAcceleration * dt );
        
        // Step 3: angular accelaration
        double ux = MAGNET_MOMENT * Math.cos( _theta );
        double uy = MAGNET_MOMENT * Math.sin( _theta );
        double Bx = super.getFieldStrength().getX();
        double By = super.getFieldStrength().getY();
        double b = DRAG_COEFFICIENT;
        double w = _angularAcceleration;
        double I = MOMENT_OF_INERTIA;
        _angularAcceleration = ( ( (ux * By) - (uy * Bx) ) - (b * w) ) / I;
            
        // If the angle has changed, then set the needle direction.
        if ( _theta != previousTheta ) {
          setDirection( Math.toDegrees( _theta ) );
        }
    }
}
