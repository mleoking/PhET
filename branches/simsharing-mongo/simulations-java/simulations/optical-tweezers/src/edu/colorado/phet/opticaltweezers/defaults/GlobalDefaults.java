// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.Point;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.opticaltweezers.model.Fluid;


/**
 * GlobalDefaults contains default settings that are common to 2 or more modules.
 * 
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes (eg, PhysicsDefaults) for each module.  Classes that 
 * are module-specific (eg, PhysicsModule, PhysicsCanvas, PhysicsConfig) should
 * use the class that corresponds to their module (eg, PhysicsDefaults).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private! */ class GlobalDefaults {

    /* Not intended for instantiation */
    private GlobalDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final int CLOCK_TIME_COLUMNS = 10;
    
    // Fluid model
    public static final double FLUID_DIRECTION = Math.toRadians( 0 ); // left-to-right flow direction
    public static final DoubleRange FLUID_SPEED_RANGE = new DoubleRange( 0, 1000000, 0 ); // nm/sec, min must be >0
    public static final DoubleRange FLUID_VISCOSITY_RANGE = new DoubleRange( 5E-4, 1E-2, Fluid.WATER_VISCOSITY ); // Pa*s
    public static final DoubleRange FLUID_TEMPERATURE_RANGE = new DoubleRange( 50, 350, 298 ); // Kelvin
    public static final DoubleRange FLUID_APT_CONCENTRATION_RANGE = new DoubleRange( 0, 0, 0 ); // no APT in most panels
    
    // Laser model, local origin at center of objective
    public static final double LASER_WAVELENGTH = 1064; // nm, invisible IR
    public static final double LASER_VISIBLE_WAVELENGTH = 632; // nm, to be used by view components
    public static final double LASER_ORIENTATION = Math.toRadians( -90 );
    public static final DoubleRange LASER_POWER_RANGE = new DoubleRange( 0, 1000, 500 ); // mW
    public static final boolean LASER_RUNNING = true;
    public static final DoubleRange LASER_TRAP_FORCE_RATIO = new DoubleRange( 0.1, 5, 0.179 );
    public static final DoubleRange LASER_ELECTRIC_FIELD_SCALE_RANGE = new DoubleRange( 0.01, 10, 1 );
    
    // Bead model
    public static final double BEAD_DIAMETER = 200; // nm
    public static final double BEAD_DENSITY = 1.05E-21; // g/nm^3, polystyrene
    public static final double BEAD_ORIENTATION = Math.toRadians( 0 );
    public static final DoubleRange BEAD_BROWNIAN_MOTION_SCALE_RANGE = new DoubleRange( 0, 5, 1.3 );
    public static final boolean BEAD_BROWNIAN_MOTION_ENABLED = true;
    public static final DoubleRange BEAD_VERLET_ACCELERATION_SCALE_RANGE = new DoubleRange( 1E-8, 1E-1, 4E-3 );
    public static final IntegerRange BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE = new IntegerRange( 1, 2000, 10 );
    public static final DoubleRange BEAD_VACUUM_FAST_THRESHOLD_RANGE = new DoubleRange( 1E-6, 1, 1E-3 );
    public static final DoubleRange BEAD_VACUUM_FAST_POWER_RANGE = new DoubleRange( LASER_POWER_RANGE.getMin(), LASER_POWER_RANGE.getMax(), 100 );
    
    // DNA Strand model
    public static final double DNA_CONTOUR_LENGTH = 2413; // nm
    public static final double DNA_PERSISTENCE_LENGTH = 50; // nm, double strand
    private static final int DNA_NUMBER_OF_SPRINGS = 38;
    public static final double DNA_SPRING_LENGTH = DNA_CONTOUR_LENGTH / DNA_NUMBER_OF_SPRINGS; // nm
    public static final double DNA_STRETCHINESS = 0.95; // % of contour length
    public static final DoubleRange DNA_SPRING_CONSTANT_RANGE = new DoubleRange( 2, 20, 10 );
    public static final DoubleRange DNA_DRAG_COEFFICIENT_RANGE = new DoubleRange( 0.1, 2, 0.5 );
    public static final DoubleRange DNA_EVOLUTION_DT_RANGE = new DoubleRange( 0.05, 0.2, 0.1 );
    public static final DoubleRange DNA_KICK_CONSTANT_RANGE = new DoubleRange( 10, 100, 60, 0 );
    public static final IntegerRange DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE = new IntegerRange( 1, 100, 30 );
    public static final DoubleRange DNA_FLUID_DRAG_COEFFICIENT_RANGE = new DoubleRange( 0, 0.00020, 0.000015 );
    public static final boolean DNA_PIVOTS_VISIBLE = false;
    public static final boolean DNA_EXTENSIONS_VISIBLE = false;
    
    // Potential Energy chart
    public static final double POTENTIAL_ENERGY_SAMPLE_WIDTH = 5; // nm
    
    // Force vectors
    public static final double FORCE_VECTOR_REFERENCE_LENGTH = 125; // pixels
    public static final boolean FORCE_VECTOR_COMPONENTS_VISIBLE = false;
    
    // Dialog locations
    public static final Point POSITION_HISTOGRAM_DIALOG_OFFSET = new Point( 15, 80 ); // offset in pixels from upper-left corner of parent frame
}
