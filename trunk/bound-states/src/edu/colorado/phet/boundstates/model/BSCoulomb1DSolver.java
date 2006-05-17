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

import edu.colorado.phet.boundstates.BSConstants;

/**
 * BSCoulomb1DSolver is the eigenstate solver and wave function solver 
 * for potentials composed of 1D Coulomb wells.
 * This is an implementation of an analytic solution specified by Sam McKagan.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DSolver extends BSAbstractCoulombSolver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double[] NORMALIZATION_COEFFICIENTS = 
        { 1.10851, -1.86636,  2.55958, -3.21387,  3.84064,
         -4.44633,  5.03504, -5.60960,  6.17208, -6.72406 };

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param potential
     * @param particle
     */
    public BSCoulomb1DSolver( BSAbstractPotential potential, BSParticle particle ) {
        super( potential, particle );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the maximum number of eigenstates that can be calculated.
     * This is limited by the number of normalization coefficients.
     * There is no analytic formula for these coefficients, and they
     * have been pre-calculated using Mathematica.
     * 
     * @return the maximum number of eigenstates
     */
    public int getMaxEigenstates() {
        return NORMALIZATION_COEFFICIENTS.length;
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractCoulombSolver implementation
    //----------------------------------------------------------------------------
    
    /*
     * Calculates the normalized wave function at a position.
     * 
     * @param n eigenstate subscript, n=1,2,3,...
     * @param x position, in nm
     * @param a common constant
     * @return normalized value of psi
     */
    protected double psiNormalized( final int n, final double x, final double a ) {
        if ( n < 1 || n > NORMALIZATION_COEFFICIENTS.length ) {
            throw new IndexOutOfBoundsException( "no normalization coefficient for n=" + n );
        }
        final double A = NORMALIZATION_COEFFICIENTS[ n - 1 ];
        return ( ( BSConstants.ELECTRON_MASS / getMass() ) * A * x * psi( n, x ) );
    }
}
