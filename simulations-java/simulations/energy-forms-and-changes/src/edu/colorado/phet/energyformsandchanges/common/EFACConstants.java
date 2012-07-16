// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

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
    public static final double MAX_HEAT_EXCHANGE_TIME_STEP = SIM_TIME_PER_TICK_NORMAL;

    // Constants used for creating projections that have a 3D-ish look.
    public static final double Z_TO_X_OFFSET_MULTIPLIER = -0.25;
    public static final double Z_TO_Y_OFFSET_MULTIPLIER = -0.25;
    public static final Function1<Double, ImmutableVector2D> MAP_Z_TO_XY_OFFSET = new Function1<Double, ImmutableVector2D>() {
        public ImmutableVector2D apply( Double zValue ) {
            return new ImmutableVector2D( zValue * Z_TO_X_OFFSET_MULTIPLIER, zValue * Z_TO_Y_OFFSET_MULTIPLIER );
        }
    };

    // For comparing temperatures.
    public static final double SIGNIFICANT_TEMPERATURE_DIFFERENCE = 1E-3; // In degrees K.

    // Constants and constant functions for energy chunk mapping.
    public static final double ENERGY_PER_CHUNK = 40000;
    public static final Function1<Double, Integer> ENERGY_TO_NUM_CHUNKS_MAPPER = new Function1<Double, Integer>() {
        private final Function.LinearFunction MAPPER_TO_DOUBLE = new Function.LinearFunction( ENERGY_PER_CHUNK, 45000, 1, 2 ); // Brick has 1 at 0 C, 2 at room temp.  Brick SH = 800

        public Integer apply( Double energy ) {
            return Math.max( (int) Math.round( MAPPER_TO_DOUBLE.evaluate( energy ) ), 0 );
        }
    };

    // Threshold for deciding when two temperatures can be considered equal.
    public static final double TEMPERATURES_EQUAL_THRESHOLD = 1E-6; // In Kelvin.

    // Colors that are used in multiple places.
    public static final float NOMINAL_WATER_OPACITY = 0.75f;
    public static final Color WATER_COLOR = new Color( 175, 238, 238, (int) ( Math.round( NOMINAL_WATER_OPACITY * 255 ) ) );
    public static final Color FIRST_TAB_BACKGROUND_COLOR = new Color( 245, 246, 247 );
    public static final Color CONTROL_PANEL_BACKGROUND_COLOR = new Color( 255, 255, 224 );

    // Model-view transform scale factor for Energy Systems tab.
    public static final double ENERGY_SYSTEMS_MVT_SCALE_FACTOR = 2200;
}
