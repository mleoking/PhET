// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class Particle {
    private double x;
    private double fractionUpPipe;
    private final Pipe container;
    private ArrayList<SimpleObserver> observers = new ArrayList<SimpleObserver>();
    private ArrayList<SimpleObserver> removalListeners = new ArrayList<SimpleObserver>();
    private double radius;

    public Particle( double x, double fractionUpPipe, Pipe container ) {
        this( x, fractionUpPipe, container, 0.1 );
    }

    public Particle( double x, double fractionUpPipe, Pipe container, double radius ) {
        this.x = x;
        this.fractionUpPipe = fractionUpPipe;
        this.container = container;
        this.radius = radius;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return container.fractionToLocation( getX(), fractionUpPipe );
    }

    public void setX( double x ) {
        this.x = x;
        for ( SimpleObserver observer : observers ) {
            observer.update();
        }
    }

    public void addObserver( SimpleObserver observer ) {
        observers.add( observer );
        observer.update();
    }

    public void notifyRemoved() {
        for ( int i = 0; i < removalListeners.size(); i++ ) {
            removalListeners.get( i ).update();
        }
    }

    public void addRemovalListener( SimpleObserver removalListener ) {
        removalListeners.add( removalListener );
    }

    public void removeRemovalListener( SimpleObserver removalListener ) {
        removalListeners.remove( removalListener );
    }

    public Point2D getPosition() {
        return new Point2D.Double( getX(), getY() );
    }

    public double getRadius() {
        return radius;
    }
}
