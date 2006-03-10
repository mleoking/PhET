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
 * TotalEnergy is the model of total energy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TotalEnergy extends QTObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _energy;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a total energy with value zero.
     */
    public TotalEnergy() {
        this( 0 );
    }
    
    /**
     * Constructs a total energy with a specified value.
     * 
     * @param energy
     */
    public TotalEnergy( final double energy ) {
        _energy = energy;
    }
    
    /**
     * Copy constructor.
     * 
     * @param totalEnergy
     */
    public TotalEnergy( final TotalEnergy totalEnergy ) {
        _energy = totalEnergy.getEnergy();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the energy.
     * @param energy
     */
    public void setEnergy( final double energy ) {
        if ( energy != _energy ) {
            _energy = energy;
            notifyObservers();
        }
    }
    
    /**
     * Gets the energy.
     * @return
     */
    public double getEnergy() {
        return _energy;
    }
}
