/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.photon;

import java.util.EventListener;

public interface PhotonEmittedListener extends EventListener {
    public void photonEmitted( PhotonEmittedEvent event );
}
