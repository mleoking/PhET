/**
 * Class: Config
 * Package: edu.colorado.phet.nuclearphysics
 * Author: Another Guy
 * Date: Mar 6, 2004
 */
package edu.colorado.phet.nuclearphysics;

import java.awt.*;


public class Config {

    public static String version = "1.08";

    // Calibration factor for alpha decay time
    public static final double ALPHA_DECAY_SIM_TIME_STEP = 5 * 0.516 / 0.471 * 0.615 / 0.372;

    public static final int VESSEL_TEMPERATURE_UPDATE_PERIOD = 400;

    //    public static double AlphaLocationUncertaintySigmaFactor = 0.34;
    public static double AlphaLocationUncertaintySigmaFactor = 0.4;
//    public static double AlphaLocationUncertaintySigmaFactor = 0.34;
//    public static double AlphaLocationUncertaintySigmaFactor = 0.3;
//    public static double AlphaLocationUncertaintySigmaFactor = 0.333;
    public static final int U235MorphSpeedFactor = 1;

    // Converts physical nuclear distances to pixels
    public static final double modelToViewDist = 30 / 7.12E-15;
    public static final double modelToViewMeV = 50 / 33.1;

    public static final double neutronSpeed = 6;
    public static final double fissionDisplacementVelocity = 2;
    public static final double defaultProfileWidth = 500;

    public static final double alphaParticleLevel = 101;
    public static final double nucleusLevel = 100;
    public static final double backgroundGraphicLevel = 0;

    public static final double MAX_TEMPERATURE = 0.5;

    // Images
    private static final String IMAGE_DIR = "images/";
    public static final String HANDLE_IMAGE_NAME = IMAGE_DIR + "wall-handle.gif";
    public static final String CONTROL_ROD_IMAGE = IMAGE_DIR + "control-rod-1.png";
    // Regulates how fast the profile rises when fission occurs

    // Fonts
    public static final Font PROFILE_PANEL_TITLE_FONT = new Font( "Lucida sans", Font.BOLD, 14 );
    public static final double CONTAINMENT_EXPLOSION_THRESHOLD = 400;
//    public static final double CONTAINMENT_EXPLOSION_THRESHOLD = 100;
    public static final double CONTAINMENT_NEUTRON_ABSORPTION_PROBABILITY = 0.9;
}
