/**
 * Class: AbstractPhotonEmitter
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse.model;

import java.util.ArrayList;
import java.util.Observable;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

public abstract class AbstractPhotonAbsorber extends Observable implements ModelElement, PhotonAbsorber {

    private ArrayList listeners = new ArrayList();

    public void addListener( PhotonAbsorber.Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( PhotonAbsorber.Listener listener ) {
        listeners.remove( listener );
    }

    protected void notifyListeners( Photon photon ) {
        for ( int j = 0; j < listeners.size(); j++ ) {
            Listener listener = (Listener) listeners.get( j );
            listener.photonAbsorbed( photon );
        }
    }
}
