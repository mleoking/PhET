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
    public final Property<ImmutableVector2D> rotationPoint = new Property<ImmutableVector2D>( new ImmutableVector2D( -0.018626373626373614, 0.5091208791208807 ) );//Values sampled from a sim runtime

    //Model the angle of rotation, 0 degrees is straight up (not tilted)
    public final DoubleProperty angle = new DoubleProperty( 0.0 );

    //True if the user has selected this dispenser type
    public final Property<Boolean> enabled = new Property<Boolean>( false );

    public void rotate( double v ) {
        angle.add( v );
    }

    //Translate the dispenser, pointing it down if it is low enough
    public void translate( Dimension2D delta ) {
        ImmutableVector2D proposedPoint = rotationPoint.get().plus( delta );
        double y = MathUtil.clamp( 0.4, proposedPoint.getY(), Double.POSITIVE_INFINITY );
        rotationPoint.set( new ImmutableVector2D( proposedPoint.getX(), y ) );
        double yRotate = 0.5;//Below this y-value, the sugar dispenser will rotate
        if ( rotationPoint.get().getY() < yRotate ) {
            double amountPast = yRotate - rotationPoint.get().getY();
            angle.set( amountPast * 20 );
        }
    }

    //Reset the dispenser's position and orientation
    public void reset() {
        //Only need to set the primary properties, others (e.g., open/enabled) are derived and will auto-reset
        rotationPoint.reset();
        angle.reset();
    }

    //Give the crystal an appropriate velocity when it comes out so it arcs.  This method is used by subclasses when creating crystals
    protected ImmutableVector2D getCrystalVelocity( ImmutableVector2D outputPoint ) {
        ImmutableVector2D directionVector = outputPoint.minus( rotationPoint.get() );
        double anglePastTheHorizontal = angle.get() - Math.PI / 2;
        return directionVector.getInstanceOfMagnitude( 0.4 + 0.3 * Math.sin( anglePastTheHorizontal ) );
    }
}