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
    
    private AbstractMagnet _magnet;
    private double _emf;  // in volts
    private double[] _emfHistory;
    private double _flux; // in webers
    private double _maxEmf; // DEBUG
    private boolean _smoothingEnabled;
    
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
        _emf = 0.0;
        _emfHistory = new double[ HISTORY_SIZE ];
        _flux = 0.0;
        _smoothingEnabled = true;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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
    
    public void updateEmf() {

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
}