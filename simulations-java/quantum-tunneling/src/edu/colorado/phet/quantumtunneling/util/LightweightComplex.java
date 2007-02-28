/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.util;


/**
 * LightweightComplex provides a bare minimum of complex number functionality. 
 * Member data can be accessed directly. 
 * Use this class with caution in algorithms that need to minimize method calls
 * (eg, RichardsonSolver).
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightweightComplex {
    
    public double _real;
    public double _imaginary;
    
    public LightweightComplex() {
        this( 0, 0 );
    }
    
    public LightweightComplex( double real, double imaginary ) {
        _real = real;
        _imaginary = imaginary;
    }
    
    public double getReal() {
        return _real;
    }
    
    public double getImaginary() {
        return _imaginary;
    }
    
    public void multiply( LightweightComplex c ) {
        double newReal = _real * c._real - _imaginary * c._imaginary;
        double newImaginary = _real * c._imaginary + _imaginary * c._real;
        _real = newReal;
        _imaginary = newImaginary;
    }
    
    public double getAbs() {
        return ( Math.sqrt( ( _real * _real ) + ( _imaginary * _imaginary ) ) );
    }
    
    public double getPhase() {
        return Math.atan2( _imaginary, _real );
    }
}