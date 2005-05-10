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
 * Harmonic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Harmonic extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _order;
    private double _amplitude;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Harmonic( int order ) {
        super();
        assert( order >= 0 );
        _order = order;
        _amplitude = 0.0;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public int getOrder() {
        return _order;
    }
    
    public void setAmplitude( double amplitude ) {
        assert( amplitude >= -1 && amplitude <= +1 );
        if ( amplitude != _amplitude ) {
            _amplitude = amplitude;
            notifyObservers();
        }
    }
    
    public double getAmplitude() {
        return _amplitude;
    }
    
    //----------------------------------------------------------------------------
    // Classes
    //----------------------------------------------------------------------------
    
    public class Fundamental extends Harmonic {
        public Fundamental() {
            super( 0 );
        }
    }
}
