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
import edu.colorado.phet.opticaltweezers.util.DoubleRange;

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
    
    private final DoubleRange _speedRange;
    private final DoubleRange _viscosityRange;
    private final DoubleRange _temperatureRange;
    
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
     * @param speedRange speed of the fluid stream
     * @param viscosityRange
     * @param temperatureRange
     */
    public Fluid( Point2D position, double orientation, double width, 
            DoubleRange speedRange, DoubleRange viscosityRange, DoubleRange temperatureRange ) {
        super( position, orientation, speedRange.getDefault() );
        
        _width = width;
        
        _speedRange = speedRange;
        _viscosityRange = viscosityRange;
        _temperatureRange = temperatureRange;
        
        _viscosity = viscosityRange.getDefault();
        _temperature = temperatureRange.getDefault();
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
    
    public DoubleRange getSpeedRange() {
        return _speedRange;
    }

    public DoubleRange getTemperatureRange() {
        return _temperatureRange;
    }
    
    public DoubleRange getViscosityRange() {
        return _viscosityRange;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
