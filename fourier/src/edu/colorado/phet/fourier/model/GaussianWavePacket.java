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
    private double _center;
    private double _significantWidth;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param spacing spacing between components, in radians/mm
     * @param width width, measured at half the packet height, in radians/mm
     * @param center the center point of the packet, in radians/mm
     * @param significantWidth the width over which we consider components to be significant, in radians/mm
     */
    public GaussianWavePacket( double spacing, double width, double center, double significantWidth ) {
        _spacing = spacing;
        _width = width;
        _center = center;
        _significantWidth = significantWidth;
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
    
    /**
     * Gets the center point of the wave packet.
     * 
     * @param center the center point, in radians/mm
     */
    public void setCenter( double center ) {
        if ( center != _center ) {
            _center = center;
            notifyObservers();
        }
    }
    
    public double getCenter() {
        return _center;
    }
    
    /**
     * Gets the number of components in the wave packet.
     * If the spacing is zero, then Integer.MAX_VALUE is returned.
     */
    public int getNumberOfComponents() {
        int numberOfComponents;
        if ( _spacing == 0 ) {
            return Integer.MAX_VALUE;
        }
        else {
            return (int)( _significantWidth / _spacing ) - 1;
        }
    }
    
    public void setSignificantWidth( double significantWidth ) {
        if ( significantWidth != _significantWidth ) {
            _significantWidth = significantWidth;
            notifyObservers();
        }
    }
    
    public double getSignificantWidth() {
        return _significantWidth;
    }
    
    //----------------------------------------------------------------------------
    // Convenience accessors
    //----------------------------------------------------------------------------
    
    /**
     * k1 is the spacing.
     * 
     * @param k1
     */
    public void setK1( double k1 ) {
        setSpacing( k1 );
    }
    
    public double getK1() {
        return getSpacing();
    }
    
    /**
     * 2 * dk is the width.
     * 
     * @param dk
     */
    public void setDeltaK( double deltaK ) {
        setWidth( 2 * deltaK );
    }
    
    public double getDeltaK() {
        return ( getWidth() / 2 );
    }
    
    /**
     * dx * dk = 1
     * 
     * @param deltaX
     */
    public void setDeltaX( double deltaX ) { 
        setDeltaK( 1 / deltaX );
    }
    
    public double getDeltaX() {
        return ( 1 / getDeltaK() );
    }
    
    /**
     * k0 is the center point.
     * 
     * @param k0
     */
    public void setK0( double k0 ) {
        setCenter( k0 );
    }
    
    public double getK0() {
        return getCenter();
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
