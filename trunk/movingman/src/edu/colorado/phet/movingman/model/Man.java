/*PhET, 2004.*/
package edu.colorado.phet.movingman.model;

import java.util.ArrayList;


/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:25:22 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class Man {
    private double x;
    private double x0;
    private boolean grabbed = false;
    private double velocity;
    private double acceleration;
    private double min;
    private double max;
    private ArrayList listeners = new ArrayList();
    private double lastX = 0.0;

    public Man( double x, double min, double max ) {
        this.x0 = x;
        this.x = x;
        this.min = min;
        this.max = max;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static interface Listener {
        void positionChanged( double x );

        void velocityChanged( double velocity );

        void accelerationChanged( double acceleration );
    }

    public double getVelocity() {
        return velocity;
    }

    public void setAcceleration( double acceleration ) {
        this.acceleration = acceleration;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.accelerationChanged( acceleration );
        }
    }

    public void setVelocity( double velocity ) {
        this.velocity = velocity;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.velocityChanged( velocity );
        }
    }

    public boolean isGrabbed() {
        return grabbed;
    }

    public void setGrabbed( boolean grabbed ) {
        this.grabbed = grabbed;
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        if( x < min ) {
            x = min;
        }
        else if( x > max ) {
            x = max;
        }
        this.x = x;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.positionChanged( x );
        }
    }

    public void reset() {
        setX( x0 );
        setVelocity( 0.0 );
        setAcceleration( 0.0 );
    }

    public double getDx() {
        return x - lastX;
    }

    public void stepInTime( double dt ) {
        double xOrig = x;
        double newVelocity = velocity + acceleration * dt;
        double newX = x + velocity * dt;

        if( newX > max || newX < min ) {
            setVelocity( 0 );
            setAcceleration( 0 );
            if( newX > max ) {
                setX( max );
            }
            else if( newX < min ) {
                setX( min );
            }
        }
        else {
            newX = Math.min( newX, max );
            newX = Math.max( newX, min );
            setVelocity( newVelocity );
            lastX = xOrig;
            setX( newX );
        }
    }

}
