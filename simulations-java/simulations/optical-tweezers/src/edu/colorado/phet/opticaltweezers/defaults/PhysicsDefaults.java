/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.model.OTClock;


/**
 * PhysicsDefaults contains default settings for PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsDefaults {

    /* Not intended for instantiation */
    private PhysicsDefaults() {}
    
    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 750, 750 );
    public static final double MODEL_TO_VIEW_SCALE = 0.5;
    
    // Clock
    public static final boolean CLOCK_PAUSED = false;
    private static final int FRAME_RATE = 25; // fps, frames per second (wall time)
    private static final double MAX_DT = ( 1E-3 / FRAME_RATE );
    private static final double MIN_DT = 140E-16 * MAX_DT;
    private static final double DEFAULT_DT = MAX_DT;
    public static final DoubleRange CLOCK_DT_RANGE = new DoubleRange( MIN_DT, MAX_DT, DEFAULT_DT, 20 /* significantDecimalPlaces */ );
    public static final OTClock CLOCK = new OTClock( FRAME_RATE, CLOCK_DT_RANGE );
    public static final String CLOCK_CONTROL_PATTERN = "0E0";
    public static final String CLOCK_DISPLAY_PATTERN = "0.0000000000000000";
    
    // Fluid model, local origin at fluid's center
    public static final double FLUID_HEIGHT = 1000; // nm
    private static final double FLUID_Y_OFFSET = 100; // nm
    public static final Point2D FLUID_POSITION = new Point2D.Double( 0, FLUID_Y_OFFSET + ( FLUID_HEIGHT /2 ) ); // nm
    public static final double FLUID_ORIENTATION = Math.toRadians( 0 ); // left-to-right flow direction
    public static final DoubleRange FLUID_SPEED_RANGE = new DoubleRange( 0, 1000000, 0, 0 ); // nm/sec, min must be >0
    public static final DoubleRange FLUID_VISCOSITY_RANGE = new DoubleRange( 1E-12, 1E-2, Fluid.WATER_VISCOSITY, 5 ); // Pa*s
    public static final DoubleRange FLUID_TEMPERATURE_RANGE = new DoubleRange( 50, 350, 200, 0 ); // Kelvin

    // Laser model, local origin at center of objective
    public static final double LASER_DIAMETER_AT_OBJECTIVE = 1000; // nm
    public static final double LASER_DIAMETER_AT_WAIST = 500; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST = 600; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL = FLUID_Y_OFFSET; // nm
    public static final double LASER_WAVELENGTH = 1064; // nm, invisible IR
    public static final double LASER_VISIBLE_WAVELENGTH = 632; // nm, to be used by view components
    public static final Point2D LASER_POSITION = new Point2D.Double( 1200, FLUID_POSITION.getY() ); // nm
    public static final double LASER_ORIENTATION = Math.toRadians( -90 );
    public static final DoubleRange LASER_POWER_RANGE = new DoubleRange( 0, 1000, 500, 0 ); // mW
    public static final boolean LASER_RUNNING = true;
    
    // Bead model, local origin at center
    public static final Point2D BEAD_POSITION = new Point2D.Double( LASER_POSITION.getX(), LASER_POSITION.getY() ); // nm
    public static final double BEAD_ORIENTATION = Math.toRadians( 0 );
    public static final double BEAD_DIAMETER = 200; // nm
    public static final double BEAD_DENSITY = 1.05E-21; // g/nm^3, polystyrene

    // Control panel settings
    public static final boolean ELECTRIC_FIELD_SELECTED = false;
    public static final boolean BEAD_CHARGES_SELECTED = false;
    public static final boolean ALL_BEAD_CHARGES_SELECTED = true;
    public static final boolean TRAP_FORCE_SELECTED = true;
    public static final boolean WHOLE_BEAD_SELECTED = true;
    public static final boolean FLUID_DRAG_FORCE_SELECTED = false;
    public static final boolean BROWNIAN_FORCE_SELECTED = false;
    public static final boolean RULER_SELECTED = false;
    public static final boolean POSITION_HISTOGRAM_SELECTED = false;
    public static final boolean ADVANCED_VISIBLE = true;
    public static final boolean FLUID_CONTROLS_SELECTED = false;
    public static final boolean MOMENTUM_CHANGE_MODEL_SELECTED = false;
    public static final boolean POTENTIAL_ENERGY_CHART_SELECTED = false;
}
