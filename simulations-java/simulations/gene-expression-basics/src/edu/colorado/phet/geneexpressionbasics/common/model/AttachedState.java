// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * @author John Blanco
 */
public class AttachedState extends BiomoleculeBehaviorState {

    private static final double DEFAULT_ATTACHMENT_HALF_LIFE = 1.0; // Seconds.
    private static final double DEFAULT_MIGRATION_HALF_LIFE = 0.05; // Seconds.
    private static final double MIN_NON_DETACH_TIME = 0.25;

    private final AttachmentSite attachmentSite;

    private double timeOfAttachment = 0;
    private double migrationHalfLife = DEFAULT_MIGRATION_HALF_LIFE;
    private double attachmentHalfLife = DEFAULT_ATTACHMENT_HALF_LIFE;

    public AttachedState( AttachmentSite attachmentSite ) {
        this.attachmentSite = attachmentSite;
        if ( attachmentSite.getAffinity() == 1 ) {
            System.out.println( "Attached to max affinity site." );
            migrationHalfLife = Double.POSITIVE_INFINITY;
            attachmentHalfLife = Double.POSITIVE_INFINITY;
        }
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
            probabilityOfAttachmentDecay = 1 - Math.pow( 0.5, dt / attachmentHalfLife );
        }
        if ( probabilityOfAttachmentDecay > 0.999 || probabilityOfAttachmentDecay > RAND.nextDouble() ) {
            // Go ahead and detach.
            attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
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
        double probabilityOfAttachmentDecay = 1 - Math.pow( 0.5, timeOfAttachment / migrationHalfLife );
        if ( probabilityOfAttachmentDecay > RAND.nextDouble() ) {
            // Okay to actually consider these proposals.
            List<AttachmentSite> copyOfProposedAttachmentSites = new ArrayList<AttachmentSite>( proposedAttachmentSites );
            for ( AttachmentSite attachmentSite : proposedAttachmentSites ) {
                if ( attachmentSite.equals( this.attachmentSite ) ) {
                    // Remove the one to which we are currently attached from
                    // the list of sites to consider.
                    copyOfProposedAttachmentSites.remove( attachmentSite );
                }
            }
            if ( copyOfProposedAttachmentSites.size() > 0 ) {
                if ( RAND.nextDouble() > 0.9 ) {
                    double maxAttraction = 0;
                    for ( AttachmentSite proposedAttachmentSite : copyOfProposedAttachmentSites ) {
                        maxAttraction = Math.max( getAttraction( biomolecule, proposedAttachmentSite ), maxAttraction );
                    }
                    for ( AttachmentSite proposedAttachmentSite : proposedAttachmentSites ) {
                        if ( getAttraction( biomolecule, proposedAttachmentSite ) < maxAttraction ) {
                            // This attachment site has less of an attraction
                            // than at least one of the others, so remove it
                            // from consideration.
                            copyOfProposedAttachmentSites.remove( proposedAttachmentSite );
                        }
                    }
                    // Choose randomly between all the equivalent attachment
                    // sites.
                    AttachmentSite newAttachmentSite = copyOfProposedAttachmentSites.get( RAND.nextInt( copyOfProposedAttachmentSites.size() ) );
                    // Release the previous attachment site.
                    attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
                    // Accept the new attachment site.
                    newAttachmentSite.attachedMolecule.set( new Option.Some<MobileBiomolecule>( biomolecule ) );
                    return new MovingTowardsAttachmentState( newAttachmentSite );
                }
            }
        }

        // If we make it to here, just stay in the current state.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
        return new UnattachedAndAvailableState();
    }
}
