/**
 * Class: LeftReflecting
 * Class: edu.colorado.phet.lasers.physics.mirror
 * User: Ron LeMaster
 * Date: Mar 31, 2003
 * Time: 7:20:26 PM
 */
package edu.colorado.phet.lasers.physics.mirror;

import edu.colorado.phet.lasers.physics.photon.Photon;

public class RightReflecting implements ReflectionStrategy {

    public boolean reflects( Photon photon ) {
        return photon.getVelocity().getX() < 0;
    }
}
