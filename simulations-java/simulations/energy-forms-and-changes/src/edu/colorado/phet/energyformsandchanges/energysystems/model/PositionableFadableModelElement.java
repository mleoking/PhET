// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Base class for model elements that can be positioned and faded.
 *
 * @author John Blanco
 */
public abstract class PositionableFadableModelElement extends PositionableModelElement {

    public final Property<Double> opacity;

    public PositionableFadableModelElement() {
        this( new Vector2D( 0, 0 ), 1 );
    }

    public PositionableFadableModelElement( Vector2D initialPosition, double initialOpacity ) {
        super( initialPosition );
        this.opacity = new Property<Double>( initialOpacity );
    }
}
