// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;

/**
 * "Three Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThreeAtomsModule extends PiccoloModule {

    public ThreeAtomsModule( Frame parentFrame ) {
        super( MPStrings.THREE_ATOMS, new MPClock() );
        setSimulationPanel( new ThreeAtomsCanvas( parentFrame ) );
        setClockControlPanel( null );
    }
}
