// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;

/**
 * Model of a make-believe triatomic (3 atoms) molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TriatomicMolecule {

    private static final double BOND_LENGTH = 150;

    private final Property<ImmutableVector2D> location; // center of atom B
    public final Property<Double> angleAB, angleBC;
    public final Atom atomA, atomB, atomC;
    public final Bond bondAB, bondBC;
    private boolean dragging; // true when the user is dragging the molecule

    public TriatomicMolecule( ImmutableVector2D location ) {
        this.location = new Property<ImmutableVector2D>( location );
        angleAB = new Property<Double>( 0d );
        angleBC = new Property<Double>( 0d );
        atomA = new Atom( MPStrings.A, 100, Color.YELLOW, MPConstants.ELECTRONEGATIVITY_RANGE.getMin(), location.minus( BOND_LENGTH / 2, 0 ) );
        atomB = new Atom( MPStrings.B, 100, Color.ORANGE, MPConstants.ELECTRONEGATIVITY_RANGE.getMax(), location.plus( BOND_LENGTH / 2, 0 ) );
        atomC = new Atom( MPStrings.C, 100, Color.GREEN, MPConstants.ELECTRONEGATIVITY_RANGE.getMax(), location.plus( BOND_LENGTH / 2, 0 ) );
        bondAB = new Bond( atomA, atomB );
        bondBC = new Bond( atomB, atomC );
        VoidFunction1<Double> observer = new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                updateAtomLocations( angle );
            }
        };
        angleAB.addObserver( observer );
        angleBC.addObserver( observer );
    }

    public void reset() {
        location.reset();
        angleAB.reset();
        angleBC.reset();
        atomA.reset();
        atomB.reset();
        atomC.reset();
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging( boolean dragging ) {
        this.dragging = dragging;
    }

    // repositions the atoms
    private void updateAtomLocations( double angle ) {
        final double radius = BOND_LENGTH / 2;
        // atom A
        double xA = location.get().getX() - BOND_LENGTH;
        double yA = location.get().getY();
        atomA.location.set( new ImmutableVector2D( xA, yA ) );
        // atom B
        atomB.location.set( location.get() );
        // atom C
        double xC = location.get().getX() + BOND_LENGTH;
        double yC = location.get().getY();
        atomC.location.set( new ImmutableVector2D( xC, yC ) );
    }
}
