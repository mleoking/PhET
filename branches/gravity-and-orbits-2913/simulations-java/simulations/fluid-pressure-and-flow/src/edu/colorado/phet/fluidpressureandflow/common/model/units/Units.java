// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;
import java.text.FieldPosition;

import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;

/**
 * The units for the Fluid Pressure and Flow model are SI, and Units converts them to and from different units systems.
 *
 * @author Sam Reid
 */
public class Units {
    public static Unit ATMOSPHERE = new LinearUnit( FPAFStrings.ATMOSPHERES, FPAFStrings.ATM, 9.8692E-6, new DecimalFormat( "0.0000" ) {
        @Override
        public StringBuffer format( double number, StringBuffer result, FieldPosition fieldPosition ) {
            final StringBuffer answer = super.format( number, result, fieldPosition );
            if ( answer.toString().equals( "1.0000" ) || number >= 1 ) {
                return new StringBuffer( new DecimalFormat( "0.00" ).format( number ) );//Show 0.9999 atm when lifted into the atmosphere so students don't think pressure doesn't decrease vs altitude
            }
            else {
                return answer;
            }
        }
    } );//http://en.wikipedia.org/wiki/Atmosphere_%28unit%29
    public static Unit PASCAL = new LinearUnit( FPAFStrings.PASCALS, FPAFStrings.PA, 1, new DecimalFormat( "0" ) );
    public static Unit PSI = new LinearUnit( FPAFStrings.POUNDS_PER_SQUARE_INCH, FPAFStrings.PSI, 145.04E-6, new DecimalFormat( "0.00" ) );

    public static Unit METERS = new LinearUnit( FPAFStrings.METERS, FPAFStrings.M, 1, new DecimalFormat( "0.0" ) );
    public static final double FEET_PER_METER = 3.2808399;
    public static Unit FEET = new LinearUnit( FPAFStrings.FEET, FPAFStrings.FT, FEET_PER_METER, new DecimalFormat( "0.0" ) );

    public static Unit METERS_PER_SECOND = new LinearUnit( FPAFStrings.METERS_PER_SECOND, FPAFStrings.M_PER_S, 1, new DecimalFormat( "0.0" ) );
    public static Unit FEET_PER_SECOND = new LinearUnit( "feet-per-second", FPAFStrings.FT_PER_S, FEET_PER_METER, new DecimalFormat( "0.0" ) );

    public double feetToMeters( double feet ) {
        return feet * 0.3048;
    }

}
