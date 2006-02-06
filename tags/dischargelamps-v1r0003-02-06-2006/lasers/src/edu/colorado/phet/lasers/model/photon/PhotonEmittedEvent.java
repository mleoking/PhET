package edu.colorado.phet.lasers.model.photon;

import java.util.EventObject;
import java.util.EventListener;

/**
 * Class: PhotonEmittedEvent
 * Package: edu.colorado.phet.lasers.model.photon
 * Author: Another Guy
 * Date: Nov 19, 2004
 * <p/>
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */

public class PhotonEmittedEvent extends EventObject {
    private Photon photon;

    public PhotonEmittedEvent( Object source, Photon photon ) {
        super( source );
        this.photon = photon;
    }

    public Photon getPhoton() {
        return photon;
    }
}
