/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric;

import edu.colorado.phet.dischargelamps.DischargeLampsConfig;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * PhotoelectricConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricConfig {

    // Version
    public static final String VERSION = "1.03";

    // Images
    public static final String IMAGE_DIRECTORY = "images/";
    public static final String LAMP_IMAGE_FILE = IMAGE_DIRECTORY + "flashlight.png";
    public static final String SLIDER_KNOB_IMAGE = IMAGE_DIRECTORY + "sliderKnob.png";
    public static final String BEAM_CONTROL_PANEL_IMAGE = IMAGE_DIRECTORY + "photoelectric-beam-control.png";
    public static final String SNAPSHOT_BUTTON_IMAGE = IMAGE_DIRECTORY + "camera.png";
    public static final String ZOOM_IN_BUTTON_IMAGE = IMAGE_DIRECTORY + "zoomIn.gif";
    public static final String ZOOM_OUT_BUTTON_IMAGE = IMAGE_DIRECTORY + "zoomOut.gif";

    // View parameters
    public static final int GRAPH_DOT_RADIUS = 5;
    public static final double TUBE_LAYER = 2000;
    public static final double CIRCUIT_LAYER = TUBE_LAYER - 1;
    public static final double LAMP_LAYER = 1000;
    public static final double BEAM_LAYER = 900;
    public static final double ELECTRON_LAYER = 900;
    public static final Dimension CHART_SIZE = new Dimension( 170, 94 );
    public static final Point BEAM_CONTROL_LOCATION = new Point( DischargeLampsConfig.CATHODE_X_LOCATION + 290, 20 );


    public static final DecimalFormat BEAM_PERCENTAGE_FORMAT =new DecimalFormat( "#%" );
    public static final DecimalFormat BEAM_WAVELENGTH_FORMAT =new DecimalFormat( "### nm" );

    // Model related fields
    public static final double MIN_WAVELENGTH = 100;
    public static final double MAX_WAVELENGTH = 850;
}
