/**
 * Class: Cesium
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.geom.Point2D;

public class Cesium extends Nucleus {
    public static final int numNeutrons = 86;
    public static final int numProtons = 55;

    public Cesium( Point2D position ) {
        super( position, numProtons, numNeutrons );
    }
}
