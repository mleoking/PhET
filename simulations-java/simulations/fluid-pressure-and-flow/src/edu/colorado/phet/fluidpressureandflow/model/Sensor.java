package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class Sensor {
    protected Property<ImmutableVector2D> locationProperty;

    public Sensor( double x, double y ) {
        this.locationProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
    }

    public double getX() {
        return getLocation().getX();
    }

    public ImmutableVector2D getLocation() {
        return locationProperty.getValue();
    }

    public double getY() {
        return getLocation().getY();
    }

    public void setPosition( double x, double y ) {
        locationProperty.setValue( new ImmutableVector2D( x, y ) );
    }

    public void addPositionObserver( SimpleObserver updatePosition ) {
        locationProperty.addObserver( updatePosition );
    }

    public void reset() {
        locationProperty.reset();
    }

    public Property<ImmutableVector2D> getLocationProperty() {
        return locationProperty;
    }
}
