/**
 * Class: PhotonFactory
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 30, 2003
 */
package edu.colorado.phet.greenhouse;

import java.util.HashSet;
import java.util.Iterator;

public class PhotonFactory implements PhotonEmitter, PhotonAbsorber {

    private HashSet emitterListeners = new HashSet();
    private HashSet absorberListeners = new HashSet();
    private Photon emittedPhoton;

    public void addListener( PhotonEmitter.Listener listener ) {
        emitterListeners.add( listener );
    }

    public void removeListener( PhotonEmitter.Listener listener ) {
        emitterListeners.remove( listener );
    }

    public double getProductionRate() {
        return 0;
    }

    public void setProductionRate( double productionRate ) {
    }

    public void emitPhoton( Photon photon ) {
        emittedPhoton = photon;
        emitPhoton();
    }

    public Photon emitPhoton() {

        for( Iterator iterator = emitterListeners.iterator(); iterator.hasNext(); ) {
            PhotonEmitter.Listener listener = (PhotonEmitter.Listener)iterator.next();
            listener.photonEmitted( emittedPhoton );
        }
        return null;
    }

    public void addListener( PhotonAbsorber.Listener listener ) {
        absorberListeners.add( listener );
    }

    public void removeListener( PhotonAbsorber.Listener listener ) {
        absorberListeners.remove( listener );
    }

    public void absorbPhoton( Photon photon ) {
        for( Iterator iterator = absorberListeners.iterator(); iterator.hasNext(); ) {
            PhotonAbsorber.Listener listener = (PhotonAbsorber.Listener)iterator.next();
            listener.photonAbsorbed( photon );
        }
    }
}
