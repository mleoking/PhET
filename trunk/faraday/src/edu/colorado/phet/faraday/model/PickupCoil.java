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
 * PickupCoil
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoil extends AbstractCoil implements ModelElement {
    
    private AbstractMagnet _magnet;
    private double _EMF;
    private double _previousEMF;
    private double _previousFlux;
    
    public PickupCoil() {
        super();
        _EMF = 0.0;
        _previousFlux = 0.0;
    }
    
    public void setMagnet( AbstractMagnet magnet ) {
        _magnet = magnet;
    }
    
    public AbstractMagnet getMagnet() {
        return _magnet;
    }

    public double getEMF() {
        return _EMF;
    }
    
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
        Point2D magnetLocation = _magnet.getLocation();
        Point2D coilLocation = super.getLocation();
        double adjacent = Math.abs( magnetLocation.getY() - coilLocation.getY() );
        double opposite = Math.abs( magnetLocation.getX() - magnetLocation.getX() );
        double theta = Math.atan( opposite / adjacent );
        
        // Magnetic field strength at the coil's location.
        AbstractVector2D strength = _magnet.getStrength( getLocation() );
        double B = strength.getMagnitude();
        double direction = Math.toDegrees( strength.getAngle() );
        if ( direction % 360 == 0 ) {
            B = -B;
        }
        
        // Calculate change in flux.
        double A = getArea();
        double flux = B * A * Math.cos( theta );
        double deltaFlux = flux - _previousFlux;
        _previousFlux = flux;
        
        // Calculate induced EMF.
        _EMF = -( getNumberOfLoops() * deltaFlux );
        if ( _EMF != _previousEMF ) {
            notifyObservers();
        }
        _previousEMF = _EMF;
    }
    
}
