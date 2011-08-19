// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;

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

    // The current attachment site, which is a location on another biomolecule
    // (e.g. DNA) where this molecule is attached or is headed.
    AttachmentSite currentAttachmentSite;

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
        // Handle changes is user control.
        userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                if ( !aBoolean ) {
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

    public void proposeAttachmentSite( AttachmentSite attachmentSite ) {
        behaviorState = behaviorState.considerAttachment( attachmentSite, this );
    }
}
