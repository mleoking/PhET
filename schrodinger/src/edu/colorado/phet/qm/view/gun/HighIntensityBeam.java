/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:04:25 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class HighIntensityBeam {
    private boolean highIntensityModeOn;
    private double intensity;
    private double intensityScale = 0.2;

    private double getTotalIntensityScale() {
        if( highIntensityModeOn ) {
            return intensity * intensityScale;
        }
        else {
            return 1.0;
        }
    }

    public void setHighIntensityModeOn( boolean highIntensityModeOn ) {
        this.highIntensityModeOn = highIntensityModeOn;
    }

    public void setIntensity( double intensity ) {
        this.intensity = intensity;
    }
}
