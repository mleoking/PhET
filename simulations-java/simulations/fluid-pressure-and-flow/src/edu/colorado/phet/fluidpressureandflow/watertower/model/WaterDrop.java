// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.model;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;

import static java.lang.Math.PI;

/**
 * A single drop of water emitted by the water tower, faucet or hose.
 *
 * @author Sam Reid
 */
public class WaterDrop {

    //Where it at
    public final Property<Vector2D> position;

    //How fast it going and in what direction
    public final Property<Vector2D> velocity;

    //The radius of the water drop, changes as a function of velocity to make it look more realistic
    //constrain fluxes to be constant: velocity_1 * volume_1 = velocity_2 * volume_2
    public final Property<Double> radius;
    private final double volume;

    //Mass and gravity for stepping in time according to newtons laws
    private static final double MASS = 1;

    //Listeners
    private final ArrayList<SimpleObserver> removalListeners = new ArrayList<SimpleObserver>();

    public WaterDrop( Vector2D x, final Vector2D v, final double volume, boolean changeRadius ) {
        this.volume = volume;
        this.position = new Property<Vector2D>( x );
        this.velocity = new Property<Vector2D>( v );

        //v = 4/3 pi * r^3
        //constrain fluxes to be constant: velocity_1 * volume_1 = velocity_2 * volume_2
        this.radius = new Property<Double>( toRadius( volume, v.magnitude(), v.magnitude() ) );

        //Water flowing out of the tower needs to get smaller to look natural, but water coming out of the hose looks weird if it gets bigger as it gets closer to the top of its arch
        if ( changeRadius ) {
            this.velocity.addObserver( new SimpleObserver() {
                public void update() {
                    radius.set( toRadius( volume, velocity.get().magnitude(), v.magnitude() ) );
                }
            } );
        }
    }

    //Helps account for fluids with initial velocity of zero and reduces the effect of the velocity
    private double toRadius( double volume, double velocity, double initialVelocity ) {
        final int fudgeFactor = 20;
        return Math.pow( volume * 3.0 / 4.0 / PI, 1.0 / 3.0 ) * ( initialVelocity + fudgeFactor ) / ( velocity + fudgeFactor );
    }

    //Apply gravity force and euler-integrate to update
    public void stepInTime( double simulationTimeChange ) {
        Vector2D force = new Vector2D( 0, -MASS * FluidPressureAndFlowModel.EARTH_GRAVITY );
        Vector2D acceleration = force.times( 1.0 / MASS );
        velocity.set( acceleration.times( simulationTimeChange ).plus( velocity.get() ) );
        position.set( velocity.get().times( simulationTimeChange ).plus( position.get() ) );
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

    //Check whether the water drop contains the specified point for calculating velocity.
    public boolean contains( double x, double y ) {
        double r = radius.get();
        final Ellipse2D.Double shape = new Ellipse2D.Double( position.get().getX() - r, position.get().getY() - r, r * 2, r * 2 );
        return shape.contains( x, y );
    }
}