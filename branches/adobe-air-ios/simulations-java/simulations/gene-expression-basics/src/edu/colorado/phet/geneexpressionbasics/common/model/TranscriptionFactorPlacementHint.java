// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;

/**
 * Specialization of placement hint for transcription factors.
 *
 * @author John Blanco
 */
public class TranscriptionFactorPlacementHint extends PlacementHint {

    private final TranscriptionFactorConfig tfConfig;

    /**
     * Constructor.
     *
     * @param transcriptionFactor
     */
    public TranscriptionFactorPlacementHint( TranscriptionFactor transcriptionFactor ) {
        super( transcriptionFactor );
        this.tfConfig = transcriptionFactor.getConfig();
    }

    @Override public boolean isMatchingBiomolecule( MobileBiomolecule testBiomolecule ) {
        if ( testBiomolecule instanceof TranscriptionFactor ){
            // Configuration of transcription factor must match.
            return ((TranscriptionFactor)testBiomolecule).getConfig().equals( tfConfig );
        }
        return false;
    }

    public void activateIfConfigMatch( TranscriptionFactorConfig transcriptionFactorConfig ){
        if ( this.tfConfig.equals( transcriptionFactorConfig )){
            active.set( true );
        }
    }
}
