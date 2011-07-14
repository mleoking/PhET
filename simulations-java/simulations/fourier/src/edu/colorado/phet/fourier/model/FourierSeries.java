// Copyright 2002-2011, University of Colorado

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

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.enums.Preset;
import edu.colorado.phet.fourier.enums.WaveType;


/**
 * FourierSeries is the model of a Fourier series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierSeries extends SimpleObservable implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final int DEFAULT_NUMBER_OF_HARMONICS = 1;
    private static final double DEFAULT_FUNDAMENTAL_FREQUENCY = FourierConstants.FUNDAMENTAL_FREQUENCY;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _fundamentalFrequency; // Hz
    private ArrayList _harmonics; // array of Harmonic
    private ArrayList _availableHarmonics; // array of Harmonic
    private Preset _preset;
    private WaveType _waveType;
    private boolean _adjusting;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor.
     */
    public FourierSeries() {
        this( DEFAULT_NUMBER_OF_HARMONICS, DEFAULT_FUNDAMENTAL_FREQUENCY );
    }

    /**
     * Constructor
     *
     * @param numberOfHarmonics
     * @param fundamentalFrequency
     */
    public FourierSeries( int numberOfHarmonics, double fundamentalFrequency ) {
        super();
        _fundamentalFrequency = fundamentalFrequency;
        _harmonics = new ArrayList();
        _availableHarmonics = new ArrayList();
        _preset = Preset.SINE_COSINE;
        _waveType = WaveType.SINES;
        _adjusting = false;
        setNumberOfHarmonics( numberOfHarmonics );
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
        assert ( fundamentalFrequency > 0 );
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
        assert ( numberOfHarmonics > 0 );

        Harmonic harmonic = null;

        int currentNumber = _harmonics.size();
        if ( numberOfHarmonics != currentNumber ) {

            _adjusting = true;

            if ( numberOfHarmonics < currentNumber ) {
                // Remove components.
                int numberToRemove = currentNumber - numberOfHarmonics;
                for ( int i = currentNumber - 1; i > currentNumber - numberToRemove - 1; i-- ) {
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
                    }
                    else {
                        harmonic = new Harmonic( currentNumber + i );
                    }
                    _harmonics.add( harmonic );
                    harmonic.addObserver( this );
                }
            }
            updateAmplitudes();
            notifyObservers();
            _adjusting = false;
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
        assert ( order >= 0 && order < _harmonics.size() );
        return (Harmonic) _harmonics.get( order );
    }

    public void setPreset( Preset preset ) {
        if ( preset != _preset ) {
            _preset = preset;
            _adjusting = true;
            updateAmplitudes();
            notifyObservers();
            _adjusting = false;
        }
    }

    public Preset getPreset() {
        return _preset;
    }

    protected void resetPreset() {
        updateAmplitudes();
    }

    public void setWaveType( WaveType waveType ) {
        if ( waveType != _waveType ) {
            _waveType = waveType;
            _adjusting = true;
            updateAmplitudes();
            notifyObservers();
            _adjusting = false;
        }
    }

    public WaveType getWaveType() {
        return _waveType;
    }

    private void updateAmplitudes() {

        int numberOfHarmonics = getNumberOfHarmonics();

        double[] amplitudes = Preset.getPresetAmplitudes( _preset, _waveType, numberOfHarmonics );

        if ( amplitudes != null ) {
            assert ( numberOfHarmonics <= amplitudes.length );
            for ( int i = 0; i < numberOfHarmonics; i++ ) {
                ( (Harmonic) _harmonics.get( i ) ).setAmplitude( amplitudes[i] );
            }
        }
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    public void update() {
        if ( !_adjusting ) {
            notifyObservers();
        }
    }
}
