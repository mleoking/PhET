// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;

/**
 * Base class for all modules in this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGModule extends SimSharingPiccoloModule {
    protected LGModule( IUserComponent userComponent, String title ) {
        super( userComponent, title, new ConstantDtClock( 25 ) );
        setLogoPanel( null );
        setControlPanel( null );
        setClockControlPanel( null );
    }
}
