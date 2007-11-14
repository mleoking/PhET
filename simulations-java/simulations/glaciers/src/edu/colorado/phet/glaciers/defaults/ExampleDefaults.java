/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.glaciers.model.GlaciersClock;


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
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;
    public static final DoubleRange CLOCK_DT_RANGE = GlobalDefaults.CLOCK_DT_RANGE;
    public static final String CLOCK_UNITS = GlobalDefaults.CLOCK_UNITS;
    public static final GlaciersClock CLOCK = new GlaciersClock( CLOCK_FRAME_RATE, CLOCK_DT_RANGE, CLOCK_UNITS );
    
    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 1500, 1500 );
    
    // ExampleModelElement
    public static final Dimension EXAMPLE_MODEL_ELEMENT_SIZE = new Dimension( 200, 100 ); // meters
    public static final Point2D EXAMPLE_MODEL_ELEMENT_POSITION = new Point2D.Double( 400, 400 );
    public static final double EXAMPLE_MODEL_ELEMENT_ORIENTATION = 0; // radians

}
