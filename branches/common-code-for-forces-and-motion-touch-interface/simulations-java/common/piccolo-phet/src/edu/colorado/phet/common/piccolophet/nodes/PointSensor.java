// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * Sensor that reads value at a single point.  The value is of type T.
 *
 * @author Sam Reid
 */
public class PointSensor<T> {
    public final Property<Vector2D> position;
    public final Property<Option<T>> value = new Property<Option<T>>( new Option.None<T>() );

    public PointSensor( double x, double y ) {
        position = new Property<Vector2D>( new Vector2D( x, y ) );
    }

    public void translate( Dimension2D delta ) {
        position.set( position.get().plus( delta.getWidth(), delta.getHeight() ) );
    }

    public void reset() {
        position.reset();
        value.reset();
    }
}
