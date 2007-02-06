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
 * MotorsDefaults contains default settings for the "Molecular Motors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MotorsDefaults {

    /* Not intended for instantiation */
    private MotorsDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true ;
    public static final boolean SLOW_SPEED_SELECTED = true;
    public static final int SLOW_SPEED = 50; // range is 1-100
    public static final int FAST_SPEED = 50; // range is 1-100
}
