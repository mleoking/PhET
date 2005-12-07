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
 * Complex numbers, immutable.
 * 
 * @author Chris Malley (based on code by Sam Reid)
 * @version $Revision$
 */

public class Complex {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final Complex I = new Complex( 0, 1 );  // i == sqrt(-1)
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _real;
    private double _imaginary;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public Complex( double real, double imaginary ) {
        _real = real;
        _imaginary = imaginary;
    }

    public Complex() {
        this( 0, 0 );
    }

    public Complex( Complex c ) {
        this( c._real, c._imaginary );
    }

    public Complex copy() {
        return new Complex( _real, _imaginary );
    }

    //----------------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------------

    public double getReal() {
        return _real;
    }

    public double getImaginary() {
        return _imaginary;
    }

    public double getComplexPhase() {
        return Math.atan2( _imaginary, _real );
    }

    public boolean isZero() {
        return ( _real == 0 && _imaginary == 0 );
    }

    //----------------------------------------------------------------------------
    // Math
    //----------------------------------------------------------------------------

    public Complex plus( Complex c ) {
        return plus( c._real, c._imaginary );
    }
    
    public Complex plus( double real, double imaginary ) {
        return new Complex( _real + real, _imaginary + imaginary );
    }

    public Complex minus( Complex c ) {
        return minus( c._real, c._imaginary );
    }
    
    public Complex minus( double real, double imaginary ) {
        return new Complex( _real - real, _imaginary - imaginary );
    }

    public Complex times( Complex c ) {
        return times( c._real, c._imaginary );
    }
    
    public Complex times( double real, double imaginary ) {
        return new Complex( _real * real - _imaginary * imaginary, _real * imaginary + _imaginary * real );
    }

    public Complex scale( double scale ) {
        return new Complex( _real * scale, _imaginary * scale );
    }

    public Complex divideBy( Complex c ) {
        return divideBy( c._real, c._imaginary );
    }
    
    public Complex divideBy( double real, double imaginary ) {
        double q = real * real + imaginary * imaginary;
        double g = _real * real + _imaginary * imaginary;
        double h = _imaginary * real - _real * imaginary;
        return new Complex( g / q, h / q );
    }
    
    public double abs() {
        return ( Math.sqrt( ( _real * _real ) + ( _imaginary * _imaginary ) ) );
    }
    
    public double modulus() {
        return abs();
    }
    
    public Complex opposite() {
        return new Complex( -_real, -_imaginary );
    }
    
    public Complex complexConjugate() {
        return new Complex( _real, -_imaginary );
    }

    public static Complex exponentiateImaginary( double theta ) {
        return new Complex( Math.cos( theta ), Math.sin( theta ) );
    }
    
    public static Complex exp( Complex c ) {
        return exp( c._real, c._imaginary );
    }
    
    // e^(a+bi) = ( e^a )( cos(b) + i * sin(b) )
    public static Complex exp( double real, double imaginary ) {
        double multiplier = Math.exp( real );
        return new Complex( multiplier * Math.cos( imaginary), multiplier * Math.sin( imaginary ) );
    }

    //----------------------------------------------------------------------------
    // Object overrides
    //----------------------------------------------------------------------------

    public boolean equals( Object obj ) {
        boolean isEqual = false;
        if ( obj instanceof Complex ) {
            Complex c = (Complex) obj;
            isEqual = ( _real == c._real && _imaginary == c._imaginary );
        }
        return isEqual;
    }

    public String toString() {
        return "[" + _real + "," + _imaginary + "]";
    }
}
