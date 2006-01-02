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


/**
 * SchrodingerSolver
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SchrodingerSolver implements IWaveFunctionSolver {
    
    private WavePacket _wavePacket;

    public SchrodingerSolver( WavePacket wavePacket ) {
        _wavePacket = wavePacket;
    }

    /**
     * Solves the wave function.
     * 
     * @param x position, in nm
     * @param t time, in fs
     * @return
     */
    public WaveFunctionSolution solve( double x, double t ) {
        // TODO Auto-generated method stub
        return new WaveFunctionSolution( x, t, new Complex(0,0), new Complex(0,0 ) );//XXX HACK
    }
    
    /**
     * Updates the internal state of the solver.
     */
    public void update() {
        System.out.println( "SchrodingerSolver.update" );//XXX
        //XX re-examine wave packet model
    }
}
