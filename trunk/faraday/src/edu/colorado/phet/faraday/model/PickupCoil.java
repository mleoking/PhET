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

import java.awt.geom.Point2D;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.model.ModelElement;


/**
 * PickupCoil is the model of a pickup coil.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoil extends AbstractCoil implements ModelElement, ICurrentSource {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    private double _current; // in amps
    private double _EMF;
    private double _flux;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public PickupCoil( AbstractMagnet magnetModel ) {
        super();
        _magnetModel = magnetModel;
        _current = 0.0;
        _EMF = 0.0;
        _flux = 0.0;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public double getEMF() {
        return _EMF;
    }
    
    public double getFlux() {
        return _flux;
    }
    
    //----------------------------------------------------------------------------
    // ICurrent implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.faraday.model.ICurrentSource#getCurrent()
     */
    public double getCurrent() {
        return _current;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Handles ticks of the simulation clock.
     * Calculates the induced emf using Faraday's Law.
     * 
     * @param dt time delta
     */
    public void stepInTime( double dt ) {
        
        // TODO This needs to be revisited ...  
        // - theta should be calculated based on strength vector
        // - doesn't handle arbitrary magnet orientation?
        
       // Calculate theta, angle between magnetic field and surface area vector.
        Point2D magnetLocation = _magnetModel.getLocation();
        Point2D coilLocation = super.getLocation();
        double adjacent = Math.abs( magnetLocation.getY() - coilLocation.getY() );
        double opposite = Math.abs( magnetLocation.getX() - magnetLocation.getX() );
        double theta = Math.atan( opposite / adjacent );
        
        // Magnetic field strength at the coil's location.
        AbstractVector2D strength = _magnetModel.getStrength( getLocation() );
        double B = strength.getMagnitude();
        double direction = Math.toDegrees( strength.getAngle() );
        if ( direction % 360 == 0 ) {
            B = -B;
        }
        
        // Calculate change in flux.
        double A = getArea();
        double flux = B * A * Math.cos( theta );
        double deltaFlux = flux - _flux;
        _flux = flux;
        
        // Calculate induced EMF.
        double EMF = -( getNumberOfLoops() * deltaFlux );
        if ( EMF != _EMF ) {
            notifyObservers();
        }
        _EMF = EMF;
        
        // Calculate induced current.
        // XXX
    }
}
