/**
 * Class: AlphaParticle
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Mar 4, 2004
 * Time: 8:55:18 AM
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class AlphaParticle extends Nucleus {

    //
    // Statics
    //
    static {
        maxStatisticalLocationOffset = 200;
    }

    public static final double RADIUS = NuclearParticle.RADIUS * 2;


    //
    // Instance fields and methods
    //
    public AlphaParticle( Point2D.Double position, double statisticalPositionSigma ) {
        super( position, 2, 2, new PotentialProfile( 1, 1, 1 ) );
        setStatisticalLocationOffsetSigma( statisticalPositionSigma );
    }


    //
    // Interfaces implemented
    //
    public Point2D.Double getCM() {
        return getLocation();
    }

    public double getMomentOfInertia() {
        return Double.NaN;
    }
}
