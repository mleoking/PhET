/**
 * Class: BasicPhotonAbsorber
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 12, 2003
 * Time: 9:24:57 AM
 */
package edu.colorado.phet.greenhouse;

public class BasicPhotonAbsorber extends AbstractPhotonAbsorber {

    public void stepInTime( double dt ) {
    }

    // Every photon is removed
    public void absorbPhoton( Photon photon ) {
        super.notifyListeners( photon );
    }
}
