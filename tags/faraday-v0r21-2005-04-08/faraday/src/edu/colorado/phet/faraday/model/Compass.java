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

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.faraday.util.Vector2D;

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
    private static final double DAMPING = 0.05;
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
    private double _theta;
    
    // Angular velocity, the change in angle over time.
    private double _omega;
    
    // Angular accelaration, the change in angular velocity over time.
    private double _alpha; 
    
    // A reusable point.
    private Point2D _point;
    
    // A reusable vector
    private Vector2D _emf;
    
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
        
        _point = new Point2D.Double();
        _emf = new Vector2D();
        
        Vector2D fieldStrength = _magnetModel.getStrength( getLocation() );
        setDirection( fieldStrength.getAngle() );
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
        if ( _rotationalKinematicsEnabled ) {
            _omega = 0.03; // adjust as needed for desired behavior
        }
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
            
            getLocation( _point /* output */ );
            _magnetModel.getStrength( _point, _emf /* output */ );
            
            if ( _emf.getMagnitude() == 0 ) {
                // Do nothing if there is no magnetic field, direction should remain unchanged.
            }
            else if ( ! _rotationalKinematicsEnabled ) {
                // If rotational kinematics is disabled, rotate the needle quickly.
                _theta = getDirection() + ( ( _emf.getAngle() - getDirection() ) / 3 );
                _omega = 0;
                _alpha = 0;
                setDirection( _theta );
            }
            else {
                // If rotational kinematics is enabled, use Verlet algorithm.
                double magnitude = _emf.getMagnitude();
                double angle = _emf.getAngle();

                // Difference between the field angle and the compass angle.
                double phi = ( ( magnitude == 0 ) ? 0.0 : ( angle - _theta ) );

                if ( Math.abs( phi ) < THRESHOLD ) {
                    // When the difference between the field angle and the compass angle is insignificant,
                    // simply set the angle and consider the compass to be at rest.
                    _theta = angle;
                    _omega = 0;
                    _alpha = 0;
                    setDirection( _theta );
                }
                else {
                    // Use the Verlet algorithm to compute angle, angular velocity, and angular acceleration.

                    // Step 1: orientation
                    double thetaOld = _theta;
                    double alphaTemp = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * _omega );
                    _theta = _theta + ( _omega * dt ) + ( 0.5 * alphaTemp * dt * dt );
                    if ( _theta != thetaOld ) {
                        // Set the compass needle direction.
                        setDirection( _theta );
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
