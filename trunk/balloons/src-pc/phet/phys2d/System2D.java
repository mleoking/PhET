package phet.phys2d;

import java.util.Vector;

public class System2D {
    Vector particles;
    Vector laws;

    public System2D() {
        particles = new Vector();
        laws = new Vector();
    }

    public void remove( Particle p ) {
        particles.remove( p );
    }

    public String toString() {
        return "particles=" + particles + ", laws=" + laws;
    }

    public void addParticle( Particle p ) {
        particles.add( p );
    }

    public void addLaw( Law lx ) {
        //util.Debug.traceln("adding law: "+lx+", "+lx.getClass().getName());
        if( !( lx instanceof Law ) ) {
            throw new RuntimeException( "What?!" );
        }
        laws.add( lx );
    }

    public void addLaw( Propagator prop ) {
        Law x = new PropagatorLaw( prop );
        addLaw( x );
    }

    public int numLaws() {
        return laws.size();
    }

    public void removeAllLaws() {
        laws = new Vector();
    }

    public void remove( Law a ) {
        laws.remove( a );
    }

    public Law lawAt( int i ) {
        return (Law)laws.get( i );
    }

    public int numParticles() {
        return particles.size();
    }

    public boolean containsParticle( Particle p ) {
        return particles.contains( p );
    }

    public Particle removeParticle( int i ) {
        return (Particle)particles.remove( i );
    }

    public Particle particleAt( int i ) {
        return (Particle)particles.get( i );
    }

    public void iterate( double time ) {
        for( int i = 0; i < numLaws(); i++ ) {
            lawAt( i ).iterate( time, this );
        }
    }
}
