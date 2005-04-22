/**
 * Class: ReflectionStrategy
 * Class: edu.colorado.phet.lasers.model
 * User: Ron LeMaster
 * Date: Mar 31, 2003
 * Time: 7:18:04 PM
 */
package edu.colorado.phet.lasers.model.mirror;

import edu.colorado.phet.lasers.model.photon.Photon;

interface ReflectionStrategy {

    public boolean reflects( Photon photon );

}
