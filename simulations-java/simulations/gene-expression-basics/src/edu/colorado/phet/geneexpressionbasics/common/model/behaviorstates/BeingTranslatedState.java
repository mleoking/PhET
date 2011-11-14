// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Ribosome;

/**
 * State that defines the behavior for a biomolecule that is being translated.
 * This state is only used by messenger RNA.
 *
 * @author John Blanco
 */
public class BeingTranslatedState extends BiomoleculeBehaviorState {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------
    private final Ribosome ribosome;
    private final MessengerRna messengerRna;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public BeingTranslatedState( MessengerRna messengerRna, Ribosome ribosome ) {
        super( messengerRna );
        this.messengerRna = messengerRna;
        this.ribosome = ribosome;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {
        // Follow the ribosome.
        // TODO: This behavior is not what this will ultimately really do.
        ImmutableVector2D currentMRnaFirstPointPosition = new ImmutableVector2D( messengerRna.getPointList().get( 0 ) );
        ImmutableVector2D motionDelta = ribosome.getEntranceOfRnaChannelPos().getSubtractedInstance( currentMRnaFirstPointPosition );
        messengerRna.translate( motionDelta );
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
