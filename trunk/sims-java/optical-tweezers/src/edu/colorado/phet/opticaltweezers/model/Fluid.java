/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.DoubleRange;

/**
 * Fluid is the model of fluid that contains the glass bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
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
    
    private final DoubleRange _speedRange; // nm/sec
    private final DoubleRange _viscosityRange; // Pa*s
    private final DoubleRange _temperatureRange; // Kelvin
    
    private final double _height; // nm
    private double _viscosity; // Pa*s
    private double _temperature; // Kelvin
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position position at the center of the fluid "stream"
     * @param orientation direction that the fluid stream flows in (radians)
     * @param height height of the fluid stream
     * @param speedRange speed of the fluid stream, nm/sec
     * @param viscosityRange
     * @param temperatureRange
     */
    public Fluid( Point2D position, double orientation, double height, 
            DoubleRange speedRange, DoubleRange viscosityRange, DoubleRange temperatureRange ) {
        super( position, orientation, speedRange.getDefault() );
        
        _height = height;
        
        _speedRange = speedRange;
        _viscosityRange = viscosityRange;
        _temperatureRange = temperatureRange;
        
        _viscosity = viscosityRange.getDefault();
        _temperature = temperatureRange.getDefault();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    public double getHeight() {
        return _height;
    }
    
    public double getMinY() {
        return getY() - ( _height / 2 );
    }
    
    public double getMaxY() {
        return getY() + ( _height / 2 );
    }
    
    public double getViscosity() {
        return _viscosity;
    }

    public void setSpeed( double speed ) {
        if ( speed < _speedRange.getMin() || speed > _speedRange.getMax() ) {
            throw new IllegalArgumentException( "speed out of range: " + speed );
        }
        super.setSpeed( speed );
    }
    
    public void setViscosity( double viscosity ) {
        if ( viscosity < _viscosityRange.getMin() || viscosity > _viscosityRange.getMax() ) {
            throw new IllegalArgumentException( "viscosity out of range: " + viscosity );
        }
        if ( viscosity != _viscosity ) {
            _viscosity = viscosity;
            notifyObservers( PROPERTY_VISCOSITY );
        }
    }
    
    public double getTemperature() {
        return _temperature;
    }

    public void setTemperature( double temperature ) {
        if ( temperature < _temperatureRange.getMin() || temperature > _temperatureRange.getMax() ) {
            throw new IllegalArgumentException( "temperature out of range: " + temperature );
        }
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
