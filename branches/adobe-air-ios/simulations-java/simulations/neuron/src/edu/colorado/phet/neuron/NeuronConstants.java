// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.neuron;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * TemplateConstants is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NeuronConstants {

    /* Not intended for instantiation. */
    private NeuronConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "neuron";

    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final Font CONTROL_PANEL_TITLE_FONT = new PhetFont( Font.BOLD, 14 );
    public static final Font CONTROL_PANEL_CONTROL_FONT = new PhetFont( Font.PLAIN, 14 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color(204, 255, 249);
    
    // Colors to use when representing various atoms.
    public static final Color SODIUM_COLOR = new Color( 240, 0, 0);
    public static final Color POTASSIUM_COLOR = new Color(0, 240, 100);
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Application Configuration
    //----------------------------------------------------------------------------
}
