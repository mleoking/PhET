package edu.colorado.phet.sugarandsaltsolutions.common.util;

/**
 * Utility class for converting units
 *
 * @author Sam Reid
 */
public class Units {
    //Convert angstroms to meters (SI)
    public static double angstromsToMeters( double angstroms ) {
        return angstroms * 1E-10;
    }

    //Convert picometers to meters (SI)
    public static double picometersToMeters( double picometers ) {
        return picometers * 1E-12;
    }

    public static double nanometersToMeters( double nanometers ) {
        return nanometers * 1E-9;
    }
}
