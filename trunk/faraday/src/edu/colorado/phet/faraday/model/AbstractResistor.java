/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * AbstractResistor
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractResistor extends SimpleObservable {

    private double _resistance;
    
    /**
     * Sole constructor.
     * 
     * @param ohms the resistance, in Ohms
     */
    public AbstractResistor( double ohms ) {
        setResistance( ohms );
    }
    
    /**
     * Sets the resistance, in ohms.
     * 
     * @param resistance the resistance, in ohms
     */
    public void setResistance( double resistance ) {
        if ( resistance < 0 ) {
            throw new IllegalArgumentException( "resistance must be >= 0: " + resistance );
        }
        _resistance = resistance;
        notifyObservers();
    }
    
    /**
     * Gets the resistance, in ohms.
     * 
     * @return the resistance
     */
    public double getResistance() {
        return _resistance;
    }

}
