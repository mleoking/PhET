// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Abstract base class for different types of sensor that read a value at their (x,y) location.
 *
 * @author Sam Reid
 * @see PressureSensor
 */
public abstract class Sensor<T> {

    public final Property<Vector2D> location;
    protected final Property<Option<T>> value;
    public final IUserComponent userComponent;

    public Sensor( double x, double y, Option<T> value, final IUserComponent userComponent ) {
        this.userComponent = userComponent;
        location = new Property<Vector2D>( new Vector2D( x, y ) );
        this.value = new Property<Option<T>>( value );
    }

    public void reset() {
        location.reset();
        value.reset();
    }

    public Option<T> getValue() {
        return value.get();
    }

    protected void setValue( Option<T> value ) {
        this.value.set( value );
    }

    public void addValueObserver( SimpleObserver observer ) {
        value.addObserver( observer );
    }
}
