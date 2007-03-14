/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

import edu.colorado.phet.rutherfordscattering.util.DoubleRange;
import edu.colorado.phet.rutherfordscattering.util.IntegerRange;


/**
 * RSConstants is a collection of constants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
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
    // Properties files
    //----------------------------------------------------------------------------
    
    public static final String SIM_PROPERTIES_NAME = "rutherfordscattering";
    public static final String SIM_STRINGS_NAME = "localization/RSStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final int CLOCK_FRAME_RATE = 25; // frames per second
    public static final double CLOCK_STEP = 1; // dt
    
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
    
    // Color used to represent beam of alpha particles coming out of gun
    public static final Color BEAM_OF_ALPHA_PARTICLES_COLOR = new Color( 160, 160, 160 ); // gray
    
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
    // Default state of the model
    //----------------------------------------------------------------------------
    
    public static final boolean CLOCK_PAUSED = false;
    public static final boolean TRACES_ENABLED = true;
    public static final boolean GUN_ENABLED = false;
    public static final double GUN_INTENSITY = 1.0; // 0-1 (1=100%)
    public static final DoubleRange INITIAL_SPEED_RANGE = new DoubleRange( 5, 12, 10, 1 );
    public static final IntegerRange NUMBER_OF_PROTONS_RANGE = new IntegerRange( 20, 100, 79 );
    public static final IntegerRange NUMBER_OF_NEUTRONS_RANGE = new IntegerRange( 20, 150, 118 );
    public static final double ELECTRON_ANGULAR_SPEED = Math.toRadians( 0.75 ); // radians per dt
}
