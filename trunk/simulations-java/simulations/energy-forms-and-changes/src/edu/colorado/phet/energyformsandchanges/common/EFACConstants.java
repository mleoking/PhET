// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Shared constants used in multiple locations within the sim.
 *
 * @author John Blanco
 */
public class EFACConstants {

    public static final double ROOM_TEMPERATURE = 296; // In Kelvin.
    public static final double FREEZING_POINT_TEMPERATURE = 273.15; // In Kelvin.
    public static final double BOILING_POINT_TEMPERATURE = 373.15; // In Kelvin.

    // Time values for normal and fast-forward motion.
    public static final double FRAMES_PER_SECOND = 30.0;
    public static final double SIM_TIME_PER_TICK_NORMAL = 1 / FRAMES_PER_SECOND;
    public static final double SIM_TIME_PER_TICK_FAST_FORWARD = SIM_TIME_PER_TICK_NORMAL * 4;

    // Constants used for creating projections that have a 3D-ish look.
    public static final double Z_TO_X_OFFSET_MULTIPLIER = 0.25;
    public static final double Z_TO_Y_OFFSET_MULTIPLIER = 0.25;
    public static Function1<Double, Dimension2D> MAP_Z_TO_XY_OFFSET = new Function1<Double, Dimension2D>() {
        public Dimension2D apply( Double zValue ) {
            return new PDimension( zValue * Z_TO_X_OFFSET_MULTIPLIER, zValue * Z_TO_Y_OFFSET_MULTIPLIER );
        }
    };

    // For comparing temperatures.
    public static final double SIGNIFICANT_TEMPERATURE_DIFFERENCE = 1E-3; // In degrees K.

    public static final double ENERGY_PER_CHUNK = 40000;

    // Constant function for mapping energy level to number of energy chunks.
    public static Function1<Double, Integer> ENERGY_TO_NUM_CHUNKS_MAPPER = new Function1<Double, Integer>() {
        //                private Function.LinearFunction MAPPER_TO_DOUBLE = new Function.LinearFunction( 42000, 400000, 2, 25 );
//        private Function.LinearFunction MAPPER_TO_DOUBLE = new Function.LinearFunction( 0, 414000, 0, 25 ); // Linear from absolute zero.
        private Function.LinearFunction MAPPER_TO_DOUBLE = new Function.LinearFunction( ENERGY_PER_CHUNK, 45000, 1, 2 ); // Brick has 1 at 0 C, 2 at room temp.  Brick SH = 800

        public Integer apply( Double energy ) {
            return Math.max( (int) Math.round( MAPPER_TO_DOUBLE.evaluate( energy ) ), 0 );
        }
    };

}
