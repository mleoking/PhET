/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri;

import java.awt.geom.Point2D;

/**
 * Calibration
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriConfig {

    public final static double scale = 1;

    // Images
    public static final String IMAGE_PATH = "images/";
    public static final String DIPOLE_IMAGE = IMAGE_PATH + "dipole.gif";

    // Physical parameters
    public static final double MAX_FADING_COIL_CURRENT = 100;
    public static final double MAX_FADING_COIL_FIELD = MAX_FADING_COIL_CURRENT;

    // Layout
    public static Point2D sampleChamberLocation = new Point2D.Double( 200, 200 );
    public static double sampleChamberWidth = 600;
    public static double sampleChamberHeight = 400;
}
