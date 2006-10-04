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
 * DoubleBarrierSolver is a closed-form solution to the 
 * wave function equation for plane waves with double-barrier potentials.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DoubleBarrierSolver extends AbstractBarrierSolver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // coefficients
    private MutableComplex _B, _C, _D, _F, _G, _H, _I, _J;
    
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
    public DoubleBarrierSolver( TotalEnergy te, BarrierPotential pe, Direction direction ) {
        super( te, pe, direction );
        assert ( pe.getNumberOfBarriers() == 2 );
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
            result = new WaveFunctionSolution( x, t, Complex.ZERO, Complex.ZERO  );
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
            case 3:
                result = solveRegion4( x, t );
                break;
            case 4:
                result = solveRegion5( x, t );
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
        WaveFunctionSolution result = new WaveFunctionSolution( x, t, incidentPart, reflectedPart );;
        return result;
    }
    
    /* 
     * Region3: psi(x,t) = ( F * e^(i*k3*x) + G * e^(-i*k3*x) ) * e^(-i*E*t/h)
     */
    private WaveFunctionSolution solveRegion3( final double x, final double t ) {
        Complex k3 = getK( 2 );
        Complex term1 = commonTerm1( k3, x ); // e^(ikx)
        Complex term2 = commonTerm2( k3, x ); // e^(-ikx)
        Complex term3 = commonTerm3( getTotalEnergy(), t ); // e^(-i*E*t/h)
        Complex incidentPart = _F.getMultiply( term1 ).getMultiply( term3 );
        Complex reflectedPart = _G.getMultiply( term2 ).getMultiply( term3 );
        WaveFunctionSolution result = new WaveFunctionSolution( x, t, incidentPart, reflectedPart );
        return result;
    }
    
    /* 
     * Region4: psi(x,t) = ( H * e^(i*k4*x) + I * e^(-i*k4*x) ) * e^(-i*E*t/h)
     */
    private WaveFunctionSolution solveRegion4( final double x, final double t ) {
        Complex k4 = getK( 3 );
        Complex term1 = commonTerm1( k4, x ); // e^(ikx)
        Complex term2 = commonTerm2( k4, x ); // e^(-ikx)
        Complex term3 = commonTerm3( getTotalEnergy(), t ); // e^(-i*E*t/h)
        Complex incidentPart = _H.getMultiply( term1 ).getMultiply( term3 );
        Complex reflectedPart = _I.getMultiply( term2 ).getMultiply( term3 );
        WaveFunctionSolution result = new WaveFunctionSolution( x, t, incidentPart, reflectedPart );
        return result;
    }
    
    /* 
     * Region5: psi(x,t) = ( J * e^(i*k5*x) ) * e^(-i*E*t/h)
     */
    private WaveFunctionSolution solveRegion5( final double x, final double t ) {
        Complex k5 = getK( 4 );
        Complex term1 = commonTerm1( k5, x ); // e^(ikx)
        Complex term3 = commonTerm3( getTotalEnergy(), t ); // e^(-i*E*t/h)
        Complex incidentPart = _J.getMultiply( term1 ).getMultiply( term3 );
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
        final double x3 = getBoundary( 2, 3 );
        final double x4 = getBoundary( 3, 4 );

        // k values
        Complex k1 = getK( 0 );
        Complex k2 = getK( 1 );
        Complex k3 = getK( 2 );
        Complex k4 = getK( 3 );
        Complex k5 = getK( 4 );
        
        // Common denominator
        Complex denominator = getDenominator( x1, x2, x3, x4, k1, k2, k3, k4, k5 );
        
        // Coefficients
        updateB( x1, x2, x3, x4, k1, k2, k3, k4, k5, denominator );
        updateC( x1, x2, x3, x4, k1, k2, k3, k4, k5, denominator );
        updateD( x1, x2, x3, x4, k1, k2, k3, k4, k5, denominator );
        updateF( x1, x2, x3, x4, k1, k2, k3, k4, k5, denominator );
        updateG( x1, x2, x3, x4, k1, k2, k3, k4, k5, denominator );
        updateH( x1, x2, x3, x4, k1, k2, k3, k4, k5, denominator );
        updateI( x1, x2, x3, x4, k1, k2, k3, k4, k5, denominator );
        updateJ( x1, x2, x3, x4, k1, k2, k3, k4, k5, denominator );
    }
    
    /*
     * B = 
     * (Power(E,2*I*k1*x1)*
     *  (Power(E,2*I*((k2 + k3)*x2 + k4*x4))*(k1 + k2)*(k2 - k3)*(k3 - k4)*(k4 - k5) -
     *   Power(E,2*I*(k2*x1 + k3*x2 + k4*x4))*(-k1 + k2)*(k2 + k3)*(k3 - k4)*(k4 - k5) -
     *   Power(E,2*I*(k2*x1 + k3*x3 + k4*x4))*(-k1 + k2)*(k2 - k3)*(k3 + k4)*(k4 - k5) +
     *   Power(E,2*I*(k2*x2 + k3*x3 + k4*x4))*(k1 + k2)*(k2 + k3)*(k3 + k4)*(k4 - k5) -
     *   Power(E,2*I*(k2*x1 + (k3 + k4)*x3))*(-k1 + k2)*(k2 - k3)*(k3 - k4)*(k4 + k5) +
     *   Power(E,2*I*(k2*x2 + (k3 + k4)*x3))*(k1 + k2)*(k2 + k3)*(k3 - k4)*(k4 + k5) +
     *   Power(E,2*I*((k2 + k3)*x2 + k4*x3))*(k1 + k2)*(k2 - k3)*(k3 + k4)*(k4 + k5) -
     *   Power(E,2*I*(k2*x1 + k3*x2 + k4*x3))*(-k1 + k2)*(k2 + k3)*(k3 + k4)*(k4 + k5))) /
     *   denominator
     */
    private void updateB( double x1, double x2, double x3, double x4,
            Complex k1, Complex k2, Complex k3, Complex k4, Complex k5, Complex denominator ) {
        
        // Power(E,2*I*k1*x1)
        MutableComplex t1 = new MutableComplex();
        t1.setValue( 2 );
        t1.multiply( Complex.I );
        t1.multiply( k1 );
        t1.multiply( x1 );
        t1.exp();
        
        // Power(E,2*I*((k2 + k3)*x2 + k4*x4))*(k1 + k2)*(k2 - k3)*(k3 - k4)*(k4 - k5)
        MutableComplex t2 = new MutableComplex();
        t2.setValue( k2.getAdd( k3 ) );
        t2.multiply( x2 );
        t2.add( k4.getMultiply( x4 ) );
        t2.multiply( 2 );
        t2.multiply( Complex.I );
        t2.exp();
        t2.multiply( k1.getAdd( k2 ) );
        t2.multiply( k2.getSubtract( k3 ) );
        t2.multiply( k3.getSubtract( k4 ) );
        t2.multiply( k4.getSubtract( k5 ) );
        
        // Power(E,2*I*(k2*x1 + k3*x2 + k4*x4))*(-k1 + k2)*(k2 + k3)*(k3 - k4)*(k4 - k5)
        MutableComplex t3 = new MutableComplex();
        t3.setValue( k2.getMultiply( x1 ) );
        t3.add( k3.getMultiply( x2 ) );
        t3.add( k4.getMultiply( x4 ) );
        t3.multiply( 2 );
        t3.multiply( Complex.I );
        t3.exp();
        t3.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
        t3.multiply( k2.getAdd( k3 ) );
        t3.multiply( k3.getSubtract( k4 ) );
        t3.multiply( k4.getSubtract( k5 ) );
         
        // Power(E,2*I*(k2*x1 + k3*x3 + k4*x4))*(-k1 + k2)*(k2 - k3)*(k3 + k4)*(k4 - k5)
        MutableComplex t4 = new MutableComplex();
        t4.setValue( k2.getMultiply( x1 ) );
        t4.add( k3.getMultiply( x3 ) );
        t4.add( k4.getMultiply( x4 ) );
        t4.multiply( 2 );
        t4.multiply( Complex.I );
        t4.exp();
        t4.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
        t4.multiply( k2.getSubtract( k3 ) );
        t4.multiply( k3.getAdd( k4 ) );
        t4.multiply( k4.getSubtract( k5 ) );
        
        // Power(E,2*I*(k2*x2 + k3*x3 + k4*x4))*(k1 + k2)*(k2 + k3)*(k3 + k4)*(k4 - k5)
        MutableComplex t5 = new MutableComplex();
        t5.setValue( k2.getMultiply( x2 ) );
        t5.add( k3.getMultiply( x3 ) );
        t5.add( k4.getMultiply( x4 ) );
        t5.multiply( 2 );
        t5.multiply( Complex.I );
        t5.exp();
        t5.multiply( k1.getAdd( k2 ) );
        t5.multiply( k2.getAdd( k3 ) );
        t5.multiply( k3.getAdd( k4 ) );
        t5.multiply( k4.getSubtract( k5 ) );
        
        // Power(E,2*I*(k2*x1 + (k3 + k4)*x3))*(-k1 + k2)*(k2 - k3)*(k3 - k4)*(k4 + k5)
        MutableComplex t6 = new MutableComplex();
        t6.setValue( k2.getMultiply( x1 ) );
        t6.add( k3.getAdd( k4 ).getMultiply( x3 ) );
        t6.multiply( 2 );
        t6.multiply( Complex.I );
        t6.exp();
        t6.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
        t6.multiply( k2.getSubtract( k3 ) );
        t6.multiply( k3.getSubtract( k4 ) );
        t6.multiply( k4.getAdd( k5 ) );
        
        // Power(E,2*I*(k2*x2 + (k3 + k4)*x3))*(k1 + k2)*(k2 + k3)*(k3 - k4)*(k4 + k5)
        MutableComplex t7 = new MutableComplex();
        t7.setValue( k2.getMultiply( x2 ) );
        t7.add( k3.getAdd( k4 ).getMultiply( x3 ) );
        t7.multiply( 2 );
        t7.multiply( Complex.I );
        t7.exp();
        t7.multiply( k1.getAdd( k2 ) );
        t7.multiply( k2.getAdd( k3 ) );
        t7.multiply( k3.getSubtract( k4 ) );
        t7.multiply( k4.getAdd( k5 ) );
        
        // Power(E,2*I*((k2 + k3)*x2 + k4*x3))*(k1 + k2)*(k2 - k3)*(k3 + k4)*(k4 + k5)
        MutableComplex t8 = new MutableComplex();
        t8.setValue( k2.getAdd( k3 ) );
        t8.multiply( x2 );
        t8.add( k4.getMultiply( x3 ) );
        t8.multiply( 2 );
        t8.multiply( Complex.I );
        t8.exp();
        t8.multiply( k1.getAdd( k2 ) );
        t8.multiply( k2.getSubtract( k3 ) );
        t8.multiply( k3.getAdd( k4 ) );
        t8.multiply( k4.getAdd( k5 ) );
        
        // Power(E,2*I*(k2*x1 + k3*x2 + k4*x3))*(-k1 + k2)*(k2 + k3)*(k3 + k4)*(k4 + k5)
        MutableComplex t9 = new MutableComplex();
        t9.setValue( k2.getMultiply( x1 ) );
        t9.add( k3.getMultiply( x2 ) );
        t9.add( k4.getMultiply( x3 ) );
        t9.multiply( 2 );
        t9.multiply( Complex.I );
        t9.exp();
        t9.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
        t9.multiply( k2.getAdd( k3 ) );
        t9.multiply( k3.getAdd( k4 ) );
        t9.multiply( k4.getAdd( k5 ) );
        
        if ( _B == null ) {
            _B = new MutableComplex();
        }
        _B.setValue( t2 );
        _B.subtract( t3 );
        _B.subtract( t4 );
        _B.add( t5 );
        _B.subtract( t6 );
        _B.add( t7 );
        _B.add( t8 );
        _B.subtract( t9 );
        _B.multiply( t1 );
        _B.divide( denominator);
    }
    
    /*
     * C = 
     * (2*Power(E,I*(k1 + k2)*x1)*k1*
     *   (Power(E,2*I*(k3*x2 + k4*x4))*(k2 + k3)*(k3 - k4)*(k4 - k5) -
     *   Power(E,2*I*(k3*x3 + k4*x4))*(-k2 + k3)*(k3 + k4)*(k4 - k5) - 
     *   Power(E,2*I*(k3 + k4)*x3)*(-k2 + k3)*(k3 - k4)*(k4 + k5) +
     *   Power(E,2*I*(k3*x2 + k4*x3))*(k2 + k3)*(k3 + k4)*(k4 + k5))) /
     *   denominator
     */
    private void updateC( double x1, double x2, double x3, double x4,
            Complex k1, Complex k2, Complex k3, Complex k4, Complex k5, Complex denominator ) {
        
        // 2*Power(E,I*(k1 + k2)*x1)*k1
        MutableComplex t1 = new MutableComplex();
        t1.setValue( Complex.I );
        t1.multiply( k1.getAdd( k2 ) );
        t1.multiply( x1 );
        t1.exp();
        t1.multiply( 2 );
        t1.multiply( k1 );
        
        // Power(E,2*I*(k3*x2 + k4*x4))*(k2 + k3)*(k3 - k4)*(k4 - k5)
        MutableComplex t2 = new MutableComplex();
        t2.setValue( k3.getMultiply( x2 ) );
        t2.add( k4.getMultiply( x4 ) );
        t2.multiply( 2 );
        t2.multiply( Complex.I );
        t2.exp();
        t2.multiply( k2.getAdd( k3 ) );
        t2.multiply( k3.getSubtract( k4 ) );
        t2.multiply( k4.getSubtract( k5 ) );
         
        // Power(E,2*I*(k3*x3 + k4*x4))*(-k2 + k3)*(k3 + k4)*(k4 - k5)
        MutableComplex t3 = new MutableComplex();
        t3.setValue( k3.getMultiply( x3 ) );
        t3.add( k4.getMultiply( x4 ) );
        t3.multiply( 2 );
        t3.multiply( Complex.I );
        t3.exp();
        t3.multiply( k2.getMultiply( -1 ).getAdd( k3 ) );
        t3.multiply( k3.getAdd( k4 ) );
        t3.multiply( k4.getSubtract( k5 ) );
        
        // Power(E,2*I*(k3 + k4)*x3)*(-k2 + k3)*(k3 - k4)*(k4 + k5)
        MutableComplex t4 = new MutableComplex();
        t4.setValue( 2 );
        t4.multiply( Complex.I );
        t4.multiply( k3.getAdd( k4 ) );
        t4.multiply( x3 );
        t4.exp();
        t4.multiply( k2.getMultiply( -1 ).getAdd( k3 ) );
        t4.multiply( k3.getSubtract( k4 ) );
        t4.multiply( k4.getAdd( k5 ) );
        
        // Power(E,2*I*(k3*x2 + k4*x3))*(k2 + k3)*(k3 + k4)*(k4 + k5)
        MutableComplex t5 = new MutableComplex();
        t5.setValue( k3.getMultiply( x2 ) );
        t5.add( k4.getMultiply( x3 ) );
        t5.multiply( 2 );
        t5.multiply( Complex.I );
        t5.exp();
        t5.multiply( k2.getAdd( k3 ) );
        t5.multiply( k3.getAdd( k4 ) );
        t5.multiply( k4.getAdd( k5 ) );
        
        if ( _C == null ) {
            _C = new MutableComplex();
        }
        _C.setValue( t2 );
        _C.subtract( t3 );
        _C.subtract( t4 );
        _C.add( t5 );
        _C.multiply( t1 );
        _C.divide( denominator);
    }
    
    /*
     * D = 
     * (2*Power(E,I*((k1 + k2)*x1 + 2*k2*x2))*k1*
     *  (-(Power(E,2*I*(k3*x2 + k4*x4))*(-k2 + k3)*(k3 - k4)*(k4 - k5)) +
     *   Power(E,2*I*(k3*x3 + k4*x4))*(k2 + k3)*(k3 + k4)*(k4 - k5) + 
     *   Power(E,2*I*(k3 + k4)*x3)*(k2 + k3)*(k3 - k4)*(k4 + k5) -
     *   Power(E,2*I*(k3*x2 + k4*x3))*(-k2 + k3)*(k3 + k4)*(k4 + k5))) /
     * denominator
     * 
     * NOTE: The above equation is slightly different than the Mathematic
     * output that I was provided. Mathematic generated a denominator for
     * D that differed from the denominator of all the other coefficients.
     * The D denominator was multiplied by -1. I multiplied the D numerator 
     * by -1 so that the same denominator is identical for all coefficients.
     */
    private void updateD( double x1, double x2, double x3, double x4,
            Complex k1, Complex k2, Complex k3, Complex k4, Complex k5, Complex denominator ) {
        
        // 2*Power(E,I*((k1 + k2)*x1 + 2*k2*x2))*k1
        MutableComplex t1 = new MutableComplex();
        t1.setValue( k1.getAdd( k2 ) );
        t1.multiply( x1 );
        t1.add( k2.getMultiply( 2 * x2 ) );
        t1.multiply( Complex.I );
        t1.exp();
        t1.multiply( 2 );
        t1.multiply( k1 );
        
        // -(Power(E,2*I*(k3*x2 + k4*x4))*(-k2 + k3)*(k3 - k4)*(k4 - k5))
        MutableComplex t2 = new MutableComplex();
        t2.setValue( k3.getMultiply( x2 ) );
        t2.add( k4.getMultiply( x4 ) );
        t2.multiply( 2 );
        t2.multiply( Complex.I );
        t2.exp();
        t2.multiply( k2.getMultiply( -1 ).getAdd( k3 ) );
        t2.multiply( k3.getSubtract( k4 ) );
        t2.multiply( k4.getSubtract( k5 ) );
        t2.multiply( -1 );
        
        // Power(E,2*I*(k3*x3 + k4*x4))*(k2 + k3)*(k3 + k4)*(k4 - k5)
        MutableComplex t3 = new MutableComplex();
        t3.setValue( k3.getMultiply( x3 ) );
        t3.add( k4.getMultiply( x4 ) );
        t3.multiply( 2 );
        t3.multiply( Complex.I );
        t3.exp();
        t3.multiply( k2.getAdd( k3 ) );
        t3.multiply( k3.getAdd( k4 ) );
        t3.multiply( k4.getSubtract( k5 ) );
        
        // Power(E,2*I*(k3 + k4)*x3)*(k2 + k3)*(k3 - k4)*(k4 + k5)
        MutableComplex t4 = new MutableComplex();
        t4.setValue( 2 );
        t4.multiply( Complex.I );
        t4.multiply( k3.getAdd( k4 ) );
        t4.multiply( x3 );
        t4.exp();
        t4.multiply( k2.getAdd( k3 ) );
        t4.multiply( k3.getSubtract( k4 ) );
        t4.multiply( k4.getAdd( k5 ) );
        
        // Power(E,2*I*(k3*x2 + k4*x3))*(-k2 + k3)*(k3 + k4)*(k4 + k5)))
        MutableComplex t5 = new MutableComplex();
        t5.setValue( k3.getMultiply( x2 ) );
        t5.add( k4.getMultiply( x3 ) );
        t5.multiply( 2 );
        t5.multiply( Complex.I );
        t5.exp();
        t5.multiply( k2.getMultiply( -1 ).getAdd( k3 ) );
        t5.multiply( k3.getAdd( k4 ) );
        t5.multiply( k4.getAdd( k5 ) );
        
        if ( _D == null ) {
            _D = new MutableComplex();
        }
        _D.setValue( t2 );
        _D.add( t3 );
        _D.add( t4 );
        _D.subtract( t5 );
        _D.multiply( t1 );
        _D.divide( denominator );
    }
    
    /*
     * F = 
     * (4*Power(E,I*((k1 + k2)*x1 + (k2 + k3)*x2))*k1*k2*
     *   (-(Power(E,2*I*k4*x4)*(-k3 + k4)*(k4 - k5)) + 
     *    Power(E,2*I*k4*x3)*(k3 + k4)*(k4 + k5))) /
     * denominator
     */
    private void updateF( double x1, double x2, double x3, double x4,
            Complex k1, Complex k2, Complex k3, Complex k4, Complex k5, Complex denominator ) {
        
        // 4*Power(E,I*((k1 + k2)*x1 + (k2 + k3)*x2))*k1*k2
        MutableComplex t1 = new MutableComplex();
        t1.setValue( k1.getAdd( k2 ).getMultiply( x1 ) );
        t1.add( k2.getAdd( k3 ).getMultiply( x2 ) );
        t1.multiply( Complex.I );
        t1.exp();
        t1.multiply( 4 );
        t1.multiply( k1 );
        t1.multiply( k2 );
           
        // -(Power(E,2*I*k4*x4)*(-k3 + k4)*(k4 - k5))
        MutableComplex t2 = new MutableComplex();
        t2.setValue( 2 );
        t2.multiply( Complex.I );
        t2.multiply( k4 );
        t2.multiply( x4 );
        t2.exp();
        t2.multiply( k3.getMultiply( -1 ).getAdd( k4 ) );
        t2.multiply( k4.getSubtract( k5 ) );
        t2.multiply( -1 );
        
        // Power(E,2*I*k4*x3)*(k3 + k4)*(k4 + k5)
        MutableComplex t3 = new MutableComplex();
        t3.setValue( 2 );
        t3.multiply( Complex.I );
        t3.multiply( k4 );
        t3.multiply( x3 );
        t3.exp();
        t3.multiply( k3.getAdd( k4 ) );
        t3.multiply( k4.getAdd( k5 ) );
        
        if ( _F == null ) {
            _F = new MutableComplex();
        }
        _F.setValue( t2 );
        _F.add( t3 );
        _F.multiply( t1 );
        _F.divide( denominator );
    }
    
    /*
     * G =
     * (4*Power(E,I*(k1*x1 + k2*(x1 + x2) + k3*(x2 + 2*x3)))*k1*k2*
     * (Power(E,2*I*k4*x4)*(k3 + k4)*(k4 - k5) - 
     * Power(E,2*I*k4*x3)*(-k3 + k4)*(k4 + k5))) /
     * denominator
     */
    private void updateG( double x1, double x2, double x3, double x4,
            Complex k1, Complex k2, Complex k3, Complex k4, Complex k5, Complex denominator ) {
        
        // 4*Power(E,I*(k1*x1 + k2*(x1 + x2) + k3*(x2 + 2*x3)))*k1*k2
        MutableComplex t1 = new MutableComplex();
        t1.setValue( k1.getMultiply( x1 ) );
        t1.add( k2.getMultiply( x1 + x2 ) );
        t1.add( k3.getMultiply( x2 + ( 2 * x3 ) ) );
        t1.multiply( Complex.I );
        t1.exp();
        t1.multiply( 4 );
        t1.multiply( k1 );
        t1.multiply( k2 );
        
        // Power(E,2*I*k4*x4)*(k3 + k4)*(k4 - k5)
        MutableComplex t2 = new MutableComplex();
        t2.setValue( 2 );
        t2.multiply( Complex.I );
        t2.multiply( k4 );
        t2.multiply( x4 );
        t2.exp();
        t2.multiply( k3.getAdd( k4 ) );
        t2.multiply( k4.getSubtract( k5 ) );
        
        // Power(E,2*I*k4*x3)*(-k3 + k4)*(k4 + k5)
        MutableComplex t3 = new MutableComplex();
        t3.setValue( 2 );
        t3.multiply( Complex.I );
        t3.multiply( k4 );
        t3.multiply( x3 );
        t3.exp();
        t3.multiply( k3.getMultiply( -1 ).getAdd( k4 ) );
        t3.multiply( k4.getAdd( k5 ) );
        
        if ( _G == null ) {
            _G = new MutableComplex();
        }
        _G.setValue( t2 );
        _G.subtract( t3 );
        _G.multiply( t1 );
        _G.divide( denominator );
    }
    
    /*
     * H =
     * (8*Power(E,I*(k1*x1 + k3*x2 + k2*(x1 + x2) + (k3 + k4)*x3))*k1*k2*k3*(k4 + k5)) /
     * denominator
     */
    private void updateH( double x1, double x2, double x3, double x4,
            Complex k1, Complex k2, Complex k3, Complex k4, Complex k5, Complex denominator ) {
        if ( _H == null ) {
            _H = new MutableComplex();
        }
        _H.setValue( k1.getMultiply( x1 ) );
        _H.add( k3.getMultiply( x2 ) );
        _H.add( k2.getMultiply( x1 + x2 ) );
        _H.add( k3.getAdd( k4 ).getMultiply( x3 ) );
        _H.multiply( Complex.I );
        _H.exp();
        _H.multiply( 8 );
        _H.multiply( k1 );
        _H.multiply( k2 );
        _H.multiply( k3 );
        _H.multiply( k4.getAdd( k5 ) );
        _H.divide( denominator );
    }
    
    /*
     * I = 
     * (8*Power(E,I*(k1*x1 + k3*x2 + k2*(x1 + x2) + k3*x3 + k4*x3 + 2*k4*x4))*k1*k2*k3*(k4 - k5)) /
     * denominator
     */
    private void updateI( double x1, double x2, double x3, double x4,
            Complex k1, Complex k2, Complex k3, Complex k4, Complex k5, Complex denominator ) {
        if ( _I == null ) {
            _I = new MutableComplex();
        }
        _I.setValue( k1.getMultiply( x1 ) );
        _I.add( k3.getMultiply( x2 ) );
        _I.add( k2.getMultiply( x1 + x2 ) );
        _I.add( k3.getMultiply( x3 ) );
        _I.add( k4.getMultiply( x3 ) );
        _I.add( k4.getMultiply( 2 * x4 ) );
        _I.multiply( Complex.I );
        _I.exp();
        _I.multiply( 8 );
        _I.multiply( k1 );
        _I.multiply( k2 );
        _I.multiply( k3 );
        _I.multiply( k4.getSubtract( k5 ) );
        _I.divide( denominator );
    }
    
    /*
     * J =
     * (16*Power(E,I*(k1*x1 + k3*x2 + k2*(x1 + x2) + k3*x3 + k4*x3 + k4*x4 - k5*x4))*k1*k2*k3*k4) /
     * denominator
     */
    private void updateJ( double x1, double x2, double x3, double x4,
            Complex k1, Complex k2, Complex k3, Complex k4, Complex k5, Complex denominator ) {
        if ( _J == null ) {
            _J = new MutableComplex();
        }
        _J.setValue( k1.getMultiply( x1 ) );
        _J.add( k3.getMultiply( x2 ) );
        _J.add( k2.getMultiply( x1 + x2 ) );
        _J.add( k3.getMultiply( x3 ) );
        _J.add( k4.getMultiply( x3 ) );
        _J.add( k4.getMultiply( x4 ) );
        _J.subtract( k5.getMultiply( x4 ) );
        _J.multiply( Complex.I );
        _J.exp();
        _J.multiply( 16 );
        _J.multiply( k1 );
        _J.multiply( k2 );
        _J.multiply( k3 );
        _J.multiply( k4 );
        _J.divide( denominator );
    }
    
    /*
     * All coeffiecients have this denominator:
     * (-(Power(E,2*I*((k2 + k3)*x2 + k4*x4))*(-k1 + k2)*(k2 - k3)*(k3 - k4)*(k4 - k5)) +
     * Power(E,2*I*(k2*x1 + k3*x2 + k4*x4))*(k1 + k2)*(k2 + k3)*(k3 - k4)*(k4 - k5) +
     * Power(E,2*I*(k2*x1 + k3*x3 + k4*x4))*(k1 + k2)*(k2 - k3)*(k3 + k4)*(k4 - k5) -
     * Power(E,2*I*(k2*x2 + k3*x3 + k4*x4))*(-k1 + k2)*(k2 + k3)*(k3 + k4)*(k4 - k5) +
     * Power(E,2*I*(k2*x1 + (k3 + k4)*x3))*(k1 + k2)*(k2 - k3)*(k3 - k4)*(k4 + k5) -
     * Power(E,2*I*(k2*x2 + (k3 + k4)*x3))*(-k1 + k2)*(k2 + k3)*(k3 - k4)*(k4 + k5) -
     * Power(E,2*I*((k2 + k3)*x2 + k4*x3))*(-k1 + k2)*(k2 - k3)*(k3 + k4)*(k4 + k5) +
     * Power(E,2*I*(k2*x1 + k3*x2 + k4*x3))*(k1 + k2)*(k2 + k3)*(k3 + k4)*(k4 + k5))
     */
    private static Complex getDenominator( 
            double x1, double x2, double x3, double x4,
            Complex k1, Complex k2, Complex k3, Complex k4, Complex k5 ) {

        // -(Power(E,2*I*((k2 + k3)*x2 + k4*x4))*(-k1 + k2)*(k2 - k3)*(k3 - k4)*(k4 - k5))
        MutableComplex t1 = new MutableComplex();
        t1.setValue( k2.getAdd( k3 ) );
        t1.multiply( x2 );
        t1.add( k4.getMultiply( x4 ) );
        t1.multiply( 2 );
        t1.multiply( Complex.I );
        t1.exp();
        t1.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
        t1.multiply( k2.getSubtract( k3 ) );
        t1.multiply( k3.getSubtract( k4 ) );
        t1.multiply( k4.getSubtract( k5 ) );
        t1.multiply( -1 );
       
        // Power(E,2*I*(k2*x1 + k3*x2 + k4*x4))*(k1 + k2)*(k2 + k3)*(k3 - k4)*(k4 - k5)
        MutableComplex t2 = new MutableComplex();
        t2.setValue( k2.getMultiply( x1 ) );
        t2.add( k3.getMultiply( x2 ) );
        t2.add( k4.getMultiply( x4 ) );
        t2.multiply( 2 );
        t2.multiply( Complex.I );
        t2.exp();
        t2.multiply( k1.getAdd( k2 ) );
        t2.multiply( k2.getAdd( k3 ) );
        t2.multiply( k3.getSubtract( k4 ) );
        t2.multiply( k4.getSubtract( k5 ) );
        
        // Power(E,2*I*(k2*x1 + k3*x3 + k4*x4))*(k1 + k2)*(k2 - k3)*(k3 + k4)*(k4 - k5)
        MutableComplex t3 = new MutableComplex();
        t3.setValue( k2.getMultiply( x1 ) );
        t3.add( k3.getMultiply( x3 ) );
        t3.add( k4.getMultiply( x4 ) );
        t3.multiply( 2 );
        t3.multiply( Complex.I );
        t3.exp();
        t3.multiply( k1.getAdd( k2 ) );
        t3.multiply( k2.getSubtract( k3 ) );
        t3.multiply( k3.getAdd( k4 ) );
        t3.multiply( k4.getSubtract( k5 ) );
        
        // Power(E,2*I*(k2*x2 + k3*x3 + k4*x4))*(-k1 + k2)*(k2 + k3)*(k3 + k4)*(k4 - k5)
        MutableComplex t4 = new MutableComplex();
        t4.setValue( k2.getMultiply( x2 ) );
        t4.add( k3.getMultiply( x3 ) );
        t4.add( k4.getMultiply( x4 ) );
        t4.multiply( 2 );
        t4.multiply( Complex.I );
        t4.exp();
        t4.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
        t4.multiply( k2.getAdd( k3 ) );
        t4.multiply( k3.getAdd( k4 ) );
        t4.multiply( k4.getSubtract( k5 ) );
        
        // Power(E,2*I*(k2*x1 + (k3 + k4)*x3))*(k1 + k2)*(k2 - k3)*(k3 - k4)*(k4 + k5)
        MutableComplex t5 = new MutableComplex();
        t5.setValue( k2.getMultiply( x1 ) );
        t5.add( k3.getAdd( k4 ).getMultiply( x3 ) );
        t5.multiply( 2 );
        t5.multiply( Complex.I );
        t5.exp();
        t5.multiply( k1.getAdd( k2 ) );
        t5.multiply( k2.getSubtract( k3 ) );
        t5.multiply( k3.getSubtract( k4 ) );
        t5.multiply( k4.getAdd( k5 ) );
        
        // Power(E,2*I*(k2*x2 + (k3 + k4)*x3))*(-k1 + k2)*(k2 + k3)*(k3 - k4)*(k4 + k5)
        MutableComplex t6 = new MutableComplex();
        t6.setValue( k2.getMultiply( x2 ) );
        t6.add( k3.getAdd( k4 ).getMultiply( x3 ) );
        t6.multiply( 2 );
        t6.multiply( Complex.I );
        t6.exp();
        t6.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
        t6.multiply( k2.getAdd( k3 ) );
        t6.multiply( k3.getSubtract( k4 ) );
        t6.multiply( k4.getAdd( k5 ) );
        
        // Power(E,2*I*((k2 + k3)*x2 + k4*x3))*(-k1 + k2)*(k2 - k3)*(k3 + k4)*(k4 + k5)
        MutableComplex t7 = new MutableComplex();
        t7.setValue( k2.getAdd( k3 ).getMultiply( x2 ) );
        t7.add( k4.getMultiply( x3 ) );
        t7.multiply( 2 );
        t7.multiply( Complex.I );
        t7.exp();
        t7.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
        t7.multiply( k2.getSubtract( k3 ) );
        t7.multiply( k3.getAdd( k4 ) );
        t7.multiply( k4.getAdd( k5 ) );
        
        // Power(E,2*I*(k2*x1 + k3*x2 + k4*x3))*(k1 + k2)*(k2 + k3)*(k3 + k4)*(k4 + k5)
        MutableComplex t8 = new MutableComplex();
        t8.setValue( k2.getMultiply( x1 ) );
        t8.add( k3.getMultiply( x2 ) );
        t8.add( k4.getMultiply( x3 ) );
        t8.multiply( 2 );
        t8.multiply( Complex.I );
        t8.exp();
        t8.multiply( k1.getAdd( k2 ) );
        t8.multiply( k2.getAdd( k3 ) );
        t8.multiply( k3.getAdd( k4 ) );
        t8.multiply( k4.getAdd( k5 ) );
        
        MutableComplex denominator = new MutableComplex();
        denominator.setValue( t1 );
        denominator.add( t2 );
        denominator.add( t3 );
        denominator.subtract( t4 );
        denominator.add( t5 );
        denominator.subtract( t6 );
        denominator.subtract( t7 );
        denominator.add( t8 );
        return denominator;
    }
    
    /**
     * Gets the coefficient B, the amplitude of the reflected wave.
     * 
     * @return Complex
     */
    protected Complex getB() {
        if ( _B == null ) {
            solve( 0, 0 );
        }
        return _B;
    }
}