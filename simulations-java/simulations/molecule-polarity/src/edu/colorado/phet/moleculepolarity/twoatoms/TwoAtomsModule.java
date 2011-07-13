// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;

/**
 * "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModule extends PiccoloModule {

    public TwoAtomsModule( Frame parentFrame ) {
        super( MPStrings.TWO_ATOMS, new MPClock() );
        setSimulationPanel( new TwoAtomsCanvas( parentFrame ) );
        setClockControlPanel( null );
    }
}
