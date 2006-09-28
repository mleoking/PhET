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
 * ConstantSolver is a closed-form solution to the 
 * wave function equation for plane waves with constant potentials.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConstantSolver extends AbstractPlaneSolver {
    
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
    public ConstantSolver( TotalEnergy te, ConstantPotential pe, Direction direction ) {
        super( te, pe, direction );
    }
    
    //----------------------------------------------------------------------------
    // AbstractPlaneSolver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Gets the coefficient B, the amplitude of the reflected wave.
     * This coefficient is always zero for constant potentials.
     * 
     * @return Complex
     */
    public Complex getB() {
        return Complex.ZERO;
    }
    
    /**
     * Solves the wave function.
     * <p>
     * The closed-form solution is:
     * <code>
     * region1: psi(x,t) = e^(i*k1*x) * e^(-i*E*t/h)
     * </code>
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
            Complex k1 = getK( 0 );
            Complex term1 = commonTerm1( k1, x ); // e^(ikx)
            Complex term3 = commonTerm3( getTotalEnergy(), t ); // e^(-iEt/h)
            Complex incidentPart = term1.getMultiply( term3 );
            result = new WaveFunctionSolution( x, t, incidentPart );
        }
        return result;
    }

    /*
     * Updates the coeffiecients.
     */
    protected void updateCoefficients() {
        // constant solution has no coefficients      
    }
}
