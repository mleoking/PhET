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

import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;


/**
 * StepSolver is a closed-form solution to the Schrodinger equation
 * for step potentials.  A step has 2 regions, region1 and region2.
 * The closed-form solution for each regions is:
 * <code>
 * Region1: psi(x,t) = e^(-i*E*t/h) * ( e^(i*k1*x) + ( B*e^(-i*k1*x) ) )
 * Region2: psi(x,t) = e^(-i*E*t/h) * ( C*e^(i*k2*x) )
 * 
 * where:
 * x = position
 * t = time
 * e = Euler's number
 * i = sqrt(-1)
 * E = total energy
 * Vn = potential energy of region n
 * kn = wave number of region n
 * h = Planck's constant
 * x1 = position of boundary between Region1 and Region2
 * B = ( e^(2*i*k1*x1) * (k1-k2) ) / (k1+k2)
 * C = ( 2 * e^(i*(k1-k2)*x1) * k1 ) / (k1+k2)
 *</code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class StepSolver extends AbstractSolver {

    /**
     * Constructor.
     * 
     * @param te
     * @param pe
     */
    public StepSolver( TotalEnergy te, StepPotential pe ) {
        super( te, pe );
    }
    
    /**
     * Solves the wave function.
     * 
     * @param x position, in nm
     * @param t time, in fs
     */
    public Complex solve( final double x, final double t ) {
        Complex result = null;
        
        final double E = getTotalEnergy().getEnergy();
        final double x1 = getPotentialEnergy().getEnd( 0 );  // boundary between regions
        Complex k1 = getK( 0 );
        Complex k2 = getK( 1 );
        
        int regionIndex = getPotentialEnergy().getRegionIndexAt( x );
        if ( regionIndex == 0 ) {
            
            Complex term1 = commonTerm1( x, 0 ); // e^(ikx)
            Complex term2 = commonTerm2( x, 0 ); // e^(-ikx)
            Complex term3 = commonTerm3( t, E ); // e^(-i*E*t/h)
            
            MutableComplex B = new MutableComplex( 0, 1 ); // i
            B.multiply( 2 * x1 );
            B.multiply( k1 );
            B.exp();
            B.multiply( k1.getSubtract( k2 ) );
            B.divide( k1.getAdd( k2 ) );
            
            Complex rightMoving = term1.getMultiply( term3 );
            Complex leftMoving = B.getMultiply( term2 ).getMultiply( term3 );
            
            result = rightMoving.getAdd( leftMoving );  
        }
        else if ( regionIndex == 1 ) { 

            Complex term1 = commonTerm1( x, 1 ); // e^(ikx)
            Complex term3 = commonTerm3( t, E ); // e^(-i*E*t/h)
            
            MutableComplex C = new MutableComplex( 0, 1 ); // i
            C.multiply( x1 );
            C.multiply( k1.getSubtract( k2 ) );
            C.exp();
            C.multiply( 2 );
            C.divide( k1.getAdd( k2 ) );
            
            Complex rightMoving = C.getMultiply( term1 );
            result = rightMoving.getMultiply( term3 );
        }
        else {
            // outside of the potential energy space
        }
        
        return result;
    }
}
