/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * This is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleConstants {

    /* Not intended for instantiation. */
    private PHScaleConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "ph-scale";
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final IntegerRange PH_RANGE = new IntegerRange( -1, 15, 7 ); // min, max, default
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    public static final PDimension BEAKER_SIZE = new PDimension( 435, 450 );
    
    // size of the liquid column that comes out of the faucets
    public static final PDimension LIQUID_COLUMN_SIZE = new PDimension( 20, BEAKER_SIZE.getHeight() + 30 );
    
    public static final double PH_PROBE_LENGTH = BEAKER_SIZE.getHeight() + 75;
    
    // vertical spacing of pH slider ticks and bar graph log scale ticks
    public static final double LOG_TICKS_Y_SPACING = 24;
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final int CONTROL_FONT_SIZE = 18;
    public static final Font CONTROL_FONT = new PhetFont( Font.PLAIN, CONTROL_FONT_SIZE );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    public static final Stroke DASHED_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );

    //----------------------------------------------------------------------------
    // Colors
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 230, 230, 230 );
    
    // H3O, OH, H2O colors (used for pH slider track & bars)
    public static final Color H3O_COLOR = new Color( 242, 102, 101 );
    public static final Color OH_COLOR = new Color( 102, 132, 242 );
    public static final Color H2O_COLOR = new Color( 193, 222, 227 );

    // H3O and OH particle colors
    public static final Color H3O_PARTICLES_COLOR = new Color( 204, 0, 0 );
    public static final Color OH_PARTICLES_COLOR = new Color( 0, 0, 255 );
}
