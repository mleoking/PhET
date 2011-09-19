// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.colorado.phet.moleculepolarity.common.model.MPModel2D;

/**
 * Model for the "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModel extends MPModel2D {

    public final DiatomicMolecule molecule;

    public TwoAtomsModel( IClock clock ) {

        molecule = new DiatomicMolecule( new ImmutableVector2D( 350, 390 ), 0 );

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                // if the E-field is on and the user isn't controlling the molecule's orientation...
                if ( eField.enabled.get() && !molecule.isDragging() ) {
                    updateMoleculeOrientation( molecule );
                }
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        molecule.reset();
    }
}
