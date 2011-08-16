// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;
import edu.colorado.phet.moleculepolarity.common.model.TriatomicMolecule;

/**
 * Model for the "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThreeAtomsModel extends MPModel {

    public final TriatomicMolecule molecule;

    public ThreeAtomsModel( IClock clock ) {

        molecule = new TriatomicMolecule( new ImmutableVector2D( 425, 390 ) ); //TODO revisit this and make sure it's accounted for in canvas layout

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                if ( eField.enabled.get() && !molecule.isDragging() ) {
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
        LinearFunction angularVelocityFunction = new LinearFunction( 0, MPConstants.ELECTRONEGATIVITY_RANGE.getLength(), 0, Math.toRadians( 10 ) );
        final double deltaDipoleAngle = Math.abs( angularVelocityFunction.evaluate( molecule.dipole.get().getMagnitude() ) );

        // convert angle to range [0,2*PI)
        final double dipoleAngle = normalizeAngle( molecule.dipole.get().getAngle() );

        // move the molecular dipole one step towards alignment with the E-field
        double newDipoleAngle = dipoleAngle;
        if ( dipoleAngle == 0 ) {
            // do nothing, molecule is aligned with E-field
        }
        else if ( dipoleAngle > 0 && dipoleAngle < Math.PI ) {
            // rotate counterclockwise
            newDipoleAngle = dipoleAngle - deltaDipoleAngle;
            if ( newDipoleAngle < 0 ) {
                // new angle would overshoot, set to zero
                newDipoleAngle = 0;
            }
        }
        else {
            // rotate clockwise
            newDipoleAngle = dipoleAngle + deltaDipoleAngle;
            if ( newDipoleAngle > 2 * Math.PI ) {
                // new angle would overshoot, set to zero
                newDipoleAngle = 0;
            }
        }

        // convert dipole rotation to molecule rotation
        double deltaMoleculeAngle = newDipoleAngle - dipoleAngle;
        molecule.setAngle( molecule.getAngle() + deltaMoleculeAngle );
    }
}
