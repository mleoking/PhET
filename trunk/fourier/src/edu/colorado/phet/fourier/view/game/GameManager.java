/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.game;

import java.util.Hashtable;
import java.util.Random;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.enum.GameLevel;
import edu.colorado.phet.fourier.enum.Preset;
import edu.colorado.phet.fourier.model.FourierSeries;


/**
 * GameManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameManager implements SimpleObserver {

    private class GameSpecification {
        
        public int numberOfHarmonics;
        public int numberOfNonZeroHarmonics;
        public int numberOfSliders;
        
        public GameSpecification( int numberOfHarmonics, int numberOfNonZeroHarmonics, int numberOfSliders ) {
            this.numberOfHarmonics = numberOfHarmonics;
            this.numberOfNonZeroHarmonics = numberOfNonZeroHarmonics;
            this.numberOfSliders = numberOfSliders;
        }
    }
    
    private FourierSeries _userFourierSeries;
    private FourierSeries _randomFourierSeries;
    private GameAmplitudesView _amplitudesView;
    private GameLevel _gameLevel;
    private Preset _preset;
    private Hashtable _gameSpecs; // hashtable of GameSpecification
    private Random _random;  // the JDK random number generator
    
    public GameManager( FourierSeries userSeries, FourierSeries randomSeries, GameAmplitudesView amplitudesView ) {
        
        _userFourierSeries = userSeries;
        _userFourierSeries.addObserver( this );
        
        _randomFourierSeries = randomSeries;
        
        _amplitudesView = amplitudesView;
        
        _gameLevel = GameLevel.UNDEFINED;
        _preset = Preset.UNDEFINED;
        
        _gameSpecs = new Hashtable();
        _gameSpecs.put( GameLevel.EASY,     new GameSpecification( 11, 1, 1 ) );
        _gameSpecs.put( GameLevel.MEDIUM,   new GameSpecification( 11, 4, 5 ) );
        _gameSpecs.put( GameLevel.HARD,     new GameSpecification( 11, 5, 9 ) );
        
        _random = new Random();
    }
    
    public void cleanup() {
        _userFourierSeries.removeObserver( this );
        _userFourierSeries = null;
    }
    
    public void setGameLevel( GameLevel gameLevel ) {
        _gameLevel = gameLevel;
        newGame();
    }
    
    public void setPreset( Preset preset ) {
        _preset = preset;
        newGame();
    }   
    
    public void newGame() {
        
        // Set all the user's harmonic amplitudes to zero.
        for ( int i = 0; i < _userFourierSeries.getNumberOfHarmonics(); i++ ) {
            _userFourierSeries.getHarmonic( i ).setAmplitude( 0 );
        }
        
        // Generate a new random series
        generate();
    }
    
    public double[] getAmplitudes() {
        double[] amplitudes = new double[ _randomFourierSeries.getNumberOfHarmonics() ];
        for ( int i = 0; i < amplitudes.length; i++ ) {
            amplitudes[i] = _randomFourierSeries.getHarmonic( i ).getAmplitude();
        }
        return amplitudes;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        //XXX did we win?
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

        int numberOfHarmonics = _randomFourierSeries.getNumberOfHarmonics();
        
        int i1 = _random.nextInt( numberOfHarmonics );
        int i2 = _random.nextInt( numberOfHarmonics );
        
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            
            if ( i == i1 || i == i2 ) {
                double amplitude = generateRandomAmplitude();
                _randomFourierSeries.getHarmonic( i ).setAmplitude( amplitude );
            }
            else {
                _randomFourierSeries.getHarmonic( i ).setAmplitude( 0 );
            }
        }
    }
    
    /*
     * Generates data for the "Medium" game level.
     * Random values for 4 harmonics, all others zero
     */
    private void generateMedium() {
        
        int numberOfHarmonics = _randomFourierSeries.getNumberOfHarmonics();
        
        int i1 = _random.nextInt( numberOfHarmonics );
        int i2 = _random.nextInt( numberOfHarmonics );
        int i3 = _random.nextInt( numberOfHarmonics );
        int i4 = _random.nextInt( numberOfHarmonics );
        
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            
            if ( i == i1 || i == i2 || i == i3 || i == i4 ) {
                double amplitude = generateRandomAmplitude();
                _randomFourierSeries.getHarmonic( i ).setAmplitude( amplitude );
            }
            else {
                _randomFourierSeries.getHarmonic( i ).setAmplitude( 0 );
            }
        }
    }
    
    /*
     * Generates data for the "Hard" game level.
     * Random values for all harmonics
     */
    private void generateHard() {
        
        int numberOfHarmonics = _randomFourierSeries.getNumberOfHarmonics();
        
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            double amplitude = generateRandomAmplitude();
            _randomFourierSeries.getHarmonic( i ).setAmplitude( amplitude );
        } 
    }
    
    /*
     * Generates data for the "Preset" game level.
     */
    private void generatePreset() {
        if ( _gameLevel == GameLevel.PRESET ) {
            _randomFourierSeries.setPreset( _preset );
        }
    }

}
