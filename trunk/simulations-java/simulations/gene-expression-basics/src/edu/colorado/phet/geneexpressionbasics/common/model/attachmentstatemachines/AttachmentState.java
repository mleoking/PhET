// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.FollowAttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MoveDirectlyToDestinationMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.RandomWalkMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.WanderInGeneralDirectionMotionStrategy;

/**
 * Base class for individual attachment states, used by the attachment state
 * machines.
 */
public abstract class AttachmentState {

    // Distance within which a molecule is considered to be attached to an
    // attachment site.  This essentially avoids floating point issues.
    public static double ATTACHED_DISTANCE_THRESHOLD = 1; // In picometers.

    public abstract void stepInTime( AttachmentStateMachine enclosingStateMachine, double dt );

    public abstract void entered( AttachmentStateMachine asm );

    public static class GenericUnattachedAndAvailableState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite == null;

            // Make the biomolecule look for attachments.
            asm.attachmentSite = asm.biomolecule.proposeAttachments();
            if ( asm.attachmentSite != null ) {
                // An attachment proposal was accepted, so start heading towards
                // the attachment site.
                asm.biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( asm.attachmentSite.locationProperty,
                                                                                                asm.biomolecule.motionBoundsProperty,
                                                                                                asm.destinationOffset ) );
                asm.setState( asm.movingTowardsAttachmentState );

                // Mark the attachment site as being in use.
                asm.attachmentSite.attachedOrAttachingMolecule.set( new Option.Some<MobileBiomolecule>( asm.biomolecule ) );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            asm.biomolecule.setMotionStrategy( new RandomWalkMotionStrategy( asm.biomolecule.motionBoundsProperty ) );
        }
    }

    public static class GenericMovingTowardsAttachmentState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get().get() == asm.biomolecule;

            // Calculate the location where this biomolecule must be in order
            // to attach to the attachment site.
            Point2D destination = new Point2D.Double( asm.attachmentSite.locationProperty.get().getX() - asm.destinationOffset.getX(),
                                                      asm.attachmentSite.locationProperty.get().getY() - asm.destinationOffset.getY() );

            // See if the attachment site has been reached.
            if ( asm.biomolecule.getPosition().distance( destination ) < ATTACHED_DISTANCE_THRESHOLD ) {
                // This molecule is now at the attachment site, so consider it
                // attached.
                asm.setState( asm.attachedState );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            // No initialization needed.
        }
    }

    // The generic "attached" state isn't very useful, but is included for
    // completeness.  The reason it isn't useful is because the different
    // biomolecules all have their own unique behavior with respect to
    // attaching, and thus define their own attached states.
    public static class GenericAttachedState extends AttachmentState {

        private static final double DEFAULT_ATTACH_TIME = 3; // In seconds.

        private double attachCountdownTime = DEFAULT_ATTACH_TIME;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite != null;
            assert asm.attachmentSite.attachedOrAttachingMolecule.get().get() == asm.biomolecule;

            // See if it is time to detach.
            attachCountdownTime -= dt;
            if ( attachCountdownTime <= 0 ) {
                // Detach.
                asm.detach();
                asm.biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( new ImmutableVector2D( 0, 1 ), asm.biomolecule.motionBoundsProperty ) );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            attachCountdownTime = DEFAULT_ATTACH_TIME;
            asm.biomolecule.setMotionStrategy( new FollowAttachmentSite( asm.attachmentSite ) );
        }
    }

    public static class GenericUnattachedButUnavailableState extends AttachmentState {

        private static final double DEFAULT_DETACH_TIME = 3; // In seconds.

        private double detachCountdownTime = DEFAULT_DETACH_TIME;

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {

            // Verify that state is consistent.
            assert asm.attachmentSite == null;

            // See if we've been detached long enough.
            detachCountdownTime -= dt;
            if ( detachCountdownTime <= 0 ) {
                // Move to the unattached-and-available state.
                asm.setState( asm.unattachedButUnavailableState );
            }
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            detachCountdownTime = DEFAULT_DETACH_TIME;
        }
    }
}
