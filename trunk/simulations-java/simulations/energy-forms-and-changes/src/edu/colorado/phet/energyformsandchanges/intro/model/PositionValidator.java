// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;

/**
 * Interface that allows a model element to validate a position before moving
 * to it.
 *
 * @author John Blanco
 */
public interface PositionValidator {
    boolean isValidPosition( UserMovableModelElement modelElement, Shape projectedPosition );
}
