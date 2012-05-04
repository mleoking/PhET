// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;

/**
 * The pool is the region of water in which the sensors can be submerged.
 *
 * @author Sam Reid
 */
public class Pool {

    //10 foot deep pool, a customary depth for the deep end in the United States
    public static final double DEFAULT_HEIGHT = new Units().feetToMeters( 10 );
    public static final double WIDTH = 4;

    //Compute the pressure above the ground
    public static double getPressureAboveGround( final double y, final boolean atmosphere, final double standardAirPressure, final double gravity ) {
        LinearFunction f = new LinearFunction( 0, 500, standardAirPressure, FluidPressureAndFlowModel.EARTH_AIR_PRESSURE_AT_500_FT );//see http://www.engineeringtoolbox.com/air-altitude-pressure-d_462.html
        return atmosphere ? f.evaluate( y ) * gravity / 9.8 : 0.0;
    }
}