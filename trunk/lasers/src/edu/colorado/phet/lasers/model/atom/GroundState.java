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
        setEnergyLevel( 0 );
        setEmittedPhotonWavelength( Photon.GRAY );
        setMeanLifetime( Double.POSITIVE_INFINITY );
    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

        // Only respond a specified percentage of the time
        if( Math.random() < s_collisionLikelihood ) {

            // absorb the photon and change state
            if( Math.abs( photon.getWavelength() - HighEnergyState.instance().getWavelength() ) < wavelengthTolerance ) {
                photon.removeFromSystem();
                atom.setState( HighEnergyState.instance() );
            }
            if( Math.abs( photon.getWavelength() - MiddleEnergyState.instance().getWavelength() ) < wavelengthTolerance ) {
                photon.removeFromSystem();
                atom.setState( MiddleEnergyState.instance() );
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
