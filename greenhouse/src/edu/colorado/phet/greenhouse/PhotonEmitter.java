/**
 * Class: PhotonEmitter
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

public interface PhotonEmitter {

    void addListener( Listener listener );

    void removeListener( Listener listener );

    double getProductionRate();

    void setProductionRate( double productionRate );

    Photon emitPhoton();

    //
    // Inner classes
    //
    public interface Listener {
        void photonEmitted( Photon photon );
    }

}
