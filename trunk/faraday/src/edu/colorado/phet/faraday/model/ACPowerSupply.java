/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.model.ModelElement;


/**
 * ACPowerSupply is the model of an AC Power Supply.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ACPowerSupply extends AbstractVoltageSource implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MIN_STEPS_PER_CYCLE = 10;
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _maxAmplitude; // 0...1
    private double _frequency; // 0...1
    private int _sign; // -1 or +1
    private double _angle; // radians
    
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
        _sign = +1; // positive voltage
        _angle = 0.0; // radians
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
            
            // Make sure the amplitude stays in range.
            double amplitude = getAmplitude();
            double clamplitude = MathUtil.clamp( -maxAmplitude, amplitude, maxAmplitude );
            setAmplitude( clamplitude );
            
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
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * Varies the amplitude over time, based on maxAmplitude and frequency.
     * 
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        if ( isEnabled() ) {

            double amplitude = 0.0;
            if ( _maxAmplitude == 0.0 ) {
                _angle = 0.0;
                amplitude = 0.0;
            }
            else {
                // Amplitude varies sinusoidally over time.
                double delta = ( 2 * Math.PI * _frequency ) / MIN_STEPS_PER_CYCLE;
                _angle += delta;
                if ( _angle > 2 * Math.PI ) {
                    _angle = _angle - ( 2 * Math.PI );
                }
                amplitude = _maxAmplitude * Math.sin( _angle );

                // Make sure we always hit zero as we pass it.
                if ( ( amplitude > 0 && getAmplitude() < 0 ) || ( amplitude < 0 && getAmplitude() > 0 ) ) {
                    amplitude = 0;
                }
            }
            setAmplitude( amplitude );
        }
    }
}
