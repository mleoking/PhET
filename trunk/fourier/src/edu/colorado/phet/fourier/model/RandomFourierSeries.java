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

import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.enum.GameLevel;
import edu.colorado.phet.fourier.enum.Preset;
import edu.colorado.phet.fourier.enum.WaveType;


/**
 * RandomFourierSeries is a FourierSeries that can generate its own
 * random values for its component amplitudes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RandomFourierSeries extends FourierSeries {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final boolean DEBUG_PRINT_AMPLITUDES = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Random _random;  // the JDK random number generator
    private GameLevel _gameLevel;  // the game level, FourierConstants.GAME_LEVEL_*
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param numberOfHarmonics
     * @param fundamentalFrequency
     */
    public RandomFourierSeries( int numberOfHarmonics, double fundamentalFrequency ) {
        super( numberOfHarmonics, fundamentalFrequency );
        setPreset( Preset.CUSTOM );
        setWaveType( WaveType.SINES );
        _random = new Random();
        _gameLevel = GameLevel.EASY;
        generate();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the game level.
     * 
     * @param gameLevel a game level, one of the FourierConstants.GAME_LEVEL_* values
     */
    public void setGameLevel( GameLevel gameLevel ) {
        _gameLevel = gameLevel;
        generate();
    }
    
    //----------------------------------------------------------------------------
    // Generation of amplitudes
    //----------------------------------------------------------------------------

    /**
     * Generates random amplitudes for the Fourier series' components,
     * based on the game level.
     */
    public void generate() {
        
        if ( _gameLevel == GameLevel.EASY ) {
            generateEasy();
        }
        else if ( _gameLevel == GameLevel.MEDIUM ) {
            generateMedium();
        }
        else if ( _gameLevel == GameLevel.HARD ) {
            generateHard();
        }
        else if ( _gameLevel == GameLevel.PRESET ) {
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
    
    /*
     * Generates a random number between +-FourierConfig.MAX_HARMONIC_AMPLITUDE
     * with 2 significant decimal places.
     * 
     * @return random number
     */
    private double generateRandomAmplitude() {
        int sign = _random.nextBoolean() ? +1 : -1;
        double step = 0.01;
        int numberOfSteps = (int) ( FourierConstants.MAX_HARMONIC_AMPLITUDE / step ) + 1;
        int multiplier = _random.nextInt( numberOfSteps );
        double amplitude = sign * multiplier * step;
        assert( amplitude <= FourierConstants.MAX_HARMONIC_AMPLITUDE );
        return amplitude;
    }
    
    /*
     * Generates data for the "Easy" game level.
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
     * Generates data for the "Medium" game level.
     * Random values for 4 harmonics, all others zero
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
     * Generates data for the "Hard" game level.
     * Random values for all harmonics
     */
    private void generateHard() {
        for ( int i = 0; i < getNumberOfHarmonics(); i++ ) {
            double amplitude = generateRandomAmplitude();
            getHarmonic( i ).setAmplitude( amplitude );
        } 
    }
    
    /*
     * Generates data for the "Preset" game level.
     */
    private void generatePreset() {
        // Don't do anything, use the preset data "as is".
    }
}
