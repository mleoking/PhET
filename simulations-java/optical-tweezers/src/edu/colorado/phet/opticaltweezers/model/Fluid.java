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

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;

/**
 * Fluid
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Fluid extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String PROPERTY_VISCOSITY = "viscosity";
    private static final String PROPERTY_TEMPERATURE = "temperature";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final double _width; // nm
    private double _viscosity; //XXX units?
    private double _temperature; //XXX units?
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position position at the center of the fluid "stream"
     * @param orientation direction that the fluid stream flows in (radians)
     * @param width width of the fluid stream
     * @param speed speed of the fluid stream
     * @param viscosity
     * @param temperature
     */
    public Fluid( Point2D position, double orientation, double width, double speed, double viscosity, double temperature ) {
        super( position, orientation, speed );
        _width = width;
        _viscosity = viscosity;
        _temperature = temperature;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    public double getWidth() {
        return _width;
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
