/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;

public class GroundState extends AtomicState {

    public interface Listener {
        void numInstancesChanged( int numInstances );
    }

    private static GroundState instance = new GroundState();

    public static GroundState instance() {
        return instance;
    }


    private GroundState() {
        setEnergyLevel( AtomicState.minEnergy );
        setEmittedPhotonWavelength( AtomicState.maxWavelength );
        setMeanLifetime( Double.POSITIVE_INFINITY );
    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

        // Only respond a specified percentage of the time
        if( Math.random() < s_collisionLikelihood ) {
            AtomicState newState = getStimulatedState( atom, photon, 0 );
            if( newState != null ) {
                photon.removeFromSystem();
                atom.setCurrState( newState );
            }
        }
    }

    public AtomicState getNextLowerEnergyState() {
        return AtomicState.MinEnergyState.instance();
    }

    public AtomicState getNextHigherEnergyState() {
        return MiddleEnergyState.instance();
    }
}
