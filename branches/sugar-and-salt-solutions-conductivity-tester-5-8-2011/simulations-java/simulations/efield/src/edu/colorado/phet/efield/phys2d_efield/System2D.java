// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield;

import java.util.Vector;

public class System2D {

    Vector particles;
    Vector laws;

    public System2D() {
        particles = new Vector();
        laws = new Vector();
    }

    public void remove( Particle particle ) {
        particles.remove( particle );
    }

    public String toString() {
        return "particles=" + particles + ", laws=" + laws;
    }

    public void addParticle( Particle particle ) {
        particles.add( particle );
    }

    public void addLaw( Law law ) {
        if ( !( law instanceof Law ) ) {
            throw new RuntimeException( "What?!" );
        }
        else {
            laws.add( law );
            return;
        }
    }

    public void addLaw( Propagator propagator ) {
        PropagatorLaw propagatorlaw = new PropagatorLaw( propagator );
        addLaw( ( (Law) ( propagatorlaw ) ) );
    }

    public int numLaws() {
        return laws.size();
    }

    public Law lawAt( int i ) {
        return (Law) laws.get( i );
    }

    public int numParticles() {
        return particles.size();
    }

    public Particle particleAt( int i ) {
        return (Particle) particles.get( i );
    }

    public void iterate( double d ) {
        for ( int i = 0; i < numLaws(); i++ ) {
            lawAt( i ).iterate( d, this );
        }

    }

}
