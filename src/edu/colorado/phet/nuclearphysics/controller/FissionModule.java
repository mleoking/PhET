/**
 * Class: FissionModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.clock.AbstractClock;

public class FissionModule extends NuclearPhysicsModule {

    public FissionModule( AbstractClock clock ) {
        super( "Fission", clock );
        super.addControlPanelElement( new TestControlPanel( this ) );
    }
}
