// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.common;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for elements in the model that change shape or move around.
 *
 * @author John Blanco
 */
public abstract class ShapeChangingModelElement {

    // Shape property, which is not public because it should only be changed
    // by descendants of the class.
    protected final Property<Shape> shapeProperty;

    public ShapeChangingModelElement( Shape initialShape ) {
        this.shapeProperty = new Property<Shape>( initialShape );
    }

    public Shape getShape() {
        return shapeProperty.get();
    }

    public void addShapeChangeObserver( VoidFunction1<Shape> shapeChangeObserver ) {
        shapeProperty.addObserver( shapeChangeObserver );
    }

    public void removeShapeChangeObserver( VoidFunction1<Shape> shapeChangeObserver ) {
        shapeProperty.removeObserver( shapeChangeObserver );
    }
}
