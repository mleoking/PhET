// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;

/**
 * Base class for sugar and salt dispensers
 *
 * @author Sam Reid
 */
public class Dispenser {
    //Start centered above the fluid
    public final Property<ImmutableVector2D> center;

    //Model the angle of rotation, 0 degrees is straight up (not tilted)
    public final DoubleProperty angle;

    //True if the user has selected this dispenser type
    public final Property<Boolean> enabled = new Property<Boolean>( false );
    protected final Beaker beaker;

    public Dispenser( double x, double y, double angle, Beaker beaker ) {
        this.beaker = beaker;
        this.angle = new DoubleProperty( angle );
        center = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
    }

    //Translate the dispenser by the specified delta in model coordinates
    public void translate( Dimension2D delta ) {

        //Translate the center, but make sure it doesn't go out of bounds
        ImmutableVector2D proposedPoint = center.get().plus( delta );
        double y = MathUtil.clamp( beaker.getTopY(), proposedPoint.getY(), Double.POSITIVE_INFINITY );
        center.set( new ImmutableVector2D( proposedPoint.getX(), y ) );
    }

    public void rotate( double v ) {
        angle.add( v );
    }

    //Reset the dispenser's position and orientation
    public void reset() {
        //Only need to set the primary properties, others (e.g., open/enabled) are derived and will auto-reset
        center.reset();
        angle.reset();
    }

    //Give the crystal an appropriate velocity when it comes out so it arcs.  This method is used by subclasses when creating crystals
    protected ImmutableVector2D getCrystalVelocity( ImmutableVector2D outputPoint ) {
        ImmutableVector2D directionVector = outputPoint.minus( center.get() );
        double anglePastTheHorizontal = angle.get() - Math.PI / 2;
        return directionVector.getInstanceOfMagnitude( 0.2 + 0.3 * Math.sin( anglePastTheHorizontal ) );
    }
}