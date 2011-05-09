// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 1:53:51 PM
 */

public class EnergyLookAndFeel {
    /**
     * W_grav and deltaPE should be the same color:  Blue (sky blue, sky-high --get it?)
     * W_fric and deltaThermal should be same color: Red (red hot)
     * W_net and deltaKE should be same color: green (green for go)
     * x-W_app and deltaTotalEnergy should be same color: Yellow (yellow for... I don't know, it just has to be different than blue, red, green).
     */
    private final Color myGreen = new Color( 0.0f, 0.8f, 0.1f );
    private Color appliedForceColor = new Color( 180, 180, 0 );//my yellow
    private Color netForceColor = myGreen;
    private Color frictionForceColor = Color.red;
    private Color weightColor = new Color( 50, 130, 215 );
    private Color normalColor = Color.magenta;
    private Color wallForceColor = Color.magenta;

    private Color accelColor = Color.black;
    private Color velColor = Color.black;
    private Color positionColor = Color.black;

    private Color appliedWorkColor = appliedForceColor;
    private Color frictionWorkColor = frictionForceColor;
    private Color gravityWorkColor = weightColor;
    private Color totalWorkColor = myGreen;

    private Color totalEnergyColor = appliedWorkColor;
    private Color kineticEnergyColor = totalWorkColor;
    private Color potentialEnergyColor = gravityWorkColor;
    private Color thermalEnergyColor = frictionWorkColor;

    public Color getKEColor() {
        return kineticEnergyColor;
    }

    public Color getPEColor() {
        return potentialEnergyColor;
    }

    public Color getTotalEnergyColor() {
        return totalEnergyColor;
    }

    public Color getThermalEnergyColor() {
        return thermalEnergyColor;
    }

    public Color getBackground() {
        return myGreen;
    }

    public static Paint getLegendBackground() {
        return new Color( 255, 255, 255, 175 );
    }
}
