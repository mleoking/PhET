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
        // Stay still - otherwise, things get too complicated with keeping
        // the mRNA in the right place, since multiple ribosomes can be
        // transcribing at the same time.
        if ( messengerRna.advanceTranslation( ribosome, TRANSLATION_RATE * dt ) ) {
            // This returned true, which signifies that translation is
            // complete.  Release the mRNA and transition to detaching state.
            messengerRna.releaseFromRibsome( ribosome );
            return new DetachingState( ribosome );
        }
        else {
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
