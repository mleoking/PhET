/**
 * Class: SinusoidalMovement
 * Package: edu.colorado.phet.emf
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf.model.movement;

import edu.colorado.phet.emf.model.movement.MovementType;
import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.emf.EmfApplication;
import edu.colorado.phet.waves.model.SineFunction;

import java.awt.geom.Point2D;

public class SinusoidalMovement implements MovementType {

    private SineFunction sineFunction = new SineFunction();
    private float frequency;
    private float amplitude;
    private Point2D nextPosition = new Point2D.Double();
    private float omega;
    private float runningTime;
//    private Electron electron;

    public SinusoidalMovement( float frequency, float amplitude ) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.omega = (float)( frequency * Math.PI * 2 );
    }

//    public SinusoidalMovement( Electron electron, float frequency, float amplitude ) {
//        this.electron = electron;
//        this.frequency = frequency;
//        omega = frequency * 2 * (float)Math.PI;
//        this.amplitude = amplitude;
//    }

//    public void setElectron( Electron electron ) {
//        this.electron = electron;
//    }

    public void stepInTime( Electron electron, double dt ) {
        runningTime += dt;
        Point2D location = electron.getStartPosition();
        nextPosition = getNextPosition( location, runningTime );
        electron.setCurrentPosition( nextPosition );
    }

    public float getVelocity( Electron electron ) {
        return (float)( omega * Math.cos( omega * runningTime ) );
    }

    public float getAcceleration( Electron electron ) {
        return (float)( 100 * -omega * omega * Math.cos( omega * runningTime ) );
    }

    /**
     * Computes the next position dictated by the movement. Note that
     * this method does not modify the position parameter, and that this
     * method is not reentrant.
     * @param position
     * @param t
     * @return
     */
    public Point2D getNextPosition( Point2D position, double t ) {
        float yNew = sineFunction.valueAtTime( frequency, amplitude, (float)( t ) );

        Math.sin( frequency * Math.PI * 2 * t );
        nextPosition.setLocation( position.getX(), position.getY() + yNew );
        return nextPosition;
    }

    public void setFrequency( float freq ) {
        this.frequency = freq;
//        this.amplitude = (float)EmfApplication.s_speedOfLight / freq );
    }

    public double getVelocity( double t ) {
        return omega * Math.cos( t );
    }

    public double getAcceleration( double t ) {
        return omega * omega * Math.sin( t );
    }
}
