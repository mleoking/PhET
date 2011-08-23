// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import java.awt.Color;
import java.awt.Dimension;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPConstants {

    /* Not intended for instantiation. */
    private MPConstants() {
    }

    public static final String PROJECT_NAME = "molecule-polarity";

    // Model
    public static final DoubleRange ELECTRONEGATIVITY_RANGE = new DoubleRange( 2, 4, 2 );
    public static final double ELECTRONEGATIVITY_SNAP_INTERVAL = 0.2;

    // Canvas
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 600 );
    public static final Color CANVAS_COLOR = new Color( 180, 205, 255 ); // light blue

    // E-field plates, all values are related to 2D projection of the plates
    public static final double PLATE_WIDTH = 50;
    public static final double PLATE_HEIGHT = 450;
    public static final double PLATE_THICKNESS = 5;
    public static final double PLATE_PERSPECTIVE_Y_OFFSET = 35; // y difference between foreground and background edges of the plate
}
