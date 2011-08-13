// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;

import com.jme3.math.ColorRGBA;

public class ElectronPair {
    // TODO: add in developer controls for all of these
    public static final double BONDED_PAIR_DISTANCE = 10.0;
    public static final double LONE_PAIR_DISTANCE = 7.0;

    public static final double ELECTRON_PAIR_REPULSION_SCALE = 30000;
    public static final double JITTER_SCALE = 0.01;
    public static final double DAMPING_FACTOR = 0.1;
    public static final double ATTRACTION_SCALE = 1.0;

    public final Property<ImmutableVector3D> position;
    public final Property<ImmutableVector3D> velocity = new Property<ImmutableVector3D>( new ImmutableVector3D() );
    public final boolean isLonePair;
    public final Property<Boolean> userControlled;

    public ElectronPair( ImmutableVector3D position, boolean isLonePair, boolean startDragged ) {
        this.position = new Property<ImmutableVector3D>( position );
        this.isLonePair = isLonePair;
        userControlled = new Property<Boolean>( startDragged );
    }

    public void attractToDistance( double timeElapsed ) {
        if ( userControlled.get() ) {
            // don't process if being dragged
            return;
        }

        ImmutableVector3D toCenter = position.get();

        double distance = toCenter.magnitude();
        ImmutableVector3D directionToCenter = toCenter.normalized();

        double offset = getIdealDistanceFromCenter() - distance;

        // just modify position for now so we don't get any oscillations
        position.set( position.get().plus( directionToCenter.times( 0.1 * offset ) ) );
    }

    public void repulseFrom( ElectronPair other, double timeElapsed ) {
        // only handle the force on this object for now

        // from other => this
        ImmutableVector3D delta = position.get().minus( other.position.get() );

        /*---------------------------------------------------------------------------*
        * coulomb repulsion
        *----------------------------------------------------------------------------*/

        // a factor that causes lone pairs to have more repulsion
        double repulsionFactor = 1;

        // mimic Coulomb's Law
        ImmutableVector3D coulombVelocityDelta = delta.normalized().times( timeElapsed * ELECTRON_PAIR_REPULSION_SCALE * repulsionFactor / ( delta.magnitude() * delta.magnitude() ) );
        addVelocity( coulombVelocityDelta.times( 1 ) );

        /*---------------------------------------------------------------------------*
        * angle-based repulsion
        *----------------------------------------------------------------------------*/

        double angle = Math.acos( position.get().normalized().dot( other.position.get().normalized() ) );

        ImmutableVector3D pushDirection = getTangentDirection( position.get(), delta ).normalized();

        double anglePushEstimate = estimatePush( angle ) - estimatePush( 2 * Math.PI - angle ); // push from the close direction (minus push from the LONG direction)

        double pushFactor = getPushFactor() * other.getPushFactor();
        ImmutableVector3D angleVelocityDelta = pushDirection.times( anglePushEstimate * pushFactor );
        addVelocity( angleVelocityDelta.times( 0 ) );
    }

    public void addVelocity( ImmutableVector3D velocityChange ) {
        if ( !userControlled.get() ) {
            velocity.set( velocity.get().plus( velocityChange ) );
        }
    }

    public double getPushFactor() {
        return isLonePair ? 1 : 1;
    }

    private double estimatePush( double angle ) {
        double result = 10 / ( angle * angle );
        if ( Double.isNaN( result ) ) {
            return 0;
        }
        return result;
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

    public ColorRGBA getColor() {
        // TODO: improve bad back to identify origin atom
        if ( position.get().equals( new ImmutableVector3D() ) ) {
            return new ColorRGBA( 1f, 0f, 0f, 1f );
        }
        else if ( isLonePair ) {
            return new ColorRGBA( 1f, 0.5f, 0f, 1f );
        }
        else {
            return new ColorRGBA( 1f, 1f, 1f, 1f );
        }
    }

    public double getIdealDistanceFromCenter() {
        return isLonePair ? LONE_PAIR_DISTANCE : BONDED_PAIR_DISTANCE;
    }

    /**
     * Returns a unit vector that is the component of "vector" that is perpendicular to the "position" vector
     */
    public static ImmutableVector3D getTangentDirection( ImmutableVector3D position, ImmutableVector3D vector ) {
        ImmutableVector3D normalizedPosition = position.normalized();
        return vector.minus( normalizedPosition.times( vector.dot( normalizedPosition ) ) );
    }

    public void dragToPosition( ImmutableVector3D immutableVector3D ) {
        position.set( immutableVector3D );

        // stop any velocity that was moving the pair
        velocity.set( new ImmutableVector3D() );
    }
}
