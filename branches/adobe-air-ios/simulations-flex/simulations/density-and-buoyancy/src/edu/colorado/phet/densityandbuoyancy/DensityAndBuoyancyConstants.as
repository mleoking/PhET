//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy {
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;

import flash.geom.ColorTransform;

import mx.formatters.NumberBaseRoundType;
import mx.formatters.NumberFormatter;

/**
 * Constants used throughout the Density and Buoyancy simulations.
 */
public class DensityAndBuoyancyConstants {
    public static const MIN_MASS: Number = 1;
    public static const MAX_MASS: Number = 10;
    public static const MIN_VOLUME: Number = litersToMetersCubed( 1 );
    public static const MAX_VOLUME: Number = litersToMetersCubed( 10 );
    public static const MIN_DENSITY: Number = kgLtoSI( 0.1 );
    public static const MAX_DENSITY: Number = kgLtoSI( 3 );

    public static const POOL_WIDTH_X: Number = 1;
    public static const POOL_DEPTH_Z: Number = 0.3;
    public static const POOL_HEIGHT_Y: Number = 0.4;
    public static const POOL_HEIGHT_Y_EXTENDED: Number = POOL_HEIGHT_Y + 0.02245 / (POOL_WIDTH_X * POOL_DEPTH_Z);//to make sure max size objects don't overflow the pool in buoyancy playground
    public static const DEFAULT_BLOCK_MASS: Number = 2;
    /**
     * Scale up all box2d computations so that they are in the sweet spot for ranges for box2d.
     * Have to keep within a range so that velocity doesn't exceed about 200, see B2Settings.b2_maxLinearVelocity.
     * When values outside of box2d's "good range" are used, the physics engine will produce inaccurate results, such as blocks not lying flush on the ground.
     *
     * For the full limitations of Box2D, please see B2Settings.  Approximate resolutions must be right and objects must not exceed certain thresholds (including spatial bounds)
     */
    public static const SCALE_BOX2D: Number = 10;
    public static const GRAVITY: Number = 9.8;
    public static const DEFAULT_BLOCK_WATER_OFFSET: Number = 10 / DensityAndBuoyancyModel.DISPLAY_SCALE;

    //Offset the objects slightly to prevent intersections
    public static const FUDGE_FACTOR: Number = 1000.0 / DensityAndBuoyancyModel.DISPLAY_SCALE;
    public static const VERTICAL_GROUND_OFFSET_AWAY_3D: Number = -FUDGE_FACTOR;  //This number was hand-tuned so that no rendering artifacts (flickering faces) occur, but may need to change if scale or other parameters change
    public static const FUDGE_FACTOR_DZ: Number = FUDGE_FACTOR;//Objects shouldn't exactly overlap in the z-dimension either

    //Flex properties that we couldn't find as enum values in Flex
    public static const FLEX_UNDERLINE: String = "underline";
    public static const FLEX_NONE: String = "none";
    public static const FLEX_FONT_SIZE: String = "fontSize";
    public static const FLEX_FONT_WEIGHT: String = "fontWeight";
    public static const FLEX_FONT_BOLD: String = "bold";
    public static const FLEX_TEXT_DECORATION: String = "textDecoration";
    public static const CONTROL_PANEL_COLOR: Number = 0xfafad7;//Color recommended by Kathy to be a pale yellow background for control panels

    public static const LARGE_BLOCK_WIDTH: Number = 0.18;
    public static const CONTROL_INSET: Number = 5;
    public static const SLIDER_READOUT_TEXT_FIELD_WIDTH: Number = 50;
    public static const YELLOW: ColorTransform = new ColorTransform( 1, 1, 0 );
    public static const RED: ColorTransform = new ColorTransform( 1, 0, 0 );
    public static const GREEN: ColorTransform = new ColorTransform( 0, 0.9, 0 );
    public static const BLUE: ColorTransform = new ColorTransform( 0, 0, 1 );
    public static const PURPLE: ColorTransform = new ColorTransform( 0.75, 0, 0.75 );
    public static const STYROFOAM_MAX_MASS: Number = 3;//To keep the volume small enough so the pool doesn't overflow
    public static const WOOD_MAX_MASS: Number = 8;
    public static const MIN_FLUID_DENSITY: Number = kgLtoSI( 0 );
    public static const MAX_FLUID_DENSITY: Number = kgLtoSI( 2 );
    //To keep the volume small enough so the pool doesn't overflow

    public static const GRAVITY_COLOR: int = 0x0000FF;
    public static const BUOYANCY_COLOR: int = 0xFF00FF;
    public static const CONTACT_COLOR: int = 0xFF8800;

    public static const formatter: NumberFormatter = new NumberFormatter();
    formatter.rounding = NumberBaseRoundType.NEAREST; //otherwise high frequency fluctuations around 1.00 and 0.99
    formatter.precision = 2;

    //So that away3d faces don't overlap
    private static function kgLtoSI( number: Number ): Number {
        return number / 0.001;
    }

    public static function litersToMetersCubed( x: Number ): Number {
        return 0.001 * x;
    }

    public static function metersCubedToLiters( v: Number ): Number {
        return v / 0.001;
    }

    public static function format( number: Number ): String {
        return formatter.format( number );
    }

    public static function formatWithPrecision( number: Number, precision: int ): String {
        var fmt: NumberFormatter = new NumberFormatter();
        fmt.rounding = formatter.rounding;
        fmt.precision = precision;
        return fmt.format( number );
    }
}
}