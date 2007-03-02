/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;


/**
 * RSConstants is a collection of constants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RSConstants {

    /* Not intended for instantiation. */
    private RSConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Dimensions
    //----------------------------------------------------------------------------
    
    public static final Dimension APP_FRAME_SIZE = new Dimension( 1024, 768 );

    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 750, 750 );

    // Animation box size, must be square!
    public static final Dimension ANIMATION_BOX_SIZE = new Dimension( 700, 700 );
    
    public static final Dimension TINY_BOX_SIZE = new Dimension( 10, 10 );

    public static final Dimension BOX_OF_HYDROGEN_SIZE = new Dimension( 70, 18 );
    public static final double BOX_OF_HYDROGEN_DEPTH = 10;
    
    public static final Dimension BEAM_SIZE = new Dimension( (int) ( .75 * BOX_OF_HYDROGEN_SIZE.width ), 75 );
    
    //----------------------------------------------------------------------------
    // Localization
    //----------------------------------------------------------------------------
    
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/RSStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)

    public static final double MIN_CLOCK_STEP = 0.5;
    public static final double MAX_CLOCK_STEP = 3.0;
    public static final double DEFAULT_CLOCK_STEP = 2;
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final String DEFAULT_FONT_NAME = new JLabel( "PhET" ).getFont().getName();
    public static final int DEFAULT_FONT_STYLE = Font.BOLD;
    public static final int DEFAULT_FONT_SIZE = 16;
    public static final Font DEFAULT_FONT = new Font( DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE );
    
    public static final Font TITLE_FONT = new Font( DEFAULT_FONT_NAME, Font.BOLD, 14 );
    public static final Font CONTROL_FONT = new Font( DEFAULT_FONT_NAME, Font.PLAIN, 14 );
    
    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.BLACK;
    
    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.WHITE;
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    // Color used for alpha particles
    public static final Color ALPHA_PARTICLES_COLOR = new Color( 160, 160, 160 ); // gray
    
    // Color of the animation box
    public static final Color ANIMATION_BOX_COLOR = Color.BLACK;
    public static final Color ANIMATION_BOX_STROKE_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    
    public static final String IMAGE_GUN = IMAGES_DIRECTORY + "gun.png";
    public static final String IMAGE_GUN_ON_BUTTON = IMAGES_DIRECTORY + "gunOnButton.png";
    public static final String IMAGE_GUN_OFF_BUTTON = IMAGES_DIRECTORY + "gunOffButton.png";
    public static final String IMAGE_PLUM_PUDDING = IMAGES_DIRECTORY + "plumPudding.png";
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );
    public static final Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final double ALPHA_PARTICLE_INITIAL_SPEED = 5; // distance moved per dt
    
}
