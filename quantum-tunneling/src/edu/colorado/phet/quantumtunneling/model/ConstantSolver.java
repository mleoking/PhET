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

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;


/**
 * ConstantSolver is a closed-form solution to the Schrodinger equation
 * for constant potentials.  A step has 1 regions, region1.
 * The closed-form solution for each regions is:
 * <code>
 * region1: psi(x,t) = e^(-i*E*t/h) * e^(i*k1*x)
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
 * </code>
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConstantSolver extends AbstractSolver implements Observer {
    
    /**
     * Constructor.
     * 
     * @param te
     * @param pe
     */
    public ConstantSolver( TotalEnergy te, ConstantPotential pe ) {
        super( te, pe );
    }
    
    /**
     * Solves the wave function.
     * 
     * @param x position, in nm
     * @param t time, in fs
     */
    public Complex solve( final double x, final double t ) {       
        final double E = getTotalEnergy().getEnergy(); 
        Complex term1 = commonTerm1( x, 0 /* k subscript */ );
        Complex term3 = commonTerm3( t, E );
        Complex result = term1.getMultiply( term3 );
        return result;
    }
}
