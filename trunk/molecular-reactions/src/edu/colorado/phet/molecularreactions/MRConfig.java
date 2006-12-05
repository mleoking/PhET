package edu.colorado.phet.molecularreactions;

import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.MoleculeA;

import javax.swing.*;
import java.awt.*;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

/**
 * edu.colorado.phet.molecularreactions.MRConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRConfig {

    // Version
    public static final String VERSION = "0.00.14";

    // Prefix of the strings bundles
    public static final String LOCALIZATION_BUNDLE = "localization/MRStrings";

    // Debug flag
    public static boolean DEBUG = true;

    // Model constants
    public static int CLOCK_FPS = 25;
    public static double MAX_REACTION_THRESHOLD = 5E2;      // Max num of any one type of molecule
    public static final int MAX_MOLECULE_CNT = 200;
    public static double DEFAULT_REACTION_THRESHOLD = MAX_REACTION_THRESHOLD * .7;
    public static final double MAX_SPEED = 3;
    public static final double LAUNCHER_MIN_THETA = -Math.PI / 4;
    public static final double LAUNCHER_MAX_THETA = Math.PI / 4;
    public static final double LAUNCHER_MAX_EXTENSION = 70;
    public static final double RUNNING_DT = 1;
    public static final double STEPPING_DT = 0.3;
    public static final EnergyProfile DEFAULT_ENERGY_PROFILE = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                                  MRConfig.DEFAULT_REACTION_THRESHOLD,
                                                                                  MRConfig.DEFAULT_REACTION_THRESHOLD * .6,
                                                                                  100 );
    public static final Class DEFAULT_LAUNCHER_MOLECULE_CLASS = MoleculeA.class;

    // View Constants
    public static int CONTROL_PANEL_WIDTH = 150;
    public static final Dimension SIMULATION_PANEL_SIZE = new Dimension( 850, 575 );
    public static final Dimension SPATIAL_VIEW_SIZE = new Dimension( 520, 530 );
//    public static final Dimension SPATIAL_VIEW_SIZE = new Dimension( 850, 575 );
//    public static final Dimension ENERGY_VIEW_SIZE = SPATIAL_VIEW_SIZE;
    public static final int ENERGY_PANE_WIDTH = 310;
    public static final Dimension MOLECULE_SEPARATION_PANE_SIZE = new Dimension( ENERGY_PANE_WIDTH, 150 );
    public static final Dimension CHART_PANE_SIZE = new Dimension( ENERGY_PANE_WIDTH, 240 );
    public static final Dimension ENERGY_VIEW_SIZE = new Dimension( ENERGY_PANE_WIDTH,
                                                                    SPATIAL_VIEW_SIZE.height );
    public static final Dimension ENERGY_VIEW_REACTION_LEGEND_SIZE = new Dimension( ENERGY_PANE_WIDTH, 40 );
    public static final int BAR_CHART_MAX_Y = 20;
    public static final int PIE_CHART_DIAM_FACTOR = 3;
    public static final double ENERGY_VIEW_PROFILE_VERTICAL_SCALE = 1;
    public static final int STRIP_CHART_BUFFER_SIZE = 3 * 60 * CLOCK_FPS;
    public static final double STRIP_CHART_VISIBLE_TIME_RANGE = 300;
    public static final int STRIP_CHART_MIN_RANGE_Y = 10;

    // Colors
    public static final Color SPATIAL_VIEW_BACKGROUND = new Color( 255, 255, 225 );
    public static final Color MOLECULE_PANE_BACKGROUND = new Color( 237, 255, 235 );
    public static final Color ENERGY_PANE_BACKGROUND = Color.white;
    public static final Color ENERGY_PANE_TEXT_COLOR = Color.black;
    public static final Paint TOTAL_ENERGY_COLOR = new Color( 100, 140, 0 );
    public static final Color POTENTIAL_ENERGY_COLOR = Color.blue;

    // Images
    public static final String IMAGE_DIRECTORY = "images/";
    public static final String STOVE_IMAGE_FILE = IMAGE_DIRECTORY + "stove.png";
    public static final String FLAMES_IMAGE_FILE = IMAGE_DIRECTORY + "flames.gif";
    public static final String ICE_IMAGE_FILE = IMAGE_DIRECTORY + "ice.gif";
    public static final String PUMP_BODY_IMAGE_FILE = IMAGE_DIRECTORY + "pump-body.gif";
    public static final String PUMP_HANDLE_IMAGE_FILE = IMAGE_DIRECTORY + "pump-handle.gif";

    // Fonts
    public static final Font CHART_TITLE_FONT = UIManager.getFont( "InternalFrame.titleFont" );

}
