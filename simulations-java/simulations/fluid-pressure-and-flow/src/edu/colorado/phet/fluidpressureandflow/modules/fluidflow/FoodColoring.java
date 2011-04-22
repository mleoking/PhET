// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author Sam Reid
 */
public class FoodColoring {
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private Pipe pipe;
    private ArrayList<SimpleObserver> removalObservers = new ArrayList<SimpleObserver>();
    private ArrayList<SimpleObserver> observers = new ArrayList<SimpleObserver>();

    public FoodColoring( double x0, double width, Pipe pipe ) {
        //top
        for ( double x = x0; x <= x0 + width; x += 0.1 ) {
            particles.add( new Particle( x, 0, pipe ) );
        }
        //right
        for ( double y = 0; y <= 1; y += 0.1 ) {
            particles.add( new Particle( x0 + width, y, pipe ) );
        }
        //bottom
        for ( double x = x0 + width; x >= x0; x -= 0.1 ) {
            particles.add( new Particle( x, 1, pipe ) );
        }
        //left
        for ( double y = 1; y >= 0; y -= 0.1 ) {
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
