/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;

public abstract class AtomicState {

    private Atom atom;

    public interface Listener {
        void numInStateChanged( int num );
    }

    protected AtomicState( Atom atom ) {
        this.atom = atom;
    }

    protected Atom getAtom() {
        return atom;
    }

    // Used by subclasses to handle time-dependent behaviors, such as
    // spontaneous emission
    abstract public void stepInTime( double dt );

    abstract public void collideWithPhoton( Photon photon );

    abstract void decrementNumInState();

    //
    // Static fields and methods
    //
    static protected double s_collisionLikelihood = 1;
    //    static protected double s_collisionLikelihood = 0.2;

}
