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
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * PickupCoil is the model of a pickup coil.
 * It's behavior follows Faraday's Law for electromagnetic induction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoil extends AbstractCoil implements ModelElement, SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /**  Number of sample points above the center of the coil. */
    public static final int SAMPLE_POINTS_ABOVE = FaradayConfig.PICKUP_SAMPLE_POINTS / 2;
    /**  Number of sample points below the center of the coil. */
    public static final int SAMPLE_POINTS_BELOW = SAMPLE_POINTS_ABOVE;
    
    // Determines how the magnetic field decreases with the distance from the magnet.
    private static final double DISTANCE_EXPONENT = 2.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    private double _flux; // in webers
    private double _deltaFlux; // in webers
    private double _emf; // in volts
    private double _biggestEmf; // in volts
    
    // Reusable objects
    private AffineTransform _someTransform;
    private Point2D _samplePoint;
    private Vector2D _sampleVector;
    private Vector2D _fieldVector;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
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
        _magnetModel.addObserver( this );
        
        _flux = 0.0;
        _deltaFlux = 0.0;
        _emf = 0.0;
        _biggestEmf = 0.0;
        
        // Reusable objects
        _someTransform = new AffineTransform();
        _samplePoint = new Point2D.Double();
        _fieldVector = new Vector2D();
        _sampleVector = new Vector2D();
        
        // loosely packed loops
        setLoopSpacing( 1.5 * getWireWidth() );
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the magnetic flux.
     * 
     * @return the flux, in Webers
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
    
    /**
     * Gets the biggest emf that the pickup coil has seen.
     * 
     * @return the biggest emf
     */
    public double getBiggestEmf() {
        return _biggestEmf;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        // Do nothing, handled by stepInTime
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
     * Updates the induced emf, using Faraday's Law.
     */
    private void updateEmf( double dt ) {
        
        // B-field sample at the center of the coil.
        {
            // Determine the point that corresponds to the center.
            getLocation( _samplePoint /* output */ );
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _samplePoint, _sampleVector /* output */, DISTANCE_EXPONENT );
            
            // Accumulate a sum of the sample points.
            _fieldVector.copy( _sampleVector );
        }
        
        // B-field samples above the center of the coil.
        for ( int i = 0; i < SAMPLE_POINTS_ABOVE; i++ ) {

            // Sample point, based on radius.
            double x = getX();
            double y = getY() - ( ( i + 1 ) * ( getRadius() / SAMPLE_POINTS_ABOVE ) );
            _samplePoint.setLocation( x, y );
            if ( getDirection() != 0 ) {
                // Adjust for rotation.
                _someTransform.setToIdentity();
                _someTransform.rotate( getDirection(), getX(), getY() );
                _someTransform.transform( _samplePoint, _samplePoint /* output */);
            }
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _samplePoint, _sampleVector /* output */, DISTANCE_EXPONENT  );
            
            // Accumulate a sum of the sample points.
            _fieldVector.add( _sampleVector );
        }
        
        // B-field samples below the center of the coil.
        for ( int i = 0; i < SAMPLE_POINTS_BELOW; i++ ) {
            
            // Sample point, based on radius.
            double x = getX();
            double y = getY() + ( ( i + 1 ) * ( getRadius() / SAMPLE_POINTS_BELOW ) );
            _samplePoint.setLocation( x, y );
            if ( getDirection() != 0 ) {
                // Adjust for rotation.
                _someTransform.setToIdentity();
                _someTransform.rotate( getDirection(), getX(), getY() );
                _someTransform.transform( _samplePoint, _samplePoint /* output */);
            }
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _samplePoint, _sampleVector /* output */, DISTANCE_EXPONENT  );
            
            // Accumulate a sum of the sample points.
            _fieldVector.add( _sampleVector );
        }
        
        // Average the B-field sample points.
        int numberOfSamples = 1 + SAMPLE_POINTS_ABOVE + SAMPLE_POINTS_BELOW;
        double scale = 1.0 / numberOfSamples;
        _fieldVector.scale( scale );
        
        // Flux in one loop.
        double B = _fieldVector.getMagnitude();
        double A = getLoopArea();
        double theta = Math.abs( _fieldVector.getAngle() );//- getDirection() );
        double loopFlux = B * A * Math.cos( theta ); 
        
        // Flux in the coil.
        double flux = getNumberOfLoops() * loopFlux;
        
        // Change in flux.
        _deltaFlux = flux - _flux;
        _flux = flux;
        
        // Induced emf.
        double emf = -( _deltaFlux / dt );
        
        // If the emf has changed, set the current in the coil and notify observers.
        if ( emf != _emf ) {
            _emf = emf;
            
            // Current amplitude is proportional to emf amplitude.
            double amplitude = MathUtil.clamp( -1,  emf / FaradayConfig.MAX_PICKUP_EMF, +1 );
            setCurrentAmplitude( amplitude );
            
            notifyObservers();
        }
        
        // Keep track of the biggest emf seen by the pickup coil.
        if ( Math.abs( _emf ) > Math.abs( _biggestEmf ) ) {
            _biggestEmf = _emf;
            if ( FaradayConfig.DEBUG_PICKUP_COIL_EMF ) {
                System.out.println( "PickupCoil.updateEmf: biggestEmf=" + _biggestEmf );
            }
        }
    }
}