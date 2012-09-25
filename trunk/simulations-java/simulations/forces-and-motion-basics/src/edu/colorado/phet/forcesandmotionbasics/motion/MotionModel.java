package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.F;
import fj.Unit;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

/**
 * @author Sam Reid
 */
public class MotionModel {

    public final DoubleProperty appliedForce = new DoubleProperty( 0.0 );
    public final DoubleProperty frictionForce = new DoubleProperty( 0.0 );
    public final DoubleProperty sumOfForces = new DoubleProperty( 0.0 );
    public final DoubleProperty velocity = new DoubleProperty( 0.0 );
    public final DoubleProperty position = new DoubleProperty( 0.0 );
    public final Property<Option<Double>> speed = new Property<Option<Double>>( new Some<Double>( 0.0 ) );
    private final boolean friction;
    public final F<Unit, Double> massOfObjectsOnSkateboard;

    //Only used in Tab 3 "Friction"
    public final SettableProperty<Double> frictionValue = new Property<Double>( FrictionSliderControl.MAX / 2 );

    public MotionModel( boolean friction, final F<Unit, Double> massOfObjectsOnSkateboard ) {
        this.friction = friction;
        this.massOfObjectsOnSkateboard = massOfObjectsOnSkateboard;
    }

    public void stepInTime( final double dt ) {
        double appliedForce = this.appliedForce.get();
        double frictionForce = getFrictionForce( appliedForce );
        this.frictionForce.set( frictionForce );
        double sumOfForces = frictionForce + appliedForce;
        this.sumOfForces.set( sumOfForces );
//        System.out.println( "applied: " + appliedForce + ", friction: " + frictionForce + ", sum = " + sumOfForces );

        final double mass = massOfObjectsOnSkateboard.f( Unit.unit() );
        double acceleration = mass != 0 ? sumOfForces / mass : 0.0;

        double newVelocity = velocity.get() + acceleration * dt;

        //friction force should not be able to make the object move backwards
        if ( MathUtil.getSign( newVelocity ) != MathUtil.getSign( velocity.get() ) && appliedForce == 0 ) {
            newVelocity = 0.0;
        }

        velocity.set( newVelocity );
        position.set( position.get() + velocity.get() * dt );
        speed.set( new Some<Double>( Math.abs( velocity.get() ) ) );
    }

    private double getFrictionForce( final double appliedForce ) {
        if ( !friction ) { return 0.0; }
        double frictionForce = Math.abs( frictionValue.get() ) * MathUtil.getSign( appliedForce );
        if ( Math.abs( velocity.get() ) < 1E-6 && frictionForce > appliedForce ) {
            frictionForce = appliedForce;
        }
        else if ( Math.abs( velocity.get() ) > 1E-6 ) {
            frictionForce = MathUtil.getSign( velocity.get() ) * frictionValue.get();
        }
        return -frictionForce;
    }
}