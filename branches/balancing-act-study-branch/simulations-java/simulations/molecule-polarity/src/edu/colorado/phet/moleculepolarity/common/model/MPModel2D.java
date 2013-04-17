// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.moleculepolarity.MPConstants;

/**
 * Base class for 2D models in this sim.
 * Every 2D model has an E-field and a molecule.
 * If the E-field is enabled, the molecule rotates until its molecular dipole is aligned with the E-field.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MPModel2D implements Resettable {

    // constants that control animation of E-field alignment
    private static final double MAX_RADIANS_PER_STEP = Math.toRadians( 10 );
    private static final LinearFunction ANGULAR_VELOCITY_FUNCTION = new LinearFunction( 0, MPConstants.ELECTRONEGATIVITY_RANGE.getLength(), 0, MAX_RADIANS_PER_STEP );

    public final EField eField;
    private final Molecule2D molecule;

    protected MPModel2D( IClock clock, final Molecule2D molecule ) {
        this.eField = new EField();
        this.molecule = molecule;
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                // if the E-field is on and the user isn't controlling the molecule's orientation...
                if ( eField.enabled.get() && !molecule.isDragging() ) {
                    updateMoleculeOrientation( molecule );
                }
            }
        } );
    }

    public void reset() {
        eField.reset();
        molecule.reset();
    }

    protected Molecule2D getMolecule() {
        return molecule;
    }

    /*
     * Rotate the molecule one step towards alignment of the molecular dipole with the E-field.
     * Angular velocity is proportional to the dipole's magnitude.
     */
    protected void updateMoleculeOrientation( Molecule2D molecule ) {

        // magnitude of angular velocity is proportional to molecular dipole magnitude
        final double deltaDipoleAngle = Math.abs( ANGULAR_VELOCITY_FUNCTION.evaluate( molecule.dipole.get().magnitude() ) );

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
        molecule.angle.set( molecule.angle.get() + deltaMoleculeAngle );
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
