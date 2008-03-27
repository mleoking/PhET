/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.defaults;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;

/**
 * This class contains the default settings for the Alpha Radiation Module.
 *
 * @author John Blanco
 */
public class AlphaRadiationDefaults {

    /* Not intended for instantiation */
    private AlphaRadiationDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final int CLOCK_FRAME_RATE = 25; // Frames per second.
    public static final double CLOCK_DT = 20; // Milliseconds per tick.
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;
    public static final NuclearPhysics2Clock CLOCK = new NuclearPhysics2Clock( CLOCK_FRAME_RATE, CLOCK_DT );
    
    // Model-view transform
    // TODO: JPB TBD - Need to figure out if this is needed.
    public static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );
}
