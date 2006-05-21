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

import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * ConstantPotential describes a potential space that has constant potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConstantPotential extends AbstractPotential {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public ConstantPotential() {
        this( 0 );
    }
    
    public ConstantPotential( double energy ) {
        super( 1 /* numberOfRegions*/ );
        setRegion( 0, getMinPosition(), getMaxPosition(), energy );
    }
    
    /**
     * Copy constructor.
     * 
     * @param constantPotential
     */
    public ConstantPotential( final ConstantPotential constantPotential ) {
        super( constantPotential );
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
