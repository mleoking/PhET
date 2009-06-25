/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.defaults;

import java.awt.*;

import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;

/**
 * Defaults for the simulation
 */
public class NaturalSelectionDefaults {
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25;
    public static final double CLOCK_DT = 1;
    public static final int CLOCK_TIME_COLUMNS = 10;

    public static double TICKS_PER_YEAR = 150.0;
    public static final double FRENZY_MILLISECONDS = 4000;
    public static final double MAX_KILL_FRACTION = 0.85;
    public static final double FRENZY_TICKS = 15 * CLOCK_FRAME_RATE; // ie X seconds

    /* Not intended for instantiation */
    private NaturalSelectionDefaults() {
    }

    // Clock


    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 1014, 366 ); // the size of the background images

    // constants specific to the natural selection module
    public static final int DEFAULT_CLIMATE = NaturalSelectionModel.CLIMATE_EQUATOR;
    public static final int DEFAULT_SELECTION_FACTOR = NaturalSelectionModel.SELECTION_NONE;
    public static final int DEFAULT_NUMBER_OF_BUNNIES = 1;

    public static final Dimension GENERATION_CHART_SIZE = new Dimension( 700, 400 );
    public static final Dimension BUNNY_STATS_SIZE = new Dimension( 550, 350 );

}