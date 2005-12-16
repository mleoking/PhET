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
 * IWave
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface IWave {

    public void setTotalEnergy( TotalEnergy te );
    
    public TotalEnergy getTotalEnergy();
    
    public void setPotentialEnergy( AbstractPotential pe );
    
    public AbstractPotential getPotentialEnergy();
    
    public void setDirection( Direction direction );
    
    public Direction getDirection();
    
    public WaveFunctionSolution solveWaveFunction( double x );
}
