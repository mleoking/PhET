/**
 * Class: HighEnergyState
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;

public class HighEnergyState extends AtomicState {

    private static HighEnergyState instance = new HighEnergyState();

    public static HighEnergyState instance() {
        return instance;
    }

    private HighEnergyState() {
        setEmittedPhotonWavelength( Photon.BLUE );
    }

    // TODO: This should emit a stimulated photon if hit by
    // a blue photon
    public void collideWithPhoton( Atom atom, Photon photon ) {
        // NOP
    }

    public AtomicState getNextLowerEnergyState() {
        return MiddleEnergyState.instance();
    }

    public AtomicState getNextHigherEnergyState() {
        return AtomicState.MaxEnergyState.instance();
    }
}
