/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

/**
 * User: Sam Reid
 * Date: May 22, 2006
 * Time: 5:58:36 PM
 * Copyright (c) May 22, 2006 by Sam Reid
 */

public class WaveInterferenceModelUnits {

    private String distanceUnits = "m";
    private double physicalWidth = 1;
    private double physicalHeight = 1;

    private String timeUnits = "sec";
    private double timeScale = 1.0;

    public WaveInterferenceModelUnits() {
    }

    public String getDistanceUnits() {
        return distanceUnits;
    }

    public double getPhysicalWidth() {
        return physicalWidth;
    }

    public double getPhysicalHeight() {
        return physicalHeight;
    }

    public String getTimeUnits() {
        return timeUnits;
    }

    public double getTimeScale() {
        return timeScale;
    }

    public void setDistanceUnits( String distanceUnits ) {
        this.distanceUnits = distanceUnits;
    }

    public void setPhysicalWidth( double physicalWidth ) {
        this.physicalWidth = physicalWidth;
    }

    public void setPhysicalHeight( double physicalHeight ) {
        this.physicalHeight = physicalHeight;
    }

    public void setTimeUnits( String timeUnits ) {
        this.timeUnits = timeUnits;
    }

    public void setTimeScale( double timeScale ) {
        this.timeScale = timeScale;
    }

    public void setPhysicalSize( double physicalWidth, double physicalHeight ) {
        this.physicalWidth = physicalWidth;
        this.physicalHeight = physicalHeight;
    }
}
