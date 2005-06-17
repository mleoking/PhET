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

import java.util.ArrayList;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.fourier.FourierConstants;


/**
 * FourierSeries
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierSeries extends SimpleObservable implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_FUNDAMENTAL_FREQUENCY = 440; // Hz  (A above middle C)
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _fundamentalFrequency; // Hz
    private ArrayList _harmonics; // array of Harmonic
    private ArrayList _availableHarmonics; // array of Harmonic
    private int _preset;
    private int _waveType;
    private boolean _isAdjusting;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public FourierSeries() {
        _fundamentalFrequency = DEFAULT_FUNDAMENTAL_FREQUENCY;
        _harmonics = new ArrayList();
        _availableHarmonics = new ArrayList();
        _preset = FourierConstants.PRESET_SINE_COSINE;
        _waveType = FourierConstants.WAVE_TYPE_SINE;
        _isAdjusting = false;
        setNumberOfHarmonics( 1 );
    }
  
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        for ( int i = 0; i < _harmonics.size(); i++ ) {
            ( (Harmonic) _harmonics.get( i ) ).removeAllObservers();
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the fundamental frequency of the series.
     * 
     * @param fundamentalFrequency the fundamental frequency, in Hz
     */
    public void setFundamentalFrequency( double fundamentalFrequency ) {
        assert( fundamentalFrequency > 0 );
        if ( fundamentalFrequency != _fundamentalFrequency ) {
            _fundamentalFrequency = fundamentalFrequency;  
            notifyObservers();
        }
    }
    
    /**
     * Gets the fundamental frequency of the series.
     * 
     * @return the fundamental frequency, in Hz
     */
    public double getFundamentalFrequency() {
        return _fundamentalFrequency;
    }
    
    /**
     * Sets the number of harmonics in the series.
     * 
     * @param numberOfHarmonics the number of harmonics
     */
    public void setNumberOfHarmonics( int numberOfHarmonics ) {
        assert( numberOfHarmonics > 0 );
        
        Harmonic harmonic = null;
        
        int currentNumber = _harmonics.size();
        if ( numberOfHarmonics != currentNumber ) {
            
            _isAdjusting = true;
            
            if ( numberOfHarmonics < currentNumber ) {
                // Remove components.
                int numberToRemove = currentNumber - numberOfHarmonics;
                for ( int i = currentNumber-1; i > currentNumber - numberToRemove - 1; i-- ) {
                    // Move the component to the "available" list.
                    harmonic = (Harmonic) _harmonics.get( i );
                    harmonic.removeAllObservers();
                    harmonic.setAmplitude( 0 );
                    _availableHarmonics.add( harmonic );
                    _harmonics.remove( i );
                }
            }
            else {
                // Add harmonics.
                int numberToAdd = numberOfHarmonics - currentNumber;
                for ( int i = 0; i < numberToAdd; i++ ) {
                    int numberAvailable = _availableHarmonics.size();
                    if ( numberAvailable > 0 ) {
                        // Get a harmonic from the "available" list.
                        harmonic = (Harmonic) _availableHarmonics.get( numberAvailable - 1 );
                        _availableHarmonics.remove( numberAvailable - 1 );
                        harmonic.setOrder( currentNumber + i );
                        harmonic.addObserver( this );
                    }
                    else {
                        harmonic = new Harmonic( currentNumber + i );
                        harmonic.addObserver( this );
                    }
                    _harmonics.add( harmonic );
                }
            }
            updateAmplitudes();
            notifyObservers();
            _isAdjusting = false;
        }
    }
    
    /**
     * Gets the number of harmonics in the series.
     * 
     * @return the number of harmonics
     */
    public int getNumberOfHarmonics() {
        return _harmonics.size();
    }
    
    /**
     * Gets a specific harmonics in the series.
     * The index of the fundamental harmonic is zero.
     * 
     * @param order the index
     * @return
     */
    public Harmonic getHarmonic( int order ) {
        assert( order >= 0 && order < _harmonics.size() );
        return (Harmonic) _harmonics.get( order );
    }
    
    public void setPreset( int preset ) {
        assert( FourierConstants.isValidPreset( preset ) );
        if ( preset != _preset ) {
            _preset = preset;
            _isAdjusting = true;
            updateAmplitudes();
            notifyObservers();
            _isAdjusting = false;
        }
    }
    
    public int getPreset() {
        return _preset;
    }
    
    public void setWaveType( int waveType ) {
        assert( FourierConstants.isValidWaveType( waveType ) );
        if ( waveType != _waveType ) {
            _waveType = waveType;
            _isAdjusting = true;
            updateAmplitudes();
            notifyObservers();
            _isAdjusting = false;
        }
    }
    
    public int getWaveType() {
        return _waveType;
    }
 
    private void updateAmplitudes() {
        
        double[] amplitudes = null;
        int numberOfHarmonics = getNumberOfHarmonics();
        
        switch( _preset ) {
        case FourierConstants.PRESET_SINE_COSINE:
            amplitudes = FourierConstants.SINE_COSINE_AMPLITUDES;
            break;
        case FourierConstants.PRESET_SQUARE:
            if ( _waveType == FourierConstants.WAVE_TYPE_SINE ) {
                amplitudes = FourierConstants.SINE_SQUARE_AMPLITUDES;
            }
            else {
                amplitudes = FourierConstants.COSINE_SQUARE_AMPLITUDES;
            }
            break;
        case FourierConstants.PRESET_SAWTOOTH:
            if ( _waveType == FourierConstants.WAVE_TYPE_SINE ) {
                amplitudes = FourierConstants.SINE_SAWTOOTH_AMPLITUDES;
            }
            else {
                throw new IllegalStateException( "you can't make a sawtooth wave out of cosines because it is asymmetric" );
            }
            break;
        case FourierConstants.PRESET_TRIANGLE:
            if ( _waveType == FourierConstants.WAVE_TYPE_SINE ) {
                amplitudes = FourierConstants.SINE_TRIANGLE_AMPLITUDES;
            }
            else {
                amplitudes = FourierConstants.COSINE_TRIANGLE_AMPLITUDES;
            }
            break;
        case FourierConstants.PRESET_WAVE_PACKET:
            amplitudes = FourierConstants.WAVE_PACKET_AMPLITUDES[ getNumberOfHarmonics() - 1 ];
            break;
        case FourierConstants.PRESET_CUSTOM:
            //Do nothing.
            break;
        default:
            throw new IllegalStateException( "you forgot to implement a preset" );
        }
        
        if ( amplitudes != null ) {
            assert( numberOfHarmonics <= amplitudes.length );
            for ( int i = 0; i < numberOfHarmonics; i++ ) {
                ((Harmonic) _harmonics.get( i )).setAmplitude( amplitudes[i] );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        if ( ! _isAdjusting ) {
            notifyObservers();
        }
    }
}
