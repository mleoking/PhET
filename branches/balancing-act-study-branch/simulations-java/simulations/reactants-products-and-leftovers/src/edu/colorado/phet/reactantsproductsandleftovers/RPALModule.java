// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;

/**
 * Base class for all modules in this sim.
 *
 * @author Chris Malley
 */
public class RPALModule extends SimSharingPiccoloModule {

    public RPALModule( IUserComponent userComponent, String title, IClock clock, boolean startsPaused ) {
        super( userComponent, title, clock, startsPaused );
        setLogoPanel( null ); // workaround for #2015
        setControlPanel( null );
        setClockControlPanel( null );
    }
}
