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
 * with respect to force, friction, inertia, etc. Instead of jumping to an
 * orientation, the needle will overshoot, then gradually reach equillibrium.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Compass extends AbstractCompass {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double SENSITIVITY = 0.001;
    private static final double DAMPING = 0.2;

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
    
    /**
     * The compass needle's behavior is based on a Verlet algorithm.
     * The algorithm was reused from edu.colorado.phet.microwave.model.WaterMolecule
     * in Ron LeMaster's "microwaves" simulation, with some minor changes.
     * The algorithm was verified by Mike Dubson.
     * 
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {

        // Field magnitude at the compass location.
        AbstractVector2D emf = getFieldStrength();
        double magnitude = emf.getMagnitude();
        
        // Difference between the field angle and the compass angle.
        double phi = (( magnitude == 0 ) ? 0.0 : ( emf.getAngle() - _theta ));
        
        // Step 1: orientation
        double thetaOld = _theta;
        double alphaTemp = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * _omega );
        _theta = _theta + ( _omega * dt ) + ( 0.5 * alphaTemp * dt * dt  );
        if ( _theta != thetaOld ) {
            // Set the compass needle direction.
            setDirection( Math.toDegrees( _theta ) );
        }
        
        // Step 2: angular accelaration
        double omegaTemp = _omega + ( alphaTemp * dt );
        _alpha = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * omegaTemp );
            
        // Step 3: angular velocity
        _omega = _omega + ( 0.5 * ( _alpha + alphaTemp ) * dt );
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
