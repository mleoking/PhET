package edu.colorado.phet.lasers.physics.atom;

import edu.colorado.phet.lasers.physics.photon.Photon;

public abstract class AtomicState {

    private Atom atom;

    protected AtomicState( Atom atom ) {
        this.atom = atom;
    }

    protected Atom getAtom() {
        return atom;
    }

    abstract public void collideWithPhoton( Photon photon );

    abstract public void stepInTime( float dt );

    abstract void decrementNumInState();

    //
    // Static fields and methods
    //
    static protected double s_collisionLikelihood = 0.2;

}
