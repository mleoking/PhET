// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;

/**
 * State that controls a biomolecule's behavior when it is transcribing DNA.
 * This state is only used by RNA polymerase, none of the other biomolecules
 * ever transcribe DNA.
 *
 * @author John Blanco
 */
public class TranscribingDnaState extends BiomoleculeBehaviorState {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double VELOCITY = 750; // In picometers per second.

    // Amount of time required to change from one conformation to another.
    private static final double CONFORMATIONAL_CHANGE_TIME = 0.5; // In seconds.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final double transcribedRegionLength;
    private final AttachmentSite attachmentSite;

    private double distanceTraveled = 0;

    private double degreeOfConformationalChange = 0;

    private final MessengerRna messengerRna;

    // Offset from the center of the biomolecule that is synthesizing the mRNA
    // to the place where the mRNA should appear.
    private final ImmutableVector2D messengerRnaEmergenceOffset;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public TranscribingDnaState( MobileBiomolecule biomolecule, AttachmentSite attachmentSite, Gene geneBeingTranscribed, ImmutableVector2D messengerRnaEmergenceOffset ) {
        super( biomolecule );
        this.attachmentSite = attachmentSite;
        this.messengerRnaEmergenceOffset = messengerRnaEmergenceOffset;
        transcribedRegionLength = geneBeingTranscribed.getTranscribedRegionLength();
        // Create the mRNA molecule that will be grown during the transcription
        // process.
        messengerRna = new MessengerRna( biomolecule.getModel(), biomolecule.getPosition() );
        biomolecule.spawnMolecule( messengerRna );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {

        if ( degreeOfConformationalChange < 1 && distanceTraveled == 0 ) {
            // The molecule is in the process of changing to the "transcribing"
            // conformation.
            degreeOfConformationalChange = Math.min( degreeOfConformationalChange + dt / CONFORMATIONAL_CHANGE_TIME, 1 );
            biomolecule.changeConformation( degreeOfConformationalChange );
        }
        else if ( distanceTraveled < transcribedRegionLength ) {
            // This polymerase molecule is transcribing the gene.
            biomolecule.setPosition( biomolecule.getPosition().getX() + dt * VELOCITY, biomolecule.getPosition().getY() );
            distanceTraveled += dt * VELOCITY;
            messengerRna.addLength( biomolecule.getPosition().getX() + messengerRnaEmergenceOffset.getX(),
                                    biomolecule.getPosition().getY() + messengerRnaEmergenceOffset.getY() );
        }
        else if ( degreeOfConformationalChange > 0 ) {
            // The molecule is changing back to the non-transcribing conformation.
            degreeOfConformationalChange = Math.max( degreeOfConformationalChange - dt / CONFORMATIONAL_CHANGE_TIME, 0 );
            biomolecule.changeConformation( degreeOfConformationalChange );
        }
        else {
            // The molecule has completed the entire transcription process.
            stopTranscription();
            // Detach and drift upwards.
            return new DetachingState( biomolecule, new ImmutableVector2D( 0, 1 ) );
        }

        // If we made it to here, there is no state change.
        return this;
    }

    private void stopTranscription() {
        // Release the transcribed molecule.
        messengerRna.release();
        // Release from the attachment site.
        attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
        // Make sure it is back to the nominal conformation.
        biomolecule.changeConformation( 0 );
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // Does not consider other attachments while transcribing.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        stopTranscription();
        return new UnattachedAndAvailableState( biomolecule );
    }
}
