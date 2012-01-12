// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rutherfordscattering.event;

import java.util.EventListener;



/**
 * ParticleListener is the interface implemented by listener who 
 * wish to be notified of changes to alpha particles.
 */
public interface ParticleListener extends EventListener {
    public void particleAdded( ParticleEvent event);
    public void particleRemoved( ParticleEvent event);
}