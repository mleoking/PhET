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
        setMeanLifetime(  Double.POSITIVE_INFINITY );
    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

        // Only respond a specified percentage of the time
        if( Math.random() < s_collisionLikelihood ) {

            // absorb the photon and change state
            if( photon.getWavelength() == Photon.BLUE ) {
                photon.removeFromSystem();
                atom.setState( HighEnergyState.instance() );
            }
            if( photon.getWavelength() == Photon.RED ) {
                photon.removeFromSystem();
                atom.setState( MiddleEnergyState.instance() );
            }
        }
        else {
            //            System.out.println( "no emission" );
        }
    }
}
