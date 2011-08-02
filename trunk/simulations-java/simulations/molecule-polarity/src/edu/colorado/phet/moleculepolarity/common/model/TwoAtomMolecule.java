// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;

/**
 * A make-believe molecule with 2 atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomMolecule {

    private static final double BOND_LENGTH = 200;

    private final Property<ImmutableVector2D> location;
    public final Property<Double> angle; // the clockwise angle between atomB and the horizontal, in radians
    public final Atom atomA, atomB;
    public final Bond bond;
    private boolean dragging;

    public TwoAtomMolecule( ImmutableVector2D location ) {
        this.location = new Property<ImmutableVector2D>( location );
        angle = new Property<Double>( 0d );
        atomA = new Atom( MPStrings.A, 150, Color.YELLOW, MPConstants.ELECTRONEGATIVITY_RANGE.getMin(), location.minus( BOND_LENGTH / 2, 0 ) );
        atomB = new Atom( MPStrings.B, 150, Color.ORANGE, MPConstants.ELECTRONEGATIVITY_RANGE.getMax(), location.plus( BOND_LENGTH / 2, 0 ) );
        bond = new Bond( atomA, atomB );
        angle.addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                updateAngle( angle );
            }
        } );
    }

    public void reset() {
        atomA.reset();
        atomB.reset();
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging( boolean dragging ) {
        this.dragging = dragging;
    }

    private void updateAngle( double angle ) {
        final double radius = BOND_LENGTH / 2;
        // atom A
        double xA = PolarCartesianConverter.getX( radius, angle + Math.PI ) + location.get().getX();
        double yA = PolarCartesianConverter.getY( radius, angle + Math.PI ) + location.get().getY();
        atomA.location.set( new ImmutableVector2D( xA, yA ) );
        // atom B
        double xB = PolarCartesianConverter.getX( radius, angle ) + location.get().getX();
        double yB = PolarCartesianConverter.getY( radius, angle ) + location.get().getY();
        atomB.location.set( new ImmutableVector2D( xB, yB ) );
    }
}
