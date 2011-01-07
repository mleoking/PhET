// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public abstract class Sensor<T> {

    protected Property<ImmutableVector2D> locationProperty;
    protected Property<T> valueProperty;

    public Sensor( double x, double y, T value ) {
        locationProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        valueProperty = new Property<T>( value );
    }

    public void reset() {
        locationProperty.reset();
        valueProperty.reset();
    }

    public ImmutableVector2D getLocation() {
        return locationProperty.getValue();
    }

    public double getX() {
        return getLocation().getX();
    }

    public double getY() {
        return getLocation().getY();
    }

    public void setLocation( double x, double y ) {
        locationProperty.setValue( new ImmutableVector2D( x, y ) );
    }

    public void addLocationObserver( SimpleObserver observer ) {
        locationProperty.addObserver( observer );
    }

    public Property<ImmutableVector2D> getLocationProperty() {
        return locationProperty;
    }

    public T getValue() {
        return valueProperty.getValue();
    }

    protected void setValue( T value ) {
        valueProperty.setValue( value );
    }

    public abstract double getScalarValue();

    public void addValueObserver( SimpleObserver observer ) {
        valueProperty.addObserver( observer );
    }
}
