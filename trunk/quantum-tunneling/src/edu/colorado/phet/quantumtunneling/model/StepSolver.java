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

import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;


/**
 * StepSolver is a closed-form solution to the 
 * wave function equation for plane waves with step potentials.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class StepSolver extends AbstractSolver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // coefficients
    private MutableComplex _B, _C;
    
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
    public StepSolver( TotalEnergy te, StepPotential pe, Direction direction ) {
        super( te, pe, direction );
    }
    
    //----------------------------------------------------------------------------
    // AbstractSolver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Solves the wave function.
     * 
     * @param x position, in nm
     * @param t time, in fs
     */
    public WaveFunctionSolution solve( final double x, final double t ) {
        WaveFunctionSolution result = null;
        
        final double E = getTotalEnergy().getEnergy();
        
        if ( isLeftToRight() && E < getPotentialEnergy().getEnergy( 0 ) ) {
            result = new WaveFunctionSolution( x, t );
        }
        else if ( isRightToLeft() && E < getPotentialEnergy().getEnergy( 1 ) ) {
            result = new WaveFunctionSolution( x, t );
        }
        else {
            int regionIndex = getPotentialEnergy().getRegionIndexAt( x );
            if ( isRightToLeft() ) {
                regionIndex = getPotentialEnergy().getNumberOfRegions() - 1 - regionIndex;
            }
            if ( regionIndex == 0 ) {
                result = solveRegion1( x, t );
            }
            else if ( regionIndex == 1 ) {
                result = solveRegion2( x, t );
            }
            else {
                // outside of the potential energy space
            }
        }

        return result;
    }
    
    /* 
     * Region1: psi(x,t) = ( e^(i*k1*x) + ( B*e^(-i*k1*x) ) ) * e^(-i*E*t/h)
     */
    private WaveFunctionSolution solveRegion1( final double x, final double t ) {        
        final int regionIndex = 0;
        final double E = getTotalEnergy().getEnergy();
        Complex k1 = getK( 0 );
        Complex term1 = commonTerm1( x, k1 ); // e^(ikx)
        Complex term2 = commonTerm2( x, k1 ); // e^(-ikx)
        Complex term3 = commonTerm3( t, E ); // e^(-i*E*t/h)
        Complex incidentPart = term1.getMultiply( term3 );
        Complex reflectedPart = _B.getMultiply( term2 ).getMultiply( term3 );
        WaveFunctionSolution result = new WaveFunctionSolution( x, t, incidentPart, reflectedPart );       
        return result;
    }
    
    /* 
     * Region2: psi(x,t) = ( C*e^(i*k2*x) ) * e^(-i*E*t/h)
     */
    private WaveFunctionSolution solveRegion2( final double x, final double t ) {
        final double E = getTotalEnergy().getEnergy();
        Complex k2 = getK( 1 );
        Complex term1 = commonTerm1( x, k2 ); // e^(ikx)
        Complex term3 = commonTerm3( t, E ); // e^(-i*E*t/h)
        Complex incidentPart = _C.getMultiply( term1 ).getMultiply( term3 );
        WaveFunctionSolution result = new WaveFunctionSolution( x, t, incidentPart );
        return result;
    }
    
    /*
     * Updates the coeffiecients.
     */
    protected void updateCoefficients() {
        
        // boundary between regions
        final double x1 = getBoundary( 0, 1 );
        
        // k values
        Complex k1 = getK( 0 );
        Complex k2 = getK( 1 );
        
        // Common denominator
        Complex denominator = getDenominator( k1, k2 );
        
        // Coefficients
        updateB( x1, k1, k2, denominator );
        updateC( x1, k1, k2, denominator );
    }
    
    /*
     * B = (Power(E,2*i*k1*x1)*(k1 - k2)) / (k1 + k2)
     */
    private void updateB( double x1, Complex k1, Complex k2, Complex denominator ) {
        if ( _B == null ) {
            _B = new MutableComplex();
        }
        _B.setValue( Complex.I );
        _B.multiply( 2 * x1 );
        _B.multiply( k1 );
        _B.exp();
        _B.multiply( k1.getSubtract( k2 ) );
        _B.divide( denominator );
    }
    
    /*
     * C = (2*Power(E,i*(k1 - k2)*x1)*k1) / (k1 + k2)
     */
    private void updateC( double x1, Complex k1, Complex k2, Complex denominator ) {
        if ( _C == null ) {
            _C = new MutableComplex();
        }
        _C.setValue( Complex.I );
        _C.multiply( x1 );
        _C.multiply( k1.getSubtract( k2 ) );
        _C.exp();
        _C.multiply( 2 );
        _C.multiply( k1 );
        _C.divide( denominator );
    }
    
    /*
     * All coeffiecients have this denominator: (k1 + k2)
     */
    private static Complex getDenominator( Complex k1, Complex k2 ) {
        return k1.getAdd( k2 );
    }
}
