/**
 * Class: Electron Package: edu.colorado.phet.waves.model Author: Another Guy
 * Date: May 23, 2003
 */

package edu.colorado.phet.radiowaves.model;

import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.mechanics.Body;
import edu.colorado.phet.common_1200.math.Vector2D;
import edu.colorado.phet.radiowaves.RadioWavesApplication;
import edu.colorado.phet.radiowaves.model.movement.ManualMovement;
import edu.colorado.phet.radiowaves.model.movement.MovementType;
import edu.colorado.phet.radiowaves.model.movement.SinusoidalMovement;

public class Electron extends Body {

    private EmfModel model;
    private Point2D startPosition;
    private Point2D prevPosition = new Point2D.Double();
    private Point2D currentPosition = new Point2D.Double();
    private Vector2D velocity = new Vector2D.Float();
    private MovementType movementStrategy;
    private double runningTime;
    private Vector2D.Float staticFieldStrength = new Vector2D.Float();
    private Vector2D.Float dynamicFieldStrength = new Vector2D.Float();


    // Number of time steps between emitting field elements. This
    // provides and animated look to the visualization.
    // TODO: this mechanism should be implemented in the view, not here
    private int steps = 0;

    // The position history of the electron
    private Point2D[] positionHistory = new Point2D.Float[s_retardedFieldLength];
    // The acceleration history of the electron
    private Vector2D.Float[] accelerationHistory = new Vector2D.Float[s_retardedFieldLength];
    // The history of the maximum acceleration the electron courld have had at
    // a point in time. This is needed so viewers can properly scale the actual
    // accelerations
    private Vector2D.Float[] maxAccelerationHistory = new Vector2D.Float[s_retardedFieldLength];
    // The history of what movement strategy was in place a point in time
    private MovementType[] movementStrategyHistory = new MovementType[s_retardedFieldLength];

    private boolean changeFreq;
    private float newFreq;
    private boolean changeAmplitude;
    private float newAmplitude;

    private boolean recordHistory = true;

    public Electron( EmfModel model, Point2D.Double startPosition ) {
        this.model = model;
        this.startPosition = new Point2D.Double( startPosition.getX(), startPosition.getY() );
        this.currentPosition = new Point2D.Double( startPosition.getX(), startPosition.getY() );
        for ( int i = 0; i < s_retardedFieldLength; i++ ) {
            positionHistory[i] = new Point2D.Float( (float) startPosition.getX(), (float) startPosition.getY() );
            accelerationHistory[i] = new Vector2D.Float();
            maxAccelerationHistory[i] = new Vector2D.Float();
        }
    }

    public Point2D getCurrentPosition() {
        return currentPosition;
    }

    public synchronized void setCurrentPosition( Point2D newPosition ) {
        currentPosition.setLocation( newPosition );
    }

    public void setMovementStrategy( MovementType movementStrategy ) {
        this.movementStrategy = movementStrategy;
    }

    public void setRecordHistory( boolean recordHistory ) {
        this.recordHistory = recordHistory;
    }

    public synchronized void stepInTime( double dt ) {

        prevPosition.setLocation( currentPosition );
        // The movement strategy is not stepped by the model. We do it here.
        movementStrategy.stepInTime( this, dt );
        velocity = movementStrategy.getVelocity( this );
        runningTime += dt;
        if ( recordHistory ) {
            recordPosition( currentPosition );
        }

        // If we have a frequency change pending, determine if this is the right time to
        // make it
        if ( changeFreq && movementStrategy instanceof SinusoidalMovement ) {
            // This computation attempts to keep things in phase when
            // the frequency changes,
            SinusoidalMovement sm = (SinusoidalMovement) movementStrategy;
            // If the new frequency isn't 0, compute the phase shift needed to keep
            // the electron moving smoothly
            if ( newFreq != 0 ) {
                double phi = sm.getRunningTime() * ( ( sm.getFrequency() / newFreq ) - 1 );
                sm.setRunningTime( (float) ( sm.getRunningTime() + phi ) );
            }
            sm.setFrequency( newFreq );
            changeFreq = false;
        }

        // If we have an amplitude change pending, determine if this is the right time to
        // make it
        if ( changeAmplitude && movementStrategy instanceof SinusoidalMovement ) {
            if ( ( prevPosition.getY() - startPosition.getY() ) * ( currentPosition.getY() - startPosition.getY() ) <= 0 ) {
                SinusoidalMovement sm = (SinusoidalMovement) movementStrategy;
                sm.setAmplitude( newAmplitude );
                changeAmplitude = false;
            }
        }
        notifyObservers();
    }

