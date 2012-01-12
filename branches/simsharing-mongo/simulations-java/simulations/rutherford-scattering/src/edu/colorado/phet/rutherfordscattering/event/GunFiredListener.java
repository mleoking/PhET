// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.event;

import java.util.EventListener;


/**
 * GunFiredListener is the interface implemented by all listeners
 * who wish to be informed when the gun is fired.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface GunFiredListener extends EventListener {
    public void alphaParticleFired( GunFiredEvent event );
}
