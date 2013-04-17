// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A single spherical particle that can travel with the flow in the pipe
 *
 * @author Sam Reid
 */
public class Particle {

    //Distance traveled along the pipe's horizontal axis.
    private double x;

    //How far up the pipe, 0=bottom, 1=top
    private final double fractionUpPipe;

    //The pipe within which the particle travels
    private final Pipe container;

    //Radius of the particle in meters
    private final double radius;

    //Listeners
    private final ArrayList<SimpleObserver> observers = new ArrayList<SimpleObserver>();
    private final ArrayList<SimpleObserver> removalListeners = new ArrayList<SimpleObserver>();
    public final Paint color;

    //True if the particle is on the grid of black particles
    public final boolean gridParticle;

    public Particle( double x, double fractionUpPipe, Pipe container, double radius, Paint color, boolean gridParticle ) {
        this.x = x;
        this.fractionUpPipe = fractionUpPipe;
        this.container = container;
        this.radius = radius;
        this.color = color;
        this.gridParticle = gridParticle;
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

        //Iterate on copy to avoid ConcurrentModificationException
        for ( SimpleObserver removalListener : new ArrayList<SimpleObserver>( removalListeners ) ) {
            removalListener.update();
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