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

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MedianFilter;
import edu.colorado.phet.common.model.ModelElement;


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
    
    private static final int HISTORY_SIZE = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    private double _emf;  // in volts
    private double[] _emfHistory;
    private double _flux; // in webers
    private boolean _smoothingEnabled;
    
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
        _emf = 0.0;
        _emfHistory = new double[ HISTORY_SIZE ];
        _flux = 0.0;
        _smoothingEnabled = true;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the magnet model.
     * 
     * @return the magnet model.
     */
    public AbstractMagnet getMagnet() {
        return _magnetModel;
    }
    
    /**
     * Sets the induced emf.
     * 
     * @param emf the emf, in volts
     */
    private void setEmf( double emf ) {
        if ( emf != _emf ) {
            //System.out.println( "PickupCoil.setEmf: emf=" + emf ); // DEBUG
            _emf = emf;
            notifyObservers();
        }
    }
    
    /**
     * Gets the induced emf.
     * 
     * @return the induced emf, in volts
     */
    public double getEmf() {
        return _emf;
    }
    
    /**
     * Gets the voltage across the ends of the coil.
     * According to Kirchhoff’s loop rule, the potential difference across
     * the ends of the coil equals the magnitude of the induced EMF in the coil.
     * 
     * @return voltage across the ends of the coil, in volts
     */
    public double getVoltage() {
        return _emf;
    }
    
    /**
     * Smooths out the behavior by removing spikes in the data.
     * Changing the value of this property has the side-effect of clearing the 
     * data history.
     * 
     * @param smoothingEnabled true to enable, false to disable
     */
    public void setSmoothingEnabled( boolean smoothingEnabled ) {
        if ( smoothingEnabled != _smoothingEnabled ) {
            _smoothingEnabled = smoothingEnabled;
            clearHistory();
        }
    }
    
    /**
     * Gets the smoothing state. See setSmoothingEnabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isSmoothingEnabled() {
        return _smoothingEnabled;
    }
    
    /**
     * Clears the emf history by setting all values to zero.
     */
    private void clearHistory() {
        for ( int i = 0; i < HISTORY_SIZE; i++ ) {
            _emfHistory[i] = 0.0;
        }
    }
 
    //----------------------------------------------------------------------------
    // Faraday's Law implementation
    //----------------------------------------------------------------------------
 
    /**
     * Updates the emf, using Faraday's Law.
     * <p>
     * This is provided as a separate method for situations 
     * where the emf needs to be recomputed immediately (independent of the 
     * simulation clock).  For example, when flipping the magnet polarity,
     * the emf needs to be recomputed immediately so that we can temporarily
     * disable smoothing of emf values.
     */
    public void updateEmf() {
        
        // Flux at the center of the coil.
        double centerFlux = 0;
        {
            // Determine the point that corresponds to the center.
            Point2D location = getLocation();
            
            // Find the B field vector at that point.
            AbstractVector2D strength = _magnetModel.getStrength( location );
            
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
            AffineTransform transform = new AffineTransform();
            transform.rotate( getDirection(), getX(), getY() );
            Point2D location = transform.transform( new Point2D.Double( x, y ), null );
            
            // Find the B field vector at that point.
            AbstractVector2D strength = _magnetModel.getStrength( location );
            
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
            AffineTransform transform = new AffineTransform();
            transform.rotate( getDirection(), getX(), getY() );
            Point2D location = transform.transform( new Point2D.Double( x, y ), null );
            
            // Find the B field vector at that point.
            AbstractVector2D strength = _magnetModel.getStrength( location );
            
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
        if ( _smoothingEnabled ) {
            // Take a median to remove spikes in data.
            for ( int i = HISTORY_SIZE - 1; i > 0; i-- ) {
                _emfHistory[i] = _emfHistory[i - 1];
            }
            _emfHistory[0] = emf;
            setEmf( MedianFilter.getMedian( _emfHistory ) );
        }
        else {
            setEmf( emf );
        }
        
//        // DEBUG: use this to determine the maximum EMF in the simulation.
//        if ( Math.abs(emf) > Math.abs(_maxEmf) ) {
//            _maxEmf = emf;
//            System.out.println( "PickupCoil.stepInTime: MAX emf=" + _maxEmf ); // DEBUG
//        }
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
        updateEmf();
    }
}