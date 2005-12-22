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

import edu.colorado.phet.quantumtunneling.enum.Direction;


/**
 * IWaveFunctionSolver
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface IWaveFunctionSolver {

    /**
     * Sets the direction of the wave.
     * 
     * @param direction
     */
    public void setDirection( Direction direction );
    
    /**
     * Solves the wave function.
     * 
     * @param x position, in nm
     * @param t time, in fs
     * @return
     */
    public abstract WaveFunctionSolution solve( final double x, final double t );
}
