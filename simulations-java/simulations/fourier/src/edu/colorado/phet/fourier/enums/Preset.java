// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.enums;

import java.awt.geom.Point2D;

import edu.colorado.phet.fourier.FourierConstants;


/**
 * Preset is a typesafe enumueration of "preset" values.
 * It also contains utility functions for getting the 
 * amplitudes and points associated with the various presets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Preset extends FourierEnum {

    /* This class is not intended for instantiation. */
    private Preset( String name ) {
        super( name );
    }
    
    // Preset values
    public static final Preset UNDEFINED = new Preset( "undefined" );
    public static final Preset SINE_COSINE = new Preset( "sineCosine" );
    public static final Preset SQUARE = new Preset( "square" );
    public static final Preset SAWTOOTH = new Preset( "sawtooth" );
    public static final Preset TRIANGLE = new Preset( "triangle" );
    public static final Preset WAVE_PACKET = new Preset( "wavePacket" );
    public static final Preset CUSTOM = new Preset( "custom" );
    
    /**
     * Retrieves a preset by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static Preset getByName( String name ) {
        Preset preset = null;
        if ( SINE_COSINE.isNamed( name ) ) {
            preset = SINE_COSINE;
        }
        else if ( SQUARE.isNamed( name ) ) {
            preset = SQUARE;
        }
        else if ( SAWTOOTH.isNamed( name ) ) {
            preset = SAWTOOTH;
        }
        else if ( TRIANGLE.isNamed( name ) ) {
            preset = TRIANGLE;
        }
        else if ( WAVE_PACKET.isNamed( name ) ) {
            preset = WAVE_PACKET;
        }
        else if ( CUSTOM.isNamed( name ) ) {
            preset = CUSTOM;
        }
        else if ( UNDEFINED.isNamed( name ) ) {
            preset = UNDEFINED;
        }
        return preset;
    }
    
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
    public static double[] getPresetAmplitudes( Preset preset, WaveType waveType, int numberOfHarmonics ) {
        
        double[] amplitudes = null;
        
        if ( preset == SINE_COSINE ) {
            amplitudes = SINE_COSINE_AMPLITUDES;
        }
        else if ( preset == SQUARE ) {
            if ( waveType == WaveType.SINES ) {
                amplitudes = SINE_SQUARE_AMPLITUDES;
            }
            else {
                amplitudes = COSINE_SQUARE_AMPLITUDES;
            }
        }
        else if ( preset == SAWTOOTH ) {
            if ( waveType == WaveType.SINES ) {
                amplitudes = SINE_SAWTOOTH_AMPLITUDES;
            }
            else {
                throw new IllegalStateException( "you can't make a sawtooth wave out of cosines because it is asymmetric" );
            }
        }
        else if ( preset == TRIANGLE ) {
            if ( waveType == WaveType.SINES ) {
                amplitudes = SINE_TRIANGLE_AMPLITUDES;
            }
            else {
                amplitudes = COSINE_TRIANGLE_AMPLITUDES;
            }
        }
        else if ( preset == WAVE_PACKET ) {
            amplitudes = WAVE_PACKET_AMPLITUDES[ numberOfHarmonics - 1 ];
        }
        else if ( preset == CUSTOM ) {
            amplitudes = null;
        }
        else {
            throw new IllegalStateException( "you forgot to implement a preset: " + preset );
        }
        
        return amplitudes;
    }
    
    
    //----------------------------------------------------------------------------
    // Data points for drawing preset functions, range -3L..+2L
    // The low end of the range is -3L to handle left-to-right animation.
    //----------------------------------------------------------------------------
    
    private static final double L = FourierConstants.L; // to make the points below more readable
    
    private static final Point2D[] SINE_SQUARE_POINTS = {
            new Point2D.Double( -3*L, -1 ),
            new Point2D.Double( -3*L, 1 ),
            new Point2D.Double( -5*L/2, 1 ),
            new Point2D.Double( -5*L/2, -1 ),
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
            new Point2D.Double( -13*L/4, 1 ),
            new Point2D.Double( -11*L/4, 1 ),
            new Point2D.Double( -11*L/4, -1 ),
            new Point2D.Double( -9*L/4, -1 ),
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
            new Point2D.Double( -13*L/4, -1 ),
            new Point2D.Double( -11*L/4, 1 ),
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
            new Point2D.Double( -3*L, 1 ),
            new Point2D.Double( -5*L/2, -1 ),
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
            new Point2D.Double( -7*L/2, -1 ),
            new Point2D.Double( -5*L/2, 1 ),
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
    public static Point2D[] getPresetPoints( Preset preset, WaveType waveType ) {
        
        Point2D[] points = null;
        
        if ( preset == SQUARE ) {
             if ( waveType == WaveType.SINES ) {
                 points = SINE_SQUARE_POINTS;
             }
             else {
                 points = COSINE_SQUARE_POINTS;
             }
         }
         else if ( preset == TRIANGLE ) {
             if ( waveType == WaveType.SINES ) {
                 points = SINE_TRIANGLE_POINTS;
             }
             else {
                 points = COSINE_TRIANGLE_POINTS;
             }
         }
         else if ( preset == SAWTOOTH ) {
             if ( waveType == WaveType.SINES ) {
                 points = SINE_SAWTOOTH_POINTS;
             }
             // There is no cosine form of sawtooth.
         }
         
         return points;
    }
}