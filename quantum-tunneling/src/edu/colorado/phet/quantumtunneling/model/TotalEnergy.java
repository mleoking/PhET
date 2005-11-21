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
 * TotalEnergy
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TotalEnergy extends QTObservable {

    private double _energy;
    
    public TotalEnergy( double energy ) {
        _energy = energy;
    }
    
    /**
     * Copy constructor.
     * 
     * @param totalEnergy
     */
    public TotalEnergy( TotalEnergy totalEnergy ) {
        _energy = totalEnergy.getEnergy();
    }
    
    public void setEnergy( double energy ) {
        if ( energy != _energy ) {
            _energy = energy;
            notifyObservers();
        }
    }
    
    public double getEnergy() {
        return _energy;
    }
}
