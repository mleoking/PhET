/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;


public class BSDoubleRange {

    double _min, _max, _default;
    
    public BSDoubleRange( double min, double max, double defaultValue ) {
        if ( max < min ) {
            throw new IllegalArgumentException( "max < min" );
        }
        if ( defaultValue < min || defaultValue > max ) {
            throw new IllegalArgumentException( "defaultValue out of range" );
        }
        _min = min;
        _max = max;
        _default = defaultValue;
    }
    
    public double getMin() {
        return _min;
    }
    
    public double getMax() {
        return _max;
    }
    
    public double getDefault() {
        return _default;
    }
    
    public boolean contains( double value ) {
        return( value >= _min && value <= _max );
    }
    
    public double getLength() {
        return _max - _min;
    }
}
