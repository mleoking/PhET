// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A model element that has a position (a.k.a. a location) which can be
 * changed.
 *
 * @author John Blanco
 */
public abstract class PositionableModelElement {
    private final Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );

    public PositionableModelElement() {
        // Default constructor - uses default initial position.
    }

    public PositionableModelElement( ImmutableVector2D initialPosition ) {
        position.set( initialPosition );
    }

    public void setPosition( ImmutableVector2D newPosition ) {
        position.set( newPosition );
    }

    public ImmutableVector2D getPosition() {
        return new ImmutableVector2D( position.get() );
    }

    public ObservableProperty<ImmutableVector2D> getObservablePosition() {
        return position;
    }
}
