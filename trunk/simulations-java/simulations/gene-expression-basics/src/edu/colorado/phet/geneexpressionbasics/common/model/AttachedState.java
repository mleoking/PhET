// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author John Blanco
 */
public class AttachedState extends BiomoleculeBehaviorState {

    private static final Random RAND = new Random();
    private static final double ATTACHMENT_HALF_LIFE = 1.0; // Seconds.
    private static final double MIGRATION_HALF_LIFE = 0.05; // Seconds.
    private static final double MIN_NON_DETACH_TIME = 0.25;

    private final AttachmentSite attachmentSite;

    private double timeOfAttachment = 0;

    public AttachedState( AttachmentSite attachmentSite ) {
        this.attachmentSite = attachmentSite;
        // TODO: The half life is currently fixed, but we may want to change it based on the affinity of the attachment.
        // If so, it would be calculated here.
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt, MobileBiomolecule biomolecule ) {

        timeOfAttachment += dt;

        if ( biomolecule.getPosition().distance( attachmentSite.locationProperty.get() ) > 0.0001 ) {
            // The attachment site appears to have moved, so follow it.
            biomolecule.setPosition( attachmentSite.locationProperty.get() );
        }

        // Decide whether to detach.  This uses the formula for the half life
        // to determine whether it is a reasonable time.
        double probabilityOfAttachmentDecay = 0;
        if ( timeOfAttachment > MIN_NON_DETACH_TIME ) {
            probabilityOfAttachmentDecay = 1 - Math.pow( 0.5, dt / ATTACHMENT_HALF_LIFE );
        }
        System.out.println( "probabilityOfAttachmentDecay = " + probabilityOfAttachmentDecay );
        if ( probabilityOfAttachmentDecay > 0.999 || probabilityOfAttachmentDecay > RAND.nextDouble() ) {
            // Go ahead and detach.
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

        // If we made it to here, there is no state change.
        return this;
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites, final MobileBiomolecule biomolecule ) {
        double probabilityOfAttachmentDecay = 1 - Math.pow( 0.5, timeOfAttachment / MIGRATION_HALF_LIFE );
        if ( probabilityOfAttachmentDecay > RAND.nextDouble() ) {
            // Okay to actually consider these proposals.
            List<AttachmentSite> copyOfProposedAttachmentSites = new ArrayList<AttachmentSite>( proposedAttachmentSites );
            for ( AttachmentSite attachmentSite : proposedAttachmentSites ) {
                if ( attachmentSite.equals( this.attachmentSite ) ) {
                    // Remove the one to which we are currently attached from the
                    // list of sites to consider.
                    copyOfProposedAttachmentSites.remove( attachmentSite );
                }
            }
            if ( copyOfProposedAttachmentSites.size() > 0 ) {
                if ( RAND.nextDouble() > 0.9 ) {
                    // Sort the proposals.
                    Collections.sort( copyOfProposedAttachmentSites, new Comparator<AttachmentSite>() {
                        public int compare( AttachmentSite as1, AttachmentSite as2 ) {
                            return Double.compare( Math.pow( biomolecule.getPosition().distance( as1.locationProperty.get() ), 2 ) * as1.getAffinity(),
                                                   Math.pow( biomolecule.getPosition().distance( as2.locationProperty.get() ), 2 ) * as2.getAffinity() );
                        }
                    } );
                    int newAttachmentSiteIndex = 0;
                    if ( copyOfProposedAttachmentSites.size() >= 2 ) {
                        // Choose randomly between the first two on the list.
                        newAttachmentSiteIndex = RAND.nextInt( 2 );
                    }
                    // Accept the first one on the list.
                    this.attachmentSite.inUse.set( false );
                    return new MovingTowardsAttachmentState( copyOfProposedAttachmentSites.get( newAttachmentSiteIndex ) );
                }
            }
        }

        // If we make it to here, just stay in the current state.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        attachmentSite.inUse.set( false );
        return new UnattachedAndAvailableState();
    }
}
