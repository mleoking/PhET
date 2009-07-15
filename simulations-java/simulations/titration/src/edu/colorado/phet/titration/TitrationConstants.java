/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration;

import java.awt.Color;
import java.awt.Dimension;


/**
 * TemplateConstants is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TitrationConstants {

    /* Not intended for instantiation. */
    private TitrationConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "titration";
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 768 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.WHITE;
}
