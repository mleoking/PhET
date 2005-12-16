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

import java.util.Observer;

import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.util.Complex;


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
        final double E = getTotalEnergy().getEnergy();
        if ( E < getPotentialEnergy().getEnergy( 0 ) ) {
            result = new WaveFunctionSolution( x, t );
        }
        else {
            Complex k1 = getK( 0 );
            Complex term1 = commonTerm1( x, k1 ); // e^(ikx)
            Complex term3 = commonTerm3( t, E ); // e^(-iEt/h)
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
