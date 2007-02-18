package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:42:19 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */

public class Particle {
    private Particle1D particle1D;
    private boolean splineMode = true;
    private double x;
    private double y;
    double vx;
    double vy;
    double g = 9.8 * 10000;

    UpdateStrategy updateStrategy = new Particle1DUpdate();

    public Particle( CubicSpline2D cubicSpline ) {
        particle1D = new Particle1D( cubicSpline );
    }

    public void stepInTime( double dt ) {
        updateStrategy.stepInTime( dt );
        update();
    }

    interface UpdateStrategy {
        void stepInTime( double dt );
    }

    class Particle1DUpdate implements UpdateStrategy {
        public void stepInTime( double dt ) {
            particle1D.stepInTime( dt );
            x = particle1D.getX();
            y = particle1D.getY();
            AbstractVector2D vel = particle1D.getVelocity();
            vx = vel.getX();
            vy = vel.getY();
        }
    }

    class FreeFall implements UpdateStrategy {

        public void stepInTime( double dt ) {
            y += vy * dt + 0.5 * g * dt * dt;
            x += vx * dt;
            vy += g * dt;
            vx += 0;
        }
    }

    class UserUpdateStrategy implements UpdateStrategy {
        public void stepInTime( double dt ) {
        }
    }

    public void setFreeFall() {
        setUpdateStrategy( new FreeFall() );
    }

    public void setParticle1DStrategy() {
        setUpdateStrategy( new Particle1DUpdate() );
    }

    public void setUserUpdateStrategy() {
        setUpdateStrategy( new UserUpdateStrategy() );
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
        update();
    }

    private void update() {
        notifyListeners();
    }

    private ArrayList listeners = new ArrayList();

    public Point2D getPosition() {
        return new Point2D.Double( x, y );
    }

    public void setPosition( Point2D pt ) {
        setPosition( pt.getX(), pt.getY() );
    }

    public void setPosition( double x, double y ) {
        setFreeFall();
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setVelocity( double vx, double vy ) {
        this.vx = vx;
        this.vy = vy;
        notifyListeners();
    }

    public static interface Listener {
        void particleChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.particleChanged();
        }
    }

}
