/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

import edu.colorado.phet.common.util.DoubleRange;
import edu.colorado.phet.common.util.IntegerRange;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.PhetProjectConfig;


/**
 * RSConstants is a collection of constants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RSConstants {

    /* Not intended for instantiation. */
    private RSConstants() {}
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    private static final String PROJECT = "rutherford-scattering";
    
    public static final PhetProjectConfig CONFIG = PhetProjectConfig.forProject( PROJECT );
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Dimensions
    //----------------------------------------------------------------------------
    
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 800, 800 );

    // Animation box size, must be square!
    public static final Dimension ANIMATION_BOX_SIZE = new Dimension( 700, 700 );
    
    public static final Dimension TINY_BOX_SIZE = new Dimension( 10, 10 );

    public static final Dimension BOX_OF_HYDROGEN_SIZE = new Dimension( 70, 18 );
    public static final double BOX_OF_HYDROGEN_DEPTH = 10;
    
    public static final Dimension BEAM_SIZE = new Dimension( (int) ( .75 * BOX_OF_HYDROGEN_SIZE.width ), 75 );
    
    public static final int MIN_CONTROL_PANEL_WIDTH = CONFIG.getInt( "int.minControlPanelWidth", 200 );
    
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
    public static final Font CONTROL_FONT = new Font( DEFAULT_FONT_NAME, Font.BOLD, 14 );
    
    //----------------------------------------------------------------------------
    // Borders
    //----------------------------------------------------------------------------
    
    // the border style to use for titled borders
    public static final Border TITLED_BORDER_STYLE = BorderFactory.createLineBorder( Color.BLACK, 2 );

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

    public static final Image IMAGE_GUN = CONFIG.getImage( "gun.png" );
    public static final Image IMAGE_GUN_ON_BUTTON = CONFIG.getImage( "gunOnButton.png" );
    public static final Image IMAGE_GUN_OFF_BUTTON = CONFIG.getImage(  "gunOffButton.png" );
    public static final Image IMAGE_PLUM_PUDDING = CONFIG.getImage(  "plumPudding.png" );
    
    //----------------------------------------------------------------------------
    // Strings
    //----------------------------------------------------------------------------
    
    public static final String S_ALPHA_PARTICLE = CONFIG.getString( "string.alphaParticle" );
    public static final String S_ALPHA_PARTICLE_PROPERTIES = CONFIG.getString( "string.alphaParticleProperties" );
    public static final String S_ATOM_PROPERTIES = CONFIG.getString( "string.atomProperties" );
    public static final String S_CONFIRM_RESET_ALL = CONFIG.getString( "string.confirmResetAll" );
    public static final String S_ELECTRON = CONFIG.getString( "string.electron" );
    public static final String S_ENERGY = CONFIG.getString( "string.energy" );
    public static final String S_LEGEND = CONFIG.getString( "string.legend" );
    public static final String S_MAX_ENERGY = CONFIG.getString( "string.maxEnergy" );
    public static final String S_MIN_ENERGY = CONFIG.getString( "string.minEnergy" );
    public static final String S_NEUTRON = CONFIG.getString( "string.neutron" );
    public static final String S_NUMBER_OF_NEUTRONS = CONFIG.getString( "string.numberOfNeutrons" );
    public static final String S_NUMBER_OF_PROTONS = CONFIG.getString( "string.numberOfProtons" );
    public static final String S_PLUM_PUDDING = CONFIG.getString( "string.plumPuddingAtom" );
    public static final String S_PROTON = CONFIG.getString( "string.proton" );
    public static final String S_RESET_ALL = CONFIG.getString( "string.resetAll" );
    public static final String S_RUTHERFORD_ATOM = CONFIG.getString( "string.rutherfordAtom" );
    public static final String S_SHOW_TRACES = CONFIG.getString( "string.showTraces" );
    public static final String S_TURN_ON_THE_GUN = CONFIG.getString( "string.turnOnTheGun" );
    
    //----------------------------------------------------------------------------
    // Default state of the model
    //----------------------------------------------------------------------------
    
    public static final boolean CLOCK_PAUSED = false;
    public static final boolean TRACES_ENABLED = false;
    public static final boolean GUN_ENABLED = false;
    public static final double GUN_INTENSITY = 1.0; // 0-1 (1=100%)
    public static final DoubleRange INITIAL_SPEED_RANGE = new DoubleRange( 5, 12, 10, 1 );
    public static final IntegerRange NUMBER_OF_PROTONS_RANGE = new IntegerRange( 20, 100, 79 );
    public static final IntegerRange NUMBER_OF_NEUTRONS_RANGE = new IntegerRange( 20, 150, 118 );
    public static final double ELECTRON_ANGULAR_SPEED = Math.toRadians( 0.75 ); // radians per dt
}
