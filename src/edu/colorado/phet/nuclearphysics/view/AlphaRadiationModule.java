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
import edu.colorado.phet.nuclearphysics.model.DecayListener;
import edu.colorado.phet.nuclearphysics.model.DecayProducts;
import edu.colorado.phet.nuclearphysics.model.Uranium235;

import java.awt.geom.Point2D;

public class AlphaRadiationModule extends NuclearPhysicsModule implements DecayListener {

    public AlphaRadiationModule( AbstractClock clock ) {
        super( "Alpha Radiation", clock );

        setUraniumNucleus( new Uranium235( new Point2D.Double( 200, 400 ) ) );
        getUraniumNucleus().addDecayListener( this );

        // Start a clock for alpha decay
    }

    public void alphaDecay( DecayProducts decayProducts ) {
        getPotentialProfilePanel().addDecayProduct( decayProducts.getN1() );
        super.handleDecay( decayProducts );
    }

}
