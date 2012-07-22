// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A group of electron pairs. The pairs may be part of a bond, or may be a lone electron pair.
 */
public class PairGroup {
    /*---------------------------------------------------------------------------*
    * constants
    *----------------------------------------------------------------------------*/
    public static final double BONDED_PAIR_DISTANCE = 10.0;
    public static final double LONE_PAIR_DISTANCE = 7.0;

    public static final double ELECTRON_PAIR_REPULSION_SCALE = 30000;
    public static final double ANGLE_REPULSION_SCALE = 3;
    public static final double JITTER_SCALE = 0.001;
    public static final double DAMPING_FACTOR = 0.1;

    // TODO: this is horrible. refactor it!
    public static final double REAL_TMP_SCALE = 5.5; // TODO: deal with units correctly in the 1st tab model so we can remove this

    /*---------------------------------------------------------------------------*
    * instance data
    *----------------------------------------------------------------------------*/
    public final Property<Vector3D> position;
    public final Property<Vector3D> velocity = new Property<Vector3D>( new Vector3D() );

    public final boolean isLonePair;

    public final Property<Boolean> userControlled;

    /**
     * @param position     Initial 3D position
     * @param startDragged Whether it is starting as a dragged object
     *                     TODO: simplify this constructor. most of it not needed. Consider a "LonePair" class?
     */
    public PairGroup( Vector3D position, boolean isLonePair, boolean startDragged ) {
        this.position = new Property<Vector3D>( position );
        this.isLonePair = isLonePair;
        userControlled = new Property<Boolean>( startDragged );

        this.position.addObserver( new ChangeObserver<Vector3D>() {
            public void update( Vector3D newValue, Vector3D oldValue ) {
                if ( Double.isNaN( newValue.getX() ) ) {
                    throw new RuntimeException( "NaN detected in position!" );
                }
                if ( oldValue.equals( new Vector3D() ) ) {
                    throw new RuntimeException( "central molecule position change?" );
                }
            }
        } );
        this.velocity.addObserver( new ChangeObserver<Vector3D>() {
            public void update( Vector3D newValue, Vector3D oldValue ) {
                if ( Double.isNaN( newValue.getX() ) ) {
                    throw new RuntimeException( "NaN detected in velocity!" );
                }
            }
        } );
    }

    public void attractToIdealDistance( double timeElapsed, double oldDistance, Bond<PairGroup> bond ) {
        if ( userControlled.get() ) {
            // don't process if being dragged
            return;
        }
        Vector3D origin = bond.getOtherAtom( this ).position.get();

        boolean isTerminalLonePair = !origin.equals( new Vector3D() );

        double idealDistanceFromCenter = bond.length * REAL_TMP_SCALE;

        /*---------------------------------------------------------------------------*
        * prevent movement away from our ideal distance
        *----------------------------------------------------------------------------*/
        double currentError = Math.abs( ( position.get().minus( origin ) ).getMagnitude() - idealDistanceFromCenter );
        double oldError = Math.abs( oldDistance - idealDistanceFromCenter );
        if ( currentError > oldError ) {
            // our error is getting worse! for now, don't let us slide AWAY from the ideal distance ever
            // set our distance to the old one, so it is easier to process
            position.set( position.get().normalized().times( oldDistance ).plus( origin ) );
        }

        /*---------------------------------------------------------------------------*
        * use damped movement towards our ideal distance
        *----------------------------------------------------------------------------*/
        Vector3D toCenter = position.get().minus( origin );

        double distance = toCenter.getMagnitude();
        Vector3D directionToCenter = toCenter.normalized();

        double offset = idealDistanceFromCenter - distance;

        // just modify position for now so we don't get any oscillations
        double ratioOfMovement = Math.pow( 0.1, 0.016 / timeElapsed ); // scale this exponentially by how much time has elapsed, so the more time taken, the faster we move towards the ideal distance
        if ( isTerminalLonePair ) {
            ratioOfMovement = 1;
        }
        position.set( position.get().plus( directionToCenter.times( ratioOfMovement * offset ) ) );
    }

