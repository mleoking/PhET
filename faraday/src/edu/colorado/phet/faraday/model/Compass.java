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
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObserver;

/**
 * Compass is the model of a compass.
 * <p>
 * The behavior of the compass needle attempts to be physically accurate
 * with respect to force, friction, inertia, etc. Instead of jumping to an
 * orientation, the needle will overshoot, then gradually reach equillibrium.
 * (This behavior can be turned on and off.)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Compass extends SpacialObservable implements ModelElement, SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double SENSITIVITY = 0.001;
    private static final double DAMPING = 0.2;
    private static final double THRESHOLD = Math.toRadians( 0.2 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Magnet that the compass is observing.
    private AbstractMagnet _magnetModel;
    
    // Whether the compass is enabled.
    private boolean _enabled;
    
    // Whether rotational kinematics behavior is enabled.
    private boolean _rotationalKinematicsEnabled;
    
    // Angle of needle orientation (in radians)
    protected double _theta;
    
    // Angular velocity, the change in angle over time.
    protected double _omega;
    
    // Angular accelaration, the change in angular velocity over time.
    protected double _alpha; 
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param magnetModel the magnet that the compass is observing
     */
    public Compass( AbstractMagnet magnetModel ) {
        super();
        
        assert( magnetModel != null );
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        _enabled = true;
        _rotationalKinematicsEnabled = false; // expensive, so disabled by default
        
        _theta = 0.0;
        _omega = 0.0;
        _alpha = 0.0;
        
        AbstractVector2D fieldStrength = _magnetModel.getStrength( getLocation() );
        setDirection( Math.toDegrees( fieldStrength.getAngle() ) );
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the strength of the magnetic field at the compass location.
     * 
     * @return the field strength vector
     */
    public AbstractVector2D getFieldStrength() {
        return _magnetModel.getStrength( getLocation() );
    }
    
    /**
     * Enables and disabled the compass.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            startMovingNow();
            notifyObservers();
        }
    }
    
    /**
     * Returns the current state of the compass.
     * 
     * @return true if enabled, false if disabled.
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
    /**
     * Enables/disabled rotational kinematics behavior.
     * This turns on a Verlet algorithm that cause the compass needle to wobble.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setRotationalKinematicsEnabled( boolean enabled ) {
        if ( enabled != _rotationalKinematicsEnabled ) {
            _rotationalKinematicsEnabled = enabled;
            // No need to notify observers, handled by stepInTime.
        }
    }
    
    /**
     * Determines whether rotational kinematics behavior is enabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isRotationalKinematicsEnabled() {
        return _rotationalKinematicsEnabled;
    }
    
    /**
     * Workaround to get the compass moving immediately.
     * In some situations, such as when the magnet polarity is flipped,
     * it can take quite awhile for the magnet to start moving.
     * <p>
     * In this case, we give the compass needle a small amount of 
     * angular velocity to get it going.
     */
    public void startMovingNow() {
          _omega = 0.03;  // adjust as needed for desired behavior
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        // Do nothing, handled by stepInTime.
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * If rotational kinematics is enabled (see setRotationalKinematicsEnabled),
     * the compass needle's behavior is based on a Verlet algorithm.
     * The algorithm was reused from edu.colorado.phet.microwave.model.WaterMolecule
     * in Ron LeMaster's "microwaves" simulation, with some minor changes.
     * The algorithm was verified by Mike Dubson.
     * 
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {

        if ( isEnabled() ) {
            
            AbstractVector2D emf = getFieldStrength();
            
            if ( ! _rotationalKinematicsEnabled ) {
                // If rotational kinematics is disabled, simply set the angle.
                setDirection( Math.toDegrees( emf.getAngle() ) );
            }
            else {
                // If rotational kinematics is enabled, use Verlet algorithm.
                double magnitude = emf.getMagnitude();
                double angle = emf.getAngle();

                // Difference between the field angle and the compass angle.
                double phi = ( ( magnitude == 0 ) ? 0.0 : ( angle - _theta ) );

                if ( Math.abs( phi ) < THRESHOLD ) {
                    // When the difference between the field angle and the compass angle is insignificant,
                    // simply set the angle and consider the compass to be at rest.
                    _theta = angle;
                    _alpha = 0;
                    _omega = 0;
                    setDirection( Math.toDegrees( _theta ) );
                }
                else {
                    // Use the Verlet algorithm to compute angle, angular velocity, and angular acceleration.

                    // Step 1: orientation
                    double thetaOld = _theta;
                    double alphaTemp = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * _omega );
                    _theta = _theta + ( _omega * dt ) + ( 0.5 * alphaTemp * dt * dt );
                    if ( _theta != thetaOld ) {
                        // Set the compass needle direction.
                        //System.out.println( "Compass.stepInTime: setDirection to " + Math.toDegrees(_theta) );
                        setDirection( Math.toDegrees( _theta ) );
                    }

                    // Step 2: angular accelaration
                    double omegaTemp = _omega + ( alphaTemp * dt );
                    _alpha = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * omegaTemp );

                    // Step 3: angular velocity
                    _omega = _omega + ( 0.5 * ( _alpha + alphaTemp ) * dt );
                }
            }
        }
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
