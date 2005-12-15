/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model;

/**
 * PhysicsUtil
 * <p/>
 * A collection of utilities having to do with physical constants and phenomena
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhysicsUtil {

    //----------------------------------------------------------------
    // Constants
    //----------------------------------------------------------------

    // Planck's constant J-s
    static public final double PLANCK = 6.626E-34;
    // Speed of light - m/s
    static public final double LIGHT_SPEED = 2.9979E8;
    // Electron rest mass - kg
    static public final double ELECTRON_MASS = 9.109E-31;
    // Joules per EV, EV per Joule
    static public final double JOULES_PER_EV = 1.6022E-19;
    static public final double EV_PER_JOULE = 1 / JOULES_PER_EV;
    // nanometers per meter
    static public final double NM_PER_M = 1E9;

    //----------------------------------------------------------------
    // Relations and Conversions
    //----------------------------------------------------------------

    /**
     * Determines the energy, in EV, of radiation with a specified wavelength
     *
     * @param wavelength in nm
     * @return
     */
    static public double wavelengthToEnergy( double wavelength ) {
        return PLANCK * LIGHT_SPEED / wavelength / JOULES_PER_EV * NM_PER_M;
    }

    /**
     * Determines the wavelength, in nm, of radiation with a specified energy
     *
     * @param ev energy of the radiation, in ev
     * @return
     */
    static public double energyToWavelength( double ev ) {
        return ( PLANCK * LIGHT_SPEED / ev ) * EV_PER_JOULE * NM_PER_M;
    }

    /**
     * Returns the frequency of RF radiation of a specified wavelength
     * @param wavelength Wavelength, in nm
     * @return
     */
    static public double wavelengthToFrequency( double wavelength ) {
            return LIGHT_SPEED * NM_PER_M / wavelength;
    }

    /**
     * Returns the wavelength, in nm, of RF radiation of a specified
     * frequency
     * @param frequency
     * @return
     */
    static public double frequencyToWavelength( double frequency ) {
        return LIGHT_SPEED * NM_PER_M / frequency;
    }
}
