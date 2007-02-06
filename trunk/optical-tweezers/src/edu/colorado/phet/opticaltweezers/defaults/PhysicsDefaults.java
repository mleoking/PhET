/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.defaults;


/**
 * PhysicsDefaults contains default settings for the "Physics of Tweezers" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhysicsDefaults {

    /* Not intended for instantiation */
    private PhysicsDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true ;
    public static final boolean SPEED_SLOW = true;
    public static final double SLOW_SPEED = 0.5; // range: 0-1
    public static final double FAST_SPEED = 0.5; // range: 0-1
    
    // Laser
    public static final boolean LASER_ON = false;
    public static final double LASER_INTENSITY = 1; // range: 0-1
    public static final double LASER_POSITION = 400;
    
    // Bead
    public static final double BEAD_POSITION_X = 300;
    public static final double BEAD_POSITION_Y = 300;
    
    // Control panel settings
    public static final boolean SHOW_LASER_ELECTRIC_FIELD = false;
    public static final boolean SHOW_BEAD_CHARGES = false;
    public static final boolean SHOW_ALL_BEAD_CHARGES = true;
    public static final boolean SHOW_OPTICAL_TRAP_FORCE = false;
    public static final boolean SHOW_WHOLE_BEAD = true;
    public static final boolean SHOW_FLUID_DRAG_FORCE = false;
    public static final boolean SHOW_BROWNIAN_FORCE = false;
    public static final boolean SHOW_RULER = false;
    public static final boolean SHOW_POSITION_HISTOGRAM = false;
    public static final boolean SHOW_ADVANCED_FEATURES = false;
    public static final boolean SHOW_FLUID_AND_FLOW_CONTROLS = false;
    public static final boolean SHOW_MOMENTUM_CHANGE_MODEL = false;
    public static final boolean SHOW_POTENTIAL_ENERGY_CHART = false;
    
}
