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
    
    /**  Number of sample points above the center of the coil. */
    public static final int SAMPLE_POINTS_ABOVE = 10;
    /**  Number of sample points below the center of the coil. */
    public static final int SAMPLE_POINTS_BELOW = SAMPLE_POINTS_ABOVE;
    
    // Determines how the magnetic field decreases with the distance from the magnet.
    private static final double DISTANCE_EXPONENT = 2.0;
    
    // If true, then flux is based on the maximum sample point.
    private static final boolean USE_MAX_FLUX = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    private double _flux; // in webers
    private double _deltaFlux; // in webers
    private double _emf; // in volts
    
    // Reusable objects
    private AffineTransform _someTransform;
    private Point2D _somePoint;
    private Vector2D _someVector;
    private double[] _fluxAbove;
    private double[] _fluxBelow;
    
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
        
        // Reusable objects
        _someTransform = new AffineTransform();
        _somePoint = new Point2D.Double();
        _someVector = new Vector2D();
        _fluxAbove = new double[ SAMPLE_POINTS_ABOVE ];
        _fluxBelow = new double[ SAMPLE_POINTS_BELOW ];
        
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
        
        double A = getLoopArea();  // surface area of one loop
        
        // Flux at the center of the coil.
        double centerFlux = 0;
        {
            // Determine the point that corresponds to the center.
            getLocation( _somePoint /* output */ );
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _somePoint, _someVector /* output */, DISTANCE_EXPONENT );
            
            // Calculate the flux.
            double B = _someVector.getMagnitude();
            double theta = Math.abs( _someVector.getAngle() - getDirection() );
            centerFlux = B * A * Math.cos( theta );
        }
        double maxFlux = centerFlux;
        
        // Flux above the center of the coil.
        for ( int i = 0; i < _fluxAbove.length; i++ ) {

            // Sample point, based on radius.
            double x = getX();
            double y = getY() - ( ( i + 1 ) * ( getRadius() / _fluxAbove.length ) );
            _somePoint.setLocation( x, y );
            if ( getDirection() != 0 ) {
                // Adjust for rotation.
                _someTransform.setToIdentity();
                _someTransform.rotate( getDirection(), getX(), getY() );
                _someTransform.transform( _somePoint, _somePoint /* output */);
            }
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _somePoint, _someVector /* output */, DISTANCE_EXPONENT  );
            
            // Calculate the flux.
            double B = _someVector.getMagnitude();
            double theta = Math.abs( _someVector.getAngle() - getDirection() );
            _fluxAbove[i] = B * A * Math.cos( theta );
            
            // Keep track of the maximum flux.
            if ( Math.abs( _fluxAbove[i] ) > Math.abs( maxFlux ) ) {
                maxFlux = _fluxAbove[i];
            }
        }
        
        // Flux below the center of the coil.
        for ( int i = 0; i < _fluxBelow.length; i++ ) {
            
            // Sample point, based on radius.
            double x = getX();
            double y = getY() + ( ( i + 1 ) * ( getRadius() / _fluxBelow.length ) );
            _somePoint.setLocation( x, y );
            if ( getDirection() != 0 ) {
                // Adjust for rotation.
                _someTransform.setToIdentity();
                _someTransform.rotate( getDirection(), getX(), getY() );
                _someTransform.transform( _somePoint, _somePoint /* output */);
            }
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _somePoint, _someVector /* output */, DISTANCE_EXPONENT  );
            
            // Calculate the flux.
            double B = _someVector.getMagnitude();
            double theta = Math.abs( _someVector.getAngle() - getDirection() );
            _fluxBelow[i] = B * A * Math.cos( theta ); 
            
            // Keep track of the maximum flux.
            if ( Math.abs( _fluxBelow[i] ) > Math.abs( maxFlux ) ) {
                maxFlux = _fluxBelow[i];
            }
        }
        
        // Calculate the flux in one loop.
        double loopFlux;
        if ( USE_MAX_FLUX ) {
            // Use the sample point with the largest flux.
            loopFlux = maxFlux;
        }
        else {
            // Use an average of the sample points.
            double fluxSum = centerFlux;
            for ( int i = 0; i < _fluxAbove.length; i++ ) {
                fluxSum += _fluxAbove[i];
            }
            for ( int i = 0; i < _fluxBelow.length; i++ ) {
                fluxSum += _fluxBelow[i];
            }
            double numberOfPoints = ( 1 + _fluxAbove.length + _fluxBelow.length );
            loopFlux = fluxSum / numberOfPoints;
        }
        
        // Calculate the total flux in the coil.
        double flux = getNumberOfLoops() * loopFlux;
        
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