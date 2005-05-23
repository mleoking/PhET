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
}