    /**
     *
     */
    public synchronized void moveToNewPosition( Point2D newLocation ) {
        if ( movementStrategy instanceof ManualMovement ) {
            ( (ManualMovement) movementStrategy ).setPosition( newLocation );
        }
    }

    private void recordPosition( Point2D position ) {

        for ( int i = s_retardedFieldLength - 1; i > s_stepSize - 1; i-- ) {
            positionHistory[i].setLocation( positionHistory[i - s_stepSize] );
            accelerationHistory[i].setX( accelerationHistory[i - s_stepSize].getX() );
            accelerationHistory[i].setY( accelerationHistory[i - s_stepSize].getY() );
            maxAccelerationHistory[i].setX( maxAccelerationHistory[i - s_stepSize].getX() );
            maxAccelerationHistory[i].setY( maxAccelerationHistory[i - s_stepSize].getY() );
            movementStrategyHistory[i] = movementStrategyHistory[i - s_stepSize];
        }

        Vector2D.Float a = accelerationHistory[0];
        double df = ( a.getY() - movementStrategy.getAcceleration( this ) * s_B ) / s_stepSize;
        for ( int i = 0; i < s_stepSize; i++ ) {
            positionHistory[i].setLocation( position );
            accelerationHistory[i].setY( movementStrategy.getAcceleration( this ) * s_B + i * df );
            maxAccelerationHistory[i].setY( movementStrategy.getMaxAcceleration( this ) * s_B );
            movementStrategyHistory[i] = this.movementStrategy;
        }
    }

    public edu.colorado.phet.common.phetcommon.math.Vector2D getVelocity() {
        //    public Vector2D.Float getVelocity() {
        return new edu.colorado.phet.common.phetcommon.math.Vector2D.Double( velocity.getX(), velocity.getY() );
        //        return this.velocity;
    }

    public Point2D getStartPosition() {
        return this.startPosition;
    }

    public Vector2D.Float getStaticFieldAt( Point2D location ) {
        staticFieldStrength.setX( (float) ( location.getX() - getCurrentPosition().getX() ) );
        staticFieldStrength.setY( (float) ( location.getY() - getCurrentPosition().getY() ) );
        staticFieldStrength.normalize();

        double distanceFromSource = location.distance( this.getCurrentPosition() );
        staticFieldStrength.scale( s_B * s_staticFieldScale / (float) ( distanceFromSource * distanceFromSource ) );
        return staticFieldStrength;
    }

    /**
     * Returns a the dynamic electric field at a specified point. Note that to minimize
     * memory allocation, the Vector2D.Float returned is re-used by every call to this method.
     * Therefore, clients should copy the values from it. Also, the method is not reentrant
     *
     * @param location
     * @return
     */
    public Vector2D.Float getDynamicFieldAt( Point2D location ) {

        // Hollywood here! This computes the field based on the origin of the
        // electron's motion, not its current position. But it looks better
        // !!!! This is where you I answered Noah's concern of 11/3/03
        // todo: this won't look right in the full-field view
        double distanceFromSource = location.distance( this.getStartPosition() );
        //                double distanceFromSource = location.distance( this.getCurrentPosition() );
        if ( distanceFromSource == 0 ) {
            throw new RuntimeException( "Asked for r=0 field." );
        }

        Point2D generatingPos = this.positionHistory[(int) distanceFromSource];

        // Using the following line may or may not be more accurate. I'm not sure, since
        // the index we use into the positionHistory buffer is based on the current position
        // of the electron.
        //        distanceFromSource = location.distance( generatingPos );

        // Determine the direction of the field.
        dynamicFieldStrength.setX( (float) ( -( location.getY() - generatingPos.getY() ) ) );
        if ( location.getX() - generatingPos.getX() < 0 ) {
            dynamicFieldStrength.setX( -dynamicFieldStrength.getX() );
        }
        dynamicFieldStrength.setY( (float) Math.abs( location.getX() - generatingPos.getX() ) );
        dynamicFieldStrength.normalize();

        // Set the magnitude of the field to the acceleration of the electron, reduced by
        // the by the distance from the source
        float acceleration = this.getAccelerationAt( (int) distanceFromSource );
        //        float distanceScaleFactor = (float)Math.max( ( Math.pow( distanceFromSource, 0.5 ) ), 1.0 );

        float distanceScaleFactor = 0;
        if ( distanceFromSource == 0 ) {
            distanceScaleFactor = 1;
        }
        else {
            distanceScaleFactor = (float) Math.pow( distanceFromSource, 0.5 );
        }
        dynamicFieldStrength.scale( acceleration / distanceScaleFactor );

        // The following factor is used to give the fall-off associated with being off-axis.
        if ( distanceFromSource == 0.0 ) {
            distanceFromSource = 1;
        }
        float dubsonFactor = (float) ( Math.abs( location.getX() - this.getStartPosition().getX() ) / distanceFromSource );
        dynamicFieldStrength.scale( dubsonFactor );

        return dynamicFieldStrength;
    }

