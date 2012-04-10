// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.common;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;

/**
 * Module for fractions sims.
 *
 * @author Sam Reid
 */
public class AbstractFractionsModule extends SimSharingPiccoloModule {
    public AbstractFractionsModule( IUserComponent tabUserComponent, String name, Clock clock ) {
        super( tabUserComponent, name, clock );
        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }
}