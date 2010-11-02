package edu.colorado.phet.gravityandorbits.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class Body {
    private Property<ImmutableVector2D> positionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    private Property<Double> diameterProperty = new Property<Double>( 50.0 );

    public Property<ImmutableVector2D> getPositionProperty() {
        return positionProperty;
    }

    public ImmutableVector2D getPosition() {
        return positionProperty.getValue();
    }

    public Property<Double> getDiameterProperty() {
        return diameterProperty;
    }

    public double getDiameter() {
        return diameterProperty.getValue();
    }

    public void translate( Point2D delta ) {
        translate( delta.getX(), delta.getY() );
    }

    public void translate( double dx, double dy ) {
        positionProperty.setValue( new ImmutableVector2D( getX() + dx, getY() + dy ) );
    }

    private double getY() {
        return positionProperty.getValue().getY();
    }

    private double getX() {
        return positionProperty.getValue().getX();
    }
}
