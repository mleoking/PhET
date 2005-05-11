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
 * FourierComponent
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierComponent extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _order; //XXX rename this
    private double _amplitude;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param order
     */
    public FourierComponent( int order ) {
        super();
        assert( order >= 0 );
        _order = order;
        _amplitude = 0.0;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setOrder( int order ) {
        if ( order != _order ) {
            _order = order;
            notifyObservers();
        }
    }
    
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
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * Fundamental is the fundamental Fourier component.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    public class Fundamental extends FourierComponent {  
        public Fundamental() {
            super( 0 );
        }
        
        public void setOrder( int order ) {
            // XX throw exception
        }
    }
}
