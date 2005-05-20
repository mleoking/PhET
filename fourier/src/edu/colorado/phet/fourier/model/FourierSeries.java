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
    
    public double _fundamentalFrequency; // Hz
    public ArrayList _harmonics; // array of Harmonic
    public ArrayList _availableHarmonics; // array of Harmonic
    
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
        setNumberOfHarmonics( 1 );
    }
  
    public void finalize() {
        for ( int i = 0; i < _harmonics.size(); i++ ) {
            ( (Harmonic) _harmonics.get( i ) ).removeObserver( this );
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
            if ( numberOfHarmonics < currentNumber ) {
                // Remove components.
                int numberToRemove = currentNumber - numberOfHarmonics;
                for ( int i = currentNumber-1; i > currentNumber - numberToRemove - 1; i-- ) {
                    // Move the component to the "available" list.
                    harmonic = (Harmonic) _harmonics.get( i );
                    harmonic.removeObserver( this );
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
            notifyObservers();
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
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        notifyObservers();
    }
}
