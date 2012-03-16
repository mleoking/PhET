// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.RandomWalkMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.StillnessMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.WanderInGeneralDirectionMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;

/**
 * Attachment state machine for messenger RNA.
 *
 * @author John Blanco
 */
public class MessengerRnaAttachmentStateMachine extends AttachmentStateMachine {

    // Local reference of appropriate type.
    private final MessengerRna messengerRna;

    // Flag to control whether the mRNA continues to exist once fully formed.
    private boolean fadeAwayWhenFormed;

    public MessengerRnaAttachmentStateMachine( MessengerRna messengerRna ) {
        super( messengerRna );
        this.messengerRna = messengerRna;
        setState( new BeingSynthesizedState() );
    }

    /**
     * Detach from the RNA polymerase.  Note that this should NOT be used to
     * detach the mRNA from ribosomes or any other biomolecules.
     */
    @Override public void detach() {
        if ( fadeAwayWhenFormed ) {
            setState( new UnattachedAndFadingState() );
        }
        else {
            setState( new DetachingFromPolymeraseState() );
        }
    }

    @Override public void forceImmediateUnattachedAndAvailable() {
        if ( attachmentSite != null ) {
            attachmentSite.attachedOrAttachingMolecule.set( null );
        }
        attachmentSite = null;
        setState( new WanderingAroundCytoplasmState() );
    }

    public void setFadeAwayWhenFormed( boolean fadeAwayWhenFormed ) {
        this.fadeAwayWhenFormed = fadeAwayWhenFormed;
    }

    /**
     * Signals this state machine that at least one ribosome is now attached
     * to the mRNA and is thus translating it.
     */
    public void attachedToRibosome() {
        setState( new BeingTranslatedState() );
    }

    /**
     * Signals this state machine that all ribosomes that were translating it
     * have completed the translation process and have detached.
     */
    public void allRibosomesDetached() {
        setState( new WanderingAroundCytoplasmState() );
    }

    public void attachToDestroyer() {
        setState( new BeingDestroyedState() );
    }

    protected class BeingSynthesizedState extends AttachmentState {
        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            // Set the motion strategy to something that doesn't move the
            // molecule, since its position will be controlled by the polymerase
            // that is synthesizing it.
            enclosingStateMachine.biomolecule.setMotionStrategy( new StillnessMotionStrategy() );
        }
    }

    // State where the mRNA is detaching from the polymerase.  During this
    // time, it moves generally upwards until either the timer runs out or
    // it is attached to by some biomolecule.
    protected class DetachingFromPolymeraseState extends AttachmentState {
        private static final double DETACHING_TIME = 3; // seconds
        private double detachingCountdownTimer = DETACHING_TIME;

        @Override public void stepInTime( AttachmentStateMachine enclosingStateMachine, double dt ) {
            detachingCountdownTimer -= dt;
            if ( detachingCountdownTimer <= 0 ) {
                // Done detaching, start wandering.
                setState( new WanderingAroundCytoplasmState() );
            }
        }

        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            // Move upwards, away from the DNA and polymerase.
            enclosingStateMachine.biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( -0.5, 1 ),
                                                                                                             enclosingStateMachine.biomolecule.motionBoundsProperty ) );
            // Update externally visible state.
            messengerRna.beingSynthesized.set( false );
        }
    }

    // State where the mRNA is wandering around the cell's cytoplasm unattached
    // to anything.
    protected class WanderingAroundCytoplasmState extends AttachmentState {
        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            enclosingStateMachine.biomolecule.setMotionStrategy( new RandomWalkMotionStrategy( enclosingStateMachine.biomolecule.motionBoundsProperty ) );
        }
    }

    // State where the mRNA is wandering around the cell's cytoplasm unattached
    // to anything.
    protected class BeingTranslatedState extends AttachmentState {
        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            // Set a motion strategy that will not move this molecule, since
            // its position will be defined by the translator(s).
            enclosingStateMachine.biomolecule.setMotionStrategy( new StillnessMotionStrategy() );
        }
    }

    // State where the mRNA is being destroyed.
    protected class BeingDestroyedState extends AttachmentState {
        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            // Set a motion strategy that will not move this molecule, since
            // its position will be defined by the destroyer and translators.
            enclosingStateMachine.biomolecule.setMotionStrategy( new StillnessMotionStrategy() );
        }
    }

    // State where the mRNA is fading out of existence.
    protected class UnattachedAndFadingState extends AttachmentState {
        private static final double FADE_OUT_TIME = 2;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {
            biomolecule.existenceStrength.set( Math.max( biomolecule.existenceStrength.get() - dt / FADE_OUT_TIME, 0 ) );
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            assert biomolecule.existenceStrength.get() == 1.0; // State checking - should be at full strength.
            // Move upwards, away from the DNA and polymerase.
            asm.biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( -0.5, 1 ),
                                                                                           asm.biomolecule.motionBoundsProperty ) );
        }
    }
}
