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
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * FluorescentLightsConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampsConfig {

    // Localization
    public static final String localizedStringsPath = "discharge-lamps/localization/discharge-lamps-strings";

    public static final int NUM_ENERGY_LEVELS = 2;
    public static final int MAX_NUM_ENERGY_LEVELS = 6;
    // Max energy is 0, in all cases (requested by Sam M., 10/24/06
    public static final double MAX_ENERGY_LEVEL = 0.0;
//    public static final double MAX_ENERGY_LEVEL = -0.378;

    // Object locations and dimensions. Everything is keyed off the location of the cathode
    public static final int ELECTRODE_Y_LOCATION = 275;
    public static final int ELECTRODE_LENGTH = 200;
    public static final int CATHODE_X_LOCATION = 130;
    public static final Point CATHODE_LOCATION = new Point( CATHODE_X_LOCATION, ELECTRODE_Y_LOCATION );
    public static final double CATHODE_LENGTH = ELECTRODE_LENGTH;
    public static final Line2D CATHODE_LINE = new Line2D.Double( CATHODE_LOCATION.getX(),
                                                                 CATHODE_LOCATION.getY() - CATHODE_LENGTH / 2,
                                                                 CATHODE_LOCATION.getX(),
                                                                 CATHODE_LOCATION.getY() + CATHODE_LENGTH / 2 );

    public static final int ANODE_X_LOCATION = 430 + CATHODE_X_LOCATION;
    public static final Point ANODE_LOCATION = new Point( ANODE_X_LOCATION, ELECTRODE_Y_LOCATION );
    public static final double ANODE_LENGTH = ELECTRODE_LENGTH;
    public static final Line2D ANODE_LINE = new Line2D.Double( ANODE_LOCATION.getX(),
                                                               ANODE_LOCATION.getY() - ANODE_LENGTH / 2,
                                                               ANODE_LOCATION.getX(),
                                                               ANODE_LOCATION.getY() + ANODE_LENGTH / 2 );
    public static final Insets ELECTRODE_INSETS = new Insets( 15, 30, 15, 30 );
    public static final Point BEAM_CONTROL_CENTER_PT = new Point( ( CATHODE_X_LOCATION + ANODE_X_LOCATION ) / 2,
                                                                  195 );

    // Images
    public static final String IMAGE_FILE_DIRECTORY = "discharge-lamps/images/";
    public static final String ELECTRON_IMAGE_FILE_NAME = IMAGE_FILE_DIRECTORY + "electron.gif";
    public static final String HEATING_ELEMENT_FILE_NAME = IMAGE_FILE_DIRECTORY + "coil-2b.png";
    public static final String POSITIVE_CIRCUIT_IMAGE_FILE_NAME = IMAGE_FILE_DIRECTORY + "battery-w-wires-2.png";
    public static final String NEGATIVE_CIRCUIT_IMAGE_FILE_NAME = IMAGE_FILE_DIRECTORY + "battery-w-wires-2a.png";
    public static final String SPECTROMETER_IMAGE_FILE_NAME = IMAGE_FILE_DIRECTORY + "spectrometer-panel.png";
    public static final String BATTERY_IMAGE = IMAGE_FILE_DIRECTORY + "battery.png";
    public static final String SLIDER_KNOB_IMAGE = IMAGE_FILE_DIRECTORY + "sliderKnob.png";
    public static final String SLIDER_KNOB_HIGHLIGHT_IMAGE = IMAGE_FILE_DIRECTORY + "sliderKnobHighlight.png";

    // Assigned graphic layers
    public static final double TUBE_LAYER = 110;
    public static final double CIRCUIT_LAYER = 120;
    public static final double ELECTRON_LAYER = CIRCUIT_LAYER + 1;
    public static final double CONTROL_LAYER = CIRCUIT_LAYER + 100;

    // Clock specification
    public static double DT = 12;
    public static final int FPS = 25;

    // Scale factors
    public static final double MODEL_TO_VIEW_DIST_FACTOR = 1E12;
    // Fonts
    public static final Font DEFAULT_CONTROL_FONT = new PhetFont( Font.BOLD, 10 );
    // Factor that scales pixels to real dimensional units
    public static double PIXELS_PER_NM = 1E6;
    // Factor that converts volts on the control panel slider to real volts
    public static final double VOLTAGE_CALIBRATION_FACTOR = 1;
    //    public static final double VOLTAGE_CALIBRATION_FACTOR = 5.55;
    // Factor that makes the electron acceleration come out right for the potential between the plates
    public static final double ELECTRON_ACCELERATION_CALIBRATION_FACTOR = 1 / 5.55;

    // Misc
    public static final DecimalFormat VOLTAGE_FORMAT = new DecimalFormat( "#0.00 "+ SimStrings.getInstance().getString( "voltage.abbreviation" ));
    // Simulation time for which EnergySquiggles remain on the screen
    public static final double ENERGY_SQUIGGLE_PERSISTENCE = 50;
}