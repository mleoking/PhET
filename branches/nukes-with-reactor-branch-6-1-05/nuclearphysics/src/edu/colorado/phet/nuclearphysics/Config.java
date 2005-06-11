/**
 * Class: Config
 * Package: edu.colorado.phet.nuclearphysics
 * Author: Another Guy
 * Date: Mar 6, 2004
 */
package edu.colorado.phet.nuclearphysics;


public class Config {

    //    public static PotentialProfile u235PotentialProfile = new PotentialProfile( 500, 250, 75 );
    //    public static double AlphaLocationUncertaintySigmaFactor = 0.34;
    public static double AlphaLocationUncertaintySigmaFactor = 0.333;

    public static final double neutronSpeed = 6;
    public static final double fissionDisplacementVelocity = 2;
    public static final double defaultProfileWidth = 500;

    public static final double alphaParticleLevel = 100;
    public static final double nucleusLevel = 100;
    public static final double backgroundGraphicLevel = 0;

    public static final double MAX_TEMPERATURE = 0.5;

    // Images
    private static final String IMAGE_DIR = "images/";
    public static final String HANDLE_IMAGE_NAME = IMAGE_DIR + "wall-handle.gif";
    public static final String CONTROL_ROD_IMAGE = IMAGE_DIR + "control-rod-1.png";
}
