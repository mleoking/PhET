/**
 * Class: FissionModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.model.Uranium235;

import java.awt.geom.Point2D;

public class FissionModule extends NuclearPhysicsModule {

    public FissionModule( AbstractClock clock ) {
        super( "Fission", clock );

        setUraniumNucleus( new Uranium235( new Point2D.Double( 200, 400 ) ) );
        addNeucleus( getUraniumNucleus() );

        // Add an incoming neutron

    }
}
