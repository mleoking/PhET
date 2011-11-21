// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates;

import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Ribosome;

/**
 * State that defines the behavior for a biomolecule that is translating
 * messenger RNA.  Only ribosomes do this.
 *
 * @author John Blanco
 */
public class TranslatingMRnaState extends BiomoleculeBehaviorState {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double TRANSLATION_RATE = 1000; // In picometers/sec.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------
    private final Ribosome ribosome;
    private final MessengerRna messengerRna;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public TranslatingMRnaState( MessengerRna messengerRna, Ribosome ribosome ) {
        super( messengerRna );
        this.messengerRna = messengerRna;
        this.ribosome = ribosome;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {
        // Advance the translation of the mRNA, which essentially pulls it
        // through the translation channel of the ribosome.
        if ( messengerRna.advanceTranslation( ribosome, TRANSLATION_RATE * dt ) ) {
            // This returned true, which signifies that translation is
            // complete.  Release the mRNA and transition to detaching state.
            messengerRna.releaseFromRibosome( ribosome );
            return new DetachingState( ribosome );
        }
        else {
            // Make sure that the ribosome is correctly positioned with respect
            // to the mRNA.
            ribosome.setPositionOfTranslationChannel( messengerRna.getRibosomeAttachmentPoint( ribosome ) );

            // Grow the protein.
            ribosome.setProteinGrowth( messengerRna.getProportionOfRnaTranslated( ribosome ) );

            // Still translating, so no state change.
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // Does not attach while in this state.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        return new UnattachedAndAvailableState( biomolecule );
    }
}
