/* Copyright 2007, University of Colorado */

package edu.colorado.phet.eatingandexercise;

import java.awt.*;


/**
 * TemplateConstants is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class EatingAndExerciseConstants {

    /* Not intended for instantiation. */
    private EatingAndExerciseConstants() {
    }

    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------

    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;

    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------

    public static final String PROJECT_NAME = "eating-and-exercise";

    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    public static final Stroke DASHED_STROKE =
            new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 3}, 0 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------

    public static final Color BACKGROUND = new Color( 156, 178, 223 );
}
