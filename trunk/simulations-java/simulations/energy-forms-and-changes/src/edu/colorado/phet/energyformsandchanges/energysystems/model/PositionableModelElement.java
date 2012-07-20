// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A model element that has a position (a.k.a. a location) which can be
 * changed.
 *
 * @author John Blanco
 */
public abstract class PositionableModelElement {
    private final Property<Vector2D> position = new Property<Vector2D>( new Vector2D( 0, 0 ) );

    public PositionableModelElement() {
        // Default constructor - uses default initial position.
    }

    public PositionableModelElement( Vector2D initialPosition ) {
        position.set( initialPosition );
    }

    public void setPosition( Vector2D newPosition ) {
        position.set( newPosition );
    }

    public Vector2D getPosition() {
        return new Vector2D( position.get() );
    }

    public ObservableProperty<Vector2D> getObservablePosition() {
        return position;
    }
}
