package edu.colorado.phet.theramp.view;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 22, 2004
 * Time: 8:14:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class RampLookAndFeel {

    /**
     * W_grav and deltaPE should be the same color:  Blue (sky blue, sky-high --get it?)
     * W_fric and deltaThermal should be same color: Red (red hot)
     * W_net and deltaKE should be same color: green (green for go)
     * x-W_app and deltaTotalEnergy should be same color: Yellow (yellow for... I don't know, it just has to be different than blue, red, green).
     */
    private final Color myGreen = new Color( 0.0f, 0.8f, 0.1f );
    private final Color lightBlue = new Color( 160, 220, 255 );
    //    private final Color drabYellow=new Color( 180, 180, 0 );
    private final Color drabYellow = new Color( 190, 190, 0 );
//    private final Color brightYellow=new Color( 228, 236, 0 );

//    private Color appliedForceColor = Color.yellow;
//    private Color appliedForceColor = new Color( 180, 180, 12 );//my yellow
//    private Color appliedForceColor = new Color( 215, 215, 40);//my yellow
//    private Color appliedForceColor = new Color( 180, 180, 0 );//my yellow

    private static final Color MY_ORANGE = new Color( 236, 153, 55 );
    private Color appliedForceColor = MY_ORANGE;
    //    private Color appliedForceColor = new Color( 230,150,10);//my yellow
    private Color netForceColor = myGreen;
    private Color frictionForceColor = Color.red;
    //    private Color weightColor = lightBlue;//used to be Color.blue
    private Color weightColor = new Color( 50, 130, 215 );
    private Color normalColor = Color.magenta;
//    private Color wallForceColor = new Color( 224, 176, 143 );//peach
    //    private Color wallForceColor = new Color( 169, 124, 71 );//brown
    private Color wallForceColor = drabYellow;

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

    public Color getAppliedForceColor() {
        return appliedForceColor;
    }

    public Color getNetForceColor() {
        return netForceColor;
    }

    public Color getFrictionForceColor() {
        return frictionForceColor;
    }

    public Color getWeightColor() {
        return weightColor;
    }

    public Color getNormalColor() {
        return normalColor;
    }

    public Color getAccelerationColor() {
        return accelColor;
    }

    public Color getVelocityColor() {
        return velColor;
    }

    public Color getPositionColor() {
        return positionColor;
    }

    public Color getWallForceColor() {
        return wallForceColor;
    }

    public Color getTotalEnergyColor() {
        return totalEnergyColor;
    }

    public Color getKineticEnergyColor() {
        return kineticEnergyColor;
    }

    public Color getPotentialEnergyColor() {
        return potentialEnergyColor;
    }

    public Color getThermalEnergyColor() {
        return thermalEnergyColor;
    }

    public Color getAppliedWorkColor() {
        return appliedWorkColor;
    }

    public Color getFrictionWorkColor() {
        return frictionWorkColor;
    }

    public Color getGravityWorkColor() {
        return gravityWorkColor;
    }

    public Color getTotalWorkColor() {
        return totalWorkColor;
    }
}
