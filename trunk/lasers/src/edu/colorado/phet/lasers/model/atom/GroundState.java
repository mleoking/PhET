/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.coreadditions.SubscriptionService;

public class GroundState extends AtomicState {

    static private int s_numInstances = 0;
    static public int s_wavelength = Photon.GRAY;

    static public interface Listener {
        void numInstancesChanged( int numInstances );
    }

    static public int getNumInstances() {
        return s_numInstances;
    }

    private static GroundState instance = new GroundState();
    public static GroundState instance() {
        return instance;
    }

    
    //
    // Instance
    //
    private GroundState() {
        setEnergyLevel( 10 );
    }

    public void stepInTime( double dt ) {
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

    void incrNumInState() {
        s_numInstances++;
    }

    void decrementNumInState() {
        s_numInstances--;
    }

    int getNumAtomsInState() {
        return getNumInstances();
    }
}
