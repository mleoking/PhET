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
    
    private double MIN_STEPS_PER_CYCLE = 10;
        
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _amplitude; // 0...1
    private double _frequency; // 0...1
    private int _sign; // -1 or +1
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ACSource() {
        super();
        _amplitude = 1.0; // biggest
        _frequency = 1.0; // fastest
        _sign = 1;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setAmplitude( double amplitude ) {
        assert( amplitude >= 0 && amplitude <= 1 );
        if ( amplitude != _amplitude ) {
            _amplitude = amplitude;
            notifyObservers();
        }
    }
    
    public double getAmplitude() {
        return _amplitude;  
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
           double max = _amplitude * getMaxVoltage();
           assert( max >= 0 );
           double delta = _sign * _frequency * ( max / MIN_STEPS_PER_CYCLE );
           double voltage = getVoltage() + delta;
           if ( voltage > max ) {
              _sign = -1;
              voltage = getVoltage() - delta;
           }
           else if ( voltage < -max ) {
              _sign = +1;
              voltage = getVoltage() + delta;
           }
           setVoltage( voltage );
        }
    } 
}
