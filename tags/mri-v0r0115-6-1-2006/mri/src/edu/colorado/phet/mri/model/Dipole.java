/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.quantum.model.Photon;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Random;

/**
 * Dipole
 * <p/>
 * The SimpleObservable/SimpleObserver mechanism is used to notify objects that want to know about
 * things that change with the frequency of stepInTime(). A change listener mechanism is used to
 * communicate with object that are interested in other state changes.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Dipole extends Body implements Collidable {

    //----------------------------------------------------------------
    // Class field and methods
    //----------------------------------------------------------------
    public static final double RADIUS = 30;

    private static EventChannel classEventChannel = new EventChannel( ClassListener.class );
    private static ClassListener classListenerProxy = (ClassListener)classEventChannel.getListenerProxy();

    public static void addClassListener( ClassListener listener ) {
        classEventChannel.addListener( listener );
    }

    public static void removeClassListener( ClassListener listener ) {
        classEventChannel.removeListener( listener );
    }

    public interface ClassListener extends EventListener {
        void instanceCreated( Dipole dipole );

        void instanceDestroyed( Dipole dipole );
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    // Range of precession, in radians
    private double precession = MriConfig.InitialConditions.DIPOLE_PRECESSION;
    private Spin spin;
    private double orientation;
    private CollidableAdapter collidableAdapter;
    private IOrientationBehavior orientationBehavior;
    private double oldOrientation;

    public Dipole() {
        collidableAdapter = new CollidableAdapter( this );
        orientationBehavior = new KinematicBehavior( this );

        classListenerProxy.instanceCreated( this );
    }

    public void stepInTime( double dt ) {
        double baseOrientation = ( spin == Spin.DOWN ? 1 : -1 ) * Math.PI / 2;
        if( oldOrientation != baseOrientation ) {
            orientationBehavior.startMovingNow();
            oldOrientation = baseOrientation;
        }
        Vector2D vect = new Vector2D.Double( 1, 0 ).rotate( baseOrientation );
        orientationBehavior.setOrientation( vect, dt );
        notifyObservers();
    }

    public double getOrientation() {
        return orientation;
    }

    private void setOrientation( double orientation ) {
        this.orientation = orientation;
    }

    public Spin getSpin() {
        return spin;
    }

    public void setSpin( Spin spin ) {
        this.spin = spin;
        changeListenerProxy.spinChanged( new ChangeEvent( this ) );
    }

    public void flip() {
        Spin newSpin = getSpin() == Spin.UP ? Spin.DOWN : Spin.UP;
        setSpin( newSpin );
    }

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public double getRadius() {
        return RADIUS;
    }

    public void setVelocity( double vx, double vy ) {
        collidableAdapter.updateVelocity();
        super.setVelocity( vx, vy );
    }

    public void setPosition( double x, double y ) {
        collidableAdapter.updatePosition();
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        collidableAdapter.updatePosition();
        super.setPosition( position );
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public void collideWithPhoton( Photon photon ) {
        setSpin( Spin.UP );
//        notifyObservers();
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Dipole source ) {
            super( source );
        }

        public Dipole getDipole() {
            return (Dipole)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        public void spinChanged( ChangeEvent event );
    }

    //----------------------------------------------------------------------------
    // Behaviors
    //----------------------------------------------------------------------------

    /**
     * IBehavior is the interface implemented by all Dipole behaviors.
     */
    private interface IOrientationBehavior {
        /*
         * Sets the Dipole needle direction.
         *
         * @param fieldVector the B-field vector at the Dipole location
         * @param dt time step, in simulation clock ticks
         */
        public void setOrientation( Vector2D fieldVector, double dt );

        /*
         * Starts the Dipole needle moving immediately.
         */
        public void startMovingNow();
    }

    /**
     * AbstractBehavior contains a base implementation shared by all behaviors.
     */
    private static abstract class AbstractBehavior implements IOrientationBehavior {

        private Dipole _compassModel;

        public AbstractBehavior( Dipole compassModel ) {
            super();
            _compassModel = compassModel;
        }

        public Dipole getCompass() {
            return _compassModel;
        }

        public abstract void setOrientation( Vector2D fieldVector, double dt );

        public void startMovingNow() {
        }
    }

    /**
     * SimpleBehavior tracks the B-field exactly.
     */
    private static class SimpleBehavior extends AbstractBehavior {

        public SimpleBehavior( Dipole compassModel ) {
            super( compassModel );
        }

        public void setOrientation( Vector2D fieldVector, double dt ) {
            getCompass().setOrientation( fieldVector.getAngle() );
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
        public static double SENSITIVITY = 0.003;
        public static double DAMPING = 0.035;
        private static final double THRESHOLD = Math.toRadians( 0.2 );
        // Used to randomize the direction that the dipole rotates
        private static Random random = new Random();

        // Angle of needle orientation (in radians)
        private double _theta;
        // Angular velocity, the change in angle over time.
        private double _omega;
        // Angular accelaration, the change in angular velocity over time.
        private double _alpha;

        public KinematicBehavior( Dipole compassModel ) {
            super( compassModel );
            _theta = _omega = _alpha = 0.0;
        }

        public void setOrientation( Vector2D fieldVector, double dt ) {

            double magnitude = fieldVector.getMagnitude();
            double angle = fieldVector.getAngle();

            // Difference between the field angle and the compass angle.
            double phi = ( ( magnitude == 0 ) ? 0.0 : ( angle - _theta ) );

            if( Math.abs( phi ) < THRESHOLD ) {
                // When the difference between the field angle and the compass angle is insignificant,
                // simply set the angle and consider the compass to be at rest.
                _theta = angle;
                _omega = 0;
                _alpha = 0;
                getCompass().setOrientation( _theta );
            }
            else {
                // Use the Verlet algorithm to compute angle, angular velocity, and angular acceleration.

                // Step 1: orientation
                double thetaOld = _theta;
                double alphaTemp = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * _omega );
                _theta = _theta + ( _omega * dt ) + ( 0.5 * alphaTemp * dt * dt );
                if( _theta != thetaOld ) {
                    // Set the compass needle direction.
                    getCompass().setOrientation( _theta );
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
            int direction = random.nextBoolean() ? 1 : -1;
            _omega = 0.03 * direction; // adjust as needed for desired behavior
        }
    }

    //----------------------------------------------------------------
    // Design & debug methods
    //----------------------------------------------------------------

    public void setPrecession( double precession ) {
        this.precession = precession;
    }
}
