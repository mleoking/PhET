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
    public static final boolean SLOW_SPEED_SELECTED = true;
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
    public static final boolean ELECTRIC_FIELD_SELECTED = false;
    public static final boolean BEAD_CHARGES_SELECTED = false;
    public static final boolean ALL_BEAD_CHARGES_SELECTED = true;
    public static final boolean TRAP_FORCE_SELECTED = false;
    public static final boolean WHOLE_BEAD_SELECTED = true;
    public static final boolean FLUID_DRAG_FORCE_SELECTED = false;
    public static final boolean BROWNIAN_FORCE_SELECTED = false;
    public static final boolean RULER_SELECTED = false;
    public static final boolean POSITION_HISTOGRAM_SELECTED = false;
    public static final boolean ADVANCED_VISIBLE = false;
    public static final boolean FLUID_CONTROLS_SELECTED = false;
    public static final boolean MOMENTUM_CHANGE_MODEL_SELECTED = false;
    public static final boolean POTENTIAL_ENERGY_CHART_SELECTED = false;
    
}
