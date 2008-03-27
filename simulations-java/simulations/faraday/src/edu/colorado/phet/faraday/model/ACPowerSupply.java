/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.phetcommon.model.ModelElement;


/**
 * ACPowerSupply is the model of an AC Power Supply.
 * <p>
 * The AC Power Supply has a configurable maximum voltage. 
 * A client varies the maximum voltage amplitude and frequency.
 * The voltage amplitude varies over time.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ACPowerSupply extends AbstractCurrentSource implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // The minimum number of steps used to approximate one sine wave cycle.
    private static final double MIN_STEPS_PER_CYCLE = 10;
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Determines how high the amplitude can go. (0...1 inclusive)
    private double _maxAmplitude;
    // Determines how fast the amplitude will vary. (0...1 inclusive)
    private double _frequency;
    // The current angle of the sine wave that describes the AC. (radians)
    private double _angle;
    // The change in angle at the current freqency. (radians)
    private double _deltaAngle;
    // The change in angle that occurred the last time stepInTime was called. (radians)
    private double _stepAngle;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public ACPowerSupply() {
        super();
        _maxAmplitude = 1.0; // biggest
        _frequency = 1.0; // fastest
        _angle = 0.0; // radians
        _deltaAngle = ( 2 * Math.PI * _frequency ) / MIN_STEPS_PER_CYCLE; // radians
        _stepAngle = 0.0; // radians
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the maximum amplitude.
     * 
     * @param maxAmplitude the maximum amplitude, 0...1 inclusive
     */
    public void setMaxAmplitude( double maxAmplitude ) {
        assert( maxAmplitude >=0 && maxAmplitude <= 1 );
        if ( maxAmplitude != _maxAmplitude ) {
            _maxAmplitude = maxAmplitude;
            _angle = 0.0;
            notifyObservers();
        }
    }
    
    /**
     * Gets the maximum amplitude.
     * 
     * @return the maximum amplitude, 0...1 inclusive
     */
    public double getMaxAmplitude() {
        return _maxAmplitude;
    }
    
    /**
     * Sets the frequency.
     * 
     * @param frequency the frequency, 0...1 inclusive
     */
    public void setFrequency( double frequency ) {
        assert( frequency >= 0 && frequency <= 1 );
        if ( frequency != _frequency ) {
            _frequency = frequency;
            _angle = 0.0;
            _deltaAngle = ( 2 * Math.PI * _frequency ) / MIN_STEPS_PER_CYCLE;
            notifyObservers();
        }
    }
    
    /**
     * Gets the frequency.
     * 
     * @return the frequency, 0...1 inclusive
     */
    public double getFrequency() {
        return _frequency;
    }
    
    /**
     * Change in angle the last time that stepInTime was called (ie, the 
     * last time that the simulation clock ticked).
     * 
     * @return the angle, in radians
     */
    public double getStepAngle() {
        return _stepAngle;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * Varies the amplitude over time, based on maxAmplitude and frequency.
     * Guaranteed to hit all peaks and zero crossings.
     * 
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        if ( isEnabled() ) {
            
            if ( _maxAmplitude == 0 ) {
                setAmplitude( 0.0 );
            }
            else {
                double previousAngle = _angle;

                // Compute the angle.
                _angle += ( dt * _deltaAngle );

                // The actual change in angle on this tick of the simulation clock.
                _stepAngle = _angle - previousAngle;

                // Limit the angle to 360 degrees.
                if ( _angle >= 2 * Math.PI ) {
                    _angle = _angle % ( 2 * Math.PI );
                }

                // Calculate and set the amplitude.
                setAmplitude( _maxAmplitude * Math.sin( _angle ) );
            }
        }
    }
}
