/* Copyright 2004, University of Colorado */

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
 * Current
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Current extends SimpleObservable {

    private double _amps;
    
    public Current() {
        this( 0.0 );
    }
    
    public Current( double amps ) {
        super();
        setAmps( amps );
    }
    
    public void setAmps( double amps ) {
        _amps = amps;
        notifyObservers();
    }
    
    public double getAmps() { 
        return _amps;
    }
}
