/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.defaults;

import java.awt.*;

import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.dialog.generationchart.GenerationChartCanvas;

/**
 * Defaults for the simulation
 */
public class NaturalSelectionDefaults {
    
    /* Not intended for instantiation */
    private NaturalSelectionDefaults() {
    }

    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final int CLOCK_FRAME_RATE = GlobalDefaults.CLOCK_FRAME_RATE;
    public static final double CLOCK_DT = GlobalDefaults.CLOCK_DT;
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;

    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 1014, 366 ); // the size of the background images

    // constants specific to the natural selection module
    public static final int DEFAULT_CLIMATE = NaturalSelectionModel.CLIMATE_EQUATOR;
    public static final int DEFAULT_SELECTION_FACTOR = NaturalSelectionModel.SELECTION_NONE;
    public static final int DEFAULT_NUMBER_OF_BUNNIES = 2;
    public static final int DEFAULT_GENERATION_CHART = GenerationChartCanvas.TYPE_HEREDITY;

    public static final Dimension GENERATION_CHART_SIZE = new Dimension( 800, 600 );

    public static final int START_MONTH = 0; // January

}