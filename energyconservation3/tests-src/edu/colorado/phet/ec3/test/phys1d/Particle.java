package edu.colorado.phet.ec3.test.phys1d;

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
    private Particle2D particle2D;
    private boolean splineMode = true;
    private double x;
    private double y;

    public Particle( CubicSpline2D cubicSpline ) {
        particle1D = new Particle1D( cubicSpline );
        particle2D = new Particle2D();
    }

    public void stepInTime( double dt ) {
        if( splineMode ) {
            particle1D.stepInTime( dt );
        }
        else {
            particle2D.stepInTime( dt );
        }
        update();
    }

    private void update() {
        if( splineMode ) {
            this.x = particle1D.getX();
            this.y = particle1D.getY();
        }
        else {
            this.x = particle2D.getX();
            this.y = particle2D.getY();
        }
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
        this.splineMode = false;
        particle2D.setPosition( x, y );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setVelocity( double vx, double vy ) {
        particle2D.setVelocity( vx, vy );
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
