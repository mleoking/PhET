// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author John Blanco
 */
public class AttachedState extends BiomoleculeBehaviorState {

    private static final double MIN_ATTACHMENT_TIME = 1;
    private static final Random RAND = new Random();

    private final AttachmentSite attachmentSite;

    private double attachmentCountdownTime;

    public AttachedState( AttachmentSite attachmentSite ) {
        this.attachmentSite = attachmentSite;
        // Calculate the attachment time.  The time is based on the affinity of
        // the attachment site for this molecule.
        if ( attachmentSite.getAffinity() == 1 ) {
            // Attach forever.
            attachmentCountdownTime = Double.POSITIVE_INFINITY;
        }
        else {
            // TODO: This is a total guess, and probably will need tweaking.
            attachmentCountdownTime = MIN_ATTACHMENT_TIME + 10 * attachmentSite.getAffinity() + ( 1 - RAND.nextDouble() ) * 5;
        }
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt, MobileBiomolecule biomolecule ) {
        if ( biomolecule.getPosition().distance( attachmentSite.locationProperty.get() ) > 0.0001 ) {
            // The attachment site appears to have moved, so follow it.
            biomolecule.setPosition( attachmentSite.locationProperty.get() );
        }
        attachmentCountdownTime -= dt;
        if ( attachmentCountdownTime <= 0 ) {
            // Time to fall off of this attachment site.
            attachmentSite.inUse.set( false );
            if ( attachmentSite.locationProperty.get().getY() < 100 ) {
                // Must be on the DNA, so drift upwards.
                return new DetachingState( new ImmutableVector2D( 0, 1 ) );
            }
            else {
                // Not on the DNA - drift randomly.
                return new DetachingState();
            }
        }
        else {
            // No state change.
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState considerAttachment( AttachmentSite attachmentSite, MobileBiomolecule biomolecule ) {
        // Already attached, so reject request by leaving the attachment site
        // unchanged and returning the current state.
        return this;
    }
}
