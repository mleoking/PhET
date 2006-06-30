/**
 * Class: Thorium143
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Mar 22, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class Thorium141 extends Nucleus {
    public static final int NUM_PROTONS = 90;
    public static final int NUM_NEUTRONS = 141;


    public Thorium141( Point2D position ) {
        // Leave the number of protons at 92, so it will give the same max potential
        // as U235, even though this is inaccurate
        super( position, 90, 141 );
//        getEnergyProfile().setMinEnergy( 2 );
        getEnergyProfile().notifyObservers();
    }
}
