/* Copyright 2006, University of Colorado */

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
    
    private static final double[] SCALING_COEFFICIENTS = 
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
     * This is limited by the number of scaling coefficients.
     * There is no analytic formula for these coefficients, and they
     * have been pre-calculated using Mathematica.
     * 
     * @return the maximum number of eigenstates
     */
    public int getMaxEigenstates() {
        return SCALING_COEFFICIENTS.length;
    }
    
    /**
     * Gets the scaling coefficient used for a specified eigenstate index.
     * @param n eigenstate subscript, n=1,2,3,...
     * @return double
     */
    public static double getScalingCoefficient( final int n ) {
        if ( n < 1 || n > SCALING_COEFFICIENTS.length ) {
            throw new IndexOutOfBoundsException( "no scaling coefficient for n=" + n );
        }
        return SCALING_COEFFICIENTS[ n - 1 ];
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractCoulombSolver implementation
    //----------------------------------------------------------------------------
    
    /*
     * Calculates the scaled wave function at a position.
     * Scaled means that the maximum amplitude is 1.
     * 
     * @param n eigenstate subscript, n=1,2,3,...
     * @param x position, in nm
     * @return scaled value of psi
     */
    protected double psiScaled( final int n, final double x ) {
        final double A = getScalingCoefficient( n );
        return ( Math.sqrt( BSConstants.ELECTRON_MASS / getMass() ) * A * x * psi( n, x ) );
    }
}
