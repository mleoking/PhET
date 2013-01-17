// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.energyformsandchanges.intro.model.Brick;

/**
 * Shared constants used in multiple locations within the sim.
 *
 * @author John Blanco
 */
public class EFACConstants {

//    public static final double ROOM_TEMPERATURE = 296; // In Kelvin.
    public static final double ROOM_TEMPERATURE = 360.15; // In Kelvin.
    public static final double FREEZING_POINT_TEMPERATURE = 273.15; // In Kelvin.
    public static final double BOILING_POINT_TEMPERATURE = 373.15; // In Kelvin.

    // Time values for normal and fast-forward motion.
    public static final double FRAMES_PER_SECOND = 30.0;
    public static final double SIM_TIME_PER_TICK_NORMAL = 1 / FRAMES_PER_SECOND;
    public static final double SIM_TIME_PER_TICK_FAST_FORWARD = SIM_TIME_PER_TICK_NORMAL * 4;
    public static final double MAX_HEAT_EXCHANGE_TIME_STEP = SIM_TIME_PER_TICK_NORMAL;

    // Constants used for creating projections that have a 3D-ish look.
    public static final double Z_TO_X_OFFSET_MULTIPLIER = -0.25;
    public static final double Z_TO_Y_OFFSET_MULTIPLIER = -0.25;
    public static final Function1<Double, Vector2D> MAP_Z_TO_XY_OFFSET = new Function1<Double, Vector2D>() {
        public Vector2D apply( Double zValue ) {
            return new Vector2D( zValue * Z_TO_X_OFFSET_MULTIPLIER, zValue * Z_TO_Y_OFFSET_MULTIPLIER );
        }
    };

    // For comparing temperatures.
    public static final double SIGNIFICANT_TEMPERATURE_DIFFERENCE = 1E-3; // In degrees K.

    // Constant function for energy chunk mapping. The basis for this function
    // is that the brick has 2 energy chunks at room temp, one at the freezing
    // point of water.
    private static double LOW_ENERGY_FOR_MAP_FUNCTION = Brick.ENERGY_AT_WATER_FREEZING_TEMPERATURE * 0.95;
    private static double HIGH_ENERGY_FOR_MAP_FUNCTION = Brick.ENERGY_AT_ROOM_TEMPERATURE * 1.05;
    public static final Function1<Double, Integer> ENERGY_TO_NUM_CHUNKS_MAPPER = new Function1<Double, Integer>() {
        private final Function.LinearFunction MAPPER_TO_DOUBLE = new Function.LinearFunction( LOW_ENERGY_FOR_MAP_FUNCTION,
                                                                                              HIGH_ENERGY_FOR_MAP_FUNCTION,
                                                                                              1,
                                                                                              2 );

        public Integer apply( Double energy ) {
            return Math.max( (int) Math.round( MAPPER_TO_DOUBLE.evaluate( energy ) ), 0 );
        }
    };
    public static final double ENERGY_PER_CHUNK = HIGH_ENERGY_FOR_MAP_FUNCTION - LOW_ENERGY_FOR_MAP_FUNCTION;

    // Threshold for deciding when two temperatures can be considered equal.
    public static final double TEMPERATURES_EQUAL_THRESHOLD = 1E-6; // In Kelvin.

    // Constant used by all of the "energy systems" in order to keep the amount
    // of energy generated, converted, and consumed consistent.
    public static final double MAX_ENERGY_RATE = 100; // In joules/sec, but not very meaningful.

    // Colors that are used in multiple places.
    public static final float NOMINAL_WATER_OPACITY = 0.75f;
    public static final Color WATER_COLOR_OPAQUE = new Color( 175, 238, 238 );
    public static final Color WATER_COLOR_IN_BEAKER = new Color( 175, 238, 238, (int) ( Math.round( NOMINAL_WATER_OPACITY * 255 ) ) );
    //    public static final Color FIRST_TAB_BACKGROUND_COLOR = new Color( 245, 246, 247 );
    public static final Color FIRST_TAB_BACKGROUND_COLOR = new Color( 238, 232, 170 );
    public static final Color CONTROL_PANEL_BACKGROUND_COLOR = new Color( 255, 255, 224 );

    // Model-view transform scale factor for Energy Systems tab.
    public static final double ENERGY_SYSTEMS_MVT_SCALE_FACTOR = 2200;

    // Constants that control the speed of the energy chunks
    public static final double ENERGY_CHUNK_VELOCITY = 0.04; // In meters/sec.
}
