// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.model;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class WaterDrop {
    public final Property<ImmutableVector2D> position;
    public final Property<ImmutableVector2D> velocity;
    public final Property<Double> radius;
    private final double mass = 1;
    private final double gravity = -9.8;
    private final ArrayList<SimpleObserver> removalListeners = new ArrayList<SimpleObserver>();
    private final double volume;

    public WaterDrop( ImmutableVector2D x, final ImmutableVector2D v, final double volume, boolean changeRadius ) {
        this.volume = volume;
        this.position = new Property<ImmutableVector2D>( x );
        this.velocity = new Property<ImmutableVector2D>( v );

        //v = 4/3 pi * r^3
        //constrain fluxes to be constant: velocity_1 * volume_1 = velocity_2 * volume_2
        this.radius = new Property<Double>( toRadius( volume, v.getMagnitude(), v.getMagnitude() ) );

        //Water flowing out of the tower needs to get smaller to look natural, but water coming out of the hose looks weird if it gets bigger as it gets closer to the top of its arch
        if ( changeRadius ) {
            this.velocity.addObserver( new SimpleObserver() {
                public void update() {
                    radius.set( toRadius( volume, velocity.get().getMagnitude(), v.getMagnitude() ) );
                }
            } );
        }
    }

    private double toRadius( double volume, double velocity, double initialVelocity ) {
        final int fudgeFactor = 20;//Helps account for fluids with initial velocity of zero and reduces the effect of the velocity
        return Math.pow( volume * 3.0 / 4.0 / Math.PI, 1.0 / 3.0 ) * ( initialVelocity + fudgeFactor ) / ( velocity + fudgeFactor );
    }

    public void stepInTime( double simulationTimeChange ) {
        ImmutableVector2D force = new ImmutableVector2D( 0, mass * gravity );
        ImmutableVector2D acceleration = force.getScaledInstance( 1.0 / mass );
        velocity.set( acceleration.getScaledInstance( simulationTimeChange ).getAddedInstance( velocity.get() ) );
        position.set( velocity.get().getScaledInstance( simulationTimeChange ).getAddedInstance( position.get() ) );
    }

    public void addRemovalListener( SimpleObserver simpleObserver ) {
        removalListeners.add( simpleObserver );
    }

    public double getVolume() {
        return volume;
    }

    public void notifyRemoved() {
        for ( SimpleObserver removalListener : removalListeners ) {
            removalListener.update();
        }
    }

    public boolean contains( double x, double y ) {
        double r = radius.get();
        final Ellipse2D.Double shape = new Ellipse2D.Double( position.get().getX() - r, position.get().getY() - r, r * 2, r * 2 );
        return shape.contains( x, y );
    }
}
