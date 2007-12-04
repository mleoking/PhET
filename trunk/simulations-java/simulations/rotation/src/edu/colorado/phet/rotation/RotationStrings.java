package edu.colorado.phet.rotation;

/**
 * Created by: Sam
 * Dec 4, 2007 at 9:48:22 AM
 */
public class RotationStrings {
    public static final String RADIANS = RotationStrings.getString( "radians" );
    public static final String DEGREES = RotationStrings.getString( "degrees" );
    public static final String METERS = RotationStrings.getString( "meters" );

    public static final String RADIANS_ABBR = RotationStrings.getString( "rad" );
    public static final String DEGREES_ABBR = RotationStrings.getString( "degrees" );

    public static final String ACCELERATION = RotationStrings.getString( "acceleration" );
    public static final String ACCELERATION_ABBR = RotationStrings.getString( "a" );
    public static final String SEC_ABBR = RotationStrings.getString( "s" );
    public static final String METERS_ABBR = RotationStrings.getString( "m" );

    public static final String X = RotationStrings.getString( "x" );
    public static final String Y = RotationStrings.getString( "y" );
    public static final String V = RotationStrings.getString( "v" );
    public static final String A = RotationStrings.getString( "a" );

    public static final String ANG_VEL_ABBR = RADIANS_ABBR + "/" + SEC_ABBR;
    public static final String ANG_VEL_DEG_ABBR = DEGREES_ABBR + "/" + SEC_ABBR;
    public static final String ANG_ACC_ABBR = RADIANS_ABBR + "/" + SEC_ABBR + "^2";//"rad/s^2";
    public static final String ANG_ACC_DEG_ABBR = DEGREES_ABBR + "/" + SEC_ABBR + "^2";//"degrees/s^2";
    public static final String VELOCITY_ABBR = METERS_ABBR + "/" + SEC_ABBR;//"m/s"
    public static final String ACCEL_ABBR = METERS_ABBR + "/" + SEC_ABBR + "^2";//"m/s^2";

    public static final String VELOCITY = RotationStrings.getString( "velocity" );
    public static final String POSITION = RotationStrings.getString( "position" );
    public static final String SPEED = RotationStrings.getString( "speed" );


    public static String getString( String s ) {
        return "!" + RotationResources.getInstance().getLocalizedString( s ) + "?";
    }

    public static String abs( String s ) {
        return "|" + s + "|";
    }

    public static String caps( String s ) {
        return s.toUpperCase();
    }

    public static String formatForChart( String s, String s1 ) {
        return RotationStrings.caps( s ) + "-" + s1;
    }

    public static String twoChar( String s ) {
        return translateChar( s.charAt( 0 ) ) + "" + translateChar( s.charAt( 1 ) );
    }

    private static String translateChar( char c ) {
        if ( c == 'x' ) {
            return X;
        }
        if ( c == 'y' ) {
            return Y;
        }
        if ( c == 'a' ) {
            return A;
        }
        if ( c == 'v' ) {
            return V;
        }
        throw new RuntimeException( "unknown character" );
    }
}
