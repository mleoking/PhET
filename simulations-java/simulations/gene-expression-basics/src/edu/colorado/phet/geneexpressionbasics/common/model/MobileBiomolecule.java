// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.AttachedState;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.BiomoleculeBehaviorState;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MotionBounds;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.MotionStrategy;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.GeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;

/**
 * Base class for all biomolecules (i.e. rna polymerase, transcription factors,
 * etc.) that move around within the simulation.  This is a very central class
 * within this simulation.  This base class provides the basic infrastructure
 * for defining the shape, the movement, and the attachment behavior (i.e.
 * how one biomolecule interacts with others in the simulation).  The specific,
 * unique behavior for each biomolecule is implemented in the subclasses of
 * this class.
 *
 * @author John Blanco
 */
public abstract class MobileBiomolecule extends ShapeChangingModelElement {

    // Reference to the model in which this biomolecule exists.  This is
    // needed in case the biomolecule needs to locate or create other
    // biomolecules.
    protected final GeneExpressionModel model;

    // Property that tracks whether this biomolecule is user controlled.  If
    // it is, it shouldn't try to move or interact with anything.
    public final BooleanProperty userControlled = new BooleanProperty( false );

    // Color to use when displaying this biomolecule to the user.  This is
    // a bit out of place here, and has nothing to do with the fact that the
    // molecule moves.  This was just a convenient place to put it (so far).
    public final Property<Color> colorProperty = new Property<Color>( Color.BLACK );

    // Bounds within which this biomolecule is allowed to move.
    public Property<MotionBounds> motionBoundsProperty = new Property<MotionBounds>( new MotionBounds() );

    // Motion strategy that governs how this biomolecule moves.  This changes
    // as the molecule interacts with other portions of the model.
    private MotionStrategy motionStrategy = null;

    // Attachment state machine that controls how the molecule interacts with
    // other model objects (primarily other biomolecules) in terms of
    // attaching, detaching, etc.
    protected final AttachmentStateMachine attachmentStateMachine;

    // A property that keeps track of this biomolecules "existence strength",
    // which is used primarily to fade out of existence.  The range for this
    // is 1 (full existence) to 0 (non-existent).
    public final Property<Double> existenceStrength = new Property<Double>( 1.0 );

    /**
     * Constructor.
     *
     * @param initialShape
     * @param baseColor
     */
    public MobileBiomolecule( GeneExpressionModel model, Shape initialShape, Color baseColor ) {
        super( initialShape );
        this.model = model;
        this.attachmentStateMachine = createAttachmentStateMachine();
        colorProperty.set( baseColor );
        // Handle changes in user control.
        userControlled.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isUserControlled, Boolean wasUserControlled ) {
                if ( wasUserControlled && !isUserControlled ) {
                    // The user has released this node after moving it.  This
                    // should cause any existing or pending attachments to be
                    // severed.
                    MobileBiomolecule.this.attachmentStateMachine.forceImmediateUnattached();
                }
            }
        } );
    }

    /**
     * Method used to set the attachment state machine during construction.
     * This is overridden in descendant classes in order to supply this base
     * class with different attachment behavior.
     *
     * @return
     */
    protected abstract AttachmentStateMachine createAttachmentStateMachine();

    public void stepInTime( double dt ) {

        if ( !userControlled.get() ) {

            // Set a new position in model space based on the current motion
            // strategy.
            setPosition( motionStrategy.getNextLocation( getPosition(), getShape(), dt ) );

            // Update the state of the attachment state machine.
            attachmentStateMachine.stepInTime( dt );
        }
    }

    public GeneExpressionModel getModel() {
        return model;
    }

    public void setMotionBounds( MotionBounds motionBounds ) {
        motionBoundsProperty.set( motionBounds );
    }

    /**
     * Add the specified messenger RNA to the model.
     *
     * @param messengerRna
     */
    public void spawnMessengerRna( MessengerRna messengerRna ) {
        model.addMessengerRna( messengerRna );
    }

    /**
     * Get a boolean value indicating whether or not this biomolecule is
     * attached to the DNA strand.
     *
     * @return - true if currently attached to DNA, false if not.
     */
    public boolean isAttachedToDna() {
        // Override for molecules that attach to DNA.
        return false;
    }

    /**
     * Get the state that this biomolecule should transition into when it
     * becomes attached to some location.  This exists to allow biomolecules to
     * return different states (through overriding this method), thus exhibiting
     * unique behavior when attached.  If this is not overridden, the default
     * attachment state is returned.
     *
     * @return
     */
    public BiomoleculeBehaviorState getAttachmentPointReachedState( AttachmentSite attachmentSite ) {
        // Return the default attachment state.  For details on what this does,
        // see the class definition.
        return new AttachedState( this, attachmentSite );
    }

    /**
     * Command the biomolecule to changes its conformation, which, for the
     * purposes of this simulation, means that both the color and the shape may
     * change.  This functionality is needed by some of the biomolecules, mostly
     * when they attach to something.  The default does nothing, and it is up to
     * the individual molecules to override in order to implement their specific
     * conformation change behavior.
     *
     * @param changeFactor - Value, from 0 to 1, representing the degree of
     *                     change from the nominal configuration.
     */
    public void changeConformation( double changeFactor ) {
        System.out.println( getClass().getName() + "Warning: Unimplemented method called in base class." );
    }

    /**
     * Search for other biomolecules (and perhaps additional model elements) to
     * which this biomolecule may legitimately attach and, if any are founds,
     * propose an attachment to them.
     *
     * @return Attachment site of accepted attachment, null if no attachments
     *         were proposed or if all were rejected.
     */
    public AttachmentSite proposeAttachments() {
        return null;
    }

    public void setMotionStrategy( MotionStrategy motionStrategy ) {
        this.motionStrategy = motionStrategy;
    }
}
