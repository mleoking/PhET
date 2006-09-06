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
import java.awt.Font;

import javax.swing.BorderFactory;
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
    // Application
    //----------------------------------------------------------------------------
    
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    
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

    public static final String FONT_NAME = "Lucida Sans";
    public static final Font CONTROL_FONT = new Font( FONT_NAME, Font.PLAIN, 16 );
    public static final Font TITLE_FONT = new Font( FONT_NAME, Font.BOLD, 20 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.WHITE;
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    // Colors used for wavelengths outside the visible spectrum
    public static final Color UV_COLOR = Color.GRAY;
    public static final Color IR_COLOR = Color.GRAY;
    
    // Color used for alpha particle intensity slider
    public static final Color ALPHA_PARTICLES_COLOR = Color.GRAY;
    
    // Colors used for foreground and background in Gun control panels
    public static final Color GUN_CONTROLS_BACKGROUND = new Color( 220, 220, 220 );
    public static final Color GUN_CONTROLS_FOREGROUND = Color.BLACK;
    
    public static final Color MODE_CONTROL_BACKGROUND = GUN_CONTROLS_BACKGROUND;
    public static final Color MODE_CONTROL_FOREGROUND = GUN_CONTROLS_FOREGROUND;
    
    public static final Color ATOMIC_MODEL_CONTROL_BACKGROUND = GUN_CONTROLS_BACKGROUND;
    public static final Color ATOMIC_MODEL_FOREGROUND = GUN_CONTROLS_FOREGROUND;
    
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
    public static final String IMAGE_ALPHA_PARTICLE = IMAGES_DIRECTORY + "alphaParticle.png";
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
    public static final String IMAGE_GUN = IMAGES_DIRECTORY + "gun.png";
    public static final String IMAGE_GUN_CONTROL_CABLE = IMAGES_DIRECTORY + "gunControlCable.png";
    public static final String IMAGE_GUN_ON_BUTTON = IMAGES_DIRECTORY + "gunOnButton.png";
    public static final String IMAGE_GUN_OFF_BUTTON = IMAGES_DIRECTORY + "gunOffButton.png";
    public static final String IMAGE_PHOTON = IMAGES_DIRECTORY + "photon.png";
    public static final String IMAGE_SPECTRUM = IMAGES_DIRECTORY + "spectrum.png";
    
    //XXX temporary images
    public static final String IMAGE_BOHR_ENERGY_DIAGRAM = IMAGES_DIRECTORY + "bohrEnergyDiagram.png";
    public static final String IMAGE_DEBROGLIE_ENERGY_DIAGRAM = IMAGES_DIRECTORY + "deBroglieEnergyDiagram.png";
    public static final String IMAGE_SCHRODINGER_ENERGY_DIAGRAM = IMAGES_DIRECTORY + "schrodingerEnergyDiagram.png";
    public static final String IMAGE_SOLAR_SYSTEM_ENERGY_DIAGRAM = IMAGES_DIRECTORY + "solarSystemEnergyDiagram.png";
    
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
