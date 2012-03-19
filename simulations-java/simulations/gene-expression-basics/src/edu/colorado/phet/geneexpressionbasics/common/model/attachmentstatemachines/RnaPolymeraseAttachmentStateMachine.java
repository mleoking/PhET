// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MoveDirectlyToDestinationMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.TeleportMotionStrategy;
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

    // Half-life of attachment to a site with affinity of 0.5.
    private static final double HALF_LIFE_FOR_HALF_AFFINITY = 1.5; // In seconds.

    private static final Random RAND = new Random();

    private AttachmentState attachedAndWanderingState = new AttachedToBasePair();
    private AttachmentState attachedAndConformingState = new AttachedAndConformingState();
    private AttachmentState attachedAndTranscribingState = new AttachedAndTranscribingState();
    private AttachmentState attachedAndDeconformingState = new AttachedAndDeconformingState();

    // RNA polymerase that is being controlled by this state machine.
    private final RnaPolymerase rnaPolymerase;

    // Separator used to deform the DNA strand when the RNA polymerase is
    // transcribing it.
    private final DnaSeparation dnaStrandSeparation;

    // This attachment site is used by the state machine to get the polymerase
    // something to attach to when transcribing.  This is a bit hokey, but was
    // a lot easier than trying to move to each and every base pair in the DNA
    // strand.
    private final AttachmentSite transcribingAttachmentSite = new AttachmentSite( new Point2D.Double( 0, 0 ), 1 );

    // Threshold for the detachment algorithm, used in deciding whether or not
    // to detach completely from the DNA at a given time step.
    private double detachFromDnaThreshold = 1;

    // A flag that tracks whether this state machine should use the "recycle
    // mode", which causes the polymerase to return to some new location once
    // it has completed transcription.
    private boolean recycleMode = false;

    private Rectangle2D recycleReturnZone = new Rectangle2D.Double( 0, 0, 0, 0 );

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

        // Initialize the attachment site used when transcribing.
        transcribingAttachmentSite.attachedOrAttachingMolecule.set( rnaPolymerase );
    }

    public void setRecycleMode( boolean recycleMode ) {
        this.recycleMode = recycleMode;
    }

    public void setRecycleReturnZone( Rectangle2D recycleReturnZone ) {
        this.recycleReturnZone.setFrame( recycleReturnZone );
    }

    // Subclass of the "attached" state for polymerase when it is attached to
    // the DNA but is not transcribing.  In this state, it is doing a 1D
    // random walk on the DNA strand.
    protected class AttachedToBasePair extends AttachmentState {

        // Scalar velocity when moving between attachment points on the DNA.
        private static final double VELOCITY_ON_DNA = 200;

        // Time for attachment to a site on the DNA.
        private static final double DEFAULT_ATTACH_TIME = 0.15; // In seconds.

        // Flag that is set upon entry that determines whether transcription occurs.
        private boolean transcribe = false;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get() == asm.biomolecule;

            // Decide whether to transcribe the DNA.  The decision is based on
            // the affinity of the site and the time of attachment.
            if ( transcribe ) {
                // Begin transcription.
                attachedState = attachedAndConformingState;
                setState( attachedState );
                detachFromDnaThreshold = 1; // Reset this threshold.
            }
            else if ( RAND.nextDouble() > ( 1 - calculateProbabilityOfDetachment( attachmentSite.getAffinity(), dt ) ) ) {

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
                    List<AttachmentSite> attachmentSites = biomolecule.getModel().getDnaMolecule().getAdjacentAttachmentSites( (RnaPolymerase) biomolecule, asm.attachmentSite );

                    // Eliminate sites that are in use or that, if moved to,
                    // would put the biomolecule out of bounds.
                    for ( AttachmentSite site : new ArrayList<AttachmentSite>( attachmentSites ) ) {
                        if ( site.isMoleculeAttached() || !biomolecule.motionBoundsProperty.get().testIfInMotionBounds( biomolecule.getShape(), site.locationProperty.get() ) ) {
                            attachmentSites.remove( site );
                        }
                    }

                    // Shuffle in order to produce random-ish behavior.
                    Collections.shuffle( attachmentSites );

                    if ( attachmentSites.size() == 0 ) {
                        // No valid adjacent sites, so detach completely.
                        detachFromDnaMolecule( asm );
                    }
                    else {
                        // Move to an adjacent base pair.  Firs, clear the
                        // previous attachment site.
                        attachmentSite.attachedOrAttachingMolecule.set( null );

                        // Set a new attachment site.
                        attachmentSite = attachmentSites.get( 0 );
                        // TODO: Debug code for tracking down an issue with site ownership.  Problem believed to be solved, delete if problem does not recur.
                        if ( attachmentSite.attachedOrAttachingMolecule.get() != null ) {
                            System.out.println( "Error: Attachment site isn't really available." );
                        }
                        assert attachmentSite.attachedOrAttachingMolecule.get() == null; // State checking - Make sure site is really available.
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
            else {
                // Just stay attached to the current site.
            }
        }

        private void detachFromDnaMolecule( AttachmentStateMachine asm ) {
            asm.attachmentSite.attachedOrAttachingMolecule.set( null );
            asm.attachmentSite = null;
            asm.setState( unattachedButUnavailableState );
            biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( 0, 1 ), biomolecule.motionBoundsProperty ) );
            detachFromDnaThreshold = 1; // Reset this threshold.
            asm.biomolecule.attachedToDna.set( false ); // Update externally visible state indication.
        }

        @Override public void entered( AttachmentStateMachine asm ) {

            // Decide right away whether or not to transcribe.
            transcribe = attachmentSite.getAffinity() > DnaMolecule.DEFAULT_AFFINITY && RAND.nextDouble() < attachmentSite.getAffinity();

            // Allow user interaction.
            asm.biomolecule.movableByUser.set( true );

            // Indicate attachment to DNA.
            asm.biomolecule.attachedToDna.set( true );

            // Update externally visible state information.
            asm.biomolecule.attachedToDna.set( true ); // Update externally visible state indication.
        }
    }

    protected class AttachedAndConformingState extends AttachmentState {
        private static final double CONFORMATIONAL_CHANGE_RATE = 1; // Proportion per second.
        private double conformationalChangeAmount;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get() == biomolecule;

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

            // Prevent user interaction.
            asm.biomolecule.movableByUser.set( false );

            // Insert the DNA strand separator.
            dnaStrandSeparation.setXPos( rnaPolymerase.getPosition().getX() );
            rnaPolymerase.getModel().getDnaMolecule().addSeparation( dnaStrandSeparation );

            conformationalChangeAmount = 0;
        }
    }

    protected class AttachedAndTranscribingState extends AttachmentState {
        private static final double TRANSCRIPTION_VELOCITY = 1000; // In picometers per second.
        private final Point2D endOfGene = new Point2D.Double();
        private MessengerRna messengerRna;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get() == biomolecule;

            // Grow the messenger RNA and position it to be attached to the
            // polymerase.
            messengerRna.addLength( TRANSCRIPTION_VELOCITY * dt );
            messengerRna.setLowerRightPosition( rnaPolymerase.getPosition().getX() + RnaPolymerase.MESSENGER_RNA_GENERATION_OFFSET.getX(),
                                                rnaPolymerase.getPosition().getY() + RnaPolymerase.MESSENGER_RNA_GENERATION_OFFSET.getY() );

            // Move the DNA strand separator.
            dnaStrandSeparation.setXPos( rnaPolymerase.getPosition().getX() );

            // Check for molecules that are in the way.
            for ( MobileBiomolecule molecule : asm.biomolecule.getModel().getOverlappingBiomolecules( asm.biomolecule.getShape() ) ) {
                if ( molecule.getPosition().getX() > asm.biomolecule.getPosition().getX() && molecule.attachedToDna.get() ) {
                    // This molecule is blocking transcription, so bump it off
                    // of the DNA strand.
                    molecule.forceDetach();
                }
            }

            // If we've reached the end of the gene, detach.
            if ( biomolecule.getPosition().equals( endOfGene ) ) {
                attachedState = attachedAndDeconformingState;
                setState( attachedState );
                messengerRna.releaseFromPolymerase();
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {

            // Prevent user interaction.
            asm.biomolecule.movableByUser.set( false );

            // Determine the gene that is being transcribed.
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

            // Free up the initial attachment site by hooking up to a somewhat
            // fictional attachment site instead.
            attachmentSite.attachedOrAttachingMolecule.set( null );
            transcribingAttachmentSite.attachedOrAttachingMolecule.set( asm.biomolecule );
            attachmentSite = transcribingAttachmentSite;
        }
    }

    protected class AttachedAndDeconformingState extends AttachmentState {
        private static final double CONFORMATIONAL_CHANGE_RATE = 1; // Proportion per second.
        private double conformationalChangeAmount;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get() == biomolecule;

            conformationalChangeAmount = Math.max( conformationalChangeAmount - CONFORMATIONAL_CHANGE_RATE * dt, 0 );
            biomolecule.changeConformation( conformationalChangeAmount );
            dnaStrandSeparation.setProportionOfTargetAmount( conformationalChangeAmount );
            if ( conformationalChangeAmount == 0 ) {
                // Remove the DNA separator, which makes the DNA close back up.
                rnaPolymerase.getModel().getDnaMolecule().removeSeparation( dnaStrandSeparation );

                // Update externally visible state indication.
                asm.biomolecule.attachedToDna.set( false );

                // Make sure that we enter the correct initial state upon the
                // next attachment.
                attachedState = attachedAndWanderingState;

                // Detach from the DNA.
                attachmentSite.attachedOrAttachingMolecule.set( null );
                attachmentSite = null;
                if ( recycleMode ) {
                    setState( new BeingRecycledState( recycleReturnZone ) );
                }
                else {
                    forceImmediateUnattachedButUnavailable();
                }
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            // Prevent user interaction.
            asm.biomolecule.movableByUser.set( false );

            conformationalChangeAmount = 1;
        }
    }

    protected class BeingRecycledState extends AttachmentState {

        private final Rectangle2D recycleReturnZone;

        public BeingRecycledState( Rectangle2D recycleReturnZone ) {
            this.recycleReturnZone = recycleReturnZone;
        }

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite == null;

            if ( recycleReturnZone.contains( asm.biomolecule.getPosition() ) ) {
                // The motion strategy has returned the biomolecule to the
                // recycle return zone, so this state is complete.
                asm.setState( unattachedAndAvailableState );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            // Prevent user interaction.
            asm.biomolecule.movableByUser.set( false );
            // Set the motion to move back to the recyle zone.
            asm.biomolecule.setMotionStrategy( new TeleportMotionStrategy( new ImmutableVector2D( 0, 1 ), recycleReturnZone, biomolecule.motionBoundsProperty ) );
        }
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
    private static double calculateProbabilityOfDetachment( double affinity, double dt ) {
        // Map affinity to a half life.  Units are in seconds.

        // Use standard half-life formula to decide on probability of detachment.
        return 1 - Math.exp( -0.693 * dt / calculateHalfLifeFromAffinity( affinity ) );
    }

    // Map an affinity value to a half life of attachment.
    private static double calculateHalfLifeFromAffinity( double affinity ) {
        return HALF_LIFE_FOR_HALF_AFFINITY * ( affinity / ( 1 - affinity ) );
    }
}
