/**
 * Class: PhotonAbsorber
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 12, 2003
 * Time: 9:13:53 AM
 */
package edu.colorado.phet.greenhouse;

public interface PhotonAbsorber {

    void addListener( PhotonAbsorber.Listener listener );

    void removeListener( PhotonAbsorber.Listener listener );

    void absorbPhoton( Photon photon );

    //
    // Inner classes
    //
    public interface Listener {
        void photonAbsorbed( Photon photon );
    }
}
