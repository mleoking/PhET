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
    private double _deltaAngle; // radians
    private double _stepAngle; // radians

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
     * 
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        if ( isEnabled() ) {
            
            double previousAngle = _angle;
            
            // Compute the angle.
            _angle += _deltaAngle;
            
            // Adjust the angle so that we hit all peaks and zero crossings.
            for ( int i = 1; i <= 4; i++ ) {
                double criticalAngle = i * ( Math.PI / 2 );  // ...at 90 degree intervals
                if ( previousAngle < criticalAngle && _angle > criticalAngle ) {
                    _angle = criticalAngle;
                    break;
                }
            }
            
            // The actual change in angle on this tick of the simulation clock.
            _stepAngle = _angle - previousAngle;

            // Limit the angle to 360 degrees.
            if ( _angle >= 2 * Math.PI ) {
                _angle = _angle % ( 2 * Math.PI );
            }
            
            // Calculate the amplitude.
            if ( _maxAmplitude == 0.0 ) {
                setAmplitude( 0.0 );
            }
            else {
                setAmplitude( _maxAmplitude * Math.sin( _angle ) );
            }
        }
    }
}
