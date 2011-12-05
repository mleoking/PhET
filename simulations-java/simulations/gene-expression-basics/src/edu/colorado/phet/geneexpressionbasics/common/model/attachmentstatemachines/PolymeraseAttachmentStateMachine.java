// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MoveDirectlyToDestinationMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.WanderInGeneralDirectionMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;

/**
 * Attachment state machine for all RNA Polymerase molecules.  This uses the
 * generic behavior for all but the "attached" state, and has three sub-states
 * for that: attachedAndWandering, changingConformation, and transcribing.
 *
 * @author John Blanco
 */
public class PolymeraseAttachmentStateMachine extends AttachmentStateMachine {

    private static final Random RAND = new Random();

    private AttachmentState attachedAndWanderingState = new AttachedAndWanderingState();
    private AttachmentState attachedAndTranscribingState = new TranscribingState();

    public PolymeraseAttachmentStateMachine( MobileBiomolecule biomolecule ) {
        super( biomolecule );

        // Set up a new "attached" state, since the behavior is different from
        // the default.
        attachedState = attachedAndWanderingState;
    }

    // Subclass of the "attached" state for polymerase when it is attached to
    // the DNA but is not transcribing.
    protected class AttachedAndWanderingState extends AttachmentState {

        // Scalar velocity when moving between attachment points on the DNA.
        private static final double VELOCITY_ON_DNA = 200;

        // Time for attachment to a site on the DNA.
        private static final double DEFAULT_ATTACH_TIME = 1; // In seconds.

        // Countdown timer for the amount of time that the polymerase is
        // attached to a single attachment site.
        private double attachCountdownTime;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedMolecule.get().get() == biomolecule;

            if ( attachmentSite.getAffinity() == 1 ) {
                // Attached to a max affinity site, which means that it is time
                // to transcribe the DNA into mRNA.
                attachedState = attachedAndTranscribingState;
                setState( attachedState );
            }
            else {
                // See if we have been attached long enough.
                attachCountdownTime -= dt;
                if ( attachCountdownTime <= 0 ) {
                    List<AttachmentSite> attachmentSites = biomolecule.getModel().getDnaMolecule().getAdjacentTranscriptionFactorAttachmentSites( asm.attachmentSite );
                    Collections.shuffle( attachmentSites );
                    // Decide whether to completely detach from the DNA strand or
                    // move to an adjacent attachment point.
                    if ( RAND.nextDouble() > 0.8 || attachmentSites.size() == 0 ) {
                        // Detach.
                        asm.attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );
                        asm.attachmentSite = null;
                        asm.setState( unattachedButUnavailableState );
                        biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( 0, 1 ), biomolecule.motionBoundsProperty ) );
                    }
                    else {
                        // Clear the old attachment site.
                        attachmentSite.attachedMolecule.set( new Option.None<MobileBiomolecule>() );

                        // Set a new attachment site.
                        attachmentSite = attachmentSites.get( 0 );
                        attachmentSite.attachedMolecule.set( new Option.Some<MobileBiomolecule>( biomolecule ) );

                        // Set up the state to move to the new attachment site.
                        setState( movingTowardsAttachmentState );
                        biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( attachmentSite.locationProperty,
                                                                                                    biomolecule.motionBoundsProperty,
                                                                                                    VELOCITY_ON_DNA ) );
                    }
                }
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            attachCountdownTime = DEFAULT_ATTACH_TIME;
        }
    }

    protected class TranscribingState extends AttachmentState {
        private static final double TRANSCRIPTION_VELOCITY = 750; // In picometers per second.
        private final Point2D endOfGene = new Point2D.Double();

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedMolecule.get().get() == biomolecule;

            // If we've reached the end of the gene, detach.
            if ( biomolecule.getPosition().equals( endOfGene ) ) {
                asm.detach();
                asm.biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( 0, 1 ), biomolecule.motionBoundsProperty ) );

                // Make sure that we enter the correct initial state upon the
                // next attachment.
                attachedState = attachedAndWanderingState;
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            Gene geneToTranscribe = biomolecule.getModel().getDnaMolecule().getGeneAtLocation( biomolecule.getPosition() );
            assert geneToTranscribe != null;
            endOfGene.setLocation( geneToTranscribe.getEndX(), DnaMolecule.Y_POS );
            biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( new Property<Point2D>( endOfGene ),
                                                                                        biomolecule.motionBoundsProperty,
                                                                                        TRANSCRIPTION_VELOCITY ) );
        }
    }
}
