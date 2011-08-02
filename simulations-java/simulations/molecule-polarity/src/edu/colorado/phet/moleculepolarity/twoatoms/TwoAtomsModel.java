// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
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

    private void updateMoleculeOrientation() {
        //TODO align molecular dipole with E-Field, use constant angular acceleration, don't overshoot
    }

}
