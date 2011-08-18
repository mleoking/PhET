// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;

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

    /**
     * Determine whether the given biomolecule matches the one that this hint is
     * meant to represent.
     *
     * @param testBiomolecule
     * @return
     */
    public boolean isMatchingBiomolecule( MobileBiomolecule testBiomolecule ) {
        boolean match = false;
        if ( testBiomolecule.getClass() == this.biomolecule.getClass() ) {
            if ( this.biomolecule instanceof TranscriptionFactor ) {
                // There are multiple configurations of transcription factor,
                // so we need to test whether this one matches.
                // TODO: This test is kind of hokey.  As the class and behavior
                // of transcription factors evolve some better way many come
                // along.
                if ( this.biomolecule.getBaseColor() == testBiomolecule.getBaseColor() ) {
                    match = true;
                }
            }
            else {
                // All other biomolecules are considered a match based on
                // type alone.
                match = true;
            }
        }
        return match;
    }
}
