// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;

/**
 * Model element for the sugar dispenser, which includes its position and rotation.
 *
 * @author Sam Reid
 */
public class SugarDispenser {
    //Start centered above the fluid
    public final Property<ImmutableVector2D> rotationPoint = new Property<ImmutableVector2D>( new ImmutableVector2D( -0.018626373626373614, 0.5091208791208807 ) );//Values sampled from a sim runtime
    public final DoubleProperty angle = new DoubleProperty( 0.0 );

    public void rotate( double v ) {
        angle.add( v );
    }

    public void translate( Dimension2D delta ) {
        ImmutableVector2D proposedPoint = rotationPoint.get().plus( delta );
        double y = MathUtil.clamp( 0.4, proposedPoint.getY(), Double.POSITIVE_INFINITY );
        rotationPoint.set( new ImmutableVector2D( proposedPoint.getX(), y ) );
        double yRotate = 0.5;//Below this y-value, the sugar dispenser will rotate
        if ( rotationPoint.get().getY() < yRotate ) {
            double amountPast = yRotate - rotationPoint.get().getY();
            angle.set( -amountPast * 20 );
        }
    }
}
