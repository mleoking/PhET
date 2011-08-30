// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author Sam Reid
 */
public class FoodColoring {
    private final ArrayList<Particle> particles = new ArrayList<Particle>();
    private final Pipe pipe;
    private final ArrayList<SimpleObserver> removalObservers = new ArrayList<SimpleObserver>();
    private final ArrayList<SimpleObserver> observers = new ArrayList<SimpleObserver>();

    public FoodColoring( double x0, double width, Pipe pipe ) {
        //Pull back the food coloring by 10 pixels or so.  It shouldn't be moving at the water/pipe interface, so we avoid the problem by not showing any dye there
        double yMin = 0 + 0.05;
        double yMax = 1 - 0.05;
        double dy = 0.1;
        //top
        for ( double x = x0; x <= x0 + width; x += 0.1 ) {
            particles.add( new Particle( x, yMin, pipe ) );
        }
        //right
        for ( double y = yMin; y <= yMax; y += dy ) {
            particles.add( new Particle( x0 + width, y, pipe ) );
        }
        //bottom
        for ( double x = x0 + width; x >= x0; x -= 0.1 ) {
            particles.add( new Particle( x, yMax, pipe ) );
        }
        //left
        for ( double y = yMax; y >= yMin; y -= dy ) {
            particles.add( new Particle( x0, y, pipe ) );
        }
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

    public void notifyRemoved() {
        for ( int i = 0; i < removalObservers.size(); i++ ) {
            removalObservers.get( i ).update();
        }
    }

    public void notifyObservers() {
        for ( int i = 0; i < observers.size(); i++ ) {
            observers.get( i ).update();
        }
    }

    public void addObserver( SimpleObserver simpleObserver ) {
        observers.add( simpleObserver );
        simpleObserver.update();
    }

    public Shape getShape() {
        DoubleGeneralPath path = new DoubleGeneralPath( particles.get( 0 ).getPosition() );
        for ( int i = 1; i < particles.size(); i++ ) {
            Particle particle = particles.get( i );
            path.lineTo( particle.getPosition() );
        }
        path.lineTo( particles.get( 0 ).getPosition() );
        path.closePath();
        final GeneralPath generalPath = path.getGeneralPath();
        Area area = new Area( generalPath );
        area.intersect( new Area( pipe.getShape() ) );//make sure fluid doesn't get outside the pipe
        return area;
    }

    public void removeRemovalListener( SimpleObserver simpleObserver ) {
        removalObservers.remove( simpleObserver );
    }

    public ArrayList<Particle> getParticles() {
        return particles;
    }

}
