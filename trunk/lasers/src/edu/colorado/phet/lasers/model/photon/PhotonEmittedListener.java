/**
 * Class: PhotonEmittedListener
 * Package: edu.colorado.phet.lasers.model.photon
 * Author: Another Guy
 * Date: Nov 19, 2004
 *
 * CVS Info:
 *  Current revision:   $Revision$
 *  On branch:          $Name$
 *  Latest change by:   $Author$
 *  On date:            $Date$      
 */
package edu.colorado.phet.lasers.model.photon;

import java.util.EventListener;

public interface PhotonEmittedListener extends EventListener {
    public void photonEmittedEventOccurred( PhotonEmittedEvent event );
}
