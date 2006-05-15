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

/**
 * BSCoulomb3DSolver is the eigenstate solver and wave function solver 
 * for potentials composed of 3D Coulomb wells.
 * This is an implementation of an analytic solution specified by Sam McKagan.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb3DSolver extends BSAbstractCoulombSolver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double SQRT_PI = Math.sqrt( Math.PI );
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param potential
     * @param particle
     */
    public BSCoulomb3DSolver( BSAbstractPotential potential, BSParticle particle ) {
        super( potential, particle );
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
        assert( n >= 1 );
        return ( SQRT_PI * Math.pow( ( n * a ), 1.5 ) * psi( n, x ) );
    }
}
