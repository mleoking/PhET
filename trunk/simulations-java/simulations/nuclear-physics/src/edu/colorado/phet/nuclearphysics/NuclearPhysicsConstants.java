// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;


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
    public static final String FLAVOR_NAME_ALPHA_DECAY = "alpha-decay";
    public static final String FLAVOR_NAME_BETA_DECAY = "beta-decay";
    public static final String FLAVOR_NAME_RADIOACTIVE_DATING_GAME = "radioactive-dating-game";
    
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
    // Paints and Colors
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.WHITE;
    
    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.BLACK;
    
    // Color of the control panels
    public static final Color NUCLEAR_FISSION_CONTROL_PANEL_COLOR = new Color( 0xfffacd );
    public static final Color ALPHA_DECAY_CONTROL_PANEL_COLOR = new Color( 227, 239, 214 );
    public static final Color BETA_DECAY_CONTROL_PANEL_COLOR = new Color( 227, 239, 214 );
    public static final Color RADIOACTIVE_DATING_GAME_CONTROL_PANEL_COLOR = new Color( 227, 239, 214 );
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    // Color for the label used for the Polonium nucleus.
    public static final Color POLONIUM_LABEL_COLOR = Color.YELLOW;
    
    // Color for label used for the Lead nucleus.
    public static final Color LEAD_LABEL_COLOR = Color.BLACK;
    
    // Color for label used for the Custom nucleus (pre-decay).
    public static final Color CUSTOM_NUCLEUS_LABEL_COLOR = new Color (0x99ffff);
    
    // Color for label used for the Decayed Custom nucleus.
    public static final Color CUSTOM_NUCLEUS_POST_DECAY_LABEL_COLOR = Color.BLACK;
    
    // Color for label used for the Hydrogen 3 nucleus.
    public static final Color HYDROGEN_3_LABEL_COLOR = new Color(127, 255, 0);
    
    // Color for label used for the Helium 3 nucleus.
    public static final Color HELIUM_3_LABEL_COLOR = new Color(255, 192, 203);
    
    // Color for label used for the Carbon 14 nucleus.
    public static final Color CARBON_14_LABEL_COLOR = Color.YELLOW;
    
    // Color for label used for the Uranium 235 nucleus.
    public static final Color NITROGEN_14_LABEL_COLOR = Color.ORANGE;
    
    // Color for label used for the Uranium 235 nucleus.
    public static final Color URANIUM_235_LABEL_COLOR = Color.GREEN;
    
    // Color for label used for the Uranium 236 nucleus.
    public static final Color URANIUM_236_LABEL_COLOR = Color.ORANGE;
    
    // Color for label used for the Uranium 238 nucleus.
    public static final Color URANIUM_238_LABEL_COLOR = Color.YELLOW;
    
    // Color for label used for the Uranium 239 nucleus.
    public static final Color URANIUM_239_LABEL_COLOR = Color.WHITE;
    
    // Color for hydrogen when represented as a circle or sphere.
    public static final Color HYDROGEN_COLOR = Color.PINK;
    
    // Color for helium when represented as a circle or sphere.
    public static final Color HELIUM_COLOR = Color.CYAN;
    
    // Color for carbon when represented as a circle or sphere.
    public static final Color CARBON_COLOR = new Color( 200, 0, 0 );
    
    // Color for nitrogen when represented as a circle or sphere.
    public static final Color NITROGEN_COLOR = new Color( 14, 86, 200 );
    
    // Color for Uranium when represented as a circle or sphere.
    public static final Color URANIUM_COLOR = new Color( 150, 150, 0 );
    
    // Color for Lead when represented as a circle or sphere.
    public static final Color LEAD_COLOR = new Color( 97, 117, 126 );
    
    // Color for Polonium when represented as a circle or sphere.
    public static final Color POLONIUM_COLOR = Color.ORANGE;
    
    // Color for pre-decay custom nucleus when represented as a circle or sphere.
    public static final Color CUSTOM_NUCLEUS_PRE_DECAY_COLOR = new Color( 155, 97, 42 );
    
    // Color for post-decay custom nucleus when represented as a circle or sphere.
    public static final Color CUSTOM_NUCLEUS_POST_DECAY_COLOR = new Color( 54, 130, 55 );
    
    // Color of the chart background for the alpha decay application.
	public static final Color  CHART_BACKGROUND_COLOR = new Color( 246, 242, 175 );
	
	// Color of the reset button that appears on many of the canvases.
	public static final Color CANVAS_RESET_BUTTON_COLOR = new Color(0xff9900);
	
	// Colors for the strata in the Radioactive Dating Game, assumed to go
	// from top to bottom.
	public static final ArrayList<Color> strataColors = new ArrayList<Color>();
	static {
		strataColors.add( new Color( 111, 131, 151 ) );
		strataColors.add( new Color( 153, 185, 216 ) );
		strataColors.add( new Color( 216, 175, 208 ) );
		strataColors.add( new Color( 198, 218, 119 ) );
		strataColors.add( new Color( 179, 179, 179 ) );
		strataColors.add( Color.DARK_GRAY );
	}

    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Misc Constants Shared within the Sim
    //----------------------------------------------------------------------------
    public static final double NUCLEON_DIAMETER        = 1.6; // In femtometers.
    public static final double ALPHA_PARTICLE_DIAMETER = 3.2; // In femtometers.
    public static final double ELECTRON_DIAMETER = 0.75; // In femtometers, not to scale, or even close.
    public static final double ANTINEUTRINO_DIAMETER = 0.3; // In femtometers, not to scale, or even close.
    public static final Color  PROTON_COLOR = new Color(0xaa0000);
    public static final Color  PROTON_HILITE_COLOR = new Color(0xffaaaa);
    public static final Color  NEUTRON_COLOR = Color.GRAY;
    public static final Color  NEUTRON_HILITE_COLOR = new Color(0xeeeeee);
    public static final Color  ELECTRON_COLOR = Color.BLUE;
    public static final Color  ELECTRON_HILITE_COLOR = Color.CYAN;
    public static final Paint PROTON_ROUND_GRADIENT = new RoundGradientPaint( -NUCLEON_DIAMETER/6, -NUCLEON_DIAMETER/6,
            PROTON_HILITE_COLOR, new Point2D.Double( NUCLEON_DIAMETER/4, NUCLEON_DIAMETER/4 ), PROTON_COLOR );
    public static final Paint NEUTRON_ROUND_GRADIENT = new RoundGradientPaint( -NUCLEON_DIAMETER/6, 
            -NUCLEON_DIAMETER/6, NEUTRON_HILITE_COLOR, new Point2D.Double( NUCLEON_DIAMETER/4, NUCLEON_DIAMETER/4 ),
            NEUTRON_COLOR );
    public static final Paint ELECTRON_ROUND_GRADIENT = new RoundGradientPaint( -ELECTRON_DIAMETER/6, 
            -ELECTRON_DIAMETER/6, ELECTRON_HILITE_COLOR, new Point2D.Double( ELECTRON_DIAMETER/4, ELECTRON_DIAMETER/4 ),
            ELECTRON_COLOR );
    public static final Color ANTINEUTRINO_COLOR = new Color(0, 220, 0);
    
    public static final double DEFAULT_CUSTOM_NUCLEUS_HALF_LIFE = HalfLifeInfo.convertYearsToMs(100E3);

}
