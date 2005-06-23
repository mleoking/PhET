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
    // Wave coefficients, precomputed for 11 harmonics
    //----------------------------------------------------------------------------
    
    private static final double PI = Math.PI; // to make the coefficents below more readable
    
    /*
     * Sine/Cosine Wave
     * A1 = 1
     */
    public static final double[] SINE_COSINE_AMPLITUDES = {
            1.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    /* Sine Square Wave:
     * An = (2 - (2 * cos(n * PI))) / (n * PI)
     */
    public static final double[] SINE_SQUARE_AMPLITUDES = {
            4/PI, 0, 4/(3*PI), 0, 4/(5*PI), 0, 4/(7*PI), 0, 4/(9*PI), 0, 4/(11*PI)
    };
    
    /* Cosine Square Wave:
     * An = ((4 * sin(n * PI / 2)) - (2 * sin(n * PI))) / (n * PI)
     */
    public static final double[] COSINE_SQUARE_AMPLITUDES = {
            4/PI, 0, -4/(3*PI), 0, 4/(5*PI), 0, -4/(7*PI), 0, 4/(9*PI), 0, -4/(11*PI)
    };
    
    /* Sine Triangle Wave:
     * An = ((2 * sin( n * PI / 2)) - (2 * sin( n * PI))) / (n^2 * PI^2) 
     */
    public static final double[] SINE_TRIANGLE_AMPLITUDES = {
            8/(PI*PI), 0, -8/(9*PI*PI), 0, 8/(25*PI*PI), 0, -8/(49*PI*PI), 0, 8/(81*PI*PI), 0, -8/(121*PI*PI)
    }; 
    
    /* Cosine Triangle Wave:
     * An = ((4 * cos(n * PI)) - (2 * n * PI * sin(n * PI)) / (n^2 * PI^2)
     */
    public static final double[] COSINE_TRIANGLE_AMPLITUDES = {
            8/(PI*PI), 0, 8/(9*PI*PI), 0, 8/(25*PI*PI), 0, 8/(49*PI*PI), 0, 8/(81*PI*PI), 0, 8/(121*PI*PI)
    };
    
    /* Sine Sawtooth Wave:
     * An = ((2 * n * PI * cos(n * PI)) + (2 * sin(n* PI))) / (n^2 * PI^2)
     */
    public static final double[] SINE_SAWTOOTH_AMPLITUDES = {
            2/PI, -1/PI, 2/(3*PI), -1/(2*PI), 2/(5*PI), -1/(3*PI), 2/(7*PI), -1/(4*PI), 2/(9*PI), -1/(5*PI), 2/(11*PI)
    };
    
    /* Gaussian Wave Packet:
     * An = (1 / (p * sqrt(2 * PI)))^( -(n-no)^2 / (2 * p^2))
     * where p = 1.5, no = (number of harmonics + 1) / 2
     */
    public static final double[][] WAVE_PACKET_AMPLITUDES = {
            { 1.000000 },
            { 0.457833, 0.457833 },
            { 0.249352, 1.000000, 0.249352 },
            { 0.172422, 0.822578, 0.822578, 0.172422 },
            { 0.135335, 0.606531, 1.000000, 0.606531, 0.135335 },
            { 0.114162, 0.457833, 0.916855, 0.916855, 0.457833, 0.114162 },
            { 0.100669, 0.360448, 0.774837, 1.000000, 0.774837, 0.360448, 0.100669 },
            { 0.091394, 0.295023, 0.644389, 0.952345, 0.952345, 0.644389, 0.295023, 0.091394 },
            { 0.084658, 0.249352, 0.539408, 0.856997, 1.000000, 0.856997, 0.539408, 0.249352, 0.084658 },
            { 0.079560, 0.216255, 0.457833, 0.754840, 0.969233, 0.969233, 0.754840, 0.457833, 0.216255, 0.079560 },
            { 0.075574, 0.191495, 0.394652, 0.661515, 0.901851, 1.000000, 0.901851, 0.661515, 0.394652, 0.191495, 0.075574 }
    };
    
    //----------------------------------------------------------------------------
    // Enumerations
    //----------------------------------------------------------------------------
    
    // Domain choices
    public static final int DOMAIN_SPACE = 0;
    public static final int DOMAIN_TIME = 1;
    public static final int DOMAIN_SPACE_AND_TIME = 2;
    
    // Preset choices
    public static final int PRESET_SINE_COSINE = 0;
    public static final int PRESET_SQUARE = 1;
    public static final int PRESET_SAWTOOTH = 2;
    public static final int PRESET_TRIANGLE = 3;
    public static final int PRESET_WAVE_PACKET = 4;
    public static final int PRESET_CUSTOM = 5;
    
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
            case PRESET_SQUARE:
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
