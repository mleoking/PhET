/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier;


/**
 * FourierConfig contains global configuration values.
 * See FourierStrings.properties for localized Strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierConfig {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Resource bundles for localization.
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/FourierStrings";

    // Clock constants
    public static final double TIME_STEP = 1;
    public static final int FRAME_RATE = 25;  // frames per second
    public static final int WAIT_TIME = ( 1000 / FRAME_RATE );  // milliseconds
    
    // Images
    private static final String IMAGES_DIRECTORY = "images/";
    
    // Dimensions
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    public static final int CONTROL_PANEL_SPACER_HEIGHT = 15;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private FourierConfig() {}
}
