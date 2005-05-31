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
    private Color appliedForceColor = Color.blue;
    private Color netForceColor = new Color( 0.0f, 0.8f, 0.0f );
    private Color frictionForceColor = Color.red;
    private Color weightColor = Color.magenta;
    private Color normalColor = Color.orange;
    private Color accelColor = Color.black;
    private Color velColor = Color.black;
    private Color positionColor = Color.black;
    private Color wallColor = Color.cyan;
    private Color gParallelColor = Color.green;
    private Color totalEnergyColor = Color.blue;
    private Color kineticEnergyColor = Color.red;
    private Color potentialEnergyColor = Color.green;
    private Color thermalEnergyColor = Color.red;
    private Color appliedWorkColor = Color.blue;
    private Color frictionWorkColor = Color.orange;
    private Color gravityWorkColor = Color.green;
    private Color totalWorkColor = Color.yellow;

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
        return wallColor;
    }

    public Color getGravityParallelColor() {
        return gParallelColor;
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
