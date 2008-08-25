/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * This class is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author John De Goes, John Blanco
 */
public class StatesOfMatterConstants {
    
    /* Not intended for instantiation. */
    private StatesOfMatterConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    // Default font properties
    public static final String DEFAULT_FONT_NAME = PhetFont.getDefaultFontName();
    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    public static final int DEFAULT_FONT_SIZE = 16;
    
    public static final Font CONTROL_PANEL_TITLE_FONT = new Font( StatesOfMatterConstants.DEFAULT_FONT_NAME, Font.BOLD, 12 );
    public static final Font CONTROL_PANEL_CONTROL_FONT = new Font( StatesOfMatterConstants.DEFAULT_FONT_NAME, Font.PLAIN, 12 );
    
    public static final Font PLAY_AREA_TITLE_FONT = new Font( StatesOfMatterConstants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    public static final Font PLAY_AREA_CONTROL_FONT = new Font( StatesOfMatterConstants.DEFAULT_FONT_NAME, Font.PLAIN, 16 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    public static final Stroke DASHED_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.BLACK;
    
    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.WHITE;
    
    // Color of labels placed directly on the play area
    public static final Color CONTROL_PANEL_COLOR = new Color( 0xeeeeee );
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    // Default color for module tabs
    public static final Color SELECTED_TAB_COLOR = new Color(0xffff99);

    //----------------------------------------------------------------------------
    // Simulation Control
    //----------------------------------------------------------------------------
    
    // Dimensions of the container in which the particles will reside, in picometers.
    public static double PARTICLE_CONTAINER_WIDTH = 10000;
    public static double PARTICLE_CONTAINER_INITIAL_HEIGHT = PARTICLE_CONTAINER_WIDTH * 1.00;
    public static final Rectangle2D.Double CONTAINER_BOUNDS = new Rectangle2D.Double(0, 0, PARTICLE_CONTAINER_WIDTH,
            PARTICLE_CONTAINER_INITIAL_HEIGHT);
    
    // Maximum temperature, in degrees Kelvin, that the Thermometer will display.
    public static final double MAX_DISPLAYED_TEMPERATURE = 1000;
    
    // TODO: JPB TBD - Many of these constants will no longer be used once the
    // sim has been cleaned up, so remember to remove them.
    private static final String PROJECT_NAME = "states-of-matter";
    public static final PhetResources RESOURCES = new PhetResources(PROJECT_NAME);
    
    public static final int INITIAL_MAX_PARTICLE_COUNT = 600;
    public static final Rectangle2D.Double ICE_CUBE_BOUNDS  = new Rectangle2D.Double(-1.5, 1, 3, 3);
    public static final double INITIAL_TOTAL_ENERGY_PER_PARTICLE = 225;
    public static final double PARTICLE_RADIUS = 0.2;
    public static final double PARTICLE_MASS   = 1.0;
    public static final double PARTICLE_MAX_KE = 100000;
    public static final double PARTICLE_CREATION_CUSHION = 0.0;

    public static final int COMPUTATIONS_PER_RENDER = 10;

    public static final double GRAVITY = -10;
    public static final double DELTA_T = 0.0000001;

    public static final double EPSILON = 10000000.0;
    public static final double RMIN    = 2.0 * PARTICLE_RADIUS;
    public static final double ICE_CUBE_DIST_FROM_FLOOR = RMIN;
    
    // Identifiers for the various supported molecules.
    public static final int NEON = 1;
    public static final int ARGON = 2;
    public static final int MONATOMIC_OXYGEN = 3;
    public static final int DIATOMIC_OXYGEN = 4;
    public static final int WATER = 5;
    
    // Lennard-Jones potential interaction values for multiatomic atoms.
    public static final double EPSILON_FOR_DIATOMIC_OXYGEN = 100;
    public static final double SIGMA_FOR_DIATOMIC_OXYGEN = 3.3;
    public static final double EPSILON_FOR_WATER = 100;
    public static final double SIGMA_FOR_WATER = 3.3;

    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String COFFEE_CUP_IMAGE = "coffee-cup-image.png";
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
}
