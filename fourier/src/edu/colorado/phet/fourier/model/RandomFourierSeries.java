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

    private static final boolean DEBUG = true;
    
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
        
        if ( _gameLevel == FourierConstants.GAME_LEVEL_PRESET ) {
            // Scale the amplitudes of the preset harmonics
            resetPreset();
            double percent = _random.nextDouble();
            for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
                Harmonic harmonic = getHarmonic( i );
                double amplitude = harmonic.getAmplitude() * percent;
                harmonic.setAmplitude( amplitude );
            }
        }
        else if ( _gameLevel == FourierConstants.GAME_LEVEL_EASY ) {
            // Random values for 2 harmonics, all others zero
            int count = 0;
            for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
                boolean isZero = _random.nextBoolean();
                if ( isZero || count == 2 ) {
                    getHarmonic( i ).setAmplitude( 0 );
                }
                else {
                    double amplitude = generateRandomAmplitude();
                    getHarmonic( i ).setAmplitude( amplitude );
                    count++;
                }
            }
        }
        else if ( _gameLevel == FourierConstants.GAME_LEVEL_MEDIUM ) {
            // Random values for 4 harmonics, all others zero
            int count = 0;
            for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
                boolean isZero = _random.nextBoolean();
                if ( isZero || count == 4 ) {
                    getHarmonic( i ).setAmplitude( 0 );
                }
                else {
                    double amplitude = generateRandomAmplitude();
                    getHarmonic( i ).setAmplitude( amplitude );
                    count++;
                }
            }
        }
        else if ( _gameLevel == FourierConstants.GAME_LEVEL_HARD ) {
            // Random values for all harmonics
            for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
                double amplitude = generateRandomAmplitude();
                getHarmonic( i ).setAmplitude( amplitude );
            } 
        }
       
        // Print the amplitudes
        if ( DEBUG ) {
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
        double percent = _random.nextDouble();
        double amplitude = sign * percent * FourierConfig.MAX_HARMONIC_AMPLITUDE;
        int multiplier = (int) ( amplitude / 0.01 );
        return multiplier * 0.01;
    }
}
