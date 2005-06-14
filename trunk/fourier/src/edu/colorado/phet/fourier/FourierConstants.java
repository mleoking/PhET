/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier;


/**
 * FourierConstants constains various constants.
 * Unlike FourierConfig, there should be no need to change these.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierConstants {

    /* Not intended for instantiation. */
    private FourierConstants() {}
    
    //----------------------------------------------------------------------------
    // Constants
    //----------------------------------------------------------------------------
    
    public static final double L = 1.0; // arbitrary value for the symbol L (length of the fundamental harmonic)
    
    //----------------------------------------------------------------------------
    // Enumerations
    //----------------------------------------------------------------------------
    
    // Domain choices
    public static final int DOMAIN_SPACE = 0;
    public static final int DOMAIN_TIME = 1;
    public static final int DOMAIN_SPACE_AND_TIME = 2;
    
    // Preset choices
    public static final int PRESET_SINE_COSINE = 0;
    public static final int PRESET_SAWTOOTH = 1;
    public static final int PRESET_TRIANGLE = 2;
    public static final int PRESET_WAVE_PACKET = 3;
    public static final int PRESET_CUSTOM = 4;
    
    // Wave Type choices
    public static final int WAVE_TYPE_SINE = 0;
    public static final int WAVE_TYPE_COSINE = 1;
    
    // Math Form choices
    public static final int MATH_FORM_WAVE_NUMBER = 0;
    public static final int MATH_FORM_WAVELENGTH = 1;
    public static final int MATH_FORM_MODE = 2;
    public static final int MATH_FORM_ANGULAR_FREQUENCY = 3;
    public static final int MATH_FORM_FREQUENCY = 4;
    public static final int MATH_FORM_PERIOD = 5;
    public static final int MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY = 6;
    public static final int MATH_FORM_WAVELENGTH_AND_PERIOD = 7;
    
    //----------------------------------------------------------------------------
    // Validation methods
    //----------------------------------------------------------------------------
    
    public static boolean isValidDomain( int domain ) {
        boolean isValid = false;
        switch ( domain ) {
            case DOMAIN_SPACE:
            case DOMAIN_TIME:
            case DOMAIN_SPACE_AND_TIME:
                 isValid = true;
                 break;
            default:
                 isValid = false;
        }
        return isValid;
    }
    
    public static boolean isValidPreset( int preset ) {
        boolean isValid = false;
        switch ( preset ) {
            case PRESET_SINE_COSINE:
            case PRESET_SAWTOOTH:
            case PRESET_TRIANGLE:
            case PRESET_WAVE_PACKET:
            case PRESET_CUSTOM:
                 isValid = true;
                 break;
            default:
                 isValid = false;
        }
        return isValid;
    }
    
    public static boolean isValidWaveType( int waveType ) {
        boolean isValid = false;
        switch ( waveType ) {
            case WAVE_TYPE_SINE:
            case WAVE_TYPE_COSINE:
                 isValid = true;
                 break;
            default:
                 isValid = false;
        }
        return isValid;
    }
    
    public static boolean isValidMathForm( int mathForm ) {
        boolean isValid = false;
        switch ( mathForm ) {
            case MATH_FORM_WAVE_NUMBER:
            case MATH_FORM_WAVELENGTH:
            case MATH_FORM_MODE:
            case MATH_FORM_ANGULAR_FREQUENCY:
            case MATH_FORM_FREQUENCY:
            case MATH_FORM_PERIOD:
            case MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY:
            case MATH_FORM_WAVELENGTH_AND_PERIOD:
                 isValid = true;
                 break;
            default:
                 isValid = false;
        }
        return isValid;
    }
}
