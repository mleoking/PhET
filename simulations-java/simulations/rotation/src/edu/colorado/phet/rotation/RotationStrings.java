package edu.colorado.phet.rotation;

/**
 * Created by: Sam
 * Dec 4, 2007 at 9:48:22 AM
 */
public class RotationStrings {
    public static final String RADIANS = "radians";
    public static final String DEGREES = "degrees";
    public static final String METERS = "meters";

    public static final String RADIANS_ABBR = "rad";
    public static final String DEGREES_ABBR = "degrees";

    public static final String ACCELERATION = "acceleration";
    public static final String ACCELERATION_ABBR = "a";
    public static final String SEC_ABBR = "s";
    public static final String METERS_ABBR = "m";

    public static final String ANG_VEL_ABBR = RADIANS_ABBR + "/" + SEC_ABBR;
    public static final String ANG_VEL_DEG_ABBR = DEGREES_ABBR + "/" + SEC_ABBR;
    public static final String ANG_ACC_ABBR = RADIANS_ABBR + "/" + SEC_ABBR + "^2";//"rad/s^2";
    public static final String ANG_ACC_DEG_ABBR = DEGREES_ABBR + "/" + SEC_ABBR + "^2";//"degrees/s^2";
    public static final String VELOCITY_ABBR = METERS_ABBR + "/" + SEC_ABBR;//"m/s"
    public static final String ACCEL_ABBR = METERS_ABBR + "/" + SEC_ABBR + "^2";//"m/s^2";


    public static String getString( String s ) {
        return RotationResources.getInstance().getLocalizedString( s );
    }
}
