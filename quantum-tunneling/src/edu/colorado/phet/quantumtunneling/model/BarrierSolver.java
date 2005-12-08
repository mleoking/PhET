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

import edu.colorado.phet.quantumtunneling.util.Complex;


/**
 * BarrierSolver
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarrierSolver extends AbstractSolver {
    
    /**
     * Constructor.
     * 
     * @param te
     * @param pe
     */
    public BarrierSolver( TotalEnergy te, BarrierPotential pe ) {
        super( te, pe );
        assert( pe.getNumberOfBarriers() == 1 );
    }

    /**
     * Solves the wave function.
     * 
     * @param x position, in nm
     * @param t time, in fs
     */
    public Complex solve( final double x, final double t ) {
        int numberOfBarriers = ((BarrierPotential)getPotentialEnergy()).getNumberOfBarriers();
        if ( numberOfBarriers == 1 ) {
            return solveSingle( x, t );
        }
        else if ( numberOfBarriers == 2 ) {
            return solveDouble( x, t );
        }
        else {
            throw new IllegalStateException( "no solution for " + numberOfBarriers + " barriers" );
        }
    }
    
    /*
     * Solves the wave function for a single barrier.
     */
    private Complex solveSingle( final double x, final double t ) {
        return new Complex( -1, 0 ); //XXX
    }
    
    /*
     * Solves the wave function for a double barrier.
     */
    private Complex solveDouble( final double x, final double t ) {
        return new Complex( 1, 0 ); //XXX
    }
}
