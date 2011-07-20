// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

public class ElectronPair {
    // TODO: add in developer controls for all of these
    public static final double BONDED_PAIR_DISTANCE = 10.0;
    public static final double LONE_PAIR_DISTANCE = 6.0;

    public static final double ELECTRON_PAIR_REPULSION_SCALE = 30000;
    public static final double JITTER_SCALE = 0.001;
    public static final double DAMPING_FACTOR = 0.1;
    public static final double ATTRACTION_SCALE = 1.0;

    public Property<ImmutableVector3D> position;
    public Property<ImmutableVector3D> velocity = new Property<ImmutableVector3D>( new ImmutableVector3D() );
    public final boolean isLonePair;

    public ElectronPair( ImmutableVector3D position, boolean isLonePair ) {
        this.position = new Property<ImmutableVector3D>( position );
        this.isLonePair = isLonePair;
    }

    public void attractTo( ElectronPair center, double timeElapsed ) {
        ImmutableVector3D toCenter = position.get().minus( center.position.get() );

        double distance = toCenter.magnitude();
        ImmutableVector3D directionToCenter = toCenter.normalized();

        double offset = getIdealDistanceFromCenter() - distance;

        // just modify position for now so we don't get any oscillations
        position.set( position.get().plus( directionToCenter.times( 0.02 * offset ) ) );
    }

    public void repulseFrom( ElectronPair other, double timeElapsed ) {
        // only handle the force on this object for now
        ImmutableVector3D delta = position.get().minus( other.position.get() );
        ImmutableVector3D velocityDelta = delta.normalized().times( timeElapsed * ELECTRON_PAIR_REPULSION_SCALE / ( delta.magnitude() * delta.magnitude() ) );
        velocity.set( velocity.get().plus( velocityDelta ) );
    }

    public void stepForward( double timeElapsed ) {
        // velocity changes so that it doesn't point at all towards or away from the origin
        double velocityMagnitudeOutwards = velocity.get().dot( position.get().normalized() );
        velocity.set( velocity.get().minus( position.get().normalized().times( velocityMagnitudeOutwards ) ) ); // subtract the outwards-component out

        // move position forward by scaled velocity
        position.set( position.get().plus( velocity.get().times( timeElapsed ) ) );

        // add in damping so we don't get the kind of oscillation that we are seeing
        velocity.set( velocity.get().times( 1 - DAMPING_FACTOR ) );

        // add in a small randomization into position, so we jitter away from unstable positions
        position.set( position.get().plus( new ImmutableVector3D( JITTER_SCALE * ( Math.random() - 0.5 ), JITTER_SCALE * ( Math.random() - 0.5 ), JITTER_SCALE * ( Math.random() - 0.5 ) ) ) );
    }

    private double getIdealDistanceFromCenter() {
        return isLonePair ? LONE_PAIR_DISTANCE : BONDED_PAIR_DISTANCE;
    }
}
