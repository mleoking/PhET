// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rutherfordscattering.event;

import java.util.EventObject;

import edu.colorado.phet.rutherfordscattering.model.AlphaParticle;

/**
 * ParticleEvent is the event used to indicate changes an alpha particle.
 */
public class ParticleEvent extends EventObject {

    private AlphaParticle _particle;

    public ParticleEvent( Object source, AlphaParticle particle ) {
        super( source );
        _particle = particle;
    }

    public AlphaParticle getParticle() {
        return _particle;
    }
}