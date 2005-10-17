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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.fourier.enum.GameLevel;
import edu.colorado.phet.fourier.enum.Preset;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.RandomFourierSeries;


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
    private RandomFourierSeries _randomFourierSeries;
    private GameAmplitudesView _amplitudesView;
    private GameLevel _gameLevel;
    private Preset _preset;
    private Hashtable _gameSpecs; // hashtable of GameSpecification
    
    public GameManager( FourierSeries userSeries, RandomFourierSeries randomSeries, GameAmplitudesView amplitudesView ) {
        
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
        
        // Set the game level
        _randomFourierSeries.setGameLevel( _gameLevel );
        
        // Restore the preset
        if ( _gameLevel == GameLevel.PRESET ) {
            _randomFourierSeries.setPreset( _preset );
        }
        else {
            _randomFourierSeries.setPreset( Preset.CUSTOM );
        }
        
        // Generate a new random series
        _randomFourierSeries.generate();
    }
    
    public double[] getAmplitudes() {
        double[] amplitudes = new double[ _randomFourierSeries.getNumberOfHarmonics() ];
        for ( int i = 0; i < amplitudes.length; i++ ) {
            amplitudes[i] = _randomFourierSeries.getHarmonic( i ).getAmplitude();
        }
        return amplitudes;
    }
    
    public void update() {
        //XXX did we win?
    }


}
