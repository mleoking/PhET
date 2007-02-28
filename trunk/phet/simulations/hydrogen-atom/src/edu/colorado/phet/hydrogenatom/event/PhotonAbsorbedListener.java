/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.event;

import java.util.EventListener;

/**
 * PhotonAbsorbedListener is the interface implemented by all listeners
 * who wish to be informed when a photon is absorbed.
 */
public interface PhotonAbsorbedListener extends EventListener {

    public void photonAbsorbed( PhotonAbsorbedEvent event );
}
