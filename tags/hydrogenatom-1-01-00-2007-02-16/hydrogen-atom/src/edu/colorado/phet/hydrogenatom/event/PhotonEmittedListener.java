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
 * PhotonEmittedListener is the interface implemented by all listeners
 * who wish to be informed when a photon is emitted.
 */
public interface PhotonEmittedListener extends EventListener {

    public void photonEmitted( PhotonEmittedEvent event );
}
