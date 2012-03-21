// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import java.awt.geom.Point2D;

import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.FollowAttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MeanderToDestinationMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.RandomWalkMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.WanderInGeneralDirectionMotionStrategy;

/**
 * Base class for individual attachment states, used by the various attachment
 * state machines.
 */
public abstract class AttachmentState {

    // Distance within which a molecule is considered to be attached to an
    // attachment site.  This essentially avoids floating point issues.
    public static double ATTACHED_DISTANCE_THRESHOLD = 1; // In picometers.

    public void stepInTime( AttachmentStateMachine enclosingStateMachine, double dt ) {
        // By default does nothing, override to implement unique behavior.
    }

    public void entered( AttachmentStateMachine enclosingStateMachine ) {
        // By default does nothing, override to implement unique behavior.
    }

    public static class GenericUnattachedAndAvailableState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine enclosingStateMachine, double dt ) {
            GenericAttachmentStateMachine gsm = (GenericAttachmentStateMachine) enclosingStateMachine;

            // Verify that state is consistent.
            assert gsm.attachmentSite == null;

            // Make the biomolecule look for attachments.
            gsm.attachmentSite = gsm.biomolecule.proposeAttachments();
            if ( gsm.attachmentSite != null ) {
                // An attachment proposal was accepted, so start heading towards
                // the attachment site.
//                gsm.biomolecule.setMotionStrategy( new MoveDirectlyToDestinationMotionStrategy( gsm.attachmentSite.locationProperty,
//                                                                                                gsm.biomolecule.motionBoundsProperty,
//                                                                                                gsm.destinationOffset ) );
                // TODO: Debug code for tracking down an issue with site ownership.  Problem believed to be solved, delete if problem does not recur.
                if ( gsm.attachmentSite.attachedOrAttachingMolecule.get() != null ) {
                    System.out.println( "About to overwrite." );
                }

                // Mark the attachment site as being in use.
                gsm.attachmentSite.attachedOrAttachingMolecule.set( gsm.biomolecule );

                // Start moving towards the site.
                gsm.biomolecule.setMotionStrategy( new MeanderToDestinationMotionStrategy( gsm.attachmentSite.locationProperty,
                                                                                           gsm.biomolecule.motionBoundsProperty,
                                                                                           gsm.destinationOffset ) );

                // Update state.
                gsm.setState( gsm.movingTowardsAttachmentState );

            }
        }

        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            enclosingStateMachine.biomolecule.setMotionStrategy( new RandomWalkMotionStrategy( enclosingStateMachine.biomolecule.motionBoundsProperty ) );

            // Allow user interaction.
            enclosingStateMachine.biomolecule.movableByUser.set( true );
        }
    }

    public static class GenericMovingTowardsAttachmentState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine enclosingStateMachine, double dt ) {
            GenericAttachmentStateMachine gsm = (GenericAttachmentStateMachine) enclosingStateMachine;

            // Verify that state is consistent.
            assert gsm.attachmentSite != null;
            // TODO: Debug code for tracking down an issue with site ownership.  Problem believed to be solved, delete if problem does not recur.
            if ( gsm.attachmentSite.attachedOrAttachingMolecule.get() != gsm.biomolecule ) {
                System.out.println( "Inconsistent attachment state during time step." );
                System.out.println( "gsm.biomolecule = " + gsm.biomolecule );
                System.out.println( "gsm.attachmentSite.attachedOrAttachingMolecule.get() = " + gsm.attachmentSite.attachedOrAttachingMolecule.get() );
            }
            assert gsm.attachmentSite.attachedOrAttachingMolecule.get() == gsm.biomolecule;

            // Calculate the location where this biomolecule must be in order
            // to attach to the attachment site.
            Point2D destination = new Point2D.Double( gsm.attachmentSite.locationProperty.get().getX() - gsm.destinationOffset.getX(),
                                                      gsm.attachmentSite.locationProperty.get().getY() - gsm.destinationOffset.getY() );

            // See if the attachment site has been reached.
            if ( gsm.biomolecule.getPosition().distance( destination ) < ATTACHED_DISTANCE_THRESHOLD ) {
                // This molecule is now at the attachment site, so consider it
                // attached.
                gsm.setState( gsm.attachedState );
            }
        }

        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            // Allow user interaction.
            enclosingStateMachine.biomolecule.movableByUser.set( true );
            // TODO: Debug code for tracking down an issue with site ownership.  Problem believed to be solved, delete if problem does not recur.
            if ( enclosingStateMachine.attachmentSite.attachedOrAttachingMolecule.get() != enclosingStateMachine.biomolecule ) {
                System.out.println( "Inconsistent attachment state at state entry." );
                System.out.println( "enclosingStateMachine.biomolecule = " + enclosingStateMachine.biomolecule );
                System.out.println( "enclosingStateMachine.attachmentSite.attachedOrAttachingMolecule.get() = " + enclosingStateMachine.attachmentSite.attachedOrAttachingMolecule.get() );
            }
        }
    }

    // The generic "attached" state isn't very useful, but is included for
    // completeness.  The reason it isn't useful is because the different
    // biomolecules all have their own unique behavior with respect to
    // attaching, and thus define their own attached states.
    public static class GenericAttachedState extends AttachmentState {

        private static final double DEFAULT_ATTACH_TIME = 3; // In seconds.

        private double attachCountdownTime = DEFAULT_ATTACH_TIME;

        @Override public void stepInTime( AttachmentStateMachine enclosingStateMachine, double dt ) {
            GenericAttachmentStateMachine gsm = (GenericAttachmentStateMachine) enclosingStateMachine;

            // Verify that state is consistent.
            assert gsm.attachmentSite != null;
            assert gsm.attachmentSite.attachedOrAttachingMolecule.get() == gsm.biomolecule;

            // See if it is time to detach.
            attachCountdownTime -= dt;
            if ( attachCountdownTime <= 0 ) {
                // Detach.
                gsm.detach();
                gsm.biomolecule.setMotionStrategy( new WanderInGeneralDirectionMotionStrategy( gsm.biomolecule.getDetachDirection(),
                                                                                               gsm.biomolecule.motionBoundsProperty ) );
            }
        }

        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            attachCountdownTime = DEFAULT_ATTACH_TIME;
            enclosingStateMachine.biomolecule.setMotionStrategy( new FollowAttachmentSite( enclosingStateMachine.attachmentSite ) );
            // Prevent user interaction.
            enclosingStateMachine.biomolecule.movableByUser.set( false );
        }
    }

    public static class GenericUnattachedButUnavailableState extends AttachmentState {

        private static final double DEFAULT_DETACH_TIME = 3; // In seconds.

        private double detachCountdownTime = DEFAULT_DETACH_TIME;

        @Override public void stepInTime( AttachmentStateMachine enclosingStateMachine, double dt ) {
            GenericAttachmentStateMachine gsm = (GenericAttachmentStateMachine) enclosingStateMachine;

            // Verify that state is consistent.
            assert gsm.attachmentSite == null;

            // See if we've been detached long enough.
            detachCountdownTime -= dt;
            if ( detachCountdownTime <= 0 ) {
                // Move to the unattached-and-available state.
                gsm.setState( gsm.unattachedAndAvailableState );
            }
        }

        @Override public void entered( AttachmentStateMachine enclosingStateMachine ) {
            detachCountdownTime = DEFAULT_DETACH_TIME;
            // Allow user interaction.
            enclosingStateMachine.biomolecule.movableByUser.set( true );
        }
    }
}
