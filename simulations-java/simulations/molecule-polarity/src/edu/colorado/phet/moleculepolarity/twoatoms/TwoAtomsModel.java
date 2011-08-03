// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomMolecule;

/**
 * Model for the "One Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModel extends MPModel {

    public final TwoAtomMolecule molecule;

    public TwoAtomsModel( IClock clock ) {

        molecule = new TwoAtomMolecule( new ImmutableVector2D( 325, 390 ) );

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                if ( eField.enabled.get() && !molecule.isDragging() && molecule.bond.deltaElectronegativity.get() != 0 ) {
                    // if the E-field is on and the user isn't controlling the molecule's orientation...
                    updateMoleculeOrientation();
                }
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        molecule.reset();
    }

    /*
     * Rotate the molecule one step towards alignment with the E-field.
     * Angular velocity proportional to the different in electronegativity between the 2 atoms.
     */
    private void updateMoleculeOrientation() {

        // magnitude of angular velocity is proportional to molecular dipole magnitude
        final double deltaEN = molecule.bond.deltaElectronegativity.get();
        LinearFunction enToAngle = new LinearFunction( 0, MPConstants.ELECTRONEGATIVITY_RANGE.getLength(), 0, Math.toRadians( 10 ) );
        final double deltaAngle = Math.abs( enToAngle.evaluate( deltaEN ) );

        // convert angle to range [0,2*PI)
        final double angle = normalizeAngle( molecule.angle.get() );

        //TODO simplify, this seems unnecessarily complicated
        // move the molecule one step towards alignment with the E-field
        double newAngle = angle;
        if ( deltaEN > 0 ) {
            // dipole points from atomA to atomB, rotate toward angle=0
            if ( angle == 0 ) {
                // do nothing, molecule is aligned with E-field
            }
            else if ( angle > 0 && angle < Math.PI ) {
                newAngle = angle - deltaAngle;
                if ( newAngle < 0 ) {
                    newAngle = 0;
                }
            }
            else {
                newAngle = angle + deltaAngle;
                if ( newAngle > 2 * Math.PI ) {
                    newAngle = 0;
                }
            }
        }
        else {
            // dipole points from atomB to atomA, rotate toward angle=PI
            if ( angle == Math.PI ) {
                // do nothing, molecule is aligned with E-field
            }
            else if ( angle >= 0 && angle < Math.PI ) {
                newAngle = angle + deltaAngle;
                if ( newAngle > Math.PI ) {
                    newAngle = Math.PI;
                }
            }
            else {
                newAngle = angle - deltaAngle;
                if ( newAngle < Math.PI ) {
                    newAngle = Math.PI;
                }
            }
        }
        molecule.angle.set( newAngle );
    }

    // converts an angle to range [0,2*PI) radians
    private static double normalizeAngle( double angle ) {
        double normalizedAngle = angle % ( 2 * Math.PI );
        if ( normalizedAngle < 0 ) {
            normalizedAngle = ( 2 * Math.PI ) + angle;
        }
        assert ( normalizedAngle >= 0 && normalizedAngle <= 2 * Math.PI );
        return normalizedAngle;
    }

}
