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
 * Several types of compass behavior can be specified using setBehavior.
 * In the case of KINEMATIC_BEHAVIOR, the compass needle attempts to be 
 * physically accurate with respect to force, friction, inertia, etc. 
 * Instead of jumping to an orientation, the needle will overshoot, 
 * then gradually reach equillibrium.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Compass extends SpacialObservable implements ModelElement, SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Public interface for specifying behavior.
    public static final int SIMPLE_BEHAVIOR = 0; // see SimpleBehavior
    public static final int INCREMENTAL_BEHAVIOR = 1; // see IncrementalBehavior
    public static final int KINEMATIC_BEHAVIOR = 2; // see KinematicBehavior

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Magnet that the compass is observing.
    private AbstractMagnet _magnetModel;
    // Whether the compass is enabled.
    private boolean _enabled;
    // The rotation behavior.
    private IBehavior _behavior;
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
        _behavior = new SimpleBehavior( this );
        
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
     * Sets the compass behavior.
     * 
     * @param behavior SIMPLE, INCREMENTAL or KINEMATIC
     * @throws IllegalArgumentException if rotationStrategy is invalid
     */
    public void setBehavior( int behavior ) {
        switch ( behavior ) {
        case SIMPLE_BEHAVIOR:
            _behavior = new SimpleBehavior( this );
            break;
        case INCREMENTAL_BEHAVIOR:
            _behavior = new IncrementalBehavior( this );
            break;
        case KINEMATIC_BEHAVIOR:
            _behavior = new KinematicBehavior( this );
            break;
        default:
            throw new IllegalArgumentException( "invalid behavior requested: " + behavior );
        }
        // No need to notify observers, handled by stepInTime.
    }
    
    /**
     * Workaround to get the compass moving immediately.
     * In some situations, such as when the magnet polarity is flipped,
     * it can take quite awhile for the magnet to start moving.
     */
    public void startMovingNow() {
        _behavior.startMovingNow();
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
            if ( _someVector.getMagnitude() != 0 ) {
                _behavior.setDirection( _someVector, dt );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Behaviors
    //----------------------------------------------------------------------------
    
    /**
     * IBehavior is the interface implemented by all compass behaviors.
     */
    private interface IBehavior {
        /*
         * Sets the compass needle direction.
         * 
         * @param fieldVector the B-field vector at the compass location
         * @param dt time step, in simulation clock ticks
         */
        public void setDirection( Vector2D fieldVector, double dt );
        
        /*
         * Starts the compass needle moving immediately.
         */
        public void startMovingNow();
    }
    
    /**
     * AbstractBehavior contains a base implementation shared by all behaviors.
     */
    private static abstract class AbstractBehavior implements IBehavior {
        
        private Compass _compassModel;
        
        public AbstractBehavior( Compass compassModel ) {
            super();
            _compassModel = compassModel;
        }
        
        public Compass getCompass() {
            return _compassModel;
        }
        
        public abstract void setDirection( Vector2D fieldVector, double dt );
        
        public void startMovingNow() { }
    }
    
    /**
     * SimpleBehavior tracks the B-field exactly.
     */
    private static class SimpleBehavior extends AbstractBehavior {
        
        public SimpleBehavior( Compass compassModel ) {
            super( compassModel );
        }

        public void setDirection( Vector2D fieldVector, double dt ) {
            getCompass().setDirection( fieldVector.getAngle() );
        }
    }
    
    /**
     * IncrementalBehavior tracks the B-field exactly, except when
     * the delta angle exceeds some threshold.  When the threshold is exceeded,
     * the needle angle changes incrementally over time.
     */
    private static class IncrementalBehavior extends AbstractBehavior {
        
        private static final double MAX_INCREMENT = Math.toRadians( 45 );
        
        public IncrementalBehavior( Compass compassModel ) {
            super( compassModel );
        }
          
        public void setDirection( Vector2D fieldVector, double dt ) {
            
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
            double needleAngle = getCompass().getDirection();
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
            
            if ( Math.abs( delta ) < MAX_INCREMENT ) {
                // If the delta is small, perform simple rotation.
                getCompass().setDirection( fieldAngle );
            }
            else {
                // If the delta is large, rotate incrementally.
                int sign = ( delta < 0 ) ? -1 : 1;
                delta = sign * MAX_INCREMENT;
                getCompass().setDirection( needleAngle + delta );
            }
        }
    }
    
    /**
     * KinematicBehavior rotates the compass needle using the Verlet algorithm
     * to mimic rotational kinematics.  The needle must overcome inertia, and it has 
     * angular velocity and angular acceleration. This causes the needle to accelerate
     * at it starts to move, and to wobble as it comes to rest.
     */
    private static class KinematicBehavior extends AbstractBehavior {
        
        /* Change these at your peril. */
        private static final double SENSITIVITY = 0.001;
        private static final double DAMPING = 0.05;
        private static final double THRESHOLD = Math.toRadians( 0.2 );
        
        // Angle of needle orientation (in radians)
        private double _theta;
        // Angular velocity, the change in angle over time.
        private double _omega;
        // Angular accelaration, the change in angular velocity over time.
        private double _alpha; 
        
        public KinematicBehavior( Compass compassModel ) {
            super( compassModel );
            _theta = _omega = _alpha = 0.0;
        }

        public void setDirection( Vector2D fieldVector, double dt ) {

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
                getCompass().setDirection( _theta );
            }
            else {
                // Use the Verlet algorithm to compute angle, angular velocity, and angular acceleration.

                // Step 1: orientation
                double thetaOld = _theta;
                double alphaTemp = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * _omega );
                _theta = _theta + ( _omega * dt ) + ( 0.5 * alphaTemp * dt * dt );
                if ( _theta != thetaOld ) {
                    // Set the compass needle direction.
                    getCompass().setDirection( _theta );
                }

                // Step 2: angular accelaration
                double omegaTemp = _omega + ( alphaTemp * dt );
                _alpha = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * omegaTemp );

                // Step 3: angular velocity
                _omega = _omega + ( 0.5 * ( _alpha + alphaTemp ) * dt );
            }
        }
      
        /**
         * Workaround to get the compass moving immediately.
         * In some situations, such as when the magnet polarity is flipped,
         * it can take quite awhile for the magnet to start moving.
         * So we give the compass needle a small amount of 
         * angular velocity to get it going.
         */
        public void startMovingNow() {
            _omega = 0.03; // adjust as needed for desired behavior
        }
    }
}
