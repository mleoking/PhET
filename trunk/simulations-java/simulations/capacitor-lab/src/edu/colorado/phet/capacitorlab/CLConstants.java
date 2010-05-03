/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLConstants {

    /* Not intended for instantiation. */
    private CLConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "capacitor-lab";
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final Point2D BATTERY_LOCATION = new Point2D.Double( -100, 0 ); // mm
    public static final DoubleRange BATTERY_VOLTAGE_RANGE = new DoubleRange( -10, 10, 0 ); // volts
    public static final boolean BATTERY_CONNECTED = true;
    
    public static final Point2D CAPACITOR_LOCATION = new Point2D.Double( 0, 0 ); // mm
    public static final DoubleRange PLATE_SIZE_RANGE = new DoubleRange( 10, 100, 50 ); // mm
    public static final DoubleRange PLATE_SEPARATION_RANGE = new DoubleRange( 10, 50, 20 ); // mm
    public static final double PLATE_THICKNESS = 1; // mm
    
    public static final DoubleRange DIELECTRIC_CONSTANT_RANGE = new DoubleRange( 0, 5, 1 ); // dimensionless
    public static final DoubleRange DIELECTRIC_OFFSET_RANGE = new DoubleRange( 0, PLATE_SIZE_RANGE.getMax(), 0 ); // mm
    
    // model-view transform
    public static final double MVT_SCALE = 3;
    public static final Point2D MVT_OFFSET = new Point2D.Double( 160, 125 ); // mm
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );
    
    public static final double VIEWING_ANGLE = Math.toRadians( 45 ); // radians
    public static final double FORESHORTENING_FACTOR = 0.5; // how much lines going away from the viewer should be shortened
    
    public static final double PSWING_SCALE = 1.5;
    
    //----------------------------------------------------------------------------
    // Control
    //----------------------------------------------------------------------------
    
}
