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

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * AbstractVoltageSource
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractVoltageSource extends SimpleObservable {

    private double _voltage;
    private boolean _enabled;
    
    public AbstractVoltageSource() {
        _voltage = 0.0;
        _enabled = true;
    }
    
    public void setVoltage( double voltage ) {
        if ( voltage != _voltage ) {
            _voltage = voltage;
            if ( _enabled ) {
                notifyObservers();
            }
        }
    }
    
    public double getVoltage() {
        return _voltage;
    }
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            notifyObservers();
        }
    }
    
    public boolean isEnabled() {
        return _enabled;
    }
}
