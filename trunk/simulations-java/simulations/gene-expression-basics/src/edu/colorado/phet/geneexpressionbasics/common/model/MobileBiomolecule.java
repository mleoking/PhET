// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;

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

    // State variable that tracks whether this molecules is available for
    // attachment to others.
    private AttachmentState attachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;

    // Motion strategy that controls how the molecule moves when it is not
    // under the control of the user.
    private IMotionStrategy motionStrategy = new StillnessMotionStrategy();

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
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public void release() {
        userControlled.set( false );
    }

    public void initiateRandomWalk() {
        this.motionStrategy = new RandomWalkMotionStrategy();
    }

    public void stepInTime( double dt ) {
        if ( !userControlled.get() ) {
            setPosition( motionStrategy.getNextLocation( dt, getPosition() ) );
        }
    }

    public boolean availableToAttach() {
        return attachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE;
    }
}
