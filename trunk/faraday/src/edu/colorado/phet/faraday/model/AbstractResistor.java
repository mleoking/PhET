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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;


/**
 * AbstractResistor is the abstract base class for all resistors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractResistor extends SimpleObservable implements ModelElement {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _resistance;
    private double _current;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param ohms the resistance, in Ohms
     */
    public AbstractResistor( double ohms ) {
        setResistance( ohms );
        setCurrent( 0.0 );
        setEnabled( false );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the resistance, in ohms.
     * 
     * @param resistance the resistance, in ohms
     * @throws IllegalArgumentException if resistance is not >= 0
     */
    public void setResistance( double resistance ) {
        if ( ! (resistance >= 0) ) {
            throw new IllegalArgumentException( "resistance must be >= 0: " + resistance );
        }
        if ( resistance != _resistance ) {
            _resistance = resistance;
            notifyObservers();
        }
    }
    
    /**
     * Gets the resistance, in ohms.
     * 
     * @return the resistance
     */
    public double getResistance() {
        return _resistance;
    }
    
    /**
     * Sets the current flowing through the resistor.
     * 
     * @param current the current, in ampheres
     */
    public void setCurrent( double current ) {
        if ( current != _current ) {
            _current = current;
            notifyObservers();
        }
    }
    
    /**
     * Gets the current flowing through the resistor.
     * 
     * @return the current, in ampheres
     */
    public double getCurrent() {
        return _current;
    }
    
    /**
     * Sets the state of the resistor.
     * If the resistor is enabled, it means that it is part of a circuit
     * and should respond to changes in current and resistance.
     * <p>
     * Disabling a resistor has the side-effect of setting its current to zero.
     * 
     * @param enabled true to enable, false to disable.
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            if ( enabled == false ) {
                _current = 0.0;
            }
            notifyObservers();
        }
    }
    
    /**
     * Gets the state of the resistor.  See setEnabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isEnabled() {
        return _enabled;
    }
}