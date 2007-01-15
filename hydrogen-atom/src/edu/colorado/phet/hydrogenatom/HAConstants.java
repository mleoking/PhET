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
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import edu.colorado.phet.common.view.util.SimStrings;
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

    // Animation box size, must be square!
    public static final Dimension ANIMATION_BOX_SIZE = new Dimension( 475, 475 );
    
    public static final Dimension TINY_BOX_SIZE = new Dimension( 10, 10 );

    public static final Dimension BOX_OF_HYDROGEN_SIZE = new Dimension( 70, 70 );
    public static final double BOX_OF_HYDROGEN_DEPTH = 10;
    
    public static final Dimension BEAM_SIZE = new Dimension( (int) ( .75 * BOX_OF_HYDROGEN_SIZE.width ), 75 );
    
    // Spectrometer
    public static final Dimension SPECTROMETER_SIZE = new Dimension( 500, 210 );
    
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
        0.5, 1, 2, 3
    };
       
    // Defaults
    public static final double DEFAULT_CLOCK_STEP = CLOCK_STEPS[ HADefaults.CLOCK_INDEX ];
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    // NOTE: font sizes are configurable in the SimStrings file!
    
    // Default font properties
    public static final String DEFAULT_FONT_NAME = new JLabel( "PhET" ).getFont().getName();
    public static final int DEFAULT_FONT_STYLE = Font.BOLD;
    public static final int DEFAULT_FONT_SIZE = 16;
    
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
    public static final Color UV_COLOR = Color.LIGHT_GRAY;
    public static final Color IR_COLOR = UV_COLOR;
    
    // Colors used for the wavelength control
    public static final Color UV_TRACK_COLOR = UV_COLOR;
    public static final Color UV_LABEL_COLOR = Color.BLACK;
    public static final Color IR_TRACK_COLOR = IR_COLOR;
    public static final Color IR_LABEL_COLOR = UV_LABEL_COLOR;
    
    // Color used for alpha particle intensity slider
    public static final Color ALPHA_PARTICLES_COLOR = UV_TRACK_COLOR;
    
    // Color of the animation box
    public static final Color ANIMATION_BOX_COLOR = Color.BLACK;
    public static final Color ANIMATION_BOX_STROKE_COLOR = Color.WHITE;
    
    // Color for photon used on controls & in legends
    public static final double PHOTON_ICON_WAVELENGTH = 530; // nm (green)
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    
    public static final String IMAGE_ATOMIC_MODEL_PANEL = IMAGES_DIRECTORY + "atomicModelPanel.png";
    public static final String IMAGE_BILLIARD_BALL_BUTTON = IMAGES_DIRECTORY + "billiardBallButton.png";
    public static final String IMAGE_BOHR_BUTTON = IMAGES_DIRECTORY + "bohrButton.png";
    public static final String IMAGE_CAMERA = IMAGES_DIRECTORY + "camera.png";
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
    public static final String IMAGE_CLOSE_BUTTON = IMAGES_DIRECTORY + "closeButton.png";
    public static final String IMAGE_DEBROGLIE_BUTTON = IMAGES_DIRECTORY + "deBroglieButton.png";
    public static final String IMAGE_GUN = IMAGES_DIRECTORY + "gun.png";
    public static final String IMAGE_GUN_CONTROL_CABLE = IMAGES_DIRECTORY + "gunControlCable.png";
    public static final String IMAGE_GUN_ON_BUTTON = IMAGES_DIRECTORY + "gunOnButton.png";
    public static final String IMAGE_GUN_OFF_BUTTON = IMAGES_DIRECTORY + "gunOffButton.png";
    public static final String IMAGE_GUN_PANEL = IMAGES_DIRECTORY + "gunPanel.png";
    public static final String IMAGE_KABOOM = IMAGES_DIRECTORY + "kaboom.png";
    public static final String IMAGE_MODE_PANEL = IMAGES_DIRECTORY + "modePanel.png";
    public static final String IMAGE_MODE_SWITCH_DOWN = IMAGES_DIRECTORY + "modeSwitchDown.png";
    public static final String IMAGE_MODE_SWITCH_UP = IMAGES_DIRECTORY + "modeSwitchUp.png";
    public static final String IMAGE_PLUM_PUDDING = IMAGES_DIRECTORY + "plumPudding.png";
    public static final String IMAGE_PLUM_PUDDING_BUTTON = IMAGES_DIRECTORY + "plumPuddingButton.png";
    public static final String IMAGE_SCHRODINGER_BUTTON = IMAGES_DIRECTORY + "schrodingerButton.png";
    public static final String IMAGE_SOLAR_SYSTEM_BUTTON = IMAGES_DIRECTORY + "solarSystemButton.png";
    public static final String IMAGE_SPECTROMETER_PANEL = IMAGES_DIRECTORY + "spectrometerPanel.png";
    public static final String IMAGE_SPECTROMETER_SNAPSHOT_PANEL = IMAGES_DIRECTORY + "spectrometerSnapshotPanel.png";
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );
    public static final Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    public static final double MIN_WAVELENGTH = 92;
    public static final double MAX_WAVELENGTH = VisibleColor.MAX_WAVELENGTH;
    
    public static final double PHOTON_INITIAL_SPEED = 5; // distance moved per dt
    public static final double ALPHA_PARTICLE_INITIAL_SPEED = 5; // distance moved per dt
    
    //----------------------------------------------------------------------------
    // Ranges
    //----------------------------------------------------------------------------
    
    public static final double SPECTROMETER_MIN_WAVELENGTH = MIN_WAVELENGTH;
    public static final double SPECTROMETER_MAX_WAVELENGTH = 7500; // nm
    
    //----------------------------------------------------------------------------
    // Features
    //----------------------------------------------------------------------------
    
    // Shows the atom's state variables in the lower right corner of the animation box
    public static final boolean SHOW_STATE_DISPLAY = true;
    
}
