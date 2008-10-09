/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * This class is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author John Blanco
 */
public class NuclearPhysicsConstants {

    /* Not intended for instantiation. */
    private NuclearPhysicsConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    public static final String PROJECT_NAME = "nuclear-physics"; 
    public static final String FLAVOR_NAME_NUCLEAR_FISSION = "nuclear-fission"; 
    public static final String FLAVOR_NAME_ALPHA_DECAY = "alpha-radiation"; 
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    // Default font properties
    public static final String DEFAULT_FONT_NAME = PhetFont.getDefaultFontName();
    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    public static final int DEFAULT_FONT_SIZE = 16;
    
    public static final Font CONTROL_PANEL_TITLE_FONT = new Font( NuclearPhysicsConstants.DEFAULT_FONT_NAME, Font.BOLD, 12 );
    public static final Font CONTROL_PANEL_CONTROL_FONT = new Font( NuclearPhysicsConstants.DEFAULT_FONT_NAME, Font.PLAIN, 12 );
    
    public static final Font PLAY_AREA_TITLE_FONT = new Font( NuclearPhysicsConstants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    public static final Font PLAY_AREA_CONTROL_FONT = new Font( NuclearPhysicsConstants.DEFAULT_FONT_NAME, Font.PLAIN, 16 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    public static final Stroke DASHED_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.WHITE;
    
    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.BLACK;
    
    // Color of the control panels
    public static final Color NUCLEAR_FISSION_CONTROL_PANEL_COLOR = new Color( 0xfffacd );
    public static final Color ALPHA_DECAY_CONTROL_PANEL_COLOR = new Color( 227, 239, 214 );
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    // Default color for module tabs
    public static final Color SELECTED_TAB_COLOR = Color.ORANGE;
    
    // Color for the label used for the Polonium nucleus.
    public static final Color POLONIUM_LABEL_COLOR = Color.MAGENTA;
    
    // Color for label used for the Lead nucleus.
    public static final Color LEAD_LABEL_COLOR = Color.BLACK;
    
    // Color for label used for the Uranium 235 nucleus.
    public static final Color URANIUM_235_LABEL_COLOR = Color.GREEN;
    
    // Color for label used for the Uranium 236 nucleus.
    public static final Color URANIUM_236_LABEL_COLOR = Color.ORANGE;
    
    // Color for label used for the Uranium 238 nucleus.
    public static final Color URANIUM_238_LABEL_COLOR = Color.YELLOW;
    
    // Color for label used for the Uranium 239 nucleus.
    public static final Color URANIUM_239_LABEL_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Misc
    //----------------------------------------------------------------------------
    public static final double NUCLEON_DIAMETER        = 1.6; // In femtometers.
    public static final double ALPHA_PARTICLE_DIAMETER = 3.2; // In femtometers.
    public static final Color  PROTON_COLOR = new Color(0xaa0000);
    public static final Color  PROTON_HILITE_COLOR = new Color(0xffaaaa);
    public static final Color  NEUTRON_COLOR = Color.GRAY;
    public static final Color  NEUTRON_HILITE_COLOR = new Color(0xeeeeee);
    public static final Paint PROTON_ROUND_GRADIENT = new RoundGradientPaint( -NUCLEON_DIAMETER/6, -NUCLEON_DIAMETER/6,
            PROTON_HILITE_COLOR, new Point2D.Double( NUCLEON_DIAMETER/4, NUCLEON_DIAMETER/4 ), PROTON_COLOR );
    public static final Paint NEUTRON_ROUND_GRADIENT = new RoundGradientPaint( -NUCLEON_DIAMETER/6, 
            -NUCLEON_DIAMETER/6, NEUTRON_HILITE_COLOR, new Point2D.Double( NUCLEON_DIAMETER/4, NUCLEON_DIAMETER/4 ),
            NEUTRON_COLOR );

}
