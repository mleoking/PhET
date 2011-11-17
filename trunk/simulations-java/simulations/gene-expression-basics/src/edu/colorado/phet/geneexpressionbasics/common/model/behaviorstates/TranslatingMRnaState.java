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
    private double amountTranslated = 0; // In picometers.
    private final double mRnaStrandLength;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public TranslatingMRnaState( MessengerRna messengerRna, Ribosome ribosome ) {
        super( messengerRna );
        this.messengerRna = messengerRna;
        this.ribosome = ribosome;
        mRnaStrandLength = messengerRna.getLength();
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {
        // Stay still - otherwise, things get too complicated with keeping
        // the mRNA in the right place, since multiple ribosomes can be
        // transcribing at the same time.
        // TODO: This is prototype code to get things working.  Definitely not final.
        messengerRna.advanceTranslation( ribosome, TRANSLATION_RATE * dt );
        amountTranslated += TRANSLATION_RATE * dt;
        if ( amountTranslated >= mRnaStrandLength ) {
            // Translation complete.
            System.out.println( "Ribosome is detaching." );
            messengerRna.release();
            return new DetachingState( ribosome );
        }
        // Still translating, so no state change.
        return this;
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // Does not attach while in this state.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        return new UnattachedAndAvailableState( biomolecule );
    }
}
