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
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaSeparation;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.RnaPolymerase;

/**
 * Attachment state machine for all RNA Polymerase molecules.  This uses the
 * generic behavior for all but the "attached" state, and has several
 * sub-states for the attached site.  See the code for details.
 *
 * @author John Blanco
 */
public class RnaPolymeraseAttachmentStateMachine extends GenericAttachmentStateMachine {

    private static final Random RAND = new Random();

    private AttachmentState attachedAndWanderingState = new AttachedAndWanderingState();
    private AttachmentState attachedAndConformingState = new AttachedAndConformingState();
    private AttachmentState attachedAndTranscribingState = new AttachedAndTranscribingState();
    private AttachmentState attachedAndDeconformingState = new AttachedAndDeconformingState();

    // RNA polymerase that is being controlled by this state machine.
    private final RnaPolymerase rnaPolymerase;

    // Separator used to deform the DNA strand when the RNA polymerase is
    // transcribing it.
    private final DnaSeparation dnaStrandSeparation;

    /**
     * Constructor.
     *
     * @param rnaPolymerase
     */
    public RnaPolymeraseAttachmentStateMachine( RnaPolymerase rnaPolymerase ) {
        super( rnaPolymerase );
        this.rnaPolymerase = rnaPolymerase;

        // Set up a new "attached" state, since the behavior is different from
        // the default.
        attachedState = attachedAndWanderingState;

        // Create the DNA strand separator.
        dnaStrandSeparation = new DnaSeparation( rnaPolymerase.getPosition().getX(), rnaPolymerase.getShape().getBounds2D().getHeight() * 0.9 );
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
            assert asm.attachmentSite.attachedOrAttachingMolecule.get().get() == biomolecule;

            if ( attachmentSite.getAffinity() == 1 ) {
                // Attached to a max affinity site, which means that it is time
                // to transcribe the DNA into mRNA.
                attachedState = attachedAndConformingState;
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
                        asm.attachmentSite.attachedOrAttachingMolecule.set( new Option.None<MobileBiomolecule>() );
                        asm.attachmentSite = null;
                        asm.setState( unattachedButUnavailableState );
                        biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( 0, 1 ), biomolecule.motionBoundsProperty ) );
                    }
                    else {
                        // Clear the old attachment site.
                        attachmentSite.attachedOrAttachingMolecule.set( new Option.None<MobileBiomolecule>() );

                        // Set a new attachment site.
                        attachmentSite = attachmentSites.get( 0 );
                        attachmentSite.attachedOrAttachingMolecule.set( new Option.Some<MobileBiomolecule>( biomolecule ) );

                        // Set up the state to move to the new attachment site.
                        setState( movingTowardsAttachmentState );
                        biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( attachmentSite.locationProperty,
                                                                                                    biomolecule.motionBoundsProperty,
                                                                                                    new ImmutableVector2D( 0, 0 ),
                                                                                                    VELOCITY_ON_DNA ) );
                    }
                }
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            attachCountdownTime = DEFAULT_ATTACH_TIME;
        }
    }

    protected class AttachedAndConformingState extends AttachmentState {
        private static final double CONFORMATIONAL_CHANGE_RATE = 1; // Proportion per second.
        private double conformationalChangeAmount;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get().get() == biomolecule;

            conformationalChangeAmount = Math.min( conformationalChangeAmount + CONFORMATIONAL_CHANGE_RATE * dt, 1 );
            biomolecule.changeConformation( conformationalChangeAmount );
            dnaStrandSeparation.setProportionOfTargetAmount( conformationalChangeAmount );
            if ( conformationalChangeAmount == 1 ) {
                // Conformational change complete, time to start actual
                // transcription.
                attachedState = attachedAndTranscribingState;
                setState( attachedState );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {

            // Insert the DNA strand separator.
            dnaStrandSeparation.setXPos( rnaPolymerase.getPosition().getX() );
            rnaPolymerase.getModel().getDnaMolecule().addSeparation( dnaStrandSeparation );

            conformationalChangeAmount = 0;
        }
    }

    protected class AttachedAndTranscribingState extends AttachmentState {
        private static final double TRANSCRIPTION_VELOCITY = 750; // In picometers per second.
        private final Point2D endOfGene = new Point2D.Double();
        private MessengerRna messengerRna;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get().get() == biomolecule;

            // Grow the messenger RNA and position it to be attached to the
            // polymerase.
            messengerRna.addLength( TRANSCRIPTION_VELOCITY * dt );
            messengerRna.setLowerRightPosition( rnaPolymerase.getPosition().getX() + RnaPolymerase.MESSENGER_RNA_GENERATION_OFFSET.getX(),
                                                rnaPolymerase.getPosition().getY() + RnaPolymerase.MESSENGER_RNA_GENERATION_OFFSET.getY() );

            // Move the DNA strand separator.
            dnaStrandSeparation.setXPos( rnaPolymerase.getPosition().getX() );

            // If we've reached the end of the gene, detach.
            if ( biomolecule.getPosition().equals( endOfGene ) ) {
                attachedState = attachedAndDeconformingState;
                setState( attachedState );
                messengerRna.releaseFromPolymerase();
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            // Determine the change that is being transcribed.
            Gene geneToTranscribe = biomolecule.getModel().getDnaMolecule().getGeneAtLocation( biomolecule.getPosition() );
            assert geneToTranscribe != null;

            // Set up the motion strategy to move to the end of the transcribed
            // region of the gene.
            endOfGene.setLocation( geneToTranscribe.getEndX(), DnaMolecule.Y_POS );
            asm.biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( new Property<Point2D>( endOfGene ),
                                                                                            biomolecule.motionBoundsProperty,
                                                                                            new ImmutableVector2D( 0, 0 ),
                                                                                            TRANSCRIPTION_VELOCITY ) );
            // Create the mRNA that will be grown as a result of this
            // transcription.
            messengerRna = new MessengerRna( biomolecule.getModel(),
                                             geneToTranscribe.getProteinPrototype(),
                                             new Point2D.Double( biomolecule.getPosition().getX() + RnaPolymerase.MESSENGER_RNA_GENERATION_OFFSET.getX(),
                                                                 biomolecule.getPosition().getY() + RnaPolymerase.MESSENGER_RNA_GENERATION_OFFSET.getY() ) );
            biomolecule.spawnMessengerRna( messengerRna );

        }
    }

    protected class AttachedAndDeconformingState extends AttachmentState {
        private static final double CONFORMATIONAL_CHANGE_RATE = 1; // Proportion per second.
        private double conformationalChangeAmount;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get().get() == biomolecule;

            conformationalChangeAmount = Math.max( conformationalChangeAmount - CONFORMATIONAL_CHANGE_RATE * dt, 0 );
            biomolecule.changeConformation( conformationalChangeAmount );
            dnaStrandSeparation.setProportionOfTargetAmount( conformationalChangeAmount );
            if ( conformationalChangeAmount == 0 ) {
                // Conformational change complete, time to detach.
                asm.detach();
                asm.biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( 0, 1 ), biomolecule.motionBoundsProperty ) );

                // Remove the DNA separator, which makes the DNA close back up.
                rnaPolymerase.getModel().getDnaMolecule().removeSeparation( dnaStrandSeparation );

                // Make sure that we enter the correct initial state upon the
                // next attachment.
                attachedState = attachedAndWanderingState;
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            conformationalChangeAmount = 1;
        }
    }
}
