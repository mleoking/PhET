// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

/**
 * Adds simsharing features to tab button presses.
 *
 * @author Sam Reid
 */
public class SimSharingPiccoloModule extends PiccoloModule {

    private final UserComponent tabUserComponent;

    public SimSharingPiccoloModule( UserComponent tabUserComponent, String name, IClock clock ) {
        super( name, clock );
        this.tabUserComponent = tabUserComponent;
    }

    //Used in Tab node code for sim sharing, to have a good ID for the tab associated with this module.
    @Override public UserComponent getTabUserComponent() {
        return tabUserComponent;
    }
}