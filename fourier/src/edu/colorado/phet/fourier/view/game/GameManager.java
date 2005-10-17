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

import java.util.ArrayList;
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

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * GameConfiguration describes game configuration.
     */
    private class GameConfiguration {
        
        private int _numberOfHarmonics;
        private int _numberOfNonZeroHarmonics;
        private int _numberOfSliders;
        
        public GameConfiguration( int numberOfHarmonics, int numberOfNonZeroHarmonics, int numberOfSliders ) {
            if ( numberOfSliders < numberOfNonZeroHarmonics ) {
                throw new IllegalArgumentException( "numberOfSlider must be >= numberOfNonZeroHarmonics" );
            }
            _numberOfHarmonics = numberOfHarmonics;
            _numberOfNonZeroHarmonics = numberOfNonZeroHarmonics;
            _numberOfSliders = numberOfSliders;
        }
        
        public int getNumberOfHarmonics() {
            return _numberOfHarmonics;
        }
        
        public int getNumberOfNonZeroHarmonics() {
            return _numberOfNonZeroHarmonics;
        }
        
        public int getNumberOfSliders() {
            return _numberOfSliders;
        }
    }
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _userFourierSeries;
    private FourierSeries _randomFourierSeries;
    private GameAmplitudesView _amplitudesView;
    private GameLevel _gameLevel;
    private Preset _preset;
    private Hashtable gameConfigs; // hashtable of GameConfiguration
    private Random _random;  // the JDK random number generator
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param userSeries the Fourier series that the user manipulates
     * @param randomSeries the Fourier series that we randomly manipulate
     * @param amplitudesView the "Amplitudes" view for the Game module
     */
    public GameManager( FourierSeries userSeries, FourierSeries randomSeries, GameAmplitudesView amplitudesView ) {
        
        _userFourierSeries = userSeries;
        _userFourierSeries.addObserver( this );
        
        _randomFourierSeries = randomSeries;
        
        _amplitudesView = amplitudesView;
        
        _gameLevel = GameLevel.UNDEFINED;
        _preset = Preset.UNDEFINED;
        
        gameConfigs = new Hashtable();
        gameConfigs.put( GameLevel.LEVEL1,   new GameConfiguration( 11,  1,  1 ) );
        gameConfigs.put( GameLevel.LEVEL2,   new GameConfiguration( 11,  1,  2 ) );
        gameConfigs.put( GameLevel.LEVEL3,   new GameConfiguration( 11,  1, 11 ) );
        gameConfigs.put( GameLevel.LEVEL4,   new GameConfiguration( 11,  2,  2 ) );
        gameConfigs.put( GameLevel.LEVEL5,   new GameConfiguration( 11,  2, 11 ) );
        gameConfigs.put( GameLevel.LEVEL6,   new GameConfiguration( 11,  3,  3 ) );
        gameConfigs.put( GameLevel.LEVEL7,   new GameConfiguration( 11,  3, 11 ) );
        gameConfigs.put( GameLevel.LEVEL8,   new GameConfiguration( 11,  4,  4 ) );
        gameConfigs.put( GameLevel.LEVEL9,   new GameConfiguration( 11,  4, 11 ) );
        gameConfigs.put( GameLevel.LEVEL10,  new GameConfiguration( 11, 11, 11 ) );
        
        _random = new Random();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _userFourierSeries.removeObserver( this );
        _userFourierSeries = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the amplitudes for the randomly-generated Fourier series.
     * 
     * @return amplitudes
     */
    public double[] getAmplitudes() {
        double[] amplitudes = new double[ _randomFourierSeries.getNumberOfHarmonics() ];
        for ( int i = 0; i < amplitudes.length; i++ ) {
            amplitudes[i] = _randomFourierSeries.getHarmonic( i ).getAmplitude();
        }
        return amplitudes;
    }
    
    /**
     * Sets the game level.
     * 
     * @param gameLevel
     */
    public void setGameLevel( GameLevel gameLevel ) {
        _gameLevel = gameLevel;
        newGame();
    }
    
    /**
     * Sets the preset.
     * 
     * @param preset
     */
    public void setPreset( Preset preset ) {
        _preset = preset;
        newGame();
    }   
    
    //----------------------------------------------------------------------------
    // Game generation
    //----------------------------------------------------------------------------
    
    /**
     * Generates a new game, based on the game level and preset settings.
     */
    public void newGame() {
        
        if ( _gameLevel == GameLevel.PRESET ) {
            // Set the number of harmonics
            _userFourierSeries.setNumberOfHarmonics( FourierConstants.MAX_HARMONICS );
            _randomFourierSeries.setNumberOfHarmonics( FourierConstants.MAX_HARMONICS );

            // Set all amplitudes to zero
            for ( int i = 0; i < _userFourierSeries.getNumberOfHarmonics(); i++ ) {
                _userFourierSeries.getHarmonic( i ).setAmplitude( 0 );
                _randomFourierSeries.getHarmonic( i ).setAmplitude( 0 );
            }
            
            // Set the preset amplitudes
            _randomFourierSeries.setPreset( Preset.CUSTOM );
            _randomFourierSeries.setPreset( _preset );
            
            // Make all sliders visible
            _amplitudesView.setSlidersVisible( true );
        }
        else {
            // Get the configuration for this game level.
            GameConfiguration gameConfig = (GameConfiguration) gameConfigs.get( _gameLevel );
            int numberOfHarmonics = gameConfig._numberOfHarmonics;
            int numberOfNonZeroHarmonic = gameConfig._numberOfNonZeroHarmonics;
            int numberOfSliders = gameConfig._numberOfSliders;
            
            // Set the number of harmonics
            _userFourierSeries.setNumberOfHarmonics( numberOfHarmonics );
            _randomFourierSeries.setNumberOfHarmonics( numberOfHarmonics );

            // Set all amplitudes to zero
            for ( int i = 0; i < _userFourierSeries.getNumberOfHarmonics(); i++ ) {
                _userFourierSeries.getHarmonic( i ).setAmplitude( 0 );
                _randomFourierSeries.getHarmonic( i ).setAmplitude( 0 );
            }
            
            // Select random harmonics
            ArrayList indicies = new ArrayList();
            while( indicies.size() < numberOfSliders ) {
                Integer index = new Integer( _random.nextInt( numberOfHarmonics ) );
                if ( ! indicies.contains( index ) ) {
                    indicies.add( index );
                }
            }
            
            // Generate random amplitudes
            for ( int i = 0; i < numberOfNonZeroHarmonic; i++ ) {
                Integer index = (Integer) indicies.get( i );
                double amplitude = generateRandomAmplitude();
                _randomFourierSeries.getHarmonic( index.intValue() ).setAmplitude( amplitude );
            }
            
            // Set visibility of sliders
            _amplitudesView.setSlidersVisible( false );
            for ( int i = 0; i < indicies.size(); i++ ) {
                Integer index = (Integer) indicies.get( i );
                _amplitudesView.setSliderVisible( index.intValue(), true );
            }
        }
    }
    
    /*
     * Generates a random non-zero number between +-FourierConfig.MAX_HARMONIC_AMPLITUDE
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
        if ( amplitude == 0 ) {
            amplitude = step;
        }
        return amplitude;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Called when the user changes the Fourier series.
     * We check to see if we've matched the randomly-generate Fourier series.
     */
    public void update() {
        //XXX did we win?
    }
}