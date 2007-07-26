package edu.colorado.phet.rotation;

import java.awt.*;

/**
 * Author: Sam Reid
 * Jul 14, 2007, 12:39:46 AM
 */
public class RotationColorScheme {
    public static final Color TEAL = new Color( 0, 128, 128 );
    public static final Color PINK = new Color( 255, 70, 144 );
    private static Color LIGHT_RED = new Color( 255, 127, 181 );
    private static Color LIGHT_BLUE = new Color( 127, 217, 255 );
    private static Color LIGHT_GREEN = new Color( 47, 156, 83 );

    public static final Color POSITION_COLOR = Color.blue;
    public static final Color VELOCITY_COLOR = Color.green;
    public static final Color ACCELERATION_COLOR = PINK;

    public static final Color ANGLE_COLOR = LIGHT_BLUE;
    public static final Color ANGULAR_VELOCITY_COLOR = LIGHT_GREEN;
    public static final Color ANGULAR_ACCELERATION_COLOR = LIGHT_RED;

    //    public static final Color X_COLOR = Color.green;
    //    public static final Color X_COLOR = new Color( 100,200,10);
    public static final Color X_COLOR = new Color( 190, 10, 200 );
    public static final Color Y_COLOR = Color.red;
    public static final Color R_COLOR = Color.blue;

    public static final Color VX_COLOR = Color.blue;
    public static final Color VY_COLOR = Color.red;
    public static final Color VM_COLOR = VELOCITY_COLOR;

    public static final Color AX_COLOR = Color.blue;
    public static final Color AY_COLOR = Color.green;
    public static final Color AM_COLOR = ACCELERATION_COLOR;

    //    public static final Color PLATFORM_ANGLE_COLOR = Color.gray;
    public static final Color PLATFORM_ANGLE_COLOR = LIGHT_BLUE;
    public static final Color PLATFORM_ANGULAR_VELOCITY_COLOR = LIGHT_GREEN;
    public static final Color PLATFORM_ANGULAR_ACCEL_COLOR = LIGHT_RED;
}
