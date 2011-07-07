// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.event;

import java.util.EventListener;

import edu.colorado.phet.hydrogenatom.model.Photon;

/**
 * PhotonListener is the interface implemented by all listeners
 * who wish to be informed when a photon is absorbed or emitted.
 */
public interface PhotonListener extends EventListener {

    public void photonAbsorbed( Photon photon );

    public void photonEmitted( Photon photon );
}
