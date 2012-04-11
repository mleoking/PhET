// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;

import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;

/**
 * The units for the Fluid Pressure and Flow model are SI, and Units converts them to and from different units systems.
 * List of all units used in the sim.
 *
 * @author Sam Reid
 */
public class Units {
    public static final Unit ATMOSPHERE = new LinearUnit( ATM, 9.8692E-6, new DecimalFormat( "0.0000" ) {
//        @Override
//        public StringBuffer format( double number, StringBuffer result, FieldPosition fieldPosition ) {
//
//            //Show 0.9999 atm when lifted into the atmosphere so students don't think pressure doesn't decrease vs altitude
//            final StringBuffer answer = super.format( number, result, fieldPosition );
//            if ( answer.toString().equals( "1.0000" ) || number >= 1 ) {
//                return new StringBuffer( new DecimalFormat( "0.00" ).format( number ) );
//            }
//            else {
//                return answer;
//            }
//        }
    } );

    //Units for atmosphere, see http://en.wikipedia.org/wiki/Atmosphere_%28unit%29
//    public static final Unit PASCAL = new LinearUnit( FluidPressureAndFlowResources.Strings.PA, 1, new DecimalFormat( "0" ) );
    public static final Unit KILOPASCAL = new LinearUnit( Strings.K_PA, 1.0 / 1000.0, new DecimalFormat( "0" ) );
    public static final Unit PSI = new LinearUnit( FluidPressureAndFlowResources.Strings.PSI, 145.04E-6, new DecimalFormat( "0.00" ) );

    //Units for length
    public static final Unit METERS = new LinearUnit( FluidPressureAndFlowResources.Strings.M, 1, new DecimalFormat( "0.0" ) );
    public static final double FEET_PER_METER = 3.2808399;
    public static final Unit FEET = new LinearUnit( FT, FEET_PER_METER, new DecimalFormat( "0.0" ) );

    //Units for velocity
    public static final Unit METERS_PER_SECOND = new LinearUnit( M_PER_S, 1, new DecimalFormat( "0.0" ) );
    public static final Unit FEET_PER_SECOND = new LinearUnit( FT_PER_S, FEET_PER_METER, new DecimalFormat( "0.0" ) );

    //Convenience method for converting feet to meters
    public static double feetToMeters( double feet ) {
        return feet / FEET_PER_METER;
    }
}