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

/**
 * PhotoelectricConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricConfig {

    public static final String IMAGE_DIRECTORY = "images/";
    public static final String LAMP_IMAGE_FILE = IMAGE_DIRECTORY + "flashlight.png";
    public static final String SLIDER_KNOB_IMAGE = IMAGE_DIRECTORY + "sliderKnob.png";
    public static final String BEAM_CONTROL_PANEL_IMAGE = IMAGE_DIRECTORY + "beam-control.png";

    public static final int GRAPH_DOT_RADIUS = 5;
    public static final double TUBE_LAYER = 2000;
    public static final double CIRCUIT_LAYER = TUBE_LAYER - 1;
    public static final double LAMP_LAYER = 1000;
    public static final double BEAM_LAYER = 900;
    public static final double ELECTRON_LAYER = 900;
}
