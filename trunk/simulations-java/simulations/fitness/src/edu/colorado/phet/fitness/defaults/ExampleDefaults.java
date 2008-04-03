/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.fitness.defaults;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.fitness.defaults.GlobalDefaults;
import edu.colorado.phet.fitness.model.SimTemplateClock;


/**
 * ExampleDefaults contains default settings for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleDefaults {

    /* Not intended for instantiation */
    private ExampleDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final int CLOCK_FRAME_RATE = GlobalDefaults.CLOCK_FRAME_RATE;
    public static final double CLOCK_DT = GlobalDefaults.CLOCK_DT;
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;
    public static final SimTemplateClock CLOCK = new SimTemplateClock( CLOCK_FRAME_RATE, CLOCK_DT );
    
    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );
    
    // ExampleModelElement
    public static final double EXAMPLE_MODEL_ELEMENT_WIDTH = 200; // meters
    public static final double EXAMPLE_MODEL_ELEMENT_HEIGHT = 100; // meters
    public static final Point2D EXAMPLE_MODEL_ELEMENT_POSITION = new Point2D.Double( 400, 400 );
    public static final double EXAMPLE_MODEL_ELEMENT_ORIENTATION = 0; // radians

}
