/**
 * Class: BandPass
 * Package: edu.colorado.phet.lasers.model.mirror
 * Author: Another Guy
 * Date: Apr 2, 2003
 */
package edu.colorado.phet.lasers.model.mirror;

import edu.colorado.phet.lasers.model.photon.Photon;

public class BandPass implements ReflectionStrategy {

    private float cutoffLow;
    private float cutoffHigh;

    public BandPass( float cutoffLow, float cutoffHigh ) {
        this.cutoffLow = cutoffLow;
        this.cutoffHigh = cutoffHigh;
    }

    public boolean reflects( Photon photon ) {
        return( photon.getWavelength() >= cutoffLow
            && photon.getWavelength() <= cutoffHigh );
    }
}
