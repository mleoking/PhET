// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for biomolecules, which includes the DNA strand, polymerase,
 * and most (if not all) of the things that interact with one another in
 * this simulation.
 *
 * @author John Blanco
 */
public abstract class Biomolecule {

    protected final Property<Shape> shapeProperty;

    public Biomolecule( Shape initialShape ) {
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
