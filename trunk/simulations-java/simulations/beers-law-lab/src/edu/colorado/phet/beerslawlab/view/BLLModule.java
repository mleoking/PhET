// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;

/**
 * Base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLModule extends SimSharingPiccoloModule {
    protected BLLModule( IUserComponent userComponent, String title ) {
        super( userComponent, title, new ConstantDtClock( 25 ) );
        setLogoPanel( null );
        setControlPanel( null );
        setClockControlPanel( null );
    }
}
