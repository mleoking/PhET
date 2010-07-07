package edu.colorado.phet.movingman;

import java.awt.*;

/**
 * @author Sam Reid
 */
public class MovingManColorScheme {
    public static final Color POSITION_COLOR = Color.blue;
    public static final Color VELOCITY_COLOR = Color.red;
    public static final Color ACCELERATION_COLOR = new Color(0,235,0);//same green as original moving man

    public static Color semitransparent(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
