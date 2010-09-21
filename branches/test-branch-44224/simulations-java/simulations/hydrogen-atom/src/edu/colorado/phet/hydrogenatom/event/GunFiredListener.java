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
 * GunFiredListener is the interface implemented by all listeners
 * who wish to be informed when the gun is fired.
 */
public interface GunFiredListener extends EventListener {
    public void photonFired( GunFiredEvent event );
    public void alphaParticleFired( GunFiredEvent event );
}
