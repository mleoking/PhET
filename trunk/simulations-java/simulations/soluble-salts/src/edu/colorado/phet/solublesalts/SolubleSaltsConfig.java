// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.salt.SodiumChloride;


/**
 * SolubleSaltsConfig
 *
 * @author Ron LeMaster
 */
public class SolubleSaltsConfig {

    // Clock parameters
    public static final double DT = 1;
    public static final int FPS = 25;

    // Defaults
    public static final Salt DEFAULT_SALT = new SodiumChloride();

    // An ion will bind to another if it is within a distance of the open binding site that is less than or
    // equal to this many times the free ion's radius
    public static double BINDING_DISTANCE_FACTOR = 2;
    public static double DEFAULT_LATTICE_STICK_LIKELIHOOD = 0.4;
    public static double DEFAULT_LATTICE_DISSOCIATION_LIKELIHOOD = 0.001;
    public static double CONCENTRATION_CALIBRATION_FACTOR = 1 / 6.22E23;  // 2/27/06

    // Physical things
    public static final double SCALE = 1;
    public static final Point2D VESSEL_ULC = new Point2D.Double( 150 / SCALE, 250 / SCALE ); // upper-left corner of vessel
    public static final Dimension VESSEL_SIZE = new Dimension( (int) ( 700 / SCALE ), (int) ( 500 / SCALE ) );
    public static final double VESSEL_WALL_THICKNESS = 20 / SCALE;
    public static final double DEFAULT_LATTICE_SPEED = 3;
    // Acceleration of lattices when they come out of the shaker
    public static final double DEFAULT_LATTICE_ACCELERATION = .2;
    public static double MAX_SPIGOT_FLOW = 1000;

    // Images
    public static final String IMAGE_PATH = "soluble-salts/images/";
    public static final String SHAKER_IMAGE_NAME = IMAGE_PATH + "shaker.png";
    public static final String BLUE_ION_IMAGE_NAME = IMAGE_PATH + "molecule-big.gif";
    public static final String STOVE_IMAGE_FILE = IMAGE_PATH + "stove.png";
    public static final String FLAMES_IMAGE_FILE = IMAGE_PATH + "flames.gif";
    public static final String ICE_IMAGE_FILE = IMAGE_PATH + "ice.gif";
    public static final String FAUCET_IMAGE = IMAGE_PATH + "faucet-gold-rt.gif";
    public static final Color WATER_COLOR = new Color( 150, 200, 255 );

    // The time (real time in ms) after an ion is released from a lattice before it can
    // be bound to that lattice again
    public static final long RELEASE_ESCAPE_TIME = 2000;

    // Debug flags
    public static boolean RANDOM_WALK = true;
    public static boolean ONE_CRYSTAL_ONLY = false;
    public static boolean DEBUG = false;
    public static boolean SHOW_BONDS = false;

    //----------------------------------------------------------------
    // Configuration parameters that can be changed programatically at runtime
    //----------------------------------------------------------------

    // Initial water level
    public static double DEFAULT_WATER_LEVEL = 5E-16;
    //    public static final double DEFAULT_CONFIGURABLE_KSP = -12;
    public static final double DEFAULT_CONFIGURABLE_KSP = 1E-12;
    public static final double DEFAULT_CONFIGURABLE_KSP_EXP = -12;
    public static final int MAX_ION_SPINNER_NUM = 400;

    //----------------------------------------------------------------
    // Calibration object
    //----------------------------------------------------------------
    public static class Calibration {
        // Volume calibration factor: pixels to liters
        public double volumeCalibrationFactor;
        public double defaultWaterLevel;
        public double majorTickSpacing;
        public double minorTickSpacing;

        public Calibration( double volumeCalibrationFactor,
                            double defaultWaterLevel,
                            double majorTickSpacing,
                            double minorTickSpacing ) {
            this.volumeCalibrationFactor = volumeCalibrationFactor;
            this.defaultWaterLevel = defaultWaterLevel;
            this.majorTickSpacing = majorTickSpacing;
            this.minorTickSpacing = minorTickSpacing;
        }
    }
}
