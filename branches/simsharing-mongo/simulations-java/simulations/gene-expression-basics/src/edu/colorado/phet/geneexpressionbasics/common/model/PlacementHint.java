// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;

/**
 * This class represents a "placement hint" in the model, which is a location
 * where a biomolecule of the provided type can be placed and which will be
 * "hinted" to the user at the appropriate times.
 *
 * @author John Blanco
 */
public class PlacementHint extends ShapeChangingModelElement {

    // Biomolecule that defines the shape of this hint.
    private final MobileBiomolecule biomolecule;

    // Property that tracks whether or not the hint is should be visible to the
    // user.
    public final BooleanProperty active = new BooleanProperty( false );

    /**
     * Constructor.
     *
     * @param biomolecule
     */
    public PlacementHint( MobileBiomolecule biomolecule ) {
        super( biomolecule.getShape() );
        this.biomolecule = biomolecule;
    }

    public Color getBaseColor() {
        return biomolecule.colorProperty.get();
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
                // of transcription factors evolve some better way may come
                // along.
                if ( this.biomolecule.colorProperty.get() == testBiomolecule.colorProperty.get() ) {
                    match = true;
                }
            }
            else {
                // All other biomolecules are considered a match based on type
                // alone.
                match = true;
            }
        }
        return match;
    }

    /**
     * If the proffered test biomolecule is of the appropriate type, activate
     * this hint.
     *
     * @param testBiomolecule
     */
    public void activateIfMatch( MobileBiomolecule testBiomolecule ) {
        if ( isMatchingBiomolecule( testBiomolecule ) ) {
            active.set( true );
        }
    }
}
