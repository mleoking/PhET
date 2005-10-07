/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.model;

import java.util.Random;

import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;


/**
 * RandomFourierSeries
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RandomFourierSeries extends FourierSeries {

    private static final boolean DEBUG_PRINT_AMPLITUDES = false;
    
    private Random _random;
    private int _gameLevel;
    
    public RandomFourierSeries( int numberOfHarmonics, double fundamentalFrequency ) {
        super( numberOfHarmonics, fundamentalFrequency );
        setPreset( FourierConstants.PRESET_CUSTOM );
        setWaveType( FourierConstants.WAVE_TYPE_SINE );
        _random = new Random();
        _gameLevel = FourierConstants.GAME_LEVEL_EASY;
        generate();
    }
    
    public void setGameLevel( int gameLevel ) {
        assert( FourierConstants.isValidGameLevel( gameLevel ) );
        _gameLevel = gameLevel;
        generate();
    }
    
    public void generate() {
        
        if ( _gameLevel == FourierConstants.GAME_LEVEL_EASY ) {
            generateEasy();
        }
        else if ( _gameLevel == FourierConstants.GAME_LEVEL_MEDIUM ) {
            generateMedium();
        }
        else if ( _gameLevel == FourierConstants.GAME_LEVEL_HARD ) {
            generateHard();
        }
        else if ( _gameLevel == FourierConstants.GAME_LEVEL_PRESET ) {
            generatePreset();
        }
       
        // Print the amplitudes
        if ( DEBUG_PRINT_AMPLITUDES ) {
            System.out.print( "random: " );
            for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
                System.out.print( getHarmonic( i ).getAmplitude() + " " );
            }
            System.out.println();
        }
    }
    
    /**
     * Generates a random number between +-FourierConfig.MAX_HARMONIC_AMPLITUDE
     * with 2 significant decimal places.
     * 
     * @return random number
     */
    private double generateRandomAmplitude() {
        int sign = _random.nextBoolean() ? +1 : -1;
//        double percent = _random.nextDouble();
//        double amplitude = sign * percent * FourierConfig.MAX_HARMONIC_AMPLITUDE;
//        int multiplier = (int) ( amplitude / 0.01 );
//        return multiplier * 0.01;
        double step = 0.01;
        int numberOfSteps = (int) ( FourierConfig.MAX_HARMONIC_AMPLITUDE / step ) + 1;
        int multiplier = _random.nextInt( numberOfSteps );
        double amplitude = sign * multiplier * step;
        assert( amplitude <= FourierConfig.MAX_HARMONIC_AMPLITUDE );
        return amplitude;
    }
    
    /*
     * Random values for 2 harmonics, all others zero
     */
    private void generateEasy() {

        int i1 = _random.nextInt( getNumberOfHarmonics() );
        int i2 = _random.nextInt( getNumberOfHarmonics() );
        
        for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
            
            if ( i == i1 || i == i2 ) {
                double amplitude = generateRandomAmplitude();
                getHarmonic( i ).setAmplitude( amplitude );
            }
            else {
                getHarmonic( i ).setAmplitude( 0 );
            }
        }
    }
    
    /*
     *  Random values for 4 harmonics, all others zero
     */
    private void generateMedium() {
        
        int i1 = _random.nextInt( getNumberOfHarmonics() );
        int i2 = _random.nextInt( getNumberOfHarmonics() );
        int i3 = _random.nextInt( getNumberOfHarmonics() );
        int i4 = _random.nextInt( getNumberOfHarmonics() );
        
        for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
            
            if ( i == i1 || i == i2 || i == i3 || i == i4 ) {
                double amplitude = generateRandomAmplitude();
                getHarmonic( i ).setAmplitude( amplitude );
            }
            else {
                getHarmonic( i ).setAmplitude( 0 );
            }
        }
    }
    
    /*
     * Random values for all harmonics
     */
    private void generateHard() {
        for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
            double amplitude = generateRandomAmplitude();
            getHarmonic( i ).setAmplitude( amplitude );
        } 
    }
    
    /*
     * Randomize a preset waveform.
     */
    private void generatePreset() {
        
        if ( getPreset() == FourierConstants.PRESET_SINE_COSINE ) {
            // For sine preset, set a random value for one of the harmonics, all others zero.
            int count = 0;
            for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
                boolean isZero = _random.nextBoolean();
                if ( isZero || count == 1 ) {
                    getHarmonic( i ).setAmplitude( 0 );
                }
                else {
                    double amplitude = generateRandomAmplitude();
                    if ( amplitude == 0 ) {
                        amplitude = 1.0;
                    }
                    else if ( amplitude < 0 ) {
                        amplitude = -amplitude;
                    }
                    getHarmonic( i ).setAmplitude( amplitude );
                    count++;
                }
            }
        }
        else {
            // For all other presets, scale the amplitudes of the preset harmonics
            double percent = _random.nextDouble();
            for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
                Harmonic harmonic = getHarmonic( i );
                double amplitude = harmonic.getAmplitude() * percent;
                harmonic.setAmplitude( amplitude );
            }
        }
        
        // After we're done randomizing, the preset is now "custom".
        setPreset( FourierConstants.PRESET_CUSTOM );
    }
}
