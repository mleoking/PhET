package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.ParticleContainer;
import edu.colorado.phet.common.phys2d.Particle;

import java.util.Vector;

public class CompositeParticleContainer {
    Vector v;

    public CompositeParticleContainer() {
        v = new Vector();
    }

    public void add( ParticleContainer pc ) {
        v.add( pc );
    }

    public void remove( ParticleContainer pc ) {
        v.remove( pc );
    }

    public void add( Particle p ) {
        for( int i = 0; i < v.size(); i++ ) {
            ( (ParticleContainer)v.get( i ) ).add( p );
        }
    }

    public void remove( Particle p ) {
        for( int i = 0; i < v.size(); i++ ) {
            ( (ParticleContainer)v.get( i ) ).remove( p );
        }
    }
}
