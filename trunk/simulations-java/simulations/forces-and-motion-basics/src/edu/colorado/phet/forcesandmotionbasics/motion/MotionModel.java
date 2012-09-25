package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.F;
import fj.Unit;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

/**
 * @author Sam Reid
 */
public class MotionModel {

    public final DoubleProperty appliedForce = new DoubleProperty( 0.0 );
    public final DoubleProperty velocity = new DoubleProperty( 0.0 );
    public final DoubleProperty position = new DoubleProperty( 0.0 );
    public final Property<Option<Double>> speed = new Property<Option<Double>>( new Some<Double>( 0.0 ) );
    public final F<Unit, Double> massOfObjectsOnSkateboard;

    public MotionModel( final F<Unit, Double> massOfObjectsOnSkateboard ) {
        this.massOfObjectsOnSkateboard = massOfObjectsOnSkateboard;
    }

    public void stepInTime( final double dt ) {
        double sumOfForces = appliedForce.get();
        final double mass = massOfObjectsOnSkateboard.f( Unit.unit() );
        double acceleration = mass != 0 ? sumOfForces / mass : 0.0;
        velocity.set( velocity.get() + acceleration * dt );
        position.set( position.get() + velocity.get() * dt );
        speed.set( new Some<Double>( Math.abs( velocity.get() ) ) );
    }
}