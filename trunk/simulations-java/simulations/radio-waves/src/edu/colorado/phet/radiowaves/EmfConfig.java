/**
 * Class: Config Package: edu.colorado.phet.emf Author: Another Guy Date: May
 * 11, 2004
 */

package edu.colorado.phet.radiowaves;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

public class EmfConfig {

    // Version
    //    public static final String VERSION = "1.04";
    public static final String VERSION = PhetApplicationConfig.getVersion( "radio-waves" ).formatForTitleBar();
    // Parameters
    public static double SINGLE_VECTOR_ROW_OFFSET = 0.5;

    // Images
    public final static String bigElectronImg = "radio-waves/images/blue-sml.gif";
    public final static String smallElectronImg = "radio-waves/images/blue-sml.gif";

    // Localization
    public static final String localizedStringsPath = "radio-waves/localization/radio-waves-strings";

    public static final double WAVEFRONT_HEIGHT = 15;

    public static final Color FORCE_COLOR = new Color( 200, 0, 0 );
    public static final Color FIELD_COLOR = new Color( 0, 100, 0 );
    public static final int SHOW_ELECTRIC_FIELD = -1;
    public static final int SHOW_FORCE_ON_ELECTRON = 1;

    public static final Object SINGLE_VECTOR_ROW_CENTERED = new Object();
    public static final Object SINGLE_VECTOR_ROW_PINNED = new Object();
}
