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
 * BSEigenstate is the immutable model of an eigenstate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSEigenstate {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int INDEX_UNDEFINED = -1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _subscript;
    private double _energy; // in eV
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param subscript
     * @param energy
     */
    public BSEigenstate( int subscript, double energy ) {
        _subscript = subscript;
        _energy = energy;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the subscript n, used to label En.
     * 
     * @return the subscript
     */
    public int getSubscript() {
        return _subscript;
    }
    
    /**
     * Gets the energy.
     * 
     * @return the energy, in eV
     */
    public double getEnergy() {
        return _energy;
    }
}
