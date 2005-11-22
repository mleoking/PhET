/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts;

import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.TwoToOneLattice;
import edu.colorado.phet.solublesalts.model.crystal.PlainCubicLattice;
import edu.colorado.phet.solublesalts.model.Sodium;
import edu.colorado.phet.solublesalts.model.Chloride;

import java.awt.geom.Point2D;
import java.awt.*;


/**
 * SolubleSaltsConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsConfig {

    // Descriptive information
    public static final String TITLE = "Soluble Salts";
    public static final String DESCRIPTION = "Soluble Salts";
    public static final String VERSION = "0.00.01";

    // Clock parameters
    public static final double DT = 1;
    public static final int FPS = 25;

    // Physical things
    private static final double scale = 1;
//    private static final double scale = 10;
    public static final Point2D VESSEL_ULC = new Point2D.Double( 150 * scale, 150 * scale ); // upper-left corner of vessel
    public static final Dimension VESSEL_SIZE = new Dimension( (int)( 700 * scale ), (int)( 500 * scale ) );
    public static final double VESSEL_WALL_THICKNESS = 20 * scale;
//    public static final Point2D VESSEL_ULC = new Point2D.Double( 150, 150 ); // upper-left corner of vessel
//    public static final Dimension VESSEL_SIZE = new Dimension( 700, 500 );
//    public static final double VESSEL_WALL_THICKNESS = 20;
//    public static final double DEFAULT_WATER_LEVEL = VESSEL_SIZE.getHeight() * 0.1;
    public static final double DEFAULT_WATER_LEVEL = VESSEL_SIZE.getHeight() * 0.7;
    public static final double DEFAULT_LATTICE_SPEED = 3;
//    public static final double DEFAULT_LATTICE_SPEED = 5;
    // Acceleration of lattices when they come out of the shaker
    public static final double DEFAULT_LATTICE_ACCELERATION = .2;
//    public static final double DEFAULT_LATTICE_ACCELERATION = .5;
    public static final double MAX_SPIGOT_FLOW = 500;


    // Images
    public static final String IMAGE_PATH = "images/";
    public static final String SHAKER_IMAGE_NAME = IMAGE_PATH + "shaker.png";
    public static final String BLUE_ION_IMAGE_NAME = IMAGE_PATH + "molecule-big.gif";
    public static final String STOVE_IMAGE_FILE = IMAGE_PATH + "stove.png";
    public static final String FLAMES_IMAGE_FILE = IMAGE_PATH + "flames.gif";
    public static final String ICE_IMAGE_FILE = IMAGE_PATH + "ice.gif";
    public static final String FAUCET_IMAGE = IMAGE_PATH + "faucet-gold-rt.gif";
//    public static final String FAUCET_IMAGE = IMAGE_PATH + "faucet.png";

    // Misc
    public static final String STRINGS_BUNDLE_NAME = "localization/SolubleSaltsStrings";

    // The time (real time in ms) after an ion is released from a lattice before it can
    // be bound to that lattice again
    public static final long RELEASE_ESCAPE_TIME = 2000;

    public static Lattice twoToOneLattice = new TwoToOneLattice( Sodium.class,
                                                         Chloride.class,
                                                         Sodium.RADIUS + Chloride.RADIUS );
    public static Lattice oneToOneLattice = new PlainCubicLattice( Sodium.RADIUS + Chloride.RADIUS );
//    public static Lattice LATTICE = twoToOneLattice;
    public static Lattice LATTICE = oneToOneLattice;
}
