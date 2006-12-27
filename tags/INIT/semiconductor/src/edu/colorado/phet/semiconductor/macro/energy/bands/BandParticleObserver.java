package edu.colorado.phet.semiconductor.macro.energy.bands;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 4:35:06 PM
 * Copyright (c) Jan 18, 2004 by Sam Reid
 */
public interface BandParticleObserver {
    void particleRemoved( BandParticle bandParticle );

    void addParticle( BandParticle bandParticle );
}
