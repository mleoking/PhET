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

import java.awt.geom.Point2D;


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
    // Amplitude coefficients, precomputed for 11 harmonics
    //----------------------------------------------------------------------------
    
    private static final double PI = Math.PI; // to make the coefficents below more readable
    
    /*
     * Sine/Cosine Wave
     * A1 = 1
     */
    private static final double[] SINE_COSINE_AMPLITUDES = {
            1.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    /* Sine Square Wave:
     * An = (2 - (2 * cos(n * PI))) / (n * PI)
     */
    private static final double[] SINE_SQUARE_AMPLITUDES = {
            4/PI, 0, 4/(3*PI), 0, 4/(5*PI), 0, 4/(7*PI), 0, 4/(9*PI), 0, 4/(11*PI)
    };
    
    /* Cosine Square Wave:
     * An = ((4 * sin(n * PI / 2)) - (2 * sin(n * PI))) / (n * PI)
     */
    private static final double[] COSINE_SQUARE_AMPLITUDES = {
            4/PI, 0, -4/(3*PI), 0, 4/(5*PI), 0, -4/(7*PI), 0, 4/(9*PI), 0, -4/(11*PI)
    };
    
    /* Sine Triangle Wave:
     * An = ((2 * sin( n * PI / 2)) - (2 * sin( n * PI))) / (n^2 * PI^2) 
     */
    private static final double[] SINE_TRIANGLE_AMPLITUDES = {
            8/(PI*PI), 0, -8/(9*PI*PI), 0, 8/(25*PI*PI), 0, -8/(49*PI*PI), 0, 8/(81*PI*PI), 0, -8/(121*PI*PI)
    }; 
    
    /* Cosine Triangle Wave:
     * An = ((4 * cos(n * PI)) - (2 * n * PI * sin(n * PI)) / (n^2 * PI^2)
     */
    private static final double[] COSINE_TRIANGLE_AMPLITUDES = {
            8/(PI*PI), 0, 8/(9*PI*PI), 0, 8/(25*PI*PI), 0, 8/(49*PI*PI), 0, 8/(81*PI*PI), 0, 8/(121*PI*PI)
    };
    
    /* Sine Sawtooth Wave:
     * An = ((2 * n * PI * cos(n * PI)) + (2 * sin(n* PI))) / (n^2 * PI^2)
     */
    private static final double[] SINE_SAWTOOTH_AMPLITUDES = {
            2/PI, -1/PI, 2/(3*PI), -1/(2*PI), 2/(5*PI), -1/(3*PI), 2/(7*PI), -1/(4*PI), 2/(9*PI), -1/(5*PI), 2/(11*PI)
    };
    
    /* Gaussian Wave Packet:
     * An = (1 / (p * sqrt(2 * PI)))^( -(n-no)^2 / (2 * p^2))
     * where p = 1.5, no = (number of harmonics + 1) / 2
     */
    private static final double[][] WAVE_PACKET_AMPLITUDES = {
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
    
    /**
     * Gets the amplitude coefficients for a specific preset, wave type and 
     * number of harmonics.
     * 
     * @param preset
     * @param waveType
     * @param numberOfHarmonics
     * @return amplitude coefficients
     */
    public static double[] getPresetAmplitudes( int preset, int waveType, int numberOfHarmonics ) {
        assert( isValidPreset( preset ) );
        assert( isValidPreset( waveType ) );
        
        double[] amplitudes = null;
        
        switch( preset ) {
        case FourierConstants.PRESET_SINE_COSINE:
            amplitudes = FourierConstants.SINE_COSINE_AMPLITUDES;
            break;
        case FourierConstants.PRESET_SQUARE:
            if ( waveType == FourierConstants.WAVE_TYPE_SINE ) {
                amplitudes = FourierConstants.SINE_SQUARE_AMPLITUDES;
            }
            else {
                amplitudes = FourierConstants.COSINE_SQUARE_AMPLITUDES;
            }
            break;
        case FourierConstants.PRESET_SAWTOOTH:
            if ( waveType == FourierConstants.WAVE_TYPE_SINE ) {
                amplitudes = FourierConstants.SINE_SAWTOOTH_AMPLITUDES;
            }
            else {
                throw new IllegalStateException( "you can't make a sawtooth wave out of cosines because it is asymmetric" );
            }
            break;
        case FourierConstants.PRESET_TRIANGLE:
            if ( waveType == FourierConstants.WAVE_TYPE_SINE ) {
                amplitudes = FourierConstants.SINE_TRIANGLE_AMPLITUDES;
            }
            else {
                amplitudes = FourierConstants.COSINE_TRIANGLE_AMPLITUDES;
            }
            break;
        case FourierConstants.PRESET_WAVE_PACKET:
            amplitudes = FourierConstants.WAVE_PACKET_AMPLITUDES[ numberOfHarmonics - 1 ];
            break;
        case FourierConstants.PRESET_CUSTOM:
            amplitudes = null;
            break;
        default:
            throw new IllegalStateException( "you forgot to implement a preset" );
        }
        
        return amplitudes;
    }
    
    //----------------------------------------------------------------------------
    // Data points for drawing preset functions, range -2L..+2L
    //----------------------------------------------------------------------------
    
    private static final Point2D[] SINE_SQUARE_POINTS = {
            new Point2D.Double( -2*L, -1 ),
            new Point2D.Double( -2*L, 1 ),
            new Point2D.Double( -3*L/2, 1 ),
            new Point2D.Double( -3*L/2, -1 ),
            new Point2D.Double( -L, -1 ),
            new Point2D.Double( -L, 1 ),
            new Point2D.Double( -L/2, 1 ),
            new Point2D.Double( -L/2, -1 ),
            new Point2D.Double( 0, -1 ),
            new Point2D.Double( 0, 1 ),
            new Point2D.Double( L/2, 1 ),
            new Point2D.Double( L/2, -1 ),
            new Point2D.Double( L, -1 ),
            new Point2D.Double( L, 1 ),
            new Point2D.Double( 3*L/2, 1 ),
            new Point2D.Double( 3*L/2, -1 ),
            new Point2D.Double( 2*L, -1 ),
            new Point2D.Double( 2*L, 1 )
    };
    
    private static final Point2D[] COSINE_SQUARE_POINTS = {
            new Point2D.Double( -9*L/4, 1 ),
            new Point2D.Double( -7*L/4, 1 ),
            new Point2D.Double( -7*L/4, -1 ),
            new Point2D.Double( -5*L/4, -1 ),
            new Point2D.Double( -5*L/4, 1 ),
            new Point2D.Double( -3*L/4, 1 ),
            new Point2D.Double( -3*L/4, -1 ),
            new Point2D.Double( -L/4, -1 ),
            new Point2D.Double( -L/4, 1 ),
            new Point2D.Double( L/4, 1 ),
            new Point2D.Double( L/4, -1 ),
            new Point2D.Double( 3*L/4, -1 ),
            new Point2D.Double( 3*L/4, 1 ),
            new Point2D.Double( 5*L/4, 1 ),
            new Point2D.Double( 5*L/4, -1 ),
            new Point2D.Double( 7*L/4, -1 ),
            new Point2D.Double( 7*L/4, 1 ),
            new Point2D.Double( 9*L/4, 1 )
    };
    
    private static final Point2D[] SINE_TRIANGLE_POINTS = {
            new Point2D.Double( -9*L/4, -1 ),
            new Point2D.Double( -7*L/4, 1 ),
            new Point2D.Double( -5*L/4, -1 ),
            new Point2D.Double( -3*L/4, 1 ),
            new Point2D.Double( -L/4, -1 ),
            new Point2D.Double( L/4, 1 ),
            new Point2D.Double( 3*L/4, -1 ),
            new Point2D.Double( 5*L/4, 1 ),
            new Point2D.Double( 7*L/4, -1 ),
            new Point2D.Double( 9*L/4, 1 )
    };
    
    private static final Point2D[] COSINE_TRIANGLE_POINTS = {
            new Point2D.Double( -2*L, 1 ),
            new Point2D.Double( -3*L/2, -1 ),
            new Point2D.Double( -L, 1 ),
            new Point2D.Double( -L/2, -1 ),
            new Point2D.Double( 0, 1 ),
            new Point2D.Double( L/2, -1 ),
            new Point2D.Double( L, 1 ),
            new Point2D.Double( 3*L/2, -1 ),
            new Point2D.Double( 2L, 1 )
    };
    
    private static final Point2D[] SINE_SAWTOOTH_POINTS = {
            new Point2D.Double( -5*L/2, -1 ),
            new Point2D.Double( -3*L/2, 1 ),
            new Point2D.Double( -3*L/2, -1 ),
            new Point2D.Double( -L/2, 1 ),
            new Point2D.Double( -L/2, -1 ),
            new Point2D.Double( L/2, 1 ),
            new Point2D.Double( L/2, -1 ),
            new Point2D.Double( 3*L/2, 1 ),
            new Point2D.Double( 3*L/2, -1 ),
            new Point2D.Double( 5*L/2, 1 )
    };
    
    /**
     * Gets the data points that correspond to the preset and wave type.
     * This may return null, since not all presets and wave types have
     * pre-computed data points.
     * 
     * @param preset
     * @param waveType
     * @return data points
     */
    public static Point2D[] getPresetPoints( int preset, int waveType ) {
        assert( isValidPreset( preset ) );
        assert( isValidPreset( waveType ) );
        
        Point2D[] points = null;
        
        if ( preset == FourierConstants.PRESET_SQUARE ) {
             if ( waveType == FourierConstants.WAVE_TYPE_SINE ) {
                 points = SINE_SQUARE_POINTS;
             }
             else {
                 points = COSINE_SQUARE_POINTS;
             }
         }
         else if ( preset == FourierConstants.PRESET_TRIANGLE ) {
             if ( waveType == FourierConstants.WAVE_TYPE_SINE ) {
                 points = SINE_TRIANGLE_POINTS;
             }
             else {
                 points = COSINE_TRIANGLE_POINTS;
             }
         }
         else if ( preset == FourierConstants.PRESET_SAWTOOTH ) {
             if ( waveType == FourierConstants.WAVE_TYPE_SINE ) {
                 points = SINE_SAWTOOTH_POINTS;
             }
             // There is no cosine form of sawtooth.
         }
         
         return points;
    }
    
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
