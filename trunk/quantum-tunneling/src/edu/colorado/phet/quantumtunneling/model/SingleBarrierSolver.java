/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;


/**
 * SingleBarrierSolver is a closed-form solution to the 
 * wave function equation for plane waves with single-barrier potentials.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SingleBarrierSolver extends AbstractPlaneSolver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // coefficients
    private MutableComplex _B, _C, _D, _F;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param te
     * @param pe
     * @param direction
     */
    public SingleBarrierSolver( TotalEnergy te, BarrierPotential pe, Direction direction ) {
        super( te, pe, direction );
        assert ( pe.getNumberOfBarriers() == 1 );
    }
    
    //----------------------------------------------------------------------------
    // AbstractPlaneSolver implementation
    //----------------------------------------------------------------------------

    /**
     * Solves the wave function.
     * 
     * @param x position, in nm
     * @param t time, in fs
     */
    public WaveFunctionSolution solve( final double x, final double t ) {
        
        WaveFunctionSolution result = null;

        if ( isSolutionZero() ) {
            result = new WaveFunctionSolution( x, t, Complex.ZERO, Complex.ZERO );
        }
        else {
            int regionIndex = getPotentialEnergy().getRegionIndexAt( x );
            if ( isRightToLeft() ) {
                regionIndex = flipRegionIndex( regionIndex );
            }
            switch ( regionIndex ) {
            case 0:
                result = solveRegion1( x, t );
                break;
            case 1:
                result = solveRegion2( x, t );
                break;
            case 2:
                result = solveRegion3( x, t );
                break;
            default:
                // outside of the potential energy space
                result = null;
            }
        }

        return result;
    }
    
    /* 
     * Region1: psi(x,t) = ( e^(i*k1*x) + B * e^(-i*k1*x) ) * e^(-i*E*t/h)
     */
    private WaveFunctionSolution solveRegion1( final double x, final double t ) {
        Complex k1 = getK( 0 );
        Complex term1 = commonTerm1( k1, x ); // e^(ikx)
        Complex term2 = commonTerm2( k1, x ); // e^(-ikx)
        Complex term3 = commonTerm3( getTotalEnergy(), t ); // e^(-i*E*t/h)
        Complex incidentPart = term1.getMultiply( term3 );
        Complex reflectedPart = _B.getMultiply( term2 ).getMultiply( term3 );
        WaveFunctionSolution result = new WaveFunctionSolution( x, t, incidentPart, reflectedPart );
        return result;
    }
    
    /* 
     * Region2: psi(x,t) = ( C * e^(i*k2*x) + D * e^(-i*k2*x) ) * e^(-i*E*t/h)
     */
    private WaveFunctionSolution solveRegion2( final double x, final double t ) {
        Complex k2 = getK( 1 );
        Complex term1 = commonTerm1( k2, x ); // e^(ikx)
        Complex term2 = commonTerm2( k2, x ); // e^(-ikx)
        Complex term3 = commonTerm3( getTotalEnergy(), t ); // e^(-i*E*t/h)
        Complex incidentPart = _C.getMultiply( term1 ).getMultiply( term3 );
        Complex reflectedPart = _D.getMultiply( term2 ).getMultiply( term3 );
        WaveFunctionSolution result = new WaveFunctionSolution( x, t, incidentPart, reflectedPart );
        return result;
    }
    
    /* 
     * Region3: psi(x,t) = ( F * e^(i*k3*x) ) * e^(-i*E*t/h) 
     */
    private WaveFunctionSolution solveRegion3( final double x, final double t ) {
        Complex k3 = getK( 2 );
        Complex term1 = commonTerm1( k3, x ); // e^(ikx)
        Complex term3 = commonTerm3( getTotalEnergy(), t ); // e^(-i*E*t/h)
        Complex incidentPart = _F.getMultiply( term1 ).getMultiply( term3 );
        WaveFunctionSolution result = new WaveFunctionSolution( x, t, incidentPart );
        return result;
    }
    
    /*
     * Updates the coeffiecients.
     */
    protected void updateCoefficients() {
        
        // boundaries between regions
        final double x1 = getBoundary( 0, 1 );
        final double x2 = getBoundary( 1, 2 );

        // k values
        Complex k1 = getK( 0 );
        Complex k2 = getK( 1 );
        Complex k3 = getK( 2 );
        
        // Common denominator
        Complex denominator = getDenominator( x1, x2, k1, k2, k3 );
        
        // Coefficients
        updateB( x1, x2, k1, k2, k3, denominator );
        updateC( x1, x2, k1, k2, k3, denominator );
        updateD( x1, x2, k1, k2, k3, denominator );
        updateF( x1, x2, k1, k2, k3, denominator );
    }

    /*
     * B = 
     * (Power(E,2*I*k1*x1)*
     *   (Power(E,2*I*k2*x2)*(k1 + k2)*(k2 - k3) - 
     *    Power(E,2*I*k2*x1)*(-k1 + k2)*(k2 + k3))) /
     * denominator
     */
    private void updateB( double x1, double x2, Complex k1, Complex k2, Complex k3, Complex denominator ) {
        // Numerator
        MutableComplex numerator = new MutableComplex();
        {
            // Power(E,2*I*k1*x1)
            MutableComplex t1 = new MutableComplex();
            t1.setValue( 2 );
            t1.multiply( Complex.I );
            t1.multiply( k1 );
            t1.multiply( x1 );
            t1.exp();

            // Power(E,2*I*k2*x2)*(k1 + k2)*(k2 - k3)
            MutableComplex t2 = new MutableComplex();
            t2.setValue( 2 );
            t2.multiply( Complex.I );
            t2.multiply( k2 );
            t2.multiply( x2 );
            t2.exp();
            t2.multiply( k1.getAdd( k2 ) );
            t2.multiply( k2.getSubtract( k3 ) );

            // Power(E,2*I*k2*x1)*(-k1 + k2)*(k2 + k3)
            MutableComplex t3 = new MutableComplex();
            t3.setValue( 2 );
            t3.multiply( Complex.I );
            t3.multiply( k2 );
            t3.multiply( x1 );
            t3.exp();
            t3.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
            t3.multiply( k2.getAdd( k3 ) );

            numerator.setValue( t2 );
            numerator.subtract( t3 );
            numerator.multiply( t1 );
        }

        if ( _B == null ) {
            _B = new MutableComplex();
        }
        _B.setValue( numerator );
        _B.divide( denominator );
    }

    /*
     * C =
     * (2*Power(E,I*(k1 + k2)*x1)*k1*(k2 + k3)) /
     * denominator
     */
    private void updateC( double x1, double x2, Complex k1, Complex k2, Complex k3, Complex denominator ) {

        // Numerator
        MutableComplex numerator = new MutableComplex();
        numerator.setValue( Complex.I );
        numerator.multiply( k1.getAdd( k2 ) );
        numerator.multiply( x1 );
        numerator.exp();
        numerator.multiply( 2 );
        numerator.multiply( k1 );
        numerator.multiply( k2.getAdd( k3 ) );

        if ( _C == null ) {
            _C = new MutableComplex();
        }
        _C.setValue( numerator );
        _C.divide( denominator );
    }

    /*
     * D = 
     * (2*Power(E,I*((k1 + k2)*x1 + 2*k2*x2))*k1*(k2 - k3)) /
     * denominator
     */
    private void updateD( double x1, double x2, Complex k1, Complex k2, Complex k3, Complex denominator ) {

        // Numerator
        MutableComplex numerator = new MutableComplex();
        numerator.setValue( k1.getAdd( k2 ) );
        numerator.multiply( x1 );
        numerator.add( k2.getMultiply( 2 * x2 ) );
        numerator.multiply( Complex.I );
        numerator.exp();
        numerator.multiply( 2 );
        numerator.multiply( k1 );
        numerator.multiply( k2.getSubtract( k3 ) );

        if ( _D == null ) {
            _D = new MutableComplex();
        }
        _D.setValue( numerator );
        _D.divide( denominator );
    }

    /*
     * F =
     * (4*Power(E,I*(k1*x1 - k3*x2 + k2*(x1 + x2)))*k1*k2) /
     * denominator
     */
    private void updateF( double x1, double x2, Complex k1, Complex k2, Complex k3, Complex denominator ) {

        // Numerator
        MutableComplex numerator = new MutableComplex();
        numerator.setValue( k1.getMultiply( x1 ) );
        numerator.subtract( k3.getMultiply( x2 ) );
        numerator.add( k2.getMultiply( x1 + x2 ) );
        numerator.multiply( Complex.I );
        numerator.exp();
        numerator.multiply( 4 );
        numerator.multiply( k1 );
        numerator.multiply( k2 );

        if ( _F == null ) {
            _F = new MutableComplex();
        }
        _F.setValue( numerator );
        _F.divide( denominator );
    }  
    
    /*
     * All coeffiecients have this denominator:
     * (-(Power(E,2*I*k2*x2)*(-k1 + k2)*(k2 - k3)) + Power(E,2*I*k2*x1)*(k1 + k2)*(k2 + k3))
     */
    private static Complex getDenominator( final double x1, final double x2, Complex k1, Complex k2, Complex k3 ) {

        // -(Power(E,2*I*k2*x2)*(-k1 + k2)*(k2 - k3))
        MutableComplex t1 = new MutableComplex();
        t1.setValue( 2 );
        t1.multiply( Complex.I );
        t1.multiply( k2 );
        t1.multiply( x2 );
        t1.exp();
        t1.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
        t1.multiply( k2.getSubtract( k3 ) );
        t1.multiply( -1 );

        // Power(E,2*I*k2*x1)*(k1 + k2)*(k2 + k3)
        MutableComplex t2 = new MutableComplex();
        t2.setValue( 2 );
        t2.multiply( Complex.I );
        t2.multiply( k2 );
        t2.multiply( x1 );
        t2.exp();
        t2.multiply( k1.getAdd( k2 ) );
        t2.multiply( k2.getAdd( k3 ) );

        MutableComplex denominator = new MutableComplex();
        denominator.setValue( t1 );
        denominator.add( t2 );

        return denominator;
    }

    /**
     * Gets the coefficient B, the amplitude of the reflected wave.
     * 
     * @return Complex
     */
    public Complex getB() {
        if ( _B == null ) {
            solve( 0, 0 );
        }
        return _B;
    }
    
}
