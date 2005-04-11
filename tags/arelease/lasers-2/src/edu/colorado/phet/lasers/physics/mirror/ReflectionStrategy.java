/**
 * Class: ReflectionStrategy
 * Class: edu.colorado.phet.lasers.physics
 * User: Ron LeMaster
 * Date: Mar 31, 2003
 * Time: 7:18:04 PM
 */
package edu.colorado.phet.lasers.physics.mirror;

import edu.colorado.phet.lasers.physics.photon.Photon;

interface ReflectionStrategy {

    public boolean reflects( Photon photon );

}
