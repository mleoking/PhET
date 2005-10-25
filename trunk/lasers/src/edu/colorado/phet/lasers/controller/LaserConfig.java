/**
 * Class: LaserConfig
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.util.VisibleColor;

import java.awt.*;
import java.awt.geom.Point2D;

public class LaserConfig {

    // Localization
    public static final String localizedStringsPath = "localization/LasersStrings";

    // Clock parameters
    public static double DT;
    public static int FPS;

    // Physical things
    public static final double ELECTRON_RADIUS = 3;
    public static Point2D.Double ORIGIN = new Point2D.Double( 120, 200 );

    // Beam parameters
    public static final int MINIMUM_SEED_PHOTON_RATE = 0;
    public static final int MAXIMUM_SEED_PHOTON_RATE = 30;
    public static final int MINIMUM_PUMPING_PHOTON_RATE = 0;
    public static final int MAXIMUM_PUMPING_PHOTON_RATE = 400;
    public static final double PUMPING_BEAM_FANOUT = 45;
    public static final double SEED_BEAM_FANOUT = Math.toRadians( 1 );

    // Spontaneous emission times, in milliseconds
    public static final int MAXIMUM_STATE_LIFETIME = 200;
    public static final int MIDDLE_ENERGY_STATE_MAX_LIFETIME = 400;
    public static final int HIGH_ENERGY_STATE_MAX_LIFETIME = 100;
    public static final int HIGH_ENERGY_STATE_DEFAULT_LIFETIME = 10;
    public static final int DEFAULT_SPONTANEOUS_EMISSION_TIME = 50;

    // Tolerances used to determine if a photon matches with an atomic state energy
    public static final double ENERGY_TOLERANCE = 0.01;

    // Graphics things
    public static final int CONTROL_FONT_SIZE = 12;
    public static final int CONTROL_FONT_STYLE = Font.BOLD;
    public static final String IMAGE_DIRECTORY = "images/";
    public static final String ATOM_IMAGE_FILE = IMAGE_DIRECTORY + "particle-gray-med.gif";
    public static final String PHOTON_IMAGE_FILE = IMAGE_DIRECTORY + "photon-comet.png";
    public static final String MID_HIGH_ENERGY_PHOTON_IMAGE_FILE = IMAGE_DIRECTORY + "particle-deep-red-xsml.gif";
    public static final String HIGH_ENERGY_PHOTON_IMAGE_FILE = IMAGE_DIRECTORY + "particle-blue-xsml.gif";
    public static final String LOW_ENERGY_PHOTON_IMAGE_FILE = IMAGE_DIRECTORY + "particle-red-xsml.gif";
    public static final String RAY_GUN_IMAGE_FILE = IMAGE_DIRECTORY + "flashlight.png";

    // Graphics layers
    public static final double CAVITY_LAYER = 10;
    public static final double ATOM_LAYER = 12;
    public static final double PHOTON_LAYER = 13;
    public static final double LEFT_MIRROR_LAYER = 14;
    public static final double RIGHT_MIRROR_LAYER = 11;

    public static final double MIN_WAVELENGTH = VisibleColor.MIN_WAVELENGTH;
    public static final double MAX_WAVELENGTH = VisibleColor.MAX_WAVELENGTH;

    // Fonts
    public static final Font DEFAULT_CONTROL_FONT = new Font( "Lucida sans", Font.BOLD, 10 );

    // Angle within which a photon is considered to be moving horizontally. This is used by the
    // mirrors to "cheat" photons into lasing, and by the wave graphic to determine its amplitude
    public static double PHOTON_CHEAT_ANGLE = 5;

    // Thickness of the mirror graphics
    public static final double MIRROR_THICKNESS = 15;

    // Threshold number of horizontal photons that is considered "lasing"
    public static int LASING_THRESHOLD = 80;
    // Number of photons in the system that will cause the thing to blow up
    public static int KABOOM_THRESHOLD = 300;

    // Factor that scales pixels to real dimensional units
    public static double PIXELS_PER_NM = 1E6;

    public static boolean ENABLE_ALL_STIMULATED_EMISSIONS = true;
}
