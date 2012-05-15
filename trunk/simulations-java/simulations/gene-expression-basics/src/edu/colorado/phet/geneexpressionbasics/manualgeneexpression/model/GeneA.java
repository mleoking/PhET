// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Specific instance of a gene.
 *
 * @author John Blanco
 */
public class GeneA extends Gene {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final Color REGULATORY_REGION_COLOR = new Color( 216, 191, 216 );
    private static final Color TRANSCRIBED_REGION_COLOR = new Color( 255, 165, 79, 150 );
    private static final int NUM_BASE_PAIRS_IN_REGULATORY_REGION = 16;
    private static final int NUM_BASE_PAIRS_IN_TRANSCRIBED_REGION = 100;
    public static final int NUM_BASE_PAIRS = NUM_BASE_PAIRS_IN_REGULATORY_REGION + NUM_BASE_PAIRS_IN_TRANSCRIBED_REGION;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param dnaMolecule     The DNA molecule within which this gene
     *                        exists.
     * @param initialBasePair Location on the DNA strand where this gene
     *                        starts.
     */
    public GeneA( DnaMolecule dnaMolecule, int initialBasePair ) {
        super( dnaMolecule,
               new IntegerRange( initialBasePair, initialBasePair + NUM_BASE_PAIRS_IN_REGULATORY_REGION ),
               REGULATORY_REGION_COLOR,
               new IntegerRange( initialBasePair + NUM_BASE_PAIRS_IN_REGULATORY_REGION + 1, initialBasePair + NUM_BASE_PAIRS_IN_REGULATORY_REGION + 1 + NUM_BASE_PAIRS_IN_TRANSCRIBED_REGION ),
               TRANSCRIBED_REGION_COLOR
        );

        // Add transcription factors that are specific to this gene.  Location
        // is withing the regulatory region, and the negative factor should
        // overlap, and thus block, the positive factor(s).
        addTranscriptionFactor( 5, TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_POS );
        addTranscriptionFactor( 2, TranscriptionFactor.TRANSCRIPTION_FACTOR_CONFIG_GENE_1_NEG );
    }

    @Override public Protein getProteinPrototype() {
        return new ProteinA();
    }
}
