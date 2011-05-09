// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.opticaltweezers.model.OTClock;


/**
 * PhysicsDefaults contains default settings for PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsDefaults {

    /* Not intended for instantiation */
    private PhysicsDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final int FRAME_RATE = GlobalDefaults.FRAME_RATE;
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;
    public static final DoubleRange SLOW_DT_RANGE = new DoubleRange( 4E-18, 5E-16 );
    public static final DoubleRange FAST_DT_RANGE = new DoubleRange( 1E-7, 4E-5 );
    public static final double DEFAULT_DT = FAST_DT_RANGE.getMax();
    public static final OTClock CLOCK = new OTClock( FRAME_RATE, SLOW_DT_RANGE, FAST_DT_RANGE, DEFAULT_DT );
    
    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 750, 750 );
    public static final double MODEL_TO_VIEW_SCALE = 0.5;
    
    // Microscope Slide model, local origin at slide's geometric center
    public static final double MICROSCOPE_SLIDE_CENTER_HEIGHT = 1000; // nm
    public static final double MICROSCOPE_SLIDE_EDGE_HEIGHT = 0.025 * MICROSCOPE_SLIDE_CENTER_HEIGHT; // nm
    private static final double MICROSCOPE_SLIDE_HEIGHT = MICROSCOPE_SLIDE_CENTER_HEIGHT + ( 2 * MICROSCOPE_SLIDE_EDGE_HEIGHT );
    private static final double MICROSCOPE_SLIDE_Y_OFFSET = 75; // nm
    public static final Point2D MICROSCOPE_SLIDE_POSITION = new Point2D.Double( 0, MICROSCOPE_SLIDE_Y_OFFSET + ( MICROSCOPE_SLIDE_HEIGHT /2 ) ); // nm
    public static final double MICROSCOPE_SLIDE_ORIENTATION = Math.toRadians( 0 );

    // Fluid model
    public static final double FLUID_DIRECTION = GlobalDefaults.FLUID_DIRECTION;
    public static final DoubleRange FLUID_SPEED_RANGE = GlobalDefaults.FLUID_SPEED_RANGE;
    public static final DoubleRange FLUID_VISCOSITY_RANGE = GlobalDefaults.FLUID_VISCOSITY_RANGE;
    public static final DoubleRange FLUID_TEMPERATURE_RANGE = GlobalDefaults.FLUID_TEMPERATURE_RANGE;
    public static final DoubleRange FLUID_APT_CONCENTRATION_RANGE = GlobalDefaults.FLUID_APT_CONCENTRATION_RANGE;
    
    // Laser model
    public static final double LASER_DIAMETER_AT_OBJECTIVE = 1000; // nm
    public static final double LASER_DIAMETER_AT_WAIST = 500; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST = ( MICROSCOPE_SLIDE_HEIGHT / 2 ) + MICROSCOPE_SLIDE_Y_OFFSET; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL = 100; // nm
    public static final Point2D LASER_POSITION = new Point2D.Double( 1200, MICROSCOPE_SLIDE_POSITION.getY() ); // nm
    public static final double LASER_WAVELENGTH = GlobalDefaults.LASER_WAVELENGTH;
    public static final double LASER_VISIBLE_WAVELENGTH = GlobalDefaults.LASER_VISIBLE_WAVELENGTH;
    public static final double LASER_ORIENTATION = GlobalDefaults.LASER_ORIENTATION;
    public static final DoubleRange LASER_POWER_RANGE = GlobalDefaults.LASER_POWER_RANGE;
    public static final boolean LASER_RUNNING = GlobalDefaults.LASER_RUNNING;
    public static final DoubleRange LASER_TRAP_FORCE_RATIO = GlobalDefaults.LASER_TRAP_FORCE_RATIO;
    public static final DoubleRange LASER_ELECTRIC_FIELD_SCALE_RANGE = GlobalDefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE;
    
    // Bead model
    public static final double BEAD_DIAMETER = GlobalDefaults.BEAD_DIAMETER;
    public static final double BEAD_DENSITY = GlobalDefaults.BEAD_DENSITY;
    public static final Point2D BEAD_POSITION = new Point2D.Double( LASER_POSITION.getX(), LASER_POSITION.getY() ); // nm
    public static final double BEAD_ORIENTATION = GlobalDefaults.BEAD_ORIENTATION;
    public static final IntegerRange BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE = new IntegerRange( 1, 2000, 100 );
    public static final DoubleRange BEAD_DT_SUBDIVISION_THRESHOLD_RANGE = new DoubleRange( FAST_DT_RANGE.getMin(), FAST_DT_RANGE.getMax(), 1E-6 );
    public static final DoubleRange BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE = new DoubleRange( FAST_DT_RANGE.getMin(), FAST_DT_RANGE.getMax(), 1E-6 );
    public static final DoubleRange BEAD_VACUUM_FAST_DT_RANGE = new DoubleRange( FAST_DT_RANGE.getMin(), FAST_DT_RANGE.getMax(), 1E-5 );
    public static final DoubleRange BEAD_BROWNIAN_MOTION_SCALE_RANGE = GlobalDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE;
    public static final boolean BEAD_BROWNIAN_MOTION_ENABLED = GlobalDefaults.BEAD_BROWNIAN_MOTION_ENABLED;
    public static final DoubleRange BEAD_VERLET_ACCELERATION_SCALE_RANGE = GlobalDefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE;
    public static final IntegerRange BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE = GlobalDefaults.BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE;
    public static final DoubleRange BEAD_VACUUM_FAST_THRESHOLD_RANGE = GlobalDefaults.BEAD_VACUUM_FAST_THRESHOLD_RANGE;
    public static final DoubleRange BEAD_VACUUM_FAST_POWER_RANGE = GlobalDefaults.BEAD_VACUUM_FAST_POWER_RANGE;
    public static final DoubleRange CHARGE_MOTION_SCALE_RANGE = new DoubleRange( 0, 1, 0.25 );

    // Ruler
    public static final double RULER_Y_POSITION = MICROSCOPE_SLIDE_POSITION.getY() + ( MICROSCOPE_SLIDE_CENTER_HEIGHT / 2 ) - 150; // nm, just above bottom of slide
    public static final int RULER_MAJOR_TICK_INTERVAL = 100; // nm
    public static final int RULER_MINOR_TICKS_BETWEEN_MAJORS = 9;
    
    // Charts
    public static final double POTENTIAL_ENERGY_SAMPLE_WIDTH = GlobalDefaults.POTENTIAL_ENERGY_SAMPLE_WIDTH;
    
    // Force vectors
    public static final double FORCE_VECTOR_REFERENCE_LENGTH = GlobalDefaults.FORCE_VECTOR_REFERENCE_LENGTH;
    public static final boolean FORCE_VECTOR_COMPONENTS_VISIBLE = GlobalDefaults.FORCE_VECTOR_COMPONENTS_VISIBLE;
    
    // Dialog locations
    public static final Point POSITION_HISTOGRAM_DIALOG_OFFSET = GlobalDefaults.POSITION_HISTOGRAM_DIALOG_OFFSET;
    public static final Point FLUID_CONTROLS_DIALOG_OFFSET = new Point( 10, 400 ); // offset in pixels from upper-left corner of parent frame
    
    // Control panel settings
    public static final boolean LASER_BEAM_VISIBLE = true;
    public static final boolean LASER_ELECTRIC_FIELD_VISIBLE = false;
    public static final boolean CHARGE_HIDDEN_SELECTED = true;
    public static final boolean CHARGE_DISTRIBUTION_SELECTED = false;
    public static final boolean CHARGE_EXCESS_SELECTED = false;
    public static final boolean TRAP_FORCE_SELECTED = false;
    public static final boolean FLUID_DRAG_FORCE_SELECTED = false;
    public static final boolean POSITION_HISTOGRAM_SELECTED = false;
    public static final boolean POTENTIAL_ENERGY_CHART_SELECTED = false;
    public static final boolean RULER_SELECTED = false;
    public static final boolean BEAD_IN_FLUID_SELECTED = true;
    public static final boolean BEAD_IN_VACUUM_SELECTED = !(BEAD_IN_FLUID_SELECTED);
    public static final boolean FLUID_CONTROLS_SELECTED = false;
}
