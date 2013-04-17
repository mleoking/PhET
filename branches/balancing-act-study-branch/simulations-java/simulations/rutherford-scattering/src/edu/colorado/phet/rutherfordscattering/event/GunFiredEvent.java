// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.event;

import java.util.EventObject;

import edu.colorado.phet.rutherfordscattering.model.AlphaParticle;

/**
 * GunFiredEvent indicates that the gun has been fired.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GunFiredEvent extends EventObject {

    private AlphaParticle _alphaParticle;

    public GunFiredEvent( Object source, AlphaParticle alphaParticle ) {
        super( source );
        assert( alphaParticle != null );
        _alphaParticle = alphaParticle;
    }
    
    public AlphaParticle getAlphaParticle() {
        return _alphaParticle;
    }
}
