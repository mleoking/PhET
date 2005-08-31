/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * FluorescentLightsConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampsConfig {

    // Localization
    public static final String localizedStringsPath = "localization/DischargeLampsStrings";

    public static final int NUM_ENERGY_LEVELS = 2;
    public static final int MAX_NUM_ENERGY_LEVELS = 6;

    // Object locations and dimensions. Everything is keyed off the location of the cathode
    public static final double ELECTRON_RADIUS = 3;
    public static final int ELECTRODE_Y_LOCATION = 300;
    public static final int ELECTRODE_LENGTH = 200;
    public static final Point CATHODE_LOCATION = new Point( 170, ELECTRODE_Y_LOCATION );
    public static final double CATHODE_LENGTH = ELECTRODE_LENGTH;
    public static final Line2D CATHODE_LINE = new Line2D.Double( CATHODE_LOCATION.getX(),
                                                                 CATHODE_LOCATION.getY() - CATHODE_LENGTH / 2,
                                                                 CATHODE_LOCATION.getX(),
                                                                 CATHODE_LOCATION.getY() + CATHODE_LENGTH / 2 );

    public static final Point ANODE_LOCATION = new Point( 600, ELECTRODE_Y_LOCATION );
    public static final double ANODE_LENGTH = ELECTRODE_LENGTH;
    public static final Line2D ANODE_LINE = new Line2D.Double( ANODE_LOCATION.getX(),
                                                               ANODE_LOCATION.getY() - ANODE_LENGTH / 2,
                                                               ANODE_LOCATION.getX(),
                                                               ANODE_LOCATION.getY() + ANODE_LENGTH / 2 );
    public static final Insets ELECTRODE_INSETS = new Insets( 15, 30, 15, 30 );


    // Images
    public static final String IMAGE_FILE_DIRECTORY = "images";
    public static final String ELECTRON_IMAGE_FILE_NAME = IMAGE_FILE_DIRECTORY + "/" + "electron.gif";
    public static final String HEATING_ELEMENT_FILE_NAME = IMAGE_FILE_DIRECTORY + "/" + "coil-2.png"; 

    // Assigned graphic layers
    public static final double TUBE_LAYER = 110;
    public static final double CIRCUIT_LAYER = 120;
    public static final double ELECTRON_LAYER = CIRCUIT_LAYER + 1;

    // Clock specification
    public static final double DT = 12;
    public static final int FPS = 25;

    // Scale factors
    public static final double MODEL_TO_VIEW_DIST_FACTOR = 1E12;
    // Fonts
    public static final Font defaultControlFont = new Font( "Lucida sans", Font.BOLD, 10 );
}