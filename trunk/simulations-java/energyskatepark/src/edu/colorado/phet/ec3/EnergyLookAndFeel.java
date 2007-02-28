/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 1:53:51 PM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class EnergyLookAndFeel {
    /**
     * W_grav and deltaPE should be the same color:  Blue (sky blue, sky-high --get it?)
     * W_fric and deltaThermal should be same color: Red (red hot)
     * W_net and deltaKE should be same color: green (green for go)
     * x-W_app and deltaTotalEnergy should be same color: Yellow (yellow for... I don't know, it just has to be different than blue, red, green).
     */
    private final Color myGreen = new Color( 0.0f, 0.8f, 0.1f );
//    private final Color lightBlue = new Color( 160, 220, 255 );

//    private Color appliedForceColor = Color.yellow;
//    private Color appliedForceColor = new Color( 180, 180, 12 );//my yellow
    //    private Color appliedForceColor = new Color( 215, 215, 40);//my yellow
    private Color appliedForceColor = new Color( 180, 180, 0 );//my yellow
    //    private Color appliedForceColor = new Color( 230,150,10);//my yellow
    private Color netForceColor = myGreen;
    private Color frictionForceColor = Color.red;
    //    private Color weightColor = lightBlue;//used to be Color.blue
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

//    private Color totalEnergyColor = new Color( 100, 0, 160 );//purple
//    private Color kineticEnergyColor = new Color( 234, 152, 168 );//light red
//    private Color potentialEnergyColor = new Color( 134, 208, 197 );//light blue
//    private Color thermalEnergyColor = new Color( 223, 188, 22 );//orange

    private Color totalEnergyColor = appliedWorkColor;
    private Color kineticEnergyColor = totalWorkColor;
    private Color potentialEnergyColor = gravityWorkColor;
    private Color thermalEnergyColor = frictionWorkColor;

//    private Color keColor = Color.red;
//    private Color peColor = Color.blue;
//    private Color totalColor = Color.green;

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
}
