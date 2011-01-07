// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation;

/**
 * Created by: Sam
 * Dec 4, 2007 at 9:48:22 AM
 */
public class RotationStrings {
    public static final String RADIANS = RotationStrings.getString( "units.radians" );
    public static final String DEGREES = RotationStrings.getString( "units.degrees" );
    public static final String METERS = RotationStrings.getString( "units.meters" );

    public static final String RADIANS_ABBR = RotationStrings.getString( "units.rad" );
    public static final String DEGREES_ABBR = RotationStrings.getString( "units.degrees" );

    public static final String ACCELERATION = RotationStrings.getString( "variable.acceleration" );
    public static final String ACCELERATION_ABBR = RotationStrings.getString( "variable.a" );
    public static final String SEC_ABBR = RotationStrings.getString( "units.s" );
    public static final String METERS_ABBR = RotationStrings.getString( "units.m" );

    public static final String X = RotationStrings.getString( "variable.x" );
    public static final String Y = RotationStrings.getString( "variable.y" );
    public static final String V = RotationStrings.getString( "variable.v" );
    public static final String A = RotationStrings.getString( "variable.a" );

    //TODO: none of this will internationalize properly
    public static final String ANG_VEL_ABBR = RADIANS_ABBR + "/" + SEC_ABBR;
    public static final String ANG_VEL_DEG_ABBR = DEGREES_ABBR + "/" + SEC_ABBR;
    public static final String ANG_ACC_ABBR = RADIANS_ABBR + "/" + SEC_ABBR + "^2";//"rad/s^2";
    public static final String ANG_ACC_DEG_ABBR = DEGREES_ABBR + "/" + SEC_ABBR + "^2";//"degrees/s^2";
    public static final String VELOCITY_ABBR = METERS_ABBR + "/" + SEC_ABBR;//"m/s"
    public static final String ACCEL_ABBR = METERS_ABBR + "/" + SEC_ABBR + "^2";//"m/s^2";

    public static final String VELOCITY = RotationStrings.getString( "variable.velocity" );
    public static final String POSITION = RotationStrings.getString( "variable.position" );
    public static final String SPEED = RotationStrings.getString( "variable.speed" );


    public static String getString( String s ) {
//        return "!" + RotationResources.getInstance().getLocalizedString( s ) + "?";
        return RotationResources.getInstance().getLocalizedString( s );
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
