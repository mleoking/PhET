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

    protected GroundState( Atom atom ) {
        super( atom );
        s_numInstances++;
    }

    public void stepInTime( double dt ) {
    }

    public void collideWithPhoton( Photon photon ) {

        // Only respond a specified percentage of the time
        if( Math.random() < s_collisionLikelihood ) {

            // Change state
            if( photon.getWavelength() == Photon.BLUE ) {
                // Absorb the photon
                photon.removeFromSystem();

                decrementNumInState();
                // todo: change things so this signature isn't so redundant
                getAtom().setState( new HighEnergyState( getAtom() ) );
            }
            if( photon.getWavelength() == Photon.RED ) {
                // Absorb the photon
                photon.removeFromSystem();

                decrementNumInState();
                getAtom().setState( new MiddleEnergyState( getAtom() ) );
            }
//            System.out.println( "emission" );
        }
        else {
//            System.out.println( "no emission" );
        }

    }

    void decrementNumInState() {
        s_numInstances--;
    }

    int getNumAtomsInState() {
        return getNumInstances();
    }

    //
    // Static fields and methods
    //
    static private int s_numInstances = 0;
    static public int s_wavelength = Photon.GRAY;

    static public int getNumInstances() {
        return s_numInstances;
    }
}
