/**
 * Class: Thorium143
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Mar 22, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class Thorium143 extends Nucleus {

    public Thorium143( Point2D.Double position ) {
        // Leave the number of protons at 92, so it will give the same max potential
        // as U235, even though this is inaccurate
        super( position, 92, 143 );
//        super( position, 90, 143 );
        getPotentialProfile().setWellPotential( 2 );
        getPotentialProfile().updateObservers();
    }
}
