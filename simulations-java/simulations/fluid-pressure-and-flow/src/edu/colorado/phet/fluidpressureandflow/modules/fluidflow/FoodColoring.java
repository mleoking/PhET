package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.model.PipePosition;

/**
 * @author Sam Reid
 */
public class FoodColoring {
    private double x;
    private double area;
    private final Pipe pipe;
    private ArrayList<SimpleObserver> observers = new ArrayList<SimpleObserver>();
    private ArrayList<SimpleObserver> removalObservers = new ArrayList<SimpleObserver>();

    public FoodColoring( double x, double area, Pipe pipe ) {
        this.x = x;
        this.area = area;
        this.pipe = pipe;
        pipe.addShapeChangeListener( new SimpleObserver() {
            public void update() {
                notifyObservers();
            }
        } );
    }

    public void addRemovalListener( SimpleObserver removalObserver ) {
        removalObservers.add( removalObserver );
    }

    public double getX() {
        return x;
    }

    public void notifyRemoved() {
        for ( int i = 0; i < removalObservers.size(); i++ ) {
            removalObservers.get( i ).update();
        }
    }

    public void setX( double x ) {
        if ( this.x != x ) {
            this.x = x;
            notifyObservers();
        }
    }

    private void notifyObservers() {
        for ( int i = 0; i < observers.size(); i++ ) {
            observers.get( i ).update();
        }
    }

    public void addObserver( SimpleObserver simpleObserver ) {
        observers.add( simpleObserver );
        simpleObserver.update();
    }

    public Shape getShape() {
        PipePosition currentSegment = pipe.getPipePosition( x );
        //TODO: create a general path that fills the correct area
        return new Rectangle2D.Double( x, currentSegment.getBottom().getY(), 0.1, currentSegment.getHeight() );
    }

    public void removeRemovalListener( SimpleObserver simpleObserver ) {
        removalObservers.remove( simpleObserver );
    }
}
