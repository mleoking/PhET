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
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the scaling coefficient used for a specified eigenstate index.
     * @param n eigenstate subscript, n=1,2,3,...
     * @param mass particle mass
     * @return double
     */
    public static double getScalingCoefficient( int n, double mass ) {
        if ( n < 1 ) {
            throw new IndexOutOfBoundsException( "Coulomb eigenstate subscripts start at 1: " + n );
        }
        final double a = get_a( mass );
        return ( SQRT_PI * Math.pow( ( n * a ), 1.5 ) );
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
        assert( n >= 1 );
        return ( getScalingCoefficient( n, getMass() ) * psi( n, x ) );
    }
}
