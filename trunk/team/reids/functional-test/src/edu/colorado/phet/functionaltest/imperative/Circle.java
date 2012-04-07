// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.imperative;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public class Circle {
    public final Property<Vector2D> position;
    public final Property<Boolean> dragging;
    public final double radius;

    public Circle( Vector2D initialPosition ) {
        this.position = new Property<Vector2D>( initialPosition );
        this.dragging = new Property<Boolean>( false );
        this.radius = 50;
    }

    @Override public String toString() {
        return position.get().toString() + ", dragging = " + dragging.get().toString() + ", radius = " + radius;
    }

    public void stepInTime( final double simulationTimeChange ) {
        if ( !dragging.get() ) {
            position.set( position.get().plus( 1, 1 ) );
        }
    }
}