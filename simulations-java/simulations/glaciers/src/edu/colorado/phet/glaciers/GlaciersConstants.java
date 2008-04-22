/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;


/**
 * GlaciersConstants is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersConstants {

    /* Not intended for instantiation. */
    private GlaciersConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Flags
    //----------------------------------------------------------------------------
    
    public static final boolean UPDATE_WHILE_DRAGGING_SLIDERS = true;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    public static final Stroke DASHED_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Generic transparent color
    public static final Color TRANSPARENT_COLOR = new Color( 0f, 0f, 0f, 0f );
    
    // Default color for module tabs
    public static final Color SELECTED_TAB_COLOR = Color.ORANGE;
    
    // color below ground level
    public static final Color UNDERGROUND_COLOR = new Color( 180, 158, 134 ); // tan
    
    // color of the Piccolo canvases
    public static final Color BIRDS_EYE_CANVAS_COLOR = new Color( 99, 173, 255 ); // sky blue
    public static final Color ZOOMED_CANVAS_COLOR = BIRDS_EYE_CANVAS_COLOR;
    
    // color of ice
    public static final Color ICE_CROSS_SECTION_COLOR = Color.WHITE;
    public static final Color ICE_SURFACE_COLOR = new Color( 232, 242, 252 );
    
    // main control panel color
    public static final Color CONTROL_PANEL_BACKGROUND_COLOR = new Color( 219, 255, 224 ); // pale green
    
    // colors for climate aspects
    public static final Color GLACIAL_BUDGET_COLOR = Color.RED;
    public static final Color ACCUMULATION_COLOR = Color.BLUE;
    public static final Color ABLATION_COLOR = Color.GREEN.darker();
    
    //----------------------------------------------------------------------------
    // Various components
    //----------------------------------------------------------------------------
    
    // Subpanels of the main control panel
    public static final Font SUBPANEL_TITLE_FONT = new PhetDefaultFont( PhetDefaultFont.getDefaultFontSize(), true /* bold */ );
    public static final Font SUBPANEL_CONTROL_FONT = new PhetDefaultFont();
    public static final Color SUBPANEL_BACKGROUND_COLOR = new Color( 82, 126, 90 ); // dark pastel green
    public static final Color SUBPANEL_TITLE_COLOR = Color.WHITE;
    public static final Color SUBPANEL_CONTROL_COLOR = Color.WHITE;
    
    // Coordinate axes
    public static final DoubleRange ELEVATION_AXIS_RANGE = new DoubleRange( 0, 7000 ); // meters
    public static final double ELEVATION_AXIS_TICK_SPACING = 500; // meters
    public static final double DISTANCE_AXIS_TICK_SPACING = 1000; // meters
}
