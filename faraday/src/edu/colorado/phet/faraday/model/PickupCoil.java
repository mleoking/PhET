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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.faraday.util.Vector2D;


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
    
    private AbstractMagnet _magnetModel;
    private double _flux; // in webers
    private double _emf; // in volts
    private AffineTransform _transform; // a reusable transform
    private Point2D _point; // a reusable point
    
    // Debugging stuff...
    private double _maxEmf;
    
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
        assert( magnetModel != null );
        _magnetModel = magnetModel;
        _flux = 0.0;
        _emf = 0.0;
        _transform = new AffineTransform();
        _point = new Point2D.Double();
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Handles ticks of the simulation clock.
     * Calculates the induced emf using Faraday's Law.
     * Performs median smoothing of data if isSmoothingEnabled.
     * 
     * @param dt time delta
     */
    public void stepInTime( double dt ) {
        if ( isEnabled() ) {
            updateEmf();
        }
    }
    
    //----------------------------------------------------------------------------
    // Update methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates the emf, using Faraday's Law.
     */
    private void updateEmf() {
        
        // Flux at the center of the coil.
        double centerFlux = 0;
        {
            // Determine the point that corresponds to the center.
            getLocation( _point /* destination */ );
            
            // Find the B field vector at that point.
            Vector2D strength = _magnetModel.getStrength( _point );
            
            // Calculate the flux.
            double B = strength.getMagnitude();
            double A = getArea();
            double theta = Math.abs( strength.getAngle() - getDirection() );
            centerFlux = B * A * Math.cos( theta );
        }
        
        // Flux at the top edge of the coil.
        double topFlux = 0;
        {
            // Determine the point that corresponds to the top edge, adjusted for coil rotation.
            double x = getX();
            double y = getY() - getRadius();
            _transform.setToIdentity();
            _transform.rotate( getDirection(), getX(), getY() );
            _transform.transform( new Point2D.Double( x, y ), _point /* destination */ );
            
            // Find the B field vector at that point.
            Vector2D strength = _magnetModel.getStrength( _point );
            
            // Calculate the flux.
            double B = strength.getMagnitude();
            double A = getArea();
            double theta = Math.abs( strength.getAngle() - getDirection() );
            topFlux = B * A * Math.cos( theta );
        }
        
        // Flux at the bottom edge of the coil.
        double bottomFlux = 0;
        {
            // Determine the point that corresponds to the bottom edge, adjusted for coil rotation.
            double x = getX();
            double y = getY() + getRadius();
            _transform.setToIdentity();
            _transform.rotate( getDirection(), getX(), getY() );
            _transform.transform( new Point2D.Double( x, y ), _point /* destination */ );
            
            // Find the B field vector at that point.
            Vector2D strength = _magnetModel.getStrength( _point  );
            
            // Calculate the flux.
            double B = strength.getMagnitude();
            double A = getArea();
            double theta = Math.abs( strength.getAngle() - getDirection() );
            bottomFlux = B * A * Math.cos( theta ); 
        }
        
        // Average the flux.
        double flux = ( centerFlux + topFlux + bottomFlux ) / 3;
        
        // Calculate the change in flux.
        double deltaFlux = flux - _flux;
        _flux = flux;
        
        // Calculate the induced EMF.
        double emf = -( getNumberOfLoops() * deltaFlux );
        
        // Kirchhoff's rule -- voltage across the ends of the coil equals the emf.
        double voltage = emf;
        
        // Update the amplitude of this voltage source.
        setAmplitude( voltage / getMaxVoltage() );
        
//        // DEBUG: use this to determine the maximum EMF in the simulation.
//        if ( Math.abs(emf) > Math.abs(_maxEmf) ) {
//            _maxEmf = emf;
//            System.out.println( "PickupCoil.stepInTime: MAX emf=" + _maxEmf ); // DEBUG
//        }
    }
}