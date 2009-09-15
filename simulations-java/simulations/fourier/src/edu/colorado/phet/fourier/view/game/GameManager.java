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

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.enums.GameLevel;
import edu.colorado.phet.fourier.enums.Preset;
import edu.colorado.phet.fourier.model.FourierSeries;


/**
 * GameManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameManager extends MouseInputAdapter implements SimpleObserver {

    /*
     * This is the step between amplitude values.
     * It controls the number of significant decimal places used in
     * comparisons, and it controls the number of significant decimal
     * places in the randomly-generated amplitude values.
     * CHANGE THIS VALUE AT YOUR PERIL!
     */
    private static final double AMPLITUDE_STEP = 0.01;

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * GameConfiguration describes game configuration.
     */
    private class GameConfiguration {

        private int _numberOfNonZeroHarmonics;
        private int _numberOfSliders;

        public GameConfiguration( int numberOfNonZeroHarmonics, int numberOfSliders ) {
            if ( numberOfSliders < numberOfNonZeroHarmonics ) {
                throw new IllegalArgumentException( "numberOfSlider must be >= numberOfNonZeroHarmonics" );
            }
            _numberOfNonZeroHarmonics = numberOfNonZeroHarmonics;
            _numberOfSliders = numberOfSliders;
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
    private boolean _mouseIsPressed;
    private boolean _isAdjusting;

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
        gameConfigs.put( GameLevel.LEVEL1,   new GameConfiguration(  1,  1 ) );
        gameConfigs.put( GameLevel.LEVEL2,   new GameConfiguration(  1,  2 ) );
        gameConfigs.put( GameLevel.LEVEL3,   new GameConfiguration(  1, 11 ) );
        gameConfigs.put( GameLevel.LEVEL4,   new GameConfiguration(  2,  2 ) );
        gameConfigs.put( GameLevel.LEVEL5,   new GameConfiguration(  2, 11 ) );
        gameConfigs.put( GameLevel.LEVEL6,   new GameConfiguration(  3,  3 ) );
        gameConfigs.put( GameLevel.LEVEL7,   new GameConfiguration(  3, 11 ) );
        gameConfigs.put( GameLevel.LEVEL8,   new GameConfiguration(  4,  4 ) );
        gameConfigs.put( GameLevel.LEVEL9,   new GameConfiguration(  4, 11 ) );
        gameConfigs.put( GameLevel.LEVEL10,  new GameConfiguration( 11, 11 ) );

        _random = new Random();

        _isAdjusting = false;
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
     * Gets the random Fourier series that the Game Manager manipulates.
     *
     * @return
     */
    public FourierSeries getRandomFourierSeries() {
        return _randomFourierSeries;
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

        _isAdjusting = true;

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
            int numberOfHarmonics = FourierConstants.MAX_HARMONICS;
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

        _isAdjusting = false;
        update();
    }

    /*
     * Generates a random number X, having 2 significant decimal places, such that:
     *
     * +0.1 <= X <= +FourierConfig.MAX_HARMONIC_AMPLITUDE, or
     * -0.1 >= X >= -FourierConfig.MAX_HARMONIC_AMPLUTUDE
     *
     * @return random number
     */
    private double generateRandomAmplitude() {

        // Randomly choose a positive or negative number.
        int sign = _random.nextBoolean() ? +1 : -1;

        // The minimum absolute amplitude that we'll generate
        double min = 0.1;

        // Randomly generate a quantity to add to the min.
        double step = AMPLITUDE_STEP;
        int numberOfSteps = (int) ( ( FourierConstants.MAX_HARMONIC_AMPLITUDE - min ) / step ) + 1;
        double delta = _random.nextInt( numberOfSteps ) * step;

        // Compute the amplitude
        double amplitude = sign * ( min + delta );
        assert( amplitude <= FourierConstants.MAX_HARMONIC_AMPLITUDE );

        return amplitude;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Called when the user changes the Fourier series.
     * We check to see if we've matched the randomly-generate Fourier series.
     * <p>
     * Criteria for a match:
     * <ul>
     * <li>the sign of all corresponding amplitudes is the same
     * <li>user amplitudes are within 0.03 of zero random amplitudes
     * <li>user amplitudes are within 0.09 of non-zero random amplitudes
     * <li>at least 2 user amplitudes are within 0.03 of non-zero random amplitudes
     * </ul>
     */
    public void update() {

        if ( _isAdjusting ) {
            // Don't respond to amplitude changes that we're making in newGame()
            return;
        }

        if ( _gameLevel == GameLevel.UNDEFINED ) {
            // We haven't been properly initialized yet.
            return;
        }

        // Don't do anything while the user is dragging an amplitude slider.
        if ( _mouseIsPressed ) {
            return;
        }

        // Inform the user when they've matched the waveform.
        if ( isMatch() ) {

            // WORKAROUND: Make sure that all other views are updated.
            {
                _userFourierSeries.removeObserver( this );
                _userFourierSeries.notifyObservers();
                _userFourierSeries.addObserver( this );
            }

            // Tell the user they won.
            JFrame frame = PhetApplication.getInstance().getPhetFrame();
            String title = FourierResources.getString( "WinDialog.title" );
            String message = FourierResources.getString( "WinDialog.message" );
            JOptionPane.showMessageDialog( frame, message, title, JOptionPane.PLAIN_MESSAGE );

            // Start a new game.
            newGame();
        }
    }

    /*
     * Checks to see if the user's Fourier series matches the
     * randomly-generated Fourier series. 2 significant decimal
     * places are used in the comparison.
     * <p>
     * Criteria for a match:
     * <ul>
     * <li>the sign of all corresponding amplitudes is the same
     * <li>user amplitudes are within 0.03 of zero random amplitudes
     * <li>user amplitudes are within 0.09 of non-zero random amplitudes
     * <li>at least 2 user amplitudes are within 0.03 of non-zero random amplitudes
     * </ul>
     */
    private boolean isMatch() {

        boolean match = true;
        int count = 0;  // count the number of matches that are within 0.03
        int numberOfNonZeroHarmonics = 0;

        int numberOfHarmonics = _userFourierSeries.getNumberOfHarmonics();
        for ( int i = 0; i < numberOfHarmonics && match == true ; i++ ) {

           double userAmplitude = _userFourierSeries.getHarmonic( i ).getAmplitude();
           double randomAmplitude = _randomFourierSeries.getHarmonic( i ).getAmplitude();

           /*
            * Convert to an integer percentage, so that we're only dealing
            * with 2 significant decimal places of the amplitude.
            */
           int userPercent = 0;
           if ( userAmplitude < 0 ) {
               userPercent = (int)( 100 * userAmplitude - 0.005 );
           }
           else {
               userPercent = (int)( 100 * userAmplitude + 0.005 );
           }
           int randomPercent = 0;
           if ( randomAmplitude < 0 ) {
               randomPercent = (int)( 100 * randomAmplitude - 0.005 );
           }
           else {
               randomPercent = (int)( 100 * randomAmplitude + 0.005 );
           }

           if ( randomPercent != 0 ) {
               numberOfNonZeroHarmonics++;
           }

           if ( ( randomPercent < 0 && userPercent > 0 ) || ( randomPercent > 0 && userPercent < 0 ) ) {
               // sign is wrong
               match = false;
           }
           else if ( randomPercent == 0 ) {
               if ( Math.abs( userPercent ) > 3 ) {
                   // not close enough to zero amplitude
                   match = false;
               }
           }
           else {
               int difference = Math.abs( userPercent - randomPercent );
               if ( difference > 9 ) {
                   // not close enough to non-zero amplitude
                   match = false;
               }
               else if ( difference <= 3 ) {
                   // count how many are within 0.03
                   count++;
               }
           }
        }

        // Make sure at least 2 are within 0.03
        if ( match ) {
            if ( count < 2 && count != numberOfNonZeroHarmonics ) {
                match = false;
            }
        }

        return match;
    }

    //----------------------------------------------------------------------------
    // MouseInputAdapter overrides
    //----------------------------------------------------------------------------

    /**
     * When the user presses the mouse, set some state that indicates
     * that we're in the process of dragging.  We don't check to see
     * if we've won until the user releases the mouse.
     */
    public void mousePressed( MouseEvent event ) {
        _mouseIsPressed = true;
    }

    /**
     * When the user releases the mouse, check to see if we've won.
     */
    public void mouseReleased( MouseEvent event ) {
        _mouseIsPressed = false;
        update();
    }
}