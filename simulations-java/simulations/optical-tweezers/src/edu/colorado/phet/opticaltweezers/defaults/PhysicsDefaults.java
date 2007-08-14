/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.control.ChargeControlPanel;
import edu.colorado.phet.opticaltweezers.control.ForcesControlPanel;
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
    public static final OTClock CLOCK = new OTClock( GlobalDefaults.FRAME_RATE, 
            GlobalDefaults.SLOW_DT_RANGE, GlobalDefaults.FAST_DT_RANGE, GlobalDefaults.DEFAULT_DT );
    
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

    // Laser model
    public static final double LASER_DIAMETER_AT_OBJECTIVE = 1000; // nm
    public static final double LASER_DIAMETER_AT_WAIST = 500; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST = ( MICROSCOPE_SLIDE_HEIGHT / 2 ) + MICROSCOPE_SLIDE_Y_OFFSET; // nm
    public static final double LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL = 100; // nm
    public static final Point2D LASER_POSITION = new Point2D.Double( 1200, MICROSCOPE_SLIDE_POSITION.getY() ); // nm
    
    // Bead model
    public static final Point2D BEAD_POSITION = new Point2D.Double( LASER_POSITION.getX(), LASER_POSITION.getY() ); // nm
    public static final DoubleRange CHARGE_MOTION_SCALE_RANGE = new DoubleRange( 0, 1, 0.5 );

    // Ruler
    public static final double RULER_Y_POSITION = LASER_POSITION.getY() + ( GlobalDefaults.BEAD_DIAMETER / 2 ) + 30; // nm, just below center of trap
    public static final int RULER_MAJOR_TICK_INTERVAL = 100; // nm
    public static final int RULER_MINOR_TICKS_BETWEEN_MAJORS = 9;
    
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
    public static final boolean VECTOR_VALUES_VISIBLE = false;
    public static final boolean VECTOR_COMPONENTS_VISIBLE = false;
}
