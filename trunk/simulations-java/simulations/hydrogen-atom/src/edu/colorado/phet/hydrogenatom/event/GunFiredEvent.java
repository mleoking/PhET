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

import edu.colorado.phet.hydrogenatom.model.AlphaParticle;
import edu.colorado.phet.hydrogenatom.model.Photon;

/**
 * GunFiredEvent indicates that the gun has been fired.
 */
public class GunFiredEvent extends EventObject {

    private Photon _photon;
    private AlphaParticle _alphaParticle;

    public GunFiredEvent( Object source, Photon photon ) {
        super( source );
        assert( photon != null );
        _photon = photon;
        _alphaParticle = null;
    }

    public GunFiredEvent( Object source, AlphaParticle alphaParticle ) {
        super( source );
        assert( alphaParticle != null );
        _photon = null;
        _alphaParticle = alphaParticle;
    }
    
    public Photon getPhoton() {
        return _photon;
    }
    
    public AlphaParticle getAlphaParticle() {
        return _alphaParticle;
    }
}
