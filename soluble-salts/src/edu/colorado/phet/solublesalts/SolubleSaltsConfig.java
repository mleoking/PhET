/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;

/**
 * SolubleSaltsConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsConfig {
    
    // Descriptive information
    public static final String TITLE = "Soluble Salts";
    public static final String DESCRIPTION = "Soluble Salts";
    public static final String VERSION = "0.00.01";
    
    // Clock parameters
    public static final double DT = 1;
    public static final int FPS = 25;

    // Images
    public static final String IMAGE_PATH = "images/";
    public static final String SHAKER_IMAGE_NAME = IMAGE_PATH + "pump.gif";
    public static final String BLUE_ION_IMAGE_NAME = IMAGE_PATH + "molecule-big.gif";

    // Misc
    public static final String STRINGS_BUNDLE_NAME = "localization/SolubleSaltsStrings" ;
}
