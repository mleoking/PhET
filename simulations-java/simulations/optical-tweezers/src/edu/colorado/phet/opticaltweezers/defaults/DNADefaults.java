/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.opticaltweezers.control.ForcesControlPanel;
import edu.colorado.phet.opticaltweezers.control.LaserDisplayControlPanel;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
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
    private static final double MAX_DT = ( 1E-3 / FRAME_RATE );
    private static final double MIN_DT = 140E-16 * MAX_DT;
    private static final double DEFAULT_DT = MAX_DT;
    public static final DoubleRange CLOCK_DT_RANGE = new DoubleRange( MIN_DT, MAX_DT, DEFAULT_DT );
    public static final OTClock CLOCK = new OTClock( FRAME_RATE, CLOCK_DT_RANGE );
    public static final String CLOCK_CONTROL_PATTERN = "0.0E0";
    public static final String CLOCK_TIME_PATTERN = "0.0000000000000000000";
    public static final int CLOCK_TIME_COLUMNS = 15;
    
    // Fluid model, local origin at fluid's center
    public static final double FLUID_HEIGHT = 2000; // nm
    private static final double FLUID_Y_OFFSET = 200; // nm
    public static final Point2D FLUID_POSITION = new Point2D.Double( 0, FLUID_Y_OFFSET + ( FLUID_HEIGHT /2 ) ); // nm
    public static final double FLUID_ORIENTATION = Math.toRadians( 0 ); // left-to-right flow direction
    public static final DoubleRange FLUID_SPEED_RANGE = new DoubleRange( 0, 1000000, 0 ); // nm/sec, min must be >0
    public static final DoubleRange FLUID_VISCOSITY_RANGE = new DoubleRange( 1E-4, 1E-2, Fluid.WATER_VISCOSITY ); // Pa*s
    public static final DoubleRange FLUID_TEMPERATURE_RANGE = new DoubleRange( 50, 350, 200 ); // Kelvin

    // Laser model, local origin at center of objective
    public static final double LASER_DIAMETER_AT_OBJECTIVE = 1800; // nm, chosen so that beam shape is similar to PhysicsDefaults
    public static final double LASER_DIAMETER_AT_WAIST = 500; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST = ( FLUID_HEIGHT / 2 ) + FLUID_Y_OFFSET; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL = 200; // nm
    public static final double LASER_WAVELENGTH = 1064; // nm, invisible IR
    public static final double LASER_VISIBLE_WAVELENGTH = 632; // nm, to be used by view components
    public static final Point2D LASER_POSITION = new Point2D.Double( 2400, FLUID_POSITION.getY() ); // nm
    public static final double LASER_ORIENTATION = Math.toRadians( -90 );
    public static final DoubleRange LASER_POWER_RANGE = new DoubleRange( 0, 1000, 500 ); // mW
    public static final boolean LASER_RUNNING = true;
    
    // Bead model, local origin at center
    public static final Point2D BEAD_POSITION = new Point2D.Double( LASER_POSITION.getX(), LASER_POSITION.getY() ); // nm
    public static final double BEAD_ORIENTATION = Math.toRadians( 0 );
    public static final double BEAD_DIAMETER = 200; // nm
    public static final double BEAD_DENSITY = 1.05E-21; // g/nm^3, polystyrene
    public static final DoubleRange BEAD_DT_SUBDIVISION_THRESHOLD_RANGE = new DoubleRange( CLOCK_DT_RANGE.getMax() * 1E-2, CLOCK_DT_RANGE.getMax(), 1E-6 );
    public static final IntegerRange BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE = new IntegerRange( 1, 1000, 10 );
    public static final DoubleRange BEAD_BROWNIAN_MOTION_SCALE_RANGE = new DoubleRange( 0, 5, 1 );
    
    // DNA Strand model
    public static final double DNA_CONTOUR_LENGTH = 2413; // nm
    public static final double DNA_PERSISTENCE_LENGTH = 50; // nm, double strand
    public static final int DNA_NUMBER_OF_SPRINGS = 39; // nm
    public static final DoubleRange DNA_SPRING_CONSTANT_RANGE = new DoubleRange( 2, 20, 10 );
    public static final DoubleRange DNA_DRAG_COEFFICIENT_RANGE = new DoubleRange( 0.1, 2, 0.5 );
    public static final DoubleRange DNA_EVOLUTION_DT_RANGE = new DoubleRange( 0.05, 0.2, 0.1 );
    public static final DoubleRange DNA_KICK_CONSTANT_RANGE = new DoubleRange( 10, 100, 50, 0 );
    public static final IntegerRange DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE = new IntegerRange( 1, 100, 5 );
    public static final boolean DNA_PIVOTS_VISIBLE = true;
    public static final boolean DNA_EXTENSION_VISIBLE = false;
    
    // Control panel settings
    public static final String LASER_DISPLAY_CHOICE = LaserDisplayControlPanel.CHOICE_BEAM;
    public static final boolean TRAP_FORCE_SELECTED = false;
    public static final String HORIZONTAL_TRAP_FORCE_CHOICE = ForcesControlPanel.CHOICE_WHOLE_BEAD;
    public static final boolean FLUID_DRAG_FORCE_SELECTED = false;
    public static final boolean DNA_FORCE_SELECTED = false;
    public static final boolean BROWNIAN_MOTION_ENABLED = true;
    public static final boolean RULER_SELECTED = false;
    public static final boolean ADVANCED_VISIBLE = true;
    public static final boolean FLUID_CONTROLS_SELECTED = false;
    public static final boolean MOMENTUM_CHANGE_MODEL_SELECTED = false;
    
    // Ruler
    public static final int RULER_MAJOR_TICK_INTERVAL = 200; // nm
    public static final int RULER_MINOR_TICKS_BETWEEN_MAJORS = 3;
    
    // View stuff
    public static final double FORCE_VECTOR_REFERENCE_LENGTH = 125; // pixels
}
