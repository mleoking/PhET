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
import edu.colorado.phet.lasers.controller.LaserConfig;

public class HighEnergyState extends SpontaneouslyEmittingState {

    protected HighEnergyState( Atom atom ) {
        super( atom );
        s_numInstances++;
    }

    // TODO: This should emit a stimulated photon if hit by
    // a blue photon
    public void collideWithPhoton( Photon photon ) {
        // NOP
    }

    //
    // Abstract methods implemented
    //
    protected double getSpontaneousEmmisionHalfLife() {
        return s_spontaneousEmmisionHalfLife;
    }

    protected AtomicState nextLowerEnergyState() {
        return new MiddleEnergyState( getAtom() );
    }

    void decrementNumInState() {
        s_numInstances--;
    }

    protected double getEmittedPhotonWavelength() {
        return s_wavelength;
    }

    //
    // Static fields and methods
    //
    static private int s_numInstances = 0;
    static public int s_wavelength = Photon.DEEP_RED;
    static private double s_spontaneousEmmisionHalfLife = LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME / 1000;

    public static void setSpontaneousEmmisionHalfLife( double spontaneousEmmisionHalfLife ) {
        s_spontaneousEmmisionHalfLife = spontaneousEmmisionHalfLife;
    }

    static public int getNumInstances() {
        return s_numInstances;
    }
}
