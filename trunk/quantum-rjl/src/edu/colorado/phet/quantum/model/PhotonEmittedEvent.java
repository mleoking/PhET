/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

import java.util.EventObject;

/**
 * PhotonEmittedEvent
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
