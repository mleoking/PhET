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
        super( position, 90, 143 );
        getPotentialProfile().setWellPotential( -50 );
//        getPotentialProfile().setWellPotential( getPotentialProfile().getWellPotential() * 2 / 3 );
        getPotentialProfile().updateObservers();
    }
}
