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
import edu.colorado.phet.common.util.SimpleObserver;


/**
 * SourceCoil
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SourceCoil extends AbstractCoil implements SimpleObserver {

    private AbstractVoltageSource _voltageSource;
    
    public SourceCoil( AbstractVoltageSource voltageSource ) {
        super();
        assert( voltageSource != null );
        setVoltageSource( voltageSource );
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
        if ( isEnabled() ) {
            setVoltage( _voltageSource.getVoltage() );
            notifyObservers();
        }
    }
}
