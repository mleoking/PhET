/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.persistence;

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
}
