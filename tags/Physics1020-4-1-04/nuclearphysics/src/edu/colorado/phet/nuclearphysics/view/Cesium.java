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
    private static final int numNeutrons = 86;
    private static final int numProtons = 55;

    public Cesium( Point2D.Double position ) {
        super( position, numProtons, numNeutrons );
    }
}
