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


public class BSIntegerRange {

    int _min, _max, _default;
    
    public BSIntegerRange( int min, int max, int defaultValue ) {
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
    
    public int getMin() {
        return _min;
    }
    
    public int getMax() {
        return _max;
    }
    
    public int getDefault() {
        return _default;
    }
    
    public boolean contains( int value ) {
        return( value >= _min && value <= _max );
    }
    
    public int getLength() {
        return _max - _min;
    }
}
