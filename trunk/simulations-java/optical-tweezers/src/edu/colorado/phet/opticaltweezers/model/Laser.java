/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.DoubleRange;

/**
 * Laser is the model of the laser.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Laser extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_POWER = "power";
    public static final String PROPERTY_RUNNING = "running";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _running;
    private final double _diameter; // nm
    private final double _wavelength; // nm
    private final double _visibleWavelength; // nm
    private double _power; // mW
    private final DoubleRange _powerRange; // mW
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position position of the laser's objective (nm)
     * @param orientation orientation of the beam (radians)
     * @param diameter diameter of the laser beam, as it enters the objective (nm)
     * @param wavelength wavelength (nm)
     * @param visibleWavelength wavelength (nm) to use in views, since actual wavelength is likely IR
     * @param power power (mW)
     */
    public Laser( Point2D position, double orientation, double diameter, double wavelength, double visibleWavelength, DoubleRange powerRange ) {
        super( position, orientation, 0 /* speed */ );
        
        _running = false;
        _diameter = diameter;
        _wavelength = wavelength;
        _visibleWavelength = visibleWavelength;
        _power = powerRange.getDefault();
        _powerRange = new DoubleRange( powerRange );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public void setRunning( boolean running ) {
        if ( running != _running ) {
            _running = running;
            notifyObservers( PROPERTY_RUNNING );
        }
    }
    
    public boolean isRunning() {
        return _running;
    }
    
    public double getDiameter() {
        return _diameter;
    }
    
    public double getWavelength() {
        return _wavelength;
    }
    
    public double getVisibleWavelength() {
        return _visibleWavelength;
    }
    
    public double getPower() {
        return _power;
    }
    
    public void setPower( double power ) {
        if ( power < _powerRange.getMin() || power > _powerRange.getMax() ) {
            throw new IllegalArgumentException( "power out of range: " + power );
        }
        if ( power != _power ) {
            _power = power;
            notifyObservers( PROPERTY_POWER );
        }
    }
    
    public DoubleRange getPowerRange() {
        return _powerRange;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
