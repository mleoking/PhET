// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.AttachedState;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.BiomoleculeBehaviorState;
import edu.colorado.phet.geneexpressionbasics.common.model.behaviorstates.UnattachedAndAvailableState;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.GeneExpressionModel;

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

    // Color to use when displaying this biomolecule to the user.  This is
    // a bit out of place here, and has nothing to do with the fact that the
    // molecule moves.  This was just a convenient place to put it (so far).
    public final Property<Color> colorProperty = new Property<Color>( Color.BLACK );

    // Behavioral state that controls how the molecule moves when it is not
    // under the control of the user and how and when it attaches to other
    // biomolecules.
    protected BiomoleculeBehaviorState behaviorState = new UnattachedAndAvailableState( this );

    protected final GeneExpressionModel model;

    /**
     * Constructor.
     *
     * @param initialShape
     * @param baseColor
     */
    public MobileBiomolecule( GeneExpressionModel model, Shape initialShape, Color baseColor ) {
        super( initialShape );
        this.model = model;
        colorProperty.set( baseColor );
        // Handle changes in user control.
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

    public void stepInTime( double dt ) {
        if ( !userControlled.get() ) {
            behaviorState = behaviorState.stepInTime( dt );
        }
    }

    public GeneExpressionModel getModel() {
        return model;
    }

    /**
     * Add the specified biomolecule to the model.
     *
     * @param biomolecule
     */
    public void spawnMolecule( MobileBiomolecule biomolecule ) {
        model.addMobileBiomolecule( biomolecule );
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
     * purposes of this simulation, means that both the color and the shape can
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
}
