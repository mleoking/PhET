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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * SourceCoil
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SourceCoil extends AbstractCoil implements SimpleObserver {

    private AbstractVoltageSource _voltageSource;
    
    public SourceCoil() {
        super();
    }
    
    public void finalize() {
        if ( _voltageSource != null ) {
            _voltageSource.removeObserver( this );
            _voltageSource = null;
        }
    }
    
    public void setVoltageSource( AbstractVoltageSource voltageSource ) {
        assert( voltageSource != null );
        if ( voltageSource != _voltageSource ) {
            if ( _voltageSource != null ) {
                _voltageSource.removeObserver( this );
            }
            _voltageSource = voltageSource;
            _voltageSource.addObserver( this );
            notifyObservers();
        }
    }
    
    public AbstractVoltageSource getVoltageSource() {
        return _voltageSource;
    }

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        if ( isEnabled() && _voltageSource != null ) {
            double sourceVoltage = _voltageSource.getVoltage();
            int numberOfLoops = getNumberOfLoops();
            double voltage = ( numberOfLoops / FaradayConfig.ELECTROMAGNET_LOOPS_MAX ) * sourceVoltage;  // XXX hack
            setVoltage( voltage );
            notifyObservers();
        }
    }
}
