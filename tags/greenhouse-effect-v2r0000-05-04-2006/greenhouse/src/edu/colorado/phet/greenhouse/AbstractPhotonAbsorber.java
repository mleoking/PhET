/**
 * Class: AbstractPhotonEmitter
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.model.ModelElement;

import java.util.ArrayList;

public abstract class AbstractPhotonAbsorber extends ModelElement implements PhotonAbsorber {

    private ArrayList listeners = new ArrayList();

    public void addListener( PhotonAbsorber.Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( PhotonAbsorber.Listener listener ) {
        listeners.remove( listener );
    }

    protected void notifyListeners( Photon photon ) {
        for( int j = 0; j < listeners.size(); j++ ) {
            Listener listener = (Listener)listeners.get( j );
            listener.photonAbsorbed( photon );
        }
    }
}
