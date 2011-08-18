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

    public static double metersCubedToLiters( double metersCubed ) {
        return metersCubed * 1000.0;
    }

    public static double numberToMoles( double number ) {
        return number / 6.02214179E23;
    }

    public static double litersToMetersCubed( double liters ) {
        return liters / 1000.0;
    }

    public static double molesPerMeterCubedToMolesPerLiter( Double molesPerMeterCubed ) {
        return molesPerMeterCubed / metersCubedToLiters( 1.0 );
    }

    public static double molesPerLiterToMolesPerMeterCubed( double molesPerLiter ) {
        return molesPerLiter / litersToMetersCubed( 1.0 );
    }

    public static double metersToPicometers( double spacing ) {
        return spacing / 1E-12;
    }
}