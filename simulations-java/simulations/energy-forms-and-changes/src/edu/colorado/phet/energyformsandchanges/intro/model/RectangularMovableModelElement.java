// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * A movable model element that, at least in the model, has an overall shape
 * that can be represented as a rectangle.
 *
 * @author John Blanco
 */
public abstract class RectangularMovableModelElement extends UserMovableModelElement {

    /**
     * Constructor.
     */
    public RectangularMovableModelElement( ImmutableVector2D initialPosition ) {
        super( initialPosition );
    }

    /**
     * Get the rectangle that defines this elements position and shape in
     * model space.
     */
    public abstract Rectangle2D getRect();
}
