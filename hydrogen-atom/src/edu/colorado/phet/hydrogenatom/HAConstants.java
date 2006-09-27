/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import edu.colorado.phet.common.view.util.VisibleColor;


/**
 * HAConstants is a collection of constants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAConstants {

    /* Not intended for instantiation. */
    private HAConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    public static final boolean SHOW_ORIGIN_NODES = false; // set to true for debugging
    
    //----------------------------------------------------------------------------
    // Dimensions
    //----------------------------------------------------------------------------
    
    public static final Dimension APP_FRAME_SIZE = new Dimension( 1024, 768 );

    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 750, 750 );

    public static final Dimension DEFAULT_ANIMATION_REGION_SIZE = new Dimension( 475, 475 );
    public static final Dimension TINY_BOX_SIZE = new Dimension( 10, 10 );

    public static final Dimension BOX_OF_HYDROGEN_SIZE = new Dimension( 70, 70 );
    public static final double BOX_OF_HYDROGEN_DEPTH = 10;
    
    public static final Dimension BEAM_SIZE = new Dimension( (int) ( .75 * BOX_OF_HYDROGEN_SIZE.width ), 75 );
    
    //----------------------------------------------------------------------------
    // Localization
    //----------------------------------------------------------------------------
    
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/HAStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    
    // The clock control area has a slider for choosing a clock "speed".
    // These are the clock steps that correspond to each speed setting.
    public static final double[] CLOCK_STEPS = {
        1, 2, 3, 4, 5
    };
       
    // Defaults
    public static final int DEFAULT_CLOCK_INDEX = 0;
    public static final double DEFAULT_CLOCK_STEP = CLOCK_STEPS[ DEFAULT_CLOCK_INDEX ];
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    // Default font name used for JLabels
    public static final String JLABEL_FONT_NAME = new JLabel( "PhET" ).getFont().getName();
    
    // Default font sizes, configurable (and overridden by) entries in the SimStrings file
    public static final int MODE_SWITCH_BIG_FONT_SIZE = 20;
    public static final int MODE_SWITCH_SMALL_FONT_SIZE = 14;
    public static final int ATOMIC_MODEL_SELECTOR_TITLE_FONT_SIZE = 20;
    public static final int ATOMIC_MODEL_SELECTOR_BUTTON_FONT_SIZE = 16;
    public static final int ATOMIC_MODEL_SELECTOR_CONTINUUM_FONT_SIZE = 16;
    public static final int BOX_OF_HYDROGEN_FONT_SIZE = 16;
    public static final int GUN_CONTROLS_FONT_SIZE = 16;
    public static final int SPECTROMETER_FONT_SIZE = 16;
    public static final int ENERGY_DIAGRAM_FONT_SIZE = 16;
    public static final int JCOMPONENT_FONT_SIZE = 16;
    public static final int LEGEND_FONT_SIZE = 16;
    public static final int NOT_TO_SCALE_FONT_SIZE = 14;
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.BLACK;
    
    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.WHITE;
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    // Colors used for wavelengths outside the visible spectrum
    public static final Color UV_TRACK_COLOR = Color.LIGHT_GRAY;
    public static final Color UV_LABEL_COLOR = Color.BLACK;
    public static final Color IR_TRACK_COLOR = UV_TRACK_COLOR;
    public static final Color IR_LABEL_COLOR = UV_LABEL_COLOR;
    
    // Color used for alpha particle intensity slider
    public static final Color ALPHA_PARTICLES_COLOR = UV_TRACK_COLOR;
    
    // Color of the animation area
    public static final Color ANIMATION_REGION_COLOR = Color.BLACK;
    public static final Color ANIMATION_REGION_STROKE_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Borders
    //----------------------------------------------------------------------------
    
    public static final Border CONTROL_PANEL_BORDER = 
        BorderFactory.createCompoundBorder(
          BorderFactory.createBevelBorder( BevelBorder.RAISED, Color.GRAY, Color.BLACK ),
          BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    public static final String IMAGE_ATOMIC_MODEL_PANEL = IMAGES_DIRECTORY + "atomicModelPanel.png";
    public static final String IMAGE_BILLIARD_BALL = IMAGES_DIRECTORY + "billiardBall.png";
    public static final String IMAGE_BILLIARD_BALL_BUTTON = IMAGES_DIRECTORY + "billiardBallButton.png";
    public static final String IMAGE_BOHR_BUTTON = IMAGES_DIRECTORY + "bohrButton.png";
    public static final String IMAGE_BOX_OF_HYDROGEN = IMAGES_DIRECTORY + "boxOfHydrogen.png";
    public static final String IMAGE_CAMERA = IMAGES_DIRECTORY + "camera.png";
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
    public static final String IMAGE_CLOSE_BUTTON = IMAGES_DIRECTORY + "closeButton.png";
    public static final String IMAGE_DEBROGLIE_BUTTON = IMAGES_DIRECTORY + "deBroglieButton.png";
    public static final String IMAGE_EXPERIMENT_ATOM = IMAGES_DIRECTORY + "experimentAtom.png";
    public static final String IMAGE_GUN = IMAGES_DIRECTORY + "gun.png";
    public static final String IMAGE_GUN_CONTROL_CABLE = IMAGES_DIRECTORY + "gunControlCable.png";
    public static final String IMAGE_GUN_ON_BUTTON = IMAGES_DIRECTORY + "gunOnButton.png";
    public static final String IMAGE_GUN_OFF_BUTTON = IMAGES_DIRECTORY + "gunOffButton.png";
    public static final String IMAGE_GUN_PANEL = IMAGES_DIRECTORY + "gunPanel.png";
    public static final String IMAGE_MODE_PANEL = IMAGES_DIRECTORY + "modePanel.png";
    public static final String IMAGE_MODE_SWITCH_DOWN = IMAGES_DIRECTORY + "modeSwitchDown.png";
    public static final String IMAGE_MODE_SWITCH_UP = IMAGES_DIRECTORY + "modeSwitchUp.png";
    public static final String IMAGE_PLUM_PUDDING = IMAGES_DIRECTORY + "plumPudding.png";
    public static final String IMAGE_PLUM_PUDDING_BUTTON = IMAGES_DIRECTORY + "plumPuddingButton.png";
    public static final String IMAGE_SCHRODINGER_BUTTON = IMAGES_DIRECTORY + "schrodingerButton.png";
    public static final String IMAGE_SOLAR_SYSTEM_BUTTON = IMAGES_DIRECTORY + "solarSystemButton.png";
    public static final String IMAGE_SPECTROMETER = IMAGES_DIRECTORY + "spectrometer.png";
    public static final String IMAGE_SPECTROMETER_SNAPSHOT = IMAGES_DIRECTORY + "spectrometerSnapshot.png";
    public static final String IMAGE_SPECTRUM = IMAGES_DIRECTORY + "spectrum.png";
    
    //XXX temporary images
    public static final String IMAGE_SCHRODINGER_ATOM = IMAGES_DIRECTORY + "tmp-schrodingerAtom.png";
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );
    public static final Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final double MIN_WAVELENGTH = 90;
    public static final double MAX_WAVELENGTH = VisibleColor.MAX_WAVELENGTH;
    
}
