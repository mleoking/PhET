// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.common;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;

/**
 * This class represents a "placement hint" in the model, which is a location
 * where a biomolecule of the provided type can be placed.
 *
 * @author John Blanco
 */
public class PlacementHint extends ShapeChangingModelElement {

    public final BooleanProperty active = new BooleanProperty( false );
    private final MobileBiomolecule biomolecule;

    public PlacementHint( MobileBiomolecule biomolecule ) {
        super( biomolecule.getShape() );
        this.biomolecule = biomolecule;
    }

    public Color getBaseColor() {
        return biomolecule.getBaseColor();
    }

    public boolean matchClass( MobileBiomolecule testBiomolecule ) {
        return testBiomolecule.getClass() == this.biomolecule.getClass();
    }
}
