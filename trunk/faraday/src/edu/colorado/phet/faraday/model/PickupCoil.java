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

import edu.colorado.phet.common.model.ModelElement;


/**
 * PickupCoil
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoil extends WireCoil implements ModelElement {
    
    private BarMagnet _magnet; // XXX should be IMagnet, any type of magnet
    private double _emf;
    private double _previousEMF;
    private double _previousFlux;
    
    public PickupCoil() {
        super();
        _emf = 0.0;
        _previousFlux = 0.0;
    }
    
    public void setMagnet( BarMagnet magnet ) {
        _magnet = magnet;
    }
    
    public BarMagnet getMagnet() {
        return _magnet;
    }

    public double getEMF() {
        return _emf;
    }
    
    /**
     * Handles ticks of the simulation clock.
     * Calculates the induced emf using Faraday's Law.
     * 
     * @param dt time delta
     */
    public void stepInTime( double dt ) {
        Point2D magnetLocation = _magnet.getLocation();
        double theta = 1.0;  // XXX calculate angle in radians between B-Field and surface area vector.
        double B = _magnet.getStrength( getLocation() );
        double A = getArea();
        double flux = B * A * theta;
        double deltaFlux = flux - _previousFlux;
        _emf = -( getNumberOfLoops() * deltaFlux );
        _previousFlux = flux;
        if ( _emf != _previousEMF ) {
            notifyObservers();
        }
        _previousEMF = _emf;
    }
    
}
