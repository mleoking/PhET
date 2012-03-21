// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.FollowAttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MoveDirectlyToDestinationMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.WanderInGeneralDirectionMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;

/**
 * Attachment state machine for all transcription factor molecules.  This
 * class controls how transcription factors behave with respect to attaching
 * to and detaching from the DNA molecule, which is the only thing to which the
 * transcription factors attach.
 *
 * @author John Blanco
 */
public class TranscriptionFactorAttachmentStateMachine extends GenericAttachmentStateMachine {

    private static final Random RAND = new Random();

    // Scalar velocity when moving between attachment points on the DNA.
    private static final double VELOCITY_ON_DNA = 200;

    // Half-life of attachment to a site with affinity of 0.5.
    private static final double HALF_LIFE_FOR_HALF_AFFINITY = 1.5; // In seconds.

    // Threshold for the detachment algorithm, used in deciding whether or not
    // to detach completely from the DNA at a given time step.
    private double detachFromDnaThreshold = 1;

    public TranscriptionFactorAttachmentStateMachine( MobileBiomolecule biomolecule ) {
        super( biomolecule );

        // Set up a new "attached" state, since the behavior is different from
        // the default.
        attachedState = new TranscriptionFactorAttachedState();
    }

    // Subclass of the "attached" state.
    protected class TranscriptionFactorAttachedState extends AttachmentState.GenericAttachedState {
        private static final double DEFAULT_ATTACH_TIME = 0.15; // In seconds.

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent with expectations.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get() == biomolecule;

            // Decide whether or not to detach from the current attachment site.
            if ( RAND.nextDouble() > ( 1 - calculateProbabilityOfDetachment( attachmentSite.getAffinity(), dt ) ) ) {

                // The decision has been made to detach.  Next, decide whether
                // to detach completely from the DNA strand or just jump to an
                // adjacent base pair.
                if ( RAND.nextDouble() > detachFromDnaThreshold ) {
                    // Detach completely from the DNA.
                    detachFromDnaMolecule( asm );
                }
                else {
                    // Move to an adjacent base pair.  Start by making a list
                    // of candidate base pairs.
                    List<AttachmentSite> attachmentSites = biomolecule.getModel().getDnaMolecule().getAdjacentAttachmentSites( (TranscriptionFactor) biomolecule, asm.attachmentSite );

                    // Eliminate sites that, if moved to, would put the
                    // biomolecule out of bounds.
                    for ( AttachmentSite site : new ArrayList<AttachmentSite>( attachmentSites ) ) {
                        if ( !biomolecule.motionBoundsProperty.get().testIfInMotionBounds( biomolecule.getShape(), site.locationProperty.get() ) ) {
                            attachmentSites.remove( site );
                        }
                    }

                    // Shuffle in order to produce ramdom-ish behavior.
                    Collections.shuffle( attachmentSites );

                    if ( attachmentSites.size() == 0 ) {
                        // No valid adjacent sites, so detach completely.
                        detachFromDnaMolecule( asm );
                    }
                    else {
                        // Clear the previous attachment site.
                        attachmentSite.attachedOrAttachingMolecule.set( null );

                        // Set a new attachment site.
                        attachmentSite = attachmentSites.get( 0 );
                        assert attachmentSite.attachedOrAttachingMolecule.get() == null; // Make sure that site is really available.
                        attachmentSite.attachedOrAttachingMolecule.set( biomolecule );

                        // Set up the state to move to the new attachment site.
                        setState( movingTowardsAttachmentState );
                        biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( attachmentSite.locationProperty,
                                                                                                    biomolecule.motionBoundsProperty,
                                                                                                    new ImmutableVector2D( 0, 0 ),
                                                                                                    VELOCITY_ON_DNA ) );
                        // Update the detachment threshold.  It gets lower over
                        // time to increase the probability of detachment.
                        // Tweak as needed.
                        detachFromDnaThreshold = detachFromDnaThreshold * Math.pow( 0.5, DEFAULT_ATTACH_TIME );
                    }
                }
            }
        }

        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            enclosingStateMachine.biomolecule.setMotionStrategy( new FollowAttachmentSite( enclosingStateMachine.attachmentSite ) );
            enclosingStateMachine.biomolecule.attachedToDna.set( true ); // Update externally visible state indication.
        }

        private void detachFromDnaMolecule( AttachmentStateMachine asm ) {
            asm.attachmentSite.attachedOrAttachingMolecule.set( null );
            asm.attachmentSite = null;
            asm.setState( unattachedButUnavailableState );
            biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( biomolecule.getDetachDirection(), biomolecule.motionBoundsProperty ) );
            detachFromDnaThreshold = 1; // Reset this threshold.
            asm.biomolecule.attachedToDna.set( false ); // Update externally visible state indication.
        }

        /**
         * Calculate the probability of detachment from the current base pair
         * during the provided time interval.  This uses the same mathematics
         * as is used for calculating probabilities of decay for radioactive
         * atomic nuclei.
         *
         * @param affinity
         * @param dt
         * @return
         */
        private double calculateProbabilityOfDetachment( double affinity, double dt ) {
            // Map affinity to a half life.  Units are in seconds.  This
            // formula can be tweaked as needed in order to make the half life
            // longer or shorter.  However, zero affinity should always map to
            // zero half life, and an affinity of one should always map to an
            // infinite half life.
            double halfLife = HALF_LIFE_FOR_HALF_AFFINITY * ( affinity / ( 1 - affinity ) );

            // Use standard half-life formula to decide on probability of detachment.
            return 1 - Math.exp( -0.693 * dt / halfLife );
        }
    }
}
