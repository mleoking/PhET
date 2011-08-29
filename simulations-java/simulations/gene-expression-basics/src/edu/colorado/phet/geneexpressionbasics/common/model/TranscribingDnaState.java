// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;

/**
 * State that controls a biomolecule's behavior when it is transcribing DNA.
 *
 * @author John Blanco
 */
public class TranscribingDnaState extends BiomoleculeBehaviorState {

    private static final double VELOCITY = 500; // In picometers per second.

    private final double transcribedRegionLength;
    private final AttachmentSite attachmentSite;

    private double distanceTraveled = 0;

    public TranscribingDnaState( MobileBiomolecule biomolecule, AttachmentSite attachmentSite, Gene geneBeingTranscribed ) {
        super( biomolecule );
        this.attachmentSite = attachmentSite;
        transcribedRegionLength = geneBeingTranscribed.getTranscribedRegionLength();
        biomolecule.distortShape( 1 );
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt ) {

        biomolecule.setPosition( biomolecule.getPosition().getX() + dt * VELOCITY, biomolecule.getPosition().getY() );
        distanceTraveled += dt * VELOCITY;

        if ( distanceTraveled > transcribedRegionLength ) {
            // Release the attachment site.
            attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
            // Return to normal shape.
            biomolecule.distortShape( 0 );
            // Detach and drift upwards.
            return new DetachingState( biomolecule, new ImmutableVector2D( 0, 1 ) );
        }

        // If we made it to here, there is no state change.
        return this;
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites ) {
        // Does not consider other attachments while transcribing.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        biomolecule.distortShape( 0 );
        attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
        return new UnattachedAndAvailableState( biomolecule );
    }
}
