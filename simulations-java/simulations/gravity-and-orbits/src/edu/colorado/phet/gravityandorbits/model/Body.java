package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class Body {
    private final Property<ImmutableVector2D> positionProperty;
    private final Property<Double> diameterProperty;
    private final String name;
    private final Color color;
    private final Color highlight;

    public Body( String name, double x, double y, double diameter, Color color, Color highlight) {
        this.name = name;
        this.color = color;
        this.highlight = highlight;
        positionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        diameterProperty = new Property<Double>( diameter );
    }

    public Color getColor() {
        return color;
    }

    public Color getHighlight() {
        return highlight;
    }

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

    public String getName() {
        return name;
    }

    public void setDiameter( double value ) {
        diameterProperty.setValue( value );
    }
}
