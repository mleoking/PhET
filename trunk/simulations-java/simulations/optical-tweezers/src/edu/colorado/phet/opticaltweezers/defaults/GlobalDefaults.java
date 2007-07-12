/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.model.OTClock;


/**
 * GlobalDefaults contains global default settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlobalDefaults {

    /* Not intended for instantiation */
    private GlobalDefaults() {}
    
    // Clock
    public static final boolean CLOCK_PAUSED = false;
    public static final int FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final DoubleRange SLOW_DT_RANGE = new DoubleRange( 4E-18, 5E-16 );
    public static final DoubleRange FAST_DT_RANGE = new DoubleRange( 8E-7, 4E-4 );
    public static final double DEFAULT_DT = FAST_DT_RANGE.getMax();
    public static final int CLOCK_TIME_COLUMNS = 10;
    
    // Fluid model
    public static final boolean FLUID_ENABLED = true;
    public static final double FLUID_DIRECTION = Math.toRadians( 0 ); // left-to-right flow direction
    public static final DoubleRange FLUID_SPEED_RANGE = new DoubleRange( 0, 1000000, 0 ); // nm/sec, min must be >0
    public static final DoubleRange FLUID_VISCOSITY_RANGE = new DoubleRange( 1E-4, 1E-2, Fluid.WATER_VISCOSITY ); // Pa*s
    public static final DoubleRange FLUID_TEMPERATURE_RANGE = new DoubleRange( 50, 350, 298 ); // Kelvin
    
    // Bead model
    public static final double BEAD_ORIENTATION = Math.toRadians( 0 );
    public static final double BEAD_DIAMETER = 200; // nm
    public static final double BEAD_DENSITY = 1.05E-21; // g/nm^3, polystyrene
    public static final DoubleRange BEAD_DT_SUBDIVISION_THRESHOLD_RANGE = new DoubleRange( FAST_DT_RANGE.getMin(), FAST_DT_RANGE.getMax(), 1E-6 );
    public static final IntegerRange BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE = new IntegerRange( 1, 2000, 1000 );
    public static final DoubleRange BEAD_BROWNIAN_MOTION_SCALE_RANGE = new DoubleRange( 0, 5, 1.3 );
    public static final boolean BEAD_BROWNIAN_MOTION_ENABLED = true;
    public static final DoubleRange BEAD_VERLET_ACCELERATION_SCALE_RANGE = new DoubleRange( 1E-8, 1E-4, 1E-6 );
    
    // Laser model, local origin at center of objective
    public static final double LASER_WAVELENGTH = 1064; // nm, invisible IR
    public static final double LASER_VISIBLE_WAVELENGTH = 632; // nm, to be used by view components
    public static final double LASER_ORIENTATION = Math.toRadians( -90 );
    public static final DoubleRange LASER_POWER_RANGE = new DoubleRange( 0, 1000, 500 ); // mW
    public static final boolean LASER_RUNNING = true;
    public static final DoubleRange LASER_TRAP_FORCE_RATIO = new DoubleRange( 0.1, 5, 0.179 );
    public static final DoubleRange LASER_ELECTRIC_FIELD_SCALE_RANGE = new DoubleRange( 0.01, 10, 1 );
    
    // Potential Energy chart
    public static final double POTENTIAL_ENERGY_SAMPLE_WIDTH = 5; // nm
    
    // View stuff
    public static final double FORCE_VECTOR_REFERENCE_LENGTH = 125; // pixels
}
