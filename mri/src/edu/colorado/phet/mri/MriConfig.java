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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.mri.model.DipoleOrientationAgent;
import edu.colorado.phet.mri.model.SampleMaterial;
import edu.colorado.phet.mri.model.PhotonDipoleExpert;
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

    // Clock parameters
    public static double DT = 12;
    public static int FPS = 25;

    // Images
    public static final String IMAGE_PATH = "images/";
    public static final String DIPOLE_IMAGE = IMAGE_PATH + "dipole.gif";
//    public static final String HEAD_IMAGE = IMAGE_PATH + "mrihead.gif";
//    public static final String HEAD_IMAGE = IMAGE_PATH + "head-1A.gif";
    public static final String HEAD_IMAGE = IMAGE_PATH + "head-1A.png";
//    public static final String HEAD_IMAGE = IMAGE_PATH + "head-2.gif";

    // Physical parameters
    public static final double MIN_FEQUENCY = 20E6;
    public static final double MAX_FEQUENCY = 80E6;

    public static final double MAX_FADING_HEIGHT = 50;
    // Conversion factor between current and B field
    public static final double MAX_FADING_COIL_CURRENT = 100;   // arbitrary units, suitable for a JSlider
    public static final double MAX_FADING_COIL_FIELD = 3;       // Teslas
    public static final double CURRENT_TO_FIELD_FACTOR = MAX_FADING_COIL_FIELD / MAX_FADING_COIL_CURRENT;
    public static final double MAX_MU = 2;
    public static final double MAX_ENERGY_LEVEL_SEPARATION = MAX_FADING_COIL_FIELD * MAX_MU;
    public static final double MAX_POWER = 100;
    // Difference in energies that is equivalent to 0
    public static final double ENERGY_EPS = PhysicsUtil.frequencyToEnergy( SampleMaterial.HYDROGEN_GYROMAGNETIC_RATIO * MAX_FADING_COIL_FIELD ) / 150;
    // Length of time (simulation time) that a dipole kicked into spin down state will stay there until
    // it spontaneously fall to spin up
    public static long SPIN_DOWN_TIMEOUT = 250;

    public static final Vector2D EMITTED_PHOTON_DIRECTION = new Vector2D.Double( 1, 0 );

    // Can photons emitted by dipoles be reabsorbed by another dipole?
    public static final boolean REABSORPTION_ALLOWED = false;

        // Model to view conversion factors
    public static class ModelToView {
        public static double FREQUENCY = 1 / 4E9;
    }

    // Layout
    public static Point2D SAMPLE_CHAMBER_LOCATION = new Point2D.Double( 200, 120 );
    public static double SCALE_FOR_ORG = 400.0 / 600;
    public static double SAMPLE_CHAMBER_WIDTH = 400;
//    public static double SAMPLE_CHAMBER_WIDTH = 600;
    public static double SAMPLE_CHAMBER_HEIGHT = 267;
//    public static double SAMPLE_CHAMBER_HEIGHT = 400;

    // Initial conditions
    public static class InitialConditions {
        public static double FADING_MAGNET_CURRENT = 0;
        public static final double DIPOLE_PRECESSION = Math.toRadians( 0 );
        public static final DipoleOrientationAgent.SpinDeterminationPolicy SPIN_DETERMINATION_POLICY = new DipoleOrientationAgent.DeterministicPolicy();
        public static final MonitorPanel.DipoleRepresentationPolicy MONITOR_PANEL_REP_POLICY_DIPOLE = new MonitorPanel.DiscretePolicyB();
    }
}
