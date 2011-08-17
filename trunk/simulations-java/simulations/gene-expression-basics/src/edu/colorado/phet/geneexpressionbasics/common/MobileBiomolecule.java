// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common;

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

    public final BooleanProperty userControlled = new BooleanProperty( false );

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

    public void stepInTime( double dt ) {
        if ( !userControlled.get() ) {
            setPosition( getPosition().getX() + 10, getPosition().getY() + 10 );
        }
    }
}
