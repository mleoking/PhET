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
public class PickupCoil extends AbstractCoil implements ModelElement, IVoltageSource {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    private double _EMF;  // in volts
    private double _flux; // in webers
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param magnetModel the magnet that is affecting the coil
     */
    public PickupCoil( AbstractMagnet magnetModel ) {
        super();
        _magnetModel = magnetModel;
        _EMF = 0.0;
        _flux = 0.0;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the induced EMF.
     * 
     * @return the induced EMF, in volts
     */
    public double getEMF() {
        return _EMF;
    }
    
    //----------------------------------------------------------------------------
    // ICurrent implementation
    //----------------------------------------------------------------------------
    
    /**
     * According to Kirchhoff’s loop rule the magnitude of the induced EMF
     * equals the potential difference across the ends of the coil
     * (and therefore, across a lightbulb or voltmeter hooked to the ends
     * of the coil).
     * 
     * @see edu.colorado.phet.faraday.model.IVoltageSource#getCurrent()
     */
    public double getVoltage() {
        return Math.abs( _EMF );
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

        // TODO handle arbitrary coil orientation
        
        // Magnetic field strength at the coil's location.
        AbstractVector2D strength = _magnetModel.getStrength( getLocation() );
        double B = strength.getMagnitude();
        double theta = strength.getAngle();
        
        // Calculate the change in flux.
        double A = getArea();
        double flux = B * A * Math.cos( theta );
        double deltaFlux = flux - _flux;
        _flux = flux;
        
        // Calculate the induced EMF.
        double EMF = -( getNumberOfLoops() * deltaFlux );
        if ( EMF != _EMF ) {
            notifyObservers();
            System.out.println( "PickupCoil.stepInTime: EMF=" + EMF );  // DEBUG
        }
        _EMF = EMF;
    }
}