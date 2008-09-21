/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.persistence;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;


/**
 * IntroConfig is a Java Bean compliant configuration of IntroModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroConfig implements IProguardKeepClass {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Module
    private boolean _active; // is the module active?
    
    // Clock
    private boolean _clockRunning;
    private int _clockFrameRate;
    private boolean _englishUnitsSelected;
    private boolean _equilibriumLineSelected;
    private boolean _snowfallSelected;
    private double _temperature;
    private double _snowfall;
    private double _zoomedViewportX;
    private double _zoomedViewportY;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public IntroConfig() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isActive() {
        return _active;
    }

    public void setActive( boolean active ) {
        _active = active;
    }
    
    public boolean isClockRunning() {
        return _clockRunning;
    }
    
    public void setClockRunning( boolean clockRunning ) {
        _clockRunning = clockRunning;
    }
    
    public int getClockFrameRate() {
        return _clockFrameRate;
    }

    public void setClockFrameRate( int clockFrameRate ) {
        _clockFrameRate = clockFrameRate;
    }
    
    public boolean isEnglishUnitsSelected() {
        return _englishUnitsSelected;
    }
    
    public void setEnglishUnitsSelected( boolean selected ) {
        _englishUnitsSelected = selected;
    }
    
    public boolean isEquilibriumLineSelected() {
        return _equilibriumLineSelected;
    }

    public void setEquilibriumLineSelected( boolean selected ) {
        _equilibriumLineSelected = selected;
    }

    public boolean isSnowfallSelected() {
        return _snowfallSelected;
    }
    
    public void setSnowfallSelected( boolean selected ) {
        _snowfallSelected = selected;
    }

    public double getTemperature() {
        return _temperature;
    }

    public void setTemperature( double temperature ) {
        _temperature = temperature;
    }

    public double getSnowfall() {
        return _snowfall;
    }

    public void setSnowfall( double snowfall ) {
        _snowfall = snowfall;
    }
    
    public void setZoomedViewportX( double x ) {
        _zoomedViewportX = x;
    }
    
    public double getZoomedViewportX() {
        return _zoomedViewportX;
    }
    
    public void setZoomedViewportY( double y ) {
        _zoomedViewportY = y;
    }
    
    public double getZoomedViewportY() {
        return _zoomedViewportY;
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods
    //----------------------------------------------------------------------------
    
    public void setZoomedViewportPosition( Point2D p ) {
        _zoomedViewportX = p.getX();
        _zoomedViewportY = p.getY();
    }
    
    public Point2D getZoomedViewportPosition() {
        return new Point2D.Double( _zoomedViewportX, _zoomedViewportY );
    }
}
