// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Specific instance of a gene.
 *
 * @author John Blanco
 */
public class GeneB extends Gene {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final Color REGULATORY_REGION_COLOR = new Color( 216, 191, 216 );
    private static final Color TRANSCRIBED_REGION_COLOR = new Color( 240, 246, 143, 150 );
    private static final int NUM_BASE_PAIRS_IN_REGULATORY_REGION = 16;
    private static final int NUM_BASE_PAIRS_IN_TRANSCRIBED_REGION = 150;
    private static final int ID = 2; // TODO: Can this be done away with?

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param dnaMolecule            The DNA molecule within which this gene
     *                               exists.
     * @param initialBasePair        Location on the DNA strand where this gene
     *                               starts.
     */
    public GeneB( DnaMolecule dnaMolecule, int initialBasePair ) {
        super( dnaMolecule,
               new IntegerRange( initialBasePair, initialBasePair + NUM_BASE_PAIRS_IN_REGULATORY_REGION ),
               REGULATORY_REGION_COLOR,
               new IntegerRange( initialBasePair + NUM_BASE_PAIRS_IN_REGULATORY_REGION + 1, initialBasePair + NUM_BASE_PAIRS_IN_REGULATORY_REGION + 1 + NUM_BASE_PAIRS_IN_TRANSCRIBED_REGION ),
               TRANSCRIBED_REGION_COLOR,
               ID );
    }
}
