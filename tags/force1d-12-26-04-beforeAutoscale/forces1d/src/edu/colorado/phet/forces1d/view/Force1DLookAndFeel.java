package edu.colorado.phet.forces1d.view;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 22, 2004
 * Time: 8:14:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class Force1DLookAndFeel {
    private Color appliedForceColor = Color.green;
    private Color netForceColor = Color.blue;
    private Color frictionForceColor = Color.red;
    private Color weightColor = Color.magenta;
    private Color normalColor = Color.orange;

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
}
