/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.model;

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * Harmonic is the model of a harmonic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Harmonic extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _order; // fundamental harmonic has order zero
    private double _amplitude;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param order
     */
    public Harmonic( int order ) {
        super();
        assert( order >= 0 );
        _order = order;
        _amplitude = 0.0;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the order of the harmonic.
     * The fundamental harmonic has an order of zero.
     * 
     * @param order
     */
    public void setOrder( int order ) {
        assert( order >= 0 );
        if ( order != _order ) {
            _order = order;
            notifyObservers();
        }
    }
    
    /**
     * Gets the order of the harmonic.
     * 
     * @return the order
     */
    public int getOrder() {
        return _order;
    }
    
    /**
     * Sets the harmonic's amplitude.
     * 
     * @param amplitude
     */
    public void setAmplitude( double amplitude ) {
        if ( amplitude != _amplitude ) {
            _amplitude = amplitude;
            notifyObservers();
        }
    }
    
    /**
     * Gets the harmonic's amplitude.
     * 
     * @return
     */
    public double getAmplitude() {
        return _amplitude;
    }
}
