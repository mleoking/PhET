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

import java.util.EventObject;

import edu.colorado.phet.hydrogenatom.model.Photon;

/**
 * PhotonAbsorbedEvent indicates that a photon has been absorbed.
 */
public class PhotonAbsorbedEvent extends EventObject {

    private Photon _photon;

    public PhotonAbsorbedEvent( Object source, Photon photon ) {
        super( source );
        assert ( photon != null );
        _photon = photon;
    }

    public Photon getPhoton() {
        return _photon;
    }
}