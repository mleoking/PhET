// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AbstractDilutionsModule extends PiccoloModule {
    protected AbstractDilutionsModule( String title ) {
        super( title, new ConstantDtClock( 25 ) );
        setLogoPanel( null );
        setControlPanel( null );
        setClockControlPanel( null );
    }
}
