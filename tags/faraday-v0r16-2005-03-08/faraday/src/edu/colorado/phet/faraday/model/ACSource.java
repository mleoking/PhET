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
 * ACSource is the model of an Alternating Current (AC) source.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ACSource extends AbstractVoltageSource implements ModelElement {

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
    
    public ACSource() {
        super();
        _maxAmplitude = 1.0; // biggest
        _frequency = 1.0; // fastest
        _sign = +1; // positive voltage
        _angle = 0.0; // radians
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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
    
    public double getMaxAmplitude() {
        return _maxAmplitude;
    }
    
    public void setFrequency( double frequency ) {
        assert( frequency >= 0 && frequency <= 1 );
        if ( frequency != _frequency ) {
            _frequency = frequency;
            notifyObservers();
        }
    }
    
    public double getFrequency() {
        return _frequency;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
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
