package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.Function0;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;

/**
 * @author Sam Reid
 */
public class Particle {
    private double x;
    private double fractionUpPipe;
    private final Pipe container;
    private ArrayList<SimpleObserver> observers = new ArrayList<SimpleObserver>();
    private ArrayList<Function0> listeners = new ArrayList<Function0>();
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
        for ( int i = 0; i < listeners.size(); i++ ) {
            listeners.get( i ).apply();
        }
    }

    public void addRemovalListener( Function0 removalListener ) {
        listeners.add( removalListener );
    }

    public void removeRemovalListener( Function0 function0 ) {
        listeners.remove( function0 );
    }

    public Point2D getPosition() {
        return new Point2D.Double( getX(), getY() );
    }

    public double getRadius() {
        return radius;
    }
}
