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
    
    // Rotation strategies
    public static final int ROTATE_SIMPLE = 0;  // follow B-field exactly
    public static final int ROTATE_INCREMENTAL = 1; // rotate large changes incrementally
    public static final int ROTATE_KINEMATIC = 2; // mimic rotational kinematics

    
    private static final double SENSITIVITY = 0.001;
    private static final double DAMPING = 0.05;
    private static final double THRESHOLD = Math.toRadians( 0.2 );
    
    private static final double MAX_ROTATION_ANGLE = Math.toRadians( 45 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Magnet that the compass is observing.
    private AbstractMagnet _magnetModel;
    
    // Whether the compass is enabled.
    private boolean _enabled;
    
    // The animation strategy.
    private int _rotationStrategy;
    
    // Angle of needle orientation (in radians)
    private double _theta;
    
    // Angular velocity, the change in angle over time.
    private double _omega;
    
    // Angular accelaration, the change in angular velocity over time.
    private double _alpha; 
    
    // A reusable point.
    private Point2D _somePoint;
    
    // A reusable vector
    private Vector2D _someVector;
    
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
        _rotationStrategy = ROTATE_SIMPLE;
        _theta = _omega = _alpha = 0.0;
        
        _somePoint = new Point2D.Double();
        _someVector = new Vector2D();
        
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
     * @param rs the rotation strategy, one of the ROTATE_* constants
     */
    public void setRotationStrategy( int rs ) {
        assert( rs == ROTATE_SIMPLE || rs == ROTATE_INCREMENTAL || rs == ROTATE_KINEMATIC );
        if ( rs != _rotationStrategy ) {
            _rotationStrategy = rs;
            _theta = _omega = _alpha = 0.0;
            // No need to notify observers, handled by stepInTime.
        }
    }
    
    /**
     * Gets the rotation strategy.
     * 
     * @return one of the ROTATE_* constants
     */
    public int getRotationStrategy() {
        return _rotationStrategy;
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
        if ( _rotationStrategy == ROTATE_KINEMATIC ) {
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
            
            getLocation( _somePoint /* output */ );
            _magnetModel.getStrength( _somePoint, _someVector /* output */ );
            
            if ( _someVector.getMagnitude() == 0 ) {
                // Do nothing if there is no magnetic field, direction should remain unchanged.
            }
            else {
                switch ( _rotationStrategy ) {
                case ROTATE_KINEMATIC:
                    rotateKinematic( _someVector, dt );
                    break;
                case ROTATE_SIMPLE:
                    rotateSimple( _someVector );
                    break;
                case ROTATE_INCREMENTAL:
                    rotateIncremental( _someVector );
                    break;
                default:
                    throw new IllegalStateException( "invalid rotation strategy: " + _rotationStrategy );
                }
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Rotation strategies
    //----------------------------------------------------------------------------
    
    /**
     * Rotates the compass needle to be exactly aligned with the B-field.
     * 
     * @param fieldVector the B-field vector at the compass location
     */
    private void rotateSimple( Vector2D fieldVector ) {
        setDirection( fieldVector.getAngle() );
    }
    
    /**
     * Rotates the compass needle incrementally when the change in angle 
     * exceeds some threshold.  When the threshold is not exceeded, 
     * rotates the needle to be exactly aligned with the B-field.
     * 
     * @param fieldVector the B-field vector at the compass location
     */   
    private void rotateIncremental( Vector2D fieldVector ) {
        
        // Normalize the field angle to the range 0-355 degrees.
        double fieldAngle = fieldVector.getAngle();
        {
            int sign = ( fieldAngle < 0 ) ? -1 : +1;
            fieldAngle = sign * ( Math.abs( fieldAngle ) % ( 2 * Math.PI ) );
            if ( fieldAngle < 0 ) {
                fieldAngle += ( 2 * Math.PI );
            }  
        }
        
        // Normalize the needle angle to the range 0-355 degrees.
        double needleAngle = getDirection();
        {
            int sign = ( needleAngle < 0 ) ? -1 : +1;
            needleAngle = sign * ( Math.abs( needleAngle ) % ( 2 * Math.PI ) );
            if ( needleAngle < 0 ) {
                needleAngle += ( 2 * Math.PI );
            }  
        }
        
        // Find the smallest delta angle between the field vector and the needle.
        double delta = fieldAngle - needleAngle;
        if ( delta > Math.PI ) {
            delta = delta - ( 2 * Math.PI );
        }
        else if ( delta < -Math.PI ) {
            delta = delta + ( 2 * Math.PI );
        }
        
        if ( Math.abs( delta ) < MAX_ROTATION_ANGLE ) {
            // If the delta is small, perform simple rotation.
            setDirection( fieldAngle );
        }
        else {
            // If the delta is large, rotate incrementally.
            int sign = ( delta < 0 ) ? -1 : 1;
            delta = sign * MAX_ROTATION_ANGLE;
            setDirection( needleAngle + delta );
        }
    }
    
    /**
     * Rotates the compass needle using the Verlet algorithm to mimic 
     * rotational kinematics.  The needle must overcome inertia,
     * and it has angular velocity and angular acceleration.
     * This causes the needle to accelerate at it starts to move,
     * and to wobble as it comes to rest.
     * 
     * @param fieldVector the B-field vector at the compass location
     * @param dt time step, in simulation clock ticks
     */
    private void rotateKinematic( Vector2D fieldVector, double dt ) {
        // Use Verlet algorithm to make the compass needle "wobble".
        double magnitude = fieldVector.getMagnitude();
        double angle = fieldVector.getAngle();

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
                "enabled=" + _enabled +
                "rotationStrategy=" + _rotationStrategy +
                "theta=" + Math.toDegrees( _theta ) +
                " omega=" + _omega +
                " alpha=" + _alpha +
                super.toString() +
                "]";
    }
}
