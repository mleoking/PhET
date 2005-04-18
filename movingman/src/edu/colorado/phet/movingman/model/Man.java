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
    private double minX;
    private double maxX;
    private ArrayList listeners = new ArrayList();

    public Man( double x, double min, double max ) {
        this.x0 = x;
        this.x = x;
        this.minX = min;
        this.maxX = max;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static interface Listener {
        void positionChanged( double x );

        void velocityChanged( double velocity );

        void accelerationChanged( double acceleration );

        void collided( Man man );
    }

    public static class Adapter implements Listener {

        public void positionChanged( double x ) {
        }

        public void velocityChanged( double velocity ) {
        }

        public void accelerationChanged( double acceleration ) {
        }

        public void collided( Man man ) {
        }
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
        if( this.grabbed != grabbed ) {
            this.grabbed = grabbed;
            if( !grabbed ) {
                setVelocity( 0.0 );
                setAcceleration( 0.0 );
            }
        }
    }

    public double getPosition() {
        return x;
    }

    public void setPosition( double x ) {
        if( x < minX ) {
            x = minX;
            notifyCollision();
        }
        else if( x > maxX ) {
            x = maxX;
            notifyCollision();
        }
        this.x = x;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.positionChanged( x );
        }
    }

    public void reset() {
        setPosition( x0 );
        setVelocity( 0.0 );
        setAcceleration( 0.0 );
    }

    public void stepInTime( double dt ) {
        if( grabbed ) {
            return;
        }
        double newVelocity = velocity + acceleration * dt;
        double newX = x + velocity * dt;

        if( newX > maxX || newX < minX ) {
            setVelocity( 0 );
            setAcceleration( 0 );
            if( newX > maxX ) {
                setPosition( maxX );
                notifyCollision();
            }
            else if( newX < minX ) {
                setPosition( minX );
                notifyCollision();
            }
        }
        else {
            newX = Math.min( newX, maxX );
            newX = Math.max( newX, minX );
            setVelocity( newVelocity );
            setPosition( newX );
        }
    }

    private void notifyCollision() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.collided( this );
        }
    }

    public boolean isMinimum() {
        return x == minX;
    }

    public boolean isMaximum() {
        return x == maxX;
    }

}
