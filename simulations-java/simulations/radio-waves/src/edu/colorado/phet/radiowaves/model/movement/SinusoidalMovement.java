/**
 * Class: SinusoidalMovement Package: edu.colorado.phet.emf Author: Another Guy
 * Date: May 23, 2003
 */

package edu.colorado.phet.radiowaves.model.movement;

import java.awt.geom.Point2D;
import java.util.Observable;

import edu.colorado.phet.common_1200.math.Vector2D;
import edu.colorado.phet.radiowaves.RadioWavesApplication;
import edu.colorado.phet.radiowaves.model.EMFSineFunction;
import edu.colorado.phet.radiowaves.model.Electron;

public class SinusoidalMovement extends Observable implements MovementType {

    private EMFSineFunction sineFunction = new EMFSineFunction();
    private float frequency;
    private float amplitude;
    private Point2D nextPosition = new Point2D.Double();
    private float omega;
    private float runningTime;
    private Vector2D.Float velocity = new Vector2D.Float();

    public SinusoidalMovement( float frequency, float amplitude ) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.omega = computeOmega( frequency );
    }

    public void stepInTime( Electron electron, double dt ) {
        runningTime += dt;
        Point2D location = electron.getStartPosition();
        nextPosition = getNextPosition( location, runningTime );
        electron.setCurrentPosition( nextPosition );
    }

    public Vector2D getVelocity( Electron electron ) {
        velocity.setY( omega * (float) Math.cos( omega * runningTime ) );
        return velocity;
    }

    public float getWaveValue( double x ) {
        double k = omega / RadioWavesApplication.s_speedOfLight;
        double s = Math.sin( k * x - omega * runningTime );
        return (float) ( -amplitude * omega * omega * s );
    }

    public float getAcceleration( Electron electron ) {
        return (float) ( -amplitude * omega * omega * Math.sin( omega * runningTime ) );
    }

    /**
     * Computes the next position dictated by the movement. Note that
     * this method does not modify the position parameter, and that this
     * method is not reentrant.
     *
     * @param position
     * @param t
     * @return
     */
    public Point2D getNextPosition( Point2D position, double t ) {
        float yNew = sineFunction.valueAtTime( frequency, amplitude, (float) ( t ) );
        Math.sin( frequency * Math.PI * 2 * t );
        nextPosition.setLocation( position.getX(), position.getY() + yNew );
        return nextPosition;
    }

    public void setFrequency( float freq ) {
        this.frequency = freq;
        this.omega = computeOmega( freq );
    }

    public void setAmplitude( float amp ) {
        this.amplitude = amp;
    }

    public float getAmplitude() {
        return amplitude;
    }

    public double getVelocity( double t ) {
        return omega * Math.cos( t );
    }

    public double getAcceleration( double t ) {
        return -amplitude * omega * omega * Math.sin( omega * t );
    }

    public float getMaxAcceleration( Electron electron ) {
        return -amplitude * omega * omega;
    }

    public void setRunningTime( float runningTime ) {
        this.runningTime = runningTime;
        if ( runningTime == Float.POSITIVE_INFINITY ) {
            System.out.println( "" );
        }
    }

    public double getFrequency() {
        return this.frequency;
    }

    public double getRunningTime() {
        return this.runningTime;
    }

    //
    // Statics
    //
    private static float computeOmega( float frequency ) {
        return (float) ( frequency * Math.PI * 2 );
    }
}
