// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Abstract base class for different types of sensor that read a value at their (x,y) location.
 *
 * @author Sam Reid
 * @see PressureSensor
 */
public abstract class Sensor<T> {

    public final Property<ImmutableVector2D> location;
    protected final Property<T> value;
    public final IUserComponent userComponent;

    public Sensor( double x, double y, T value, final IUserComponent userComponent ) {
        this.userComponent = userComponent;
        location = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        this.value = new Property<T>( value );
    }

    public void reset() {
        location.reset();
        value.reset();
    }

    public T getValue() {
        return value.get();
    }

    protected void setValue( T value ) {
        this.value.set( value );
    }

    public void addValueObserver( SimpleObserver observer ) {
        value.addObserver( observer );
    }
}
