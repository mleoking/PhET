// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.moleculepolarity.MPConstants;

/**
 * Base class for models in this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MPModel implements Resettable {

    public final EField eField = new EField();

    public void reset() {
        eField.reset();
    }

    /*
     * Rotate the molecule one step towards alignment of the molecular dipole with the E-field.
     * Angular velocity is proportional to the dipole's magnitude.
     */
    protected void updateMoleculeOrientation( IMolecule molecule ) {

        // magnitude of angular velocity is proportional to molecular dipole magnitude
        LinearFunction angularVelocityFunction = new LinearFunction( 0, MPConstants.ELECTRONEGATIVITY_RANGE.getLength(), 0, Math.toRadians( 10 ) );
        final double deltaDipoleAngle = Math.abs( angularVelocityFunction.evaluate( molecule.getDipole().getMagnitude() ) );

        // convert angle to range [0,2*PI)
        final double dipoleAngle = normalizeAngle( molecule.getDipole().getAngle() );

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

    // converts an angle to range [0,2*PI) radians
    protected static double normalizeAngle( double angle ) {
        double normalizedAngle = angle % ( 2 * Math.PI );
        if ( normalizedAngle < 0 ) {
            normalizedAngle = ( 2 * Math.PI ) + angle;
        }
        assert ( normalizedAngle >= 0 && normalizedAngle <= 2 * Math.PI );
        return normalizedAngle;
    }
}
