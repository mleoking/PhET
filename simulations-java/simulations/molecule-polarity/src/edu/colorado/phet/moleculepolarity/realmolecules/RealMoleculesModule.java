// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;

/**
 * "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesModule extends PiccoloModule {

    public RealMoleculesModule() {
        super( "X", new MPClock() );
        setSimulationPanel( new RealMoleculesCanvas() );
    }
}
