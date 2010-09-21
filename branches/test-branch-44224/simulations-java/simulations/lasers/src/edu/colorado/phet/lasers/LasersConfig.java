/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers;

import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class LasersConfig {
    
    public static final String PROJECT_NAME = "lasers";
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 750 );

    // Clock parameters
    public static double DT = 12;
    public static int FPS = 25;

    //----------------------------------------------------------------
    // Physical configuration
    //----------------------------------------------------------------

    // Physical things
    //    public static final double ELECTRON_RADIUS = 3;
    public static Point2D.Double ORIGIN = new Point2D.Double( 120, 200 );

    // Beam parameters
    public static final int MINIMUM_SEED_PHOTON_RATE = 0;
    public static final int MAXIMUM_SEED_PHOTON_RATE = 30;
    public static final int MINIMUM_PUMPING_PHOTON_RATE = 0;
    public static final int MAXIMUM_PUMPING_PHOTON_RATE = 400;
    public static final double PUMPING_BEAM_FANOUT = Math.toRadians( 45 );
    public static final double SEED_BEAM_FANOUT = Math.toRadians( 1 );

    // Spontaneous emission times, in milliseconds
    public static final int MAXIMUM_STATE_LIFETIME = 400;
    public static final int MINIMUM_GROUND_STATE_LIFETIME = 200;

    // Angle within which a photon is considered to be moving horizontally. This is used by the
    // mirrors to "cheat" photons into lasing, and by the wave graphic to determine its amplitude
    public static double PHOTON_CHEAT_ANGLE = Math.toRadians( 3 );

    // Thickness of the mirror graphics
    public static final double MIRROR_THICKNESS = 15;

    // Threshold number of horizontal photons that is considered "lasing"
    public static int LASING_THRESHOLD = 40;
//    public static int LASING_THRESHOLD = 80;

    // Number of photons in the system that will cause the thing to blow up
    public static int KABOOM_THRESHOLD = 300;

    // The period over which the number of atoms in each level is averaged before the
    // number of atoms is updated for the energy levels monitor panel
    public static final double ENERGY_LEVEL_MONITOR_AVERAGING_PERIOD = 0;

    public static boolean ENABLE_ALL_STIMULATED_EMISSIONS = true;

    //----------------------------------------------------------------
    // Graphic configuration
    //----------------------------------------------------------------

    // Fonts
    public static final int CONTROL_FONT_SIZE = 12;
    public static final int CONTROL_FONT_STYLE = Font.BOLD;
    
    // Images
    public static final String ATOM_IMAGE_FILE = "particle-gray-med.gif";
    public static final String RAY_GUN_IMAGE_FILE = "flashlight.png";
    public static final String SEED_BEAM_CONTROL_PANEL_IMAGE = "beam-control-lower.png";
    public static final String PUMP_BEAM_CONTROL_PANEL_IMAGE = "beam-control-upper.png";
    public static final String BEAM_CONTROL_IMAGE = "beam-control.png";
    public static final String WIRE_IMAGE = "wire.png";
    public static final String POWER_METER_IMAGE = "power-meter.png";

    // Graphics layers
    public static final double CAVITY_LAYER = 10;
    public static final double ATOM_LAYER = 12;
    public static final double PHOTON_LAYER = 13;
    public static final double LEFT_MIRROR_LAYER = 14;
    public static final double RIGHT_MIRROR_LAYER = 11;
    public static final double CONTROL_LAYER = 1E2;

    // Fonts
    public static final Font DEFAULT_CONTROL_FONT = new PhetFont( Font.BOLD, 10 );

}
