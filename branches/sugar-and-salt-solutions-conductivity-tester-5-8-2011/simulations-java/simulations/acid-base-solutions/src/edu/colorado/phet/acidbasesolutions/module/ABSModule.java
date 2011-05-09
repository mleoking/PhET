// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.module;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Base class for all modules.
 * The clock is irrelevant, not used in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ abstract class ABSModule extends PiccoloModule {

    public ABSModule( String name ) {
        super( name, new ConstantDtClock( 40, 1 ), true /* startsPaused */ );
        setClockControlPanel( null );
        setLogoPanelVisible( false );
    }
}
