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

import java.awt.Dimension;
import java.awt.Point;
import java.text.DecimalFormat;

import edu.colorado.phet.dischargelamps.DischargeLampsConfig;

/**
 * PhotoelectricConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricConfig {

    public static final String PROJECT_NAME = "photoelectric";

    // Images
    public static final String LAMP_IMAGE_FILE = "flashlight.png";
    public static final String BEAM_CONTROL_PANEL_IMAGE = "photoelectric-beam-control.png";
    public static final String SNAPSHOT_BUTTON_IMAGE = "camera.png";
    public static final String ZOOM_IN_BUTTON_IMAGE = "zoomIn.gif";
    public static final String ZOOM_OUT_BUTTON_IMAGE = "zoomOut.gif";
    public static final String CIRCUIT_A_IMAGE = "circuit-A.png";
    public static final String CIRCUIT_B_IMAGE = "circuit-B.png";

    // View parameters
    public static final int GRAPH_DOT_RADIUS = 5;
    public static final double TUBE_LAYER = 2000;
    public static final double CIRCUIT_LAYER = TUBE_LAYER - 1;
    public static final double LAMP_LAYER = 1000;
    public static final double BEAM_LAYER = 900;
    public static final double ELECTRON_LAYER = 900;
    public static final Dimension CHART_SIZE = new Dimension( 170, 94 );
    public static final Point BEAM_CONTROL_LOCATION = new Point( DischargeLampsConfig.CATHODE_X_LOCATION + 290, 20 );


    public static final DecimalFormat BEAM_PERCENTAGE_FORMAT = new DecimalFormat( "#%" );
    public static final DecimalFormat BEAM_WAVELENGTH_FORMAT = new DecimalFormat( "### nm" );

    // Model related fields
    public static final double MIN_WAVELENGTH = 100;
    public static final double MAX_WAVELENGTH = 850;
}
