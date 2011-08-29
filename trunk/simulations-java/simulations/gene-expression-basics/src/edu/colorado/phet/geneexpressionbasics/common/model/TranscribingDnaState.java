// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;

/**
 * State the controls a biomolecule's behavior when it is transcribing DNA.
 *
 * @author John Blanco
 */
public class TranscribingDnaState extends BiomoleculeBehaviorState {

    private static final double VELOCITY = 500; // In picometers per second.

    private final double transcribedRegionLength;
    private final AttachmentSite attachmentSite;

    private double distanceTraveled = 0;

    public TranscribingDnaState( AttachmentSite attachmentSite, Gene geneBeingTranscribed ) {
        this.attachmentSite = attachmentSite;
        transcribedRegionLength = geneBeingTranscribed.getTranscribedRegionLength();
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt, MobileBiomolecule biomolecule ) {

        biomolecule.setPosition( biomolecule.getPosition().getX() + dt * VELOCITY, biomolecule.getPosition().getY() );
        distanceTraveled += dt * VELOCITY;

        if ( distanceTraveled > transcribedRegionLength ) {
            // Release the attachment site.
            attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
            // Detach and drift upwards.
            return new DetachingState( new ImmutableVector2D( 0, 1 ) );
        }

        // If we made it to here, there is no state change.
        return this;
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites, MobileBiomolecule biomolecule ) {
        // Does not consider other attachments while transcribing.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
        return new UnattachedAndAvailableState();
    }
}
