// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for all biomolecules (i.e. rna polymerase, transcription factors,
 * etc.) that move around within the simulation.
 *
 * @author John Blanco
 */
public abstract class MobileBiomolecule extends ShapeChangingModelElement {

    // Property that tracks whether this biomolecule is user controlled.  If
    // it is, it shouldn't try to move or interact with anything.
    public final BooleanProperty userControlled = new BooleanProperty( false );

    // Behavioral state that controls how the molecule moves when it is not
    // under the control of the user and how and when it attaches to other
    // biomolecules.
    private BiomoleculeBehaviorState behaviorState = new UnattachedAndAvailableState();

    // Color to use when displaying this biomolecule to the user.  This is
    // a bit out of place here, and has nothing to do with the fact that the
    // molecule moves.  This was just a convenient place to put it (so far).
    private final Color baseColor;

    /**
     * Constructor.
     *
     * @param initialShape
     * @param baseColor
     */
    public MobileBiomolecule( Shape initialShape, Color baseColor ) {
        super( initialShape );
        this.baseColor = baseColor;
        // Handle changes in user control.us
        userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( !userControlled ) {
                    // The user has released this node after moving it.  This
                    // should cause any existing or pending attachments to be
                    // severed.
                    behaviorState = behaviorState.movedByUser();
                }
            }
        } );
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public void stepInTime( double dt ) {
        if ( !userControlled.get() ) {
            behaviorState = behaviorState.stepInTime( dt, this );
        }
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
        return new AttachedState( attachmentSite );
    }

    public void proposeAttachmentSites( List<AttachmentSite> proposedAttachmentSites ) {
        behaviorState = behaviorState.considerAttachment( proposedAttachmentSites, this );
    }
}
