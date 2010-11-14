package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.util.ArrayList;

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

    public Particle( double x, double fractionUpPipe, Pipe container ) {
        this.x = x;
        this.fractionUpPipe = fractionUpPipe;
        this.container = container;
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
}
