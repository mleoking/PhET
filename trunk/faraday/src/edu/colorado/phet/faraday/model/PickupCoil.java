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

import edu.colorado.phet.common.math.MathUtil;
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
    // Class data
    //----------------------------------------------------------------------------
    
    // Determines how the magnetic field decreases with the distance from the magnet.
    private static final double DISTANCE_EXPONENT = 2.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    private double _flux; // in webers
    private double _deltaFlux; // in webers
    private double _emf; // in volts
    private AffineTransform _transform; // a reusable transform
    private Point2D _point; // a reusable point
    private Vector2D _fieldVector; // a reusable vector
    
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
        _deltaFlux = 0.0;
        _emf = 0.0;
        _transform = new AffineTransform();
        _point = new Point2D.Double();
        _fieldVector = new Vector2D();
        
        // loosely packed loops
        setLoopSpacing( 1.5 * getWireWidth() );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the magnetic flux.
     * 
     * @param magnetic flux, in Webers
     */
    public double getFlux() {
        return _flux;
    }
    
    /**
     * Gets the change in magnetic flux.
     * 
     * @return change in magnetic flux, in Webers
     */
    public double getDeltaFlux() {
        return _deltaFlux;
    }
    
    /**
     * Gets the emf.
     * 
     * @return the emf
     */
    public double getEmf() {
        return _emf;
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
            updateEmf( dt );
        }
    }
    
    /**
     * Updates the emf, using Faraday's Law.
     */
    private void updateEmf( double dt ) {
        
        double A = getLoopArea();  // surface area of the one loop
        
        // Flux at the center of the coil.
        double centerFlux = 0;
        {
            // Determine the point that corresponds to the center.
            getLocation( _point /* output */ );
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _point, _fieldVector /* output */, DISTANCE_EXPONENT );
            
            // Calculate the flux.
            double B = _fieldVector.getMagnitude();
            double theta = Math.abs( _fieldVector.getAngle() - getDirection() );
            centerFlux = B * A * Math.cos( theta );
        }
        
        // Flux at the top edge of the coil.
        double topFlux = 0;
        {
            // Determine the point that corresponds to the top edge.
            double x = getX();
            double y = getY() - getRadius();
            _point.setLocation( x, y );
            if ( getDirection() != 0 ) {
                // Adjust for rotation.
                _transform.setToIdentity();
                _transform.rotate( getDirection(), getX(), getY() );
                _transform.transform( _point, _point /* output */);
            }
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _point, _fieldVector /* output */, DISTANCE_EXPONENT  );
            
            // Calculate the flux.
            double B = _fieldVector.getMagnitude();
            double theta = Math.abs( _fieldVector.getAngle() - getDirection() );
            topFlux = B * A * Math.cos( theta );
        }
        
        // Flux at the bottom edge of the coil.
        double bottomFlux = 0;
        {
            // Determine the point that corresponds to the bottom edge.
            double x = getX();
            double y = getY() + getRadius();
            _point.setLocation( x, y );
            if ( getDirection() != 0 ) {
                // Adjust for rotation.
                _transform.setToIdentity();
                _transform.rotate( getDirection(), getX(), getY() );
                _transform.transform( _point, _point /* output */);
            }
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _point, _fieldVector /* output */, DISTANCE_EXPONENT  );
            
            // Calculate the flux.
            double B = _fieldVector.getMagnitude();
            double theta = Math.abs( _fieldVector.getAngle() - getDirection() );
            bottomFlux = B * A * Math.cos( theta ); 
        }
        
        // Average the flux.
        double flux = getNumberOfLoops() * ( centerFlux + topFlux + bottomFlux ) / 3;
        
        // Calculate the change in flux.
        _deltaFlux = flux - _flux;
        _flux = flux;
        
        //********************************************
        // Faraday's Law - Calculate the induced EMF.
        //********************************************
        _emf = -( _deltaFlux / dt );
        
        // Kirchhoff's rule -- voltage across the ends of the coil equals the emf.
        double voltage = _emf;
        if ( Math.abs( voltage ) > getMaxVoltage() ) {
//            System.out.println( "PickupCoil.updateEmf: voltage exceeded maximum voltage: " + voltage ); //DEBUG
            voltage = MathUtil.clamp( -getMaxVoltage(), voltage, getMaxVoltage() );
        }
        
        // Update the amplitude of this voltage source.
        setAmplitude( voltage / getMaxVoltage() );
        
//        // DEBUG: use this to determine the maximum EMF in the simulation.
//        if ( Math.abs(emf) > Math.abs(_maxEmf) ) {
//            _maxEmf = emf;
//            System.out.println( "PickupCoil.stepInTime: MAX emf=" + _maxEmf ); // DEBUG
//        }
    }
}