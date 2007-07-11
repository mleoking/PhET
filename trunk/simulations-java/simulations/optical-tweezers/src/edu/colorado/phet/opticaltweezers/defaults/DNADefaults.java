/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.opticaltweezers.control.ForcesControlPanel;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.model.OTClock;


/**
 * DNADefaults contains default settings for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNADefaults {

    /* Not intended for instantiation */
    private DNADefaults() {}
    
    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 750, 750 );
    public static final double MODEL_TO_VIEW_SCALE = 0.25;
    
    // Clock
    public static final boolean CLOCK_PAUSED = false;
    private static final int FRAME_RATE = 25; // fps, frames per second (wall time)
    private static final DoubleRange SLOW_DT_RANGE = new DoubleRange( 4E-18, 5E-16 );
    private static final DoubleRange FAST_DT_RANGE = new DoubleRange( 8E-7, 4E-5 );
    public static final double DEFAULT_DT = FAST_DT_RANGE.getMax();
    public static final OTClock CLOCK = new OTClock( FRAME_RATE, SLOW_DT_RANGE, FAST_DT_RANGE, DEFAULT_DT );
    public static final int CLOCK_TIME_COLUMNS = 8;

    // Microscope Slide model, local origin at slide's geometric center
    public static final double MICROSCOPE_SLIDE_CENTER_HEIGHT = 2000; // nm
    public static final double MICROSCOPE_SLIDE_EDGE_HEIGHT = 0.025 * MICROSCOPE_SLIDE_CENTER_HEIGHT; // nm
    private static final double MICROSCOPE_SLIDE_HEIGHT = MICROSCOPE_SLIDE_CENTER_HEIGHT + ( 2 * MICROSCOPE_SLIDE_EDGE_HEIGHT );
    private static final double MICROSCOPE_SLIDE_Y_OFFSET = 150; // nm
    public static final Point2D MICROSCOPE_SLIDE_POSITION = new Point2D.Double( 0, MICROSCOPE_SLIDE_Y_OFFSET + ( MICROSCOPE_SLIDE_HEIGHT /2 ) ); // nm
    public static final double MICROSCOPE_SLIDE_ORIENTATION = Math.toRadians( 0 );
    
    // Fluid model
    public static final boolean FLUID_ENABLED = true;
    public static final double FLUID_DIRECTION = Math.toRadians( 0 ); // left-to-right flow direction
    public static final DoubleRange FLUID_SPEED_RANGE = new DoubleRange( 0, 1000000, 0 ); // nm/sec, min must be >0
    public static final DoubleRange FLUID_VISCOSITY_RANGE = new DoubleRange( 1E-4, 1E-2, Fluid.WATER_VISCOSITY ); // Pa*s
    public static final DoubleRange FLUID_TEMPERATURE_RANGE = new DoubleRange( 50, 350, 298 ); // Kelvin

    // Laser model, local origin at center of objective
    public static final double LASER_DIAMETER_AT_OBJECTIVE = 1800; // nm, chosen so that beam shape is similar to PhysicsDefaults
    public static final double LASER_DIAMETER_AT_WAIST = 500; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST = ( MICROSCOPE_SLIDE_HEIGHT / 2 ) + MICROSCOPE_SLIDE_Y_OFFSET; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL = 200; // nm
    public static final double LASER_WAVELENGTH = 1064; // nm, invisible IR
    public static final double LASER_VISIBLE_WAVELENGTH = 632; // nm, to be used by view components
    public static final Point2D LASER_POSITION = new Point2D.Double( 2400, MICROSCOPE_SLIDE_POSITION.getY() ); // nm
    public static final double LASER_ORIENTATION = Math.toRadians( -90 );
    public static final DoubleRange LASER_POWER_RANGE = new DoubleRange( 0, 1000, 500 ); // mW
    public static final boolean LASER_RUNNING = true;
    public static final DoubleRange LASER_TRAP_FORCE_RATIO = new DoubleRange( 0.1, 5, 0.179 );
    public static final DoubleRange LASER_ELECTRIC_FIELD_SCALE_RANGE = new DoubleRange( 0.01, 10, 1 );
    
    // Bead model, local origin at center
    public static final Point2D BEAD_POSITION = new Point2D.Double( LASER_POSITION.getX(), LASER_POSITION.getY() ); // nm
    public static final double BEAD_ORIENTATION = Math.toRadians( 0 );
    public static final double BEAD_DIAMETER = 200; // nm
    public static final double BEAD_DENSITY = 1.05E-21; // g/nm^3, polystyrene
    public static final DoubleRange BEAD_DT_SUBDIVISION_THRESHOLD_RANGE = new DoubleRange( FAST_DT_RANGE.getMax() * 1E-2, FAST_DT_RANGE.getMax(), 1E-6 );
    public static final IntegerRange BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE = new IntegerRange( 1, 1000, 10 );
    public static final DoubleRange BEAD_BROWNIAN_MOTION_SCALE_RANGE = new DoubleRange( 0, 5, 1.3 );
    public static final boolean BEAD_BROWNIAN_MOTION_ENABLED = true;
    public static final DoubleRange BEAD_VERLET_ACCELERATION_SCALE_RANGE = new DoubleRange( 1E-8, 1E-4, 1E-6 );
    
    // DNA Strand model
    public static final double DNA_CONTOUR_LENGTH = 2413; // nm
    public static final double DNA_PERSISTENCE_LENGTH = 50; // nm, double strand
    public static final int DNA_NUMBER_OF_SPRINGS = 39; // nm
    public static final DoubleRange DNA_SPRING_CONSTANT_RANGE = new DoubleRange( 2, 20, 10 );
    public static final DoubleRange DNA_DRAG_COEFFICIENT_RANGE = new DoubleRange( 0.1, 2, 0.5 );
    public static final DoubleRange DNA_EVOLUTION_DT_RANGE = new DoubleRange( 0.05, 0.2, 0.1 );
    public static final DoubleRange DNA_KICK_CONSTANT_RANGE = new DoubleRange( 10, 100, 50, 0 );
    public static final IntegerRange DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE = new IntegerRange( 1, 100, 30 );
    public static final DoubleRange DNA_FLUID_DRAG_COEFFICIENT_RANGE = new DoubleRange( 0, 0.00020, 0.000015 );
    public static final boolean DNA_PIVOTS_VISIBLE = false;
    public static final boolean DNA_EXTENSION_VISIBLE = false;
    
    // Control panel settings
    public static final boolean LASER_BEAM_VISIBLE = true;
    public static final boolean LASER_ELECTRIC_FIELD_VISIBLE = false;
    public static final boolean TRAP_FORCE_SELECTED = false;
    public static final String HORIZONTAL_TRAP_FORCE_CHOICE = ForcesControlPanel.CHOICE_WHOLE_BEAD;
    public static final boolean FLUID_DRAG_FORCE_SELECTED = false;
    public static final boolean DNA_FORCE_SELECTED = false;
    public static final boolean BROWNIAN_MOTION_ENABLED = true;
    public static final boolean POSITION_HISTOGRAM_SELECTED = false;
    public static final boolean POTENTIAL_ENERGY_CHART_SELECTED = false;
    public static final boolean RULER_SELECTED = false;
    public static final boolean FLUID_CONTROLS_SELECTED = false;
    public static final boolean MOMENTUM_CHANGE_MODEL_SELECTED = false;
    public static final boolean VECTOR_VALUES_VISIBLE = false;
    public static final boolean VECTOR_COMPONENTS_VISIBLE = false;
    
    // Ruler
    public static final double RULER_Y_POSITION = LASER_POSITION.getY() + ( BEAD_DIAMETER / 2 ) + 30; // nm, just below center of trap
    public static final int RULER_MAJOR_TICK_INTERVAL = 200; // nm
    public static final int RULER_MINOR_TICKS_BETWEEN_MAJORS = 3;
    
    // Potential Energy chart
    public static final double POTENTIAL_ENERGY_SAMPLE_WIDTH = 5; // nm
    
    // View stuff
    public static final double FORCE_VECTOR_REFERENCE_LENGTH = 125; // pixels
}
