package edu.colorado.phet.movingman;

import java.awt.*;

/**
 * @author Sam Reid
 */
public class MovingManColorScheme {
    public static final Color POSITION_COLOR = Color.blue;
    public static final Color VELOCITY_COLOR = Color.red;
    //Original moving man used acceleration color = new Color(0,235,0);, but that was too similar to the grass color, so we made it darker.
    public static final Color ACCELERATION_COLOR = new Color(34,139,34);//Forest Green from http://www.tayloredmktg.com/rgb/

    public static Color semitransparent(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
