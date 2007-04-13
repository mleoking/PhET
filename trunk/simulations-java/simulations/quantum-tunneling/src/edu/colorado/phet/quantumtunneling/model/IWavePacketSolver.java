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

import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

/**
 * IWavePacketSolver is the interface implemented by all classes that
 * solve the Schrodinger equation for wave packets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface IWavePacketSolver {

    /**
     * Gets the position (x-axis) values were used to compute the wave function solution.
     * 
     * @return double[]
     */
    public double[] getPositionValues();

    /**
     * Gets the wave function (y-axis) values.
     * 
     * @return LightweightComplex[]
     */
    public LightweightComplex[] getWaveFunctionValues();

    /**
     * Sets the dx (position spacing) between sample points.
     * 
     * @param dx
     */
    public void setDx( double dx );

    /**
     * Updates the internal state of the solver.
     */
    public void update();

    /**
     * Propagates the solution one step.
     */
    public void propagate();
}