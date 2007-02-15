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


public class Laser extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String PROPERTY_POWER = "power";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _width; // nm
    private double _wavelength; // nm
    private double _power; // mW
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Laser( Point2D position, double orientation, double width, double wavelength, double power ) {
        super( position, orientation, 0 /* speed */ );
        _width = width;
        _wavelength = wavelength;
        _power = power;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public double getWidth() {
        return _width;
    }
    
    private void setWidth( double width ) {
        throw new UnsupportedOperationException( "width is immutable" );
    }
    
    public double getWavelength() {
        return _wavelength;
    }
    
    private void setWavelength( double wavelength ) {
        throw new UnsupportedOperationException( "wavelength is immutable" );
    }
    
    public double getPower() {
        return _power;
    }
    
    public void setPower( double power ) {
        if ( power < 0 ) {
            throw new IllegalArgumentException( "power < 0: " + power );
        }
        if ( power != _power ) {
            _power = power;
            notifyObservers( PROPERTY_POWER );
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
