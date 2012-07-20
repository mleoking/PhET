// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;

/**
 * Model of a make-believe diatomic (2 atoms) molecule.
 * Variables are named based on the English labels applied to the atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicMolecule extends Molecule2D {

    private static final double ATOM_DIAMETER = 100;
    private static final double BOND_LENGTH = 1.5 * ATOM_DIAMETER;

    public final Atom atomA, atomB; // the atoms labeled A and B
    public final Bond bond; // the bond connecting atoms A and B

    public DiatomicMolecule( Vector2D location, double angle ) {
        super( location, angle );
        atomA = new Atom( MPStrings.A, ATOM_DIAMETER, MPColors.ATOM_A, MPConstants.ELECTRONEGATIVITY_RANGE.getMin() );
        atomB = new Atom( MPStrings.B, ATOM_DIAMETER, MPColors.ATOM_B, MPConstants.ELECTRONEGATIVITY_RANGE.getMin() + ( MPConstants.ELECTRONEGATIVITY_RANGE.getLength() / 2 ) );
        bond = new Bond( atomA, atomB );
        initObservers();
    }

    public Atom[] getAtoms() {
        return new Atom[] { atomA, atomB };
    }

    protected Bond[] getBonds() {
        return new Bond[] { bond };
    }

    // Gets the difference in electronegativity (EN), positive sign indicates atom B has higher EN.
    public double getDeltaEN() {
        return atomB.electronegativity.get() - atomA.electronegativity.get();
    }

    // repositions the atoms
    protected void updateAtomLocations() {
        final double radius = BOND_LENGTH / 2;
        // atom A
        double xA = PolarCartesianConverter.getX( radius, angle.get() + Math.PI ) + location.getX();
        double yA = PolarCartesianConverter.getY( radius, angle.get() + Math.PI ) + location.getY();
        atomA.location.set( new Vector2D( xA, yA ) );
        // atom B
        double xB = PolarCartesianConverter.getX( radius, angle.get() ) + location.getX();
        double yB = PolarCartesianConverter.getY( radius, angle.get() ) + location.getY();
        atomB.location.set( new Vector2D( xB, yB ) );
    }

    // updates partial charges
    protected void updatePartialCharges() {
        final double deltaEN = atomA.electronegativity.get() - atomB.electronegativity.get();
        // in our simplified model, partial charge and deltaEN are equivalent. not so in the real world.
        atomA.partialCharge.set( -deltaEN );
        atomB.partialCharge.set( deltaEN );
    }
}
