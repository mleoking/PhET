// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;

/**
 * A make-believe molecule with 2 atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomMolecule {

    public final Property<Double> angle;
    public final Atom atomA, atomB;

    public TwoAtomMolecule( ImmutableVector2D location ) {
        final double bondLength = 200;
        atomA = new Atom( MPStrings.A, 150, Color.YELLOW, MPConstants.ELECTRONEGATIVITY_RANGE.getDefault(), location.minus( bondLength / 2, 0 ) );
        atomB = new Atom( MPStrings.B, 150, Color.ORANGE, MPConstants.ELECTRONEGATIVITY_RANGE.getDefault(), location.plus( bondLength / 2, 0 ) );
        angle = new Property<Double>( 0.0 );
    }

    public void reset() {
        atomA.reset();
        atomB.reset();
    }

    public ImmutableVector2D getBondDipole() {
        return new ImmutableVector2D( 0, 0 );//XXX
    }
}
