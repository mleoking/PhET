/**
 * Class: AlphaRadiationModule
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 11:58:03 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.controller.NuclearPhysicsModule;
import edu.colorado.phet.nuclearphysics.model.Uranium2235;

import java.awt.geom.Point2D;

public class AlphaRadiationModule extends NuclearPhysicsModule {

    public AlphaRadiationModule( AbstractClock clock ) {
        super( "Alpha Radiation", clock );

        uraniumNucleus = new Uranium2235( new Point2D.Double( 200, 400 ) );
        addNeucleus( uraniumNucleus );

        // Start a clock for alpha decay
    }
}
