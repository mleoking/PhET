/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;


public class Fluid extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String PROPERTY_VELOCITY = "velocity";
    private static final String PROPERTY_VISCOSITY = "viscosity";
    private static final String PROPERTY_TEMPERATURE = "temperature";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _velocity; //XXX units?
    private double _viscosity; //XXX units?
    private double _temperature; //XXX units?
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Fluid( double velocity, double viscosity, double temperature ) {
        super();
        _velocity = velocity;
        _viscosity = viscosity;
        _temperature = temperature;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public double getVelocity() {
        return _velocity;
    }

    public void setVelocity( double velocity ) {
        if ( velocity != _velocity ) {
            _velocity = velocity;
            notifyObservers( PROPERTY_VELOCITY );
        }
    }

    public double getViscosity() {
        return _viscosity;
    }

    public void setViscosity( double viscosity ) {
        if ( viscosity != _viscosity ) {
            _viscosity = viscosity;
            notifyObservers( PROPERTY_VISCOSITY );
        }
    }
    
    public double getTemperature() {
        return _temperature;
    }

    public void setTemperature( double temperature ) {
        if ( temperature != _temperature ) {
            _temperature = temperature;
            notifyObservers( PROPERTY_TEMPERATURE );
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