    private double interpolate( double a, double b, double ratio ) {
        return a * ( 1 - ratio ) + b * ratio;
    }

    /**
     * @param other                    The pair group whose force on this object we want
     * @param timeElapsed              Time elapsed (thus we return an impulse instead of a force)
     * @param trueLengthsRatioOverride From 0 to 1. If 0, lone pairs will behave the same as bonds. If 1, lone pair distance will be taken into account
     * @return Repulsion force on this pair group, from the other pair group
     */
    public Vector3D getRepulsionImpulse( PairGroup other, double timeElapsed, double trueLengthsRatioOverride ) {
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
        double adjustedMagnitude = interpolate( BONDED_PAIR_DISTANCE, position.get().getMagnitude(), trueLengthsRatioOverride );
        double adjustedOtherMagnitude = interpolate( BONDED_PAIR_DISTANCE, other.position.get().getMagnitude(), trueLengthsRatioOverride );

        // adjusted positions
        Vector3D adjustedPosition = position.get().normalized().times( adjustedMagnitude );
        Vector3D adjustedOtherPosition = other.position.get().getMagnitude() == 0 ? new Vector3D() : other.position.get().normalized().times( adjustedOtherMagnitude );

        // from other => this (adjusted)
        Vector3D delta = adjustedPosition.minus( adjustedOtherPosition );

        /*---------------------------------------------------------------------------*
        * coulomb repulsion
        *----------------------------------------------------------------------------*/

        double repulsionFactor = 1;

        // mimic Coulomb's Law
        Vector3D coulombVelocityDelta = delta.normalized().times( timeElapsed * ELECTRON_PAIR_REPULSION_SCALE * repulsionFactor / ( delta.getMagnitude() * delta.getMagnitude() ) );

        // apply a nonphysical reduction on coulomb's law when the frame-rate is low, so we can avoid oscillation
        double coulombDowngrade = getTimescaleImpulseFactor( timeElapsed ); // TODO: isolate the "standard" tpf?
        return coulombVelocityDelta.times( coulombDowngrade );
    }

    // helps avoid oscillation when the frame-rate is low, due to how the damping is implemented
    public static double getTimescaleImpulseFactor( double timeElapsed ) {
        return Math.sqrt( ( timeElapsed > 0.017 ) ? 0.017 / timeElapsed : 1 );
    }

    public void repulseFrom( PairGroup other, double timeElapsed, double trueLengthsRatioOverride ) {
        addVelocity( getRepulsionImpulse( other, timeElapsed, trueLengthsRatioOverride ) );
    }

    public void addVelocity( Vector3D velocityChange ) {
        // don't allow velocity changes if we are dragging it, OR if it is an atom at the origin
        if ( !userControlled.get() && !isCentralAtom() ) {
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
        if ( position.get().getMagnitude() > 0 ) {
            velocity.set( velocity.get().minus( position.get().normalized().times( velocityMagnitudeOutwards ) ) ); // subtract the outwards-component out
        }

        // move position forward by scaled velocity
        position.set( position.get().plus( velocity.get().times( timeElapsed ) ) );

        // add in damping so we don't get the kind of oscillation that we are seeing
        double damping = 1 - DAMPING_FACTOR;
        damping = Math.pow( damping, timeElapsed / 0.017 ); // based so that we have no modification at 0.017
        velocity.set( velocity.get().times( damping ) );
    }

    /**
     * Returns a unit vector that is the component of "vector" that is perpendicular to the "position" vector
     */
    public static Vector3D getTangentDirection( Vector3D position, Vector3D vector ) {
        Vector3D normalizedPosition = position.normalized();
        return vector.minus( normalizedPosition.times( vector.dot( normalizedPosition ) ) );
    }

    public void dragToPosition( Vector3D vector3D ) {
        position.set( vector3D );

        // stop any velocity that was moving the pair
        velocity.set( new Vector3D() );
    }

    public boolean isCentralAtom() {
        return !isLonePair && position.get().equals( new Vector3D() );
    }
}
