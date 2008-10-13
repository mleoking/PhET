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
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.mri.model.DipoleOrientationAgent;
import edu.colorado.phet.mri.model.SampleMaterial;
import edu.colorado.phet.mri.view.MonitorPanel;

/**
 * Calibration
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriConfig {

    public static final String PROJECT_NAME = "mri";

    public final static double scale = 1;

    // Clock parameters
    public static double DT = 12;
    public static int FPS = 25;

    // Images
    public static final String DIPOLE_IMAGE = "dipole-4.gif";
//    public static final String DIPOLE_ARROW_IMAGE = "dipole-5-arrow.gif";
//    public static final String DIPOLE_DONUT_IMAGE = "dipole-5-donut.gif";
    public static final String HEAD_IMAGE = "head-1A.png";
    public static final String COIL_IMAGE = "coil-1.png";

    // Physical parameters
    public static final double MIN_FEQUENCY = 10;
    public static final double MAX_FEQUENCY = 110;
    public static final double FREQUENCY_UNIT = 1E6;
    //    public static final double MIN_FEQUENCY = 20E6;
    //    public static final double MAX_FEQUENCY = 80E6;
    public static final double MAX_FADING_HEIGHT = 30;
    // Conversion factor between current and B field
    //    public static final double MAX_GRADIENT_COIL_CURRENT = 5;   // arbitrary units, suitable for a JSlider
    public static final double MAX_GRADIENT_COIL_FIELD = 0.08;   // arbitrary units, suitable for a JSlider
    public static final double MAX_FADING_COIL_CURRENT = 100;   // arbitrary units, suitable for a JSlider
    public static final double MAX_FADING_COIL_FIELD = 3;       // Teslas
    public static final double CURRENT_TO_FIELD_FACTOR = MAX_FADING_COIL_FIELD / MAX_FADING_COIL_CURRENT / 2;
    public static final double MAGNET_FIELD_DB = 0.03;
    public static final double MAX_MU = 2;
    public static final double MAX_ENERGY_LEVEL_SEPARATION = MAX_FADING_COIL_FIELD * MAX_MU;
    public static final double MAX_POWER = 100;
    // Difference in energies that is equivalent to 0
    public static final double ENERGY_EPS = PhysicsUtil.frequencyToEnergy( SampleMaterial.HYDROGEN.getMu() * MAX_FADING_COIL_FIELD ) / 150 * 4;

    // Length of time (simulation time) that a dipole kicked into spin down state will stay there until
    // it spontaneously fall to spin up
    public static long SPIN_DOWN_TIMEOUT = 250;
    // Mean time between injections of thermal noise
    public static double MEAN_THERMAL_NOISE_INJECTION_TIME = 500;
    // The direction photons come out of the radio wave source
    public static final Vector2D EMITTED_PHOTON_DIRECTION = new Vector2D.Double( 1, 0 );

    // Can photons emitted by dipoles be reabsorbed by another dipole?
    public static final boolean REABSORPTION_ALLOWED = false;
    // Default detector period
    public static final double DETECTOR_DEFAULT_PERIOD = 200;
    // The maximum fraction of dipoles that can have spin down
    public static final double MAX_SPIN_DOWN_FRACTION = 0.9;

    // Model to view conversion factors
    public static class ModelToView {
        public static double FREQUENCY = 1 / 8E9;
//        public static double FREQUENCY = 1 / 4E9;
    }

    // Layout
    public static Point2D SAMPLE_CHAMBER_LOCATION = new Point2D.Double( 170, 55 );
    public static double SCALE_FOR_ORG = 400.0 / 600;
    public static double SAMPLE_CHAMBER_WIDTH = 400;
    public static double SAMPLE_CHAMBER_HEIGHT = 350;
    //    public static double SAMPLE_CHAMBER_HEIGHT = 300;
    public static Rectangle2D SAMPLE_CHAMBER_BOUNDS = new Rectangle2D.Double( SAMPLE_CHAMBER_LOCATION.getX(),
                                                                              SAMPLE_CHAMBER_LOCATION.getY(),
                                                                              SAMPLE_CHAMBER_WIDTH,
                                                                              SAMPLE_CHAMBER_HEIGHT );

    // Initial conditions
    public static class InitialConditions {
        public static double FADING_MAGNET_FIELD = 0;
        //        public static double FADING_MAGNET_CURRENT = 0;
        public static final double DIPOLE_PRECESSION = Math.toRadians( 0 );
        public static final DipoleOrientationAgent.SpinDeterminationPolicy SPIN_DETERMINATION_POLICY = new DipoleOrientationAgent.DeterministicPolicy();
        public static final MonitorPanel.DipoleRepresentationPolicy MONITOR_PANEL_REP_POLICY_DIPOLE = new MonitorPanel.DiscretePolicy();
    }
}
