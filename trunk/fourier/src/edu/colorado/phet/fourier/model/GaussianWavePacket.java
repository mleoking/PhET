/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.model;

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * GaussianWavePacket is the model of a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GaussianWavePacket extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _spacing;
    private double _width;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param spacing spacing between components, in radians/mm
     * @param width width, measured at half the packet height, in radians/mm
     */
    public GaussianWavePacket( double spacing, double width ) {
        _spacing = spacing;
        _width = width;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the spacing between components.
     * 
     * @param spacing spacing in radians/mm
     */
    public void setSpacing( double spacing ) {
        assert( spacing >= 0 );
        if ( spacing != _spacing ) {
            _spacing = spacing;
            notifyObservers();
        }
    }
    
    public double getSpacing() {
        return _spacing;
    }
    
    /**
     * Sets the wave packet width.
     * The width is measured as half the packet's height.
     * 
     * @param width width in radians/mm
     */
    public void setWidth( double width ) {
        assert(  width > 0 );
        if ( width != _width ) {
            _width = width;
            notifyObservers();
        }
    }
    
    public double getWidth() {
        return _width;
    }
    
    //----------------------------------------------------------------------------
    // Convenience accessors
    //----------------------------------------------------------------------------
    
    public void setK1( double k1 ) {
        setSpacing( k1 );
    }
    
    public double getK1() {
        return getSpacing();
    }
    
    public void setDeltaK( double deltaK ) {
        setWidth( 2 * deltaK );
    }
    
    public double getDeltaK() {
        return ( getWidth() / 2 );
    }
    
    public void setDeltaX( double deltaX ) { 
        setDeltaK( 1 / deltaX );
    }
    
    public double getDeltaX() {
        return ( 1 / getDeltaK() );
    }
    
    //----------------------------------------------------------------------------
    // Static utilities
    //----------------------------------------------------------------------------
    
    /**
     * Gets the amplitude of some component, using the standard Gaussian formula:
     * <br>
     * A(k,k0,dk) = exp[ -((k-k0)^2) / (2 * (dk^2) )  ] / (dk * sqrt( 2pi ))
     * 
     * @param k component value, in radians/mm
     * @param k0 center point of the wave packet, in radians/mm
     * @param dk delta k, in radians/mm
     * @return the amplitude
     */
    public static double getAmplitude( double k, double k0, double dk ) {
        return Math.exp( -Math.pow( k - k0, 2 ) / ( 2 * dk * dk ) ) / ( dk * Math.sqrt( 2 * Math.PI ) );
    }
}
