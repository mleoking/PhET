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
 * Current is the model of electrical current.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Current extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _amps;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor.
     */
    public Current() {
        this( 0.0 );
    }
    
    /**
     * Fully-specified constructor.
     * 
     * @param amps the current value, in amps
     */
    public Current( double amps ) {
        super();
        setAmps( amps );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the current value.
     * 
     * @param amps the current value, in amps
     * @throws IllegalArgumentException if amps is < 0
     */
    public void setAmps( double amps ) {
        if ( amps < 0 ) {
            throw new IllegalArgumentException( "amps must be >= 0: " + amps );
        }
        _amps = amps;
        notifyObservers();
    }
    
    /**
     * Gets the current.
     * 
     * @return the current, in amps
     */
    public double getAmps() { 
        return _amps;
    }
}
