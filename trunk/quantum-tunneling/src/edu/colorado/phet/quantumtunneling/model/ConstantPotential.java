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


/**
 * ConstantPotential describes a potential space that has constant potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConstantPotential extends AbstractPotentialSpace {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_ENERGY = 5;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public ConstantPotential() {
        super( 1 /* numberOfRegions */ );
        setRegion( 0, getMinPosition(), getMaxPosition(), DEFAULT_ENERGY );
    }
    
    /**
     * Copy constructor.
     * 
     * @param step
     */
    public ConstantPotential( final ConstantPotential step ) {
        super( step );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the potential energy.
     * 
     * @param energy
     */
    public void setEnergy( final double energy ) {
        setEnergy( 0, energy );
    }
    
    /**
     * Gets the potential energy.
     * 
     * @return energy
     */
    public double getEnergy() {
        return getEnergy( 0 );
    }
}
