// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;

/**
 * "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModule extends PiccoloModule {

    public TwoAtomsModule() {
        super( "X", new MPClock() );
        setSimulationPanel( new TwoAtomsCanvas() );
    }
}
