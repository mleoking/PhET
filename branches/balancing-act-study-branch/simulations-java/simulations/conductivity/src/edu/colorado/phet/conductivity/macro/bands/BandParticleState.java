// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.macro.bands;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            BandParticle

public interface BandParticleState {

    public abstract BandParticleState stepInTime( BandParticle bandparticle, double d );
}
