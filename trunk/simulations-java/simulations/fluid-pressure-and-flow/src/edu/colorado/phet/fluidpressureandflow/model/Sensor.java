package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class Sensor {
    protected Property<Double> xProperty;
    protected Property<Double> yProperty;

    public Sensor( double x, double y ) {
        this.xProperty = new Property<Double>( x );
        this.yProperty = new Property<Double>( y );
    }

    public double getX() {
        return xProperty.getValue();
    }

    public double getY() {
        return yProperty.getValue();
    }

    public void setPosition( double x, double y ) {
        xProperty.setValue( x );
        yProperty.setValue( y );
    }

    public Point2D getPosition() {
        return new Point2D.Double( getX(), getY() );
    }

    public void addPositionObserver( SimpleObserver updatePosition ) {
        xProperty.addObserver( updatePosition );
        yProperty.addObserver( updatePosition );
    }

    public void reset() {
        xProperty.reset();
        yProperty.reset();
    }
}