    private Vector2D.Float fieldStrength = new Vector2D.Float();

    public Vector2D.Float getFieldAtLocation( Point2D location ) {
        fieldStrength.setX( 0 );
        fieldStrength.setY( 0 );
        if ( model.isStaticFieldEnabled() ) {
            fieldStrength.add( getStaticFieldAt( location ) );
        }
        if ( model.isDynamicFieldEnabled() ) {
            fieldStrength.add( getDynamicFieldAt( location ) );
        }
        return fieldStrength;
    }

    private float getAccelerationAt( int x ) {
        return (float) accelerationHistory[Math.min( x, accelerationHistory.length - 1 )].getY();
    }

    public float getPositionAt( int x ) {
        return (float) positionHistory[Math.min( x, positionHistory.length - 1 )].getY();
    }

    public float getPositionAt( Point2D p ) {
        int x = (int) ( p.distance( this.currentPosition ) );
        return getPositionAt( x );
    }

    public MovementType getMovementTypeAt( Point2D location ) {
        int x = (int) ( location.distance( this.currentPosition ) );
        return movementStrategyHistory[x];
    }

    public double getMass() {
        //mr = m0 /sqrt(1 - v2/c2)
        float vMag = (float) this.getVelocity().getMagnitude();
        //        float vMag = this.getVelocity().getLength();
        float denom = (float) Math.sqrt( 1 - ( vMag * vMag ) / ( RadioWavesApplication.s_speedOfLight * RadioWavesApplication.s_speedOfLight ) );
        if ( denom < 1 ) {
            System.out.println( denom );
        }
        float mr = s_restMass / denom;
        return mr;
    }

    public void setFrequency( float freq ) {
        if ( this.movementStrategy instanceof SinusoidalMovement ) {
            changeFreq = true;
            newFreq = freq;
        }
    }


    public void setAmplitude( float amplitude ) {
        if ( this.movementStrategy instanceof SinusoidalMovement ) {
            changeAmplitude = true;
            newAmplitude = amplitude;
        }
    }

    public Vector2D.Float getMaxAccelerationAtLocation( Point2D.Double location ) {
        double distanceFromSource = location.distance( this.getStartPosition() );
        return this.maxAccelerationHistory[(int) distanceFromSource];
    }

    public Vector2D.Float getMaxAccelerationAtLocation( Point location ) {
        double distanceFromSource = location.distance( this.getStartPosition() );
        return this.maxAccelerationHistory[(int) distanceFromSource];
    }

    /**
     * Tells if the field is zero between the electron and a specified x coordinate
     *
     * @return
     */
    public boolean isFieldOff( double x ) {
        boolean result = true;
        for ( int i = 0; i < accelerationHistory.length && i < (int) x && result == true; i++ ) {
            Vector2D.Float field = accelerationHistory[i];
            if ( field.getX() != 0 || field.getY() != 0 ) {
                result = false;
            }
        }
        return result;
    }

    //    public Point2D getCM() {
    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    //
    // Static fields and methods
    //
    private static final int s_retardedFieldLength = 2000;
    // Fudge factor for scaling the field strength from the acceleration
    private static final float s_B = 1000;
    private static final float s_staticFieldScale = 50;
    private static final float s_restMass = 1;
    private static int s_stepSize = (int) ( RadioWavesApplication.s_speedOfLight  );

    //    private static int s_stepSize = (int)( EmfApplication.s_speedOfLight / 4 );

    public static float getRestMass() {
        return s_restMass;
    }

    public Class getMovementStrategyType() {
        return this.movementStrategy.getClass();
    }
}