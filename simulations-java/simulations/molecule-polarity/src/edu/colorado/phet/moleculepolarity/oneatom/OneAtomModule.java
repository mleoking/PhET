// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.oneatom;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;

/**
 * "One Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OneAtomModule extends PiccoloModule {

    public OneAtomModule() {
        super( MPStrings.ONE_ATOM, new MPClock() );
        setSimulationPanel( new OneAtomCanvas() );
    }
}
