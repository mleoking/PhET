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
public class PickupCoil extends AbstractCoil implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnet;
    private AbstractResistor _resistor;
    private double _EMF;  // in volts
    private double _flux; // in webers
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param magnet the magnet that is affecting the coil
     */
    public PickupCoil( AbstractMagnet magnet ) {
        super();
        assert( magnet != null );
        _magnet = magnet;
        _resistor = null;
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
    
    /**
     * Sets the resistor that is placed across the coil.
     * 
     * @param resistor the resistor, possibly null
     */
    public void setResistor( AbstractResistor resistor ) {
        _resistor = resistor;
    }
    
    /**
     * Gets the resistor that is across the coil.
     * 
     * @return the resistor, possibly null
     */
    public AbstractResistor getResistor() {
        return _resistor;
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
        AbstractVector2D strength = _magnet.getStrength( getLocation() );
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
            
            _EMF = EMF;
            
            /* 
             * Set the current in the resistor.
             * According to Kirchhoff’s loop rule the magnitude of the induced EMF
             * equals the potential difference across the ends of the coil
             * (and therefore, across a lightbulb or voltmeter hooked to the ends
             * of the coil).
             */
            if ( _resistor != null ) {
                double voltage = EMF;  // Kirchhoff's loop rule
                double current = voltage / _resistor.getResistance();  // Ohm's law: I = V/R
                _resistor.setCurrent( current );
            }
            
            notifyObservers();
            
            System.out.println( "PickupCoil.stepInTime: EMF=" + EMF );  // DEBUG
        }
    }
}