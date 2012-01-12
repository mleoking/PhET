// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Adds simsharing features to tab button presses.
 *
 * @author Sam Reid
 */
public class SimSharingPiccoloModule extends PiccoloModule {

    private final IUserComponent tabUserComponent;

    public SimSharingPiccoloModule( IUserComponent tabUserComponent, String name, IClock clock ) {
        super( name, clock );
        this.tabUserComponent = tabUserComponent;
    }

    public SimSharingPiccoloModule( IUserComponent tabUserComponent, String name, IClock clock, boolean startsPaused ) {
        super( name, clock, startsPaused );
        this.tabUserComponent = tabUserComponent;
    }

    //Used in Tab node code for sim sharing, to have a good ID for the tab associated with this module.
    @Override public IUserComponent getTabUserComponent() {
        return tabUserComponent;
    }
}