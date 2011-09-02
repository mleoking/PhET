// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;

import com.jme3.math.ColorRGBA;

/**
 * A group of electron pairs. The pairs may be part of a bond, or may be a lone electron pair.
 */
public class PairGroup {
    public static final double BONDED_PAIR_DISTANCE = 10.0;
    public static final double LONE_PAIR_DISTANCE = 7.0;

    public static final double ELECTRON_PAIR_REPULSION_SCALE = 30000;
    public static final double JITTER_SCALE = 0.001;
    public static final double DAMPING_FACTOR = 0.1;

    public final Property<ImmutableVector3D> position;
    public final Property<ImmutableVector3D> velocity = new Property<ImmutableVector3D>( new ImmutableVector3D() );

    public final boolean isLonePair;

    public final int bondOrder; // is zero if it is a lone pair
    public final Property<Boolean> userControlled;

    /**
     * @param position     Initial 3D position
     * @param bondOrder    Bond order, (0 if it is a lone pair)
     * @param startDragged Whether it is starting as a dragged object
     */
    public PairGroup( ImmutableVector3D position, int bondOrder, boolean startDragged ) {
        this.position = new Property<ImmutableVector3D>( position );
        this.bondOrder = bondOrder;
        this.isLonePair = bondOrder == 0;
        userControlled = new Property<Boolean>( startDragged );
    }

    public int getNumberOfPairs() {
        return bondOrder == 0 ? 1 : bondOrder;
    }

    public void attractToIdealDistance( double timeElapsed, double oldDistance ) {
        if ( userControlled.get() ) {
            // don't process if being dragged
            return;
        }

        double idealDistanceFromCenter = getIdealDistanceFromCenter();

        /*---------------------------------------------------------------------------*
        * prevent movement away from our ideal distance
        *----------------------------------------------------------------------------*/
        double currentError = Math.abs( position.get().magnitude() - idealDistanceFromCenter );
        double oldError = Math.abs( oldDistance - idealDistanceFromCenter );
        if ( currentError > oldError ) {
            // our error is getting worse! for now, don't let us slide AWAY from the ideal distance ever
            // set our distance to the old one, so it is easier to process
            position.set( position.get().normalized().times( oldDistance ) );
        }

        /*---------------------------------------------------------------------------*
        * use damped movement towards our ideal distance
        *----------------------------------------------------------------------------*/
        ImmutableVector3D toCenter = position.get();

        double distance = toCenter.magnitude();
        ImmutableVector3D directionToCenter = toCenter.normalized();

        double offset = idealDistanceFromCenter - distance;

        // just modify position for now so we don't get any oscillations
        double ratioOfMovement = Math.pow( 0.1, 0.016 / timeElapsed ); // scale this exponentially by how much time has elapsed, so the more time taken, the faster we move towards the ideal distance
        position.set( position.get().plus( directionToCenter.times( ratioOfMovement * offset ) ) );
    }

    private double interpolate( double a, double b, double ratio ) {
        return a * ( 1 - ratio ) + b * ratio;
    }

    public void repulseFrom( PairGroup other, double timeElapsed, double trueLengthsRatioOverride ) {
        // only handle the force on this object for now

        /*---------------------------------------------------------------------------*
        * adjust the logical positions when the repulsion modifier is less than 1
        *
        * (this allows us to get the "VSEPR" correct geometry even with lone pairs.
        * since lone pairs are closer in, an actual Coulomb model would diverge from
        * the VSEPR model angles. Here, we converge to the model VSEPR behavior, but
        * allow correct Coulomb calculations at greater distances
        *----------------------------------------------------------------------------*/

        // adjusted distances from the center atom
        double adjustedMagnitude = interpolate( BONDED_PAIR_DISTANCE, position.get().magnitude(), trueLengthsRatioOverride );
        double adjustedOtherMagnitude = interpolate( BONDED_PAIR_DISTANCE, other.position.get().magnitude(), trueLengthsRatioOverride );

        // adjusted positions
        ImmutableVector3D adjustedPosition = position.get().normalized().times( adjustedMagnitude );
        ImmutableVector3D adjustedOtherPosition = other.position.get().normalized().times( adjustedOtherMagnitude );

        // from other => this (adjusted)
        ImmutableVector3D delta = adjustedPosition.minus( adjustedOtherPosition );

        /*---------------------------------------------------------------------------*
        * coulomb repulsion
        *----------------------------------------------------------------------------*/

        double repulsionFactor = 1;

        // mimic Coulomb's Law
        ImmutableVector3D coulombVelocityDelta = delta.normalized().times( timeElapsed * ELECTRON_PAIR_REPULSION_SCALE * repulsionFactor / ( delta.magnitude() * delta.magnitude() ) );

        // apply a nonphysical reduction on coulomb's law when the frame-rate is low, so we can avoid oscillation
        double coulombDowngrade = Math.sqrt( ( timeElapsed > 0.017 ) ? 0.017 / timeElapsed : 1 ); // TODO: isolate the "standard" tpf?
        addVelocity( coulombVelocityDelta.times( coulombDowngrade ) );

        /*---------------------------------------------------------------------------*
        * angle-based repulsion
        *----------------------------------------------------------------------------*/

        /* kept for possible future tweaks
        double angle = Math.acos( position.get().normalized().dot( other.position.get().normalized() ) );

        ImmutableVector3D pushDirection = getTangentDirection( position.get(), delta ).normalized();

        double anglePushEstimate = estimatePush( angle ) - estimatePush( 2 * Math.PI - angle ); // push from the close direction (minus push from the LONG direction)

        double pushFactor = getPushFactor() * other.getPushFactor();
        ImmutableVector3D angleVelocityDelta = pushDirection.times( anglePushEstimate * pushFactor );
        addVelocity( angleVelocityDelta.times( 0 ) );
        */
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
        double damping = 1 - DAMPING_FACTOR;
        damping = Math.pow( damping, timeElapsed / 0.017 ); // based so that we have no modification at 0.017
        velocity.set( velocity.get().times( damping ) );

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
