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

import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.photon.Photon;

public class HighEnergyState extends SpontaneouslyEmittingState {

    private static HighEnergyState instance = new HighEnergyState();
    public static HighEnergyState instance() {
        return instance;
    }

    private HighEnergyState() {
    }

    // TODO: This should emit a stimulated photon if hit by
    // a blue photon
    public void collideWithPhoton( Atom atom, Photon photon ) {
        // NOP
    }

    protected double getSpontaneousEmmisionHalfLife() {
        return s_spontaneousEmmisionHalfLife;
    }

    protected AtomicState nextLowerEnergyState() {
        return MiddleEnergyState.instance();
//        return new MiddleEnergyState( getAtom() );
    }


    void incrNumInState() {
        s_numInstances++;
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
