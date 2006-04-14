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

import edu.colorado.phet.mri.model.DipoleOrientationAgent;
import edu.colorado.phet.mri.view.MonitorPanel;

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
    public static final double MAX_FADING_HEIGHT = 50;
    public static final double MAX_FADING_COIL_CURRENT = 100;
    public static final double MAX_FADING_COIL_FIELD = MAX_FADING_COIL_CURRENT;

    // Layout
    public static Point2D SAMPLE_CHAMBER_LOCATION = new Point2D.Double( 200, 200 );
    public static double SAMPLE_CHAMBER_WIDTH = 600;
    public static double SAMPLE_CHAMBER_HEIGHT = 400;

    // Initial conditions
    public static class InitialConditions {
        public static double FADING_MAGNET_CURRENT = 0;
        public static final double DIPOLE_PRECESSION = Math.toRadians( 0 );
        public static final DipoleOrientationAgent.SpinDeterminationPolicy SPIN_DETERMINATION_POLICY = new DipoleOrientationAgent.DeterministicPolicy();
        public static final MonitorPanel.RepresentationPolicy MONITOR_PANEL_REP_POLICY = new MonitorPanel.DiscretePolicy();
    }
}
