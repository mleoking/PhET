// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;

/**
 * Model of a make-believe triatomic (3 atoms) molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TriatomicMolecule implements IMolecule {

    private static final double BOND_LENGTH = 150;

    public final Atom atomA, atomB, atomC; // the atoms labeled A, B, C
    public final Bond bondAB; // the bond connecting atoms A and B
    public final Bond bondBC; // the bond connecting atoms B and C
    private final Property<ImmutableVector2D> location; // location is at the center of atom B
    private final Property<Double> angle; // angle of rotation of the entire molecule about the location
    public final Property<Double> bondAngleA; // the bond angle of atom A relative to atom B, before applying molecule rotation
    public final Property<Double> bondAngleC; // the bond angle of atom C relative to atom B, before applying molecule rotation

    private boolean dragging; // true when the user is dragging the molecule

    public TriatomicMolecule( ImmutableVector2D location ) {

        this.location = new Property<ImmutableVector2D>( location );
        atomA = new Atom( MPStrings.A, 100, Color.YELLOW, MPConstants.ELECTRONEGATIVITY_RANGE.getMin() );
        atomB = new Atom( MPStrings.B, 100, Color.ORANGE, MPConstants.ELECTRONEGATIVITY_RANGE.getMax() );
        atomC = new Atom( MPStrings.C, 100, Color.GREEN, MPConstants.ELECTRONEGATIVITY_RANGE.getMin() );
        bondAB = new Bond( atomA, atomB );
        bondBC = new Bond( atomB, atomC );
        angle = new Property<Double>( 0d );
        bondAngleA = new Property<Double>( Math.PI );
        bondAngleC = new Property<Double>( 0d );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                updateAtomLocations();
            }
        };
        angle.addObserver( observer );
        bondAngleA.addObserver( observer );
        bondAngleC.addObserver( observer );
    }

    public void reset() {
        location.reset();
        atomA.reset();
        atomB.reset();
        atomC.reset();
        angle.reset();
        bondAngleA.reset();
        bondAngleC.reset();
    }

    public Atom[] getAtoms() {
        return new Atom[] { atomA, atomB, atomC };
    }

    public ImmutableVector2D getLocation() {
        return location.get();
    }

    public void setAngle( double angle ) {
        this.angle.set( angle );
    }

    public double getAngle() {
        return angle.get();
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging( boolean dragging ) {
        this.dragging = dragging;
    }

    // repositions the atoms
    private void updateAtomLocations() {
        final double radius = BOND_LENGTH;
        // atom B remains at the molecule's location
        atomB.location.set( location.get() );
        // atom A
        double thetaA = angle.get() + bondAngleA.get();
        double xA = PolarCartesianConverter.getX( radius, thetaA ) + location.get().getX();
        double yA = PolarCartesianConverter.getY( radius, thetaA ) + location.get().getY();
        atomA.location.set( new ImmutableVector2D( xA, yA ) );
        // atom C
        double thetaC = angle.get() + bondAngleC.get();
        double xC = PolarCartesianConverter.getX( radius, thetaC ) + location.get().getX();
        double yC = PolarCartesianConverter.getY( radius, thetaC ) + location.get().getY();
        atomC.location.set( new ImmutableVector2D( xC, yC ) );
    }
}
