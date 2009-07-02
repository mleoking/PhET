/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.defaults;

import java.awt.*;

import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;

/**
 * Defaults for the simulation
 */
public class NaturalSelectionDefaults {
    /* Not intended for instantiation */
    private NaturalSelectionDefaults() {
    }

    public static final boolean CLOCK_RUNNING = true;

    public static final int CLOCK_FRAME_RATE = 25;

    public static final double CLOCK_DT = 1;
    public static double TICKS_PER_YEAR = 150.0;
    public static double SELECTION_TICK = TICKS_PER_YEAR / 4;
    public static final double FRENZY_TICKS = 15 * CLOCK_FRAME_RATE; // ie X seconds
    public static final int BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD = 5;
    public static final boolean BUNNIES_BECOME_STERILE = false;
    public static final int BUNNIES_STERILE_WHEN_THIS_OLD = 2;
    public static final double MAX_KILL_FRACTION = 0.85;
    public static final Dimension VIEW_SIZE = new Dimension( 1014, 366 ); // the size of the background images
    public static final int DEFAULT_CLIMATE = NaturalSelectionModel.CLIMATE_EQUATOR;
    public static final int DEFAULT_SELECTION_FACTOR = NaturalSelectionModel.SELECTION_NONE;
    public static final Dimension GENERATION_CHART_SIZE = new Dimension( 700, 400 );
    public static final Dimension BUNNY_STATS_SIZE = new Dimension( 550, 350 );
    public static final int BUNNY_BETWEEN_HOP_TIME = 50;
    public static final int BUNNY_HOP_TIME = 10;
    public static final int BUNNY_HOP_HEIGHT = 50;
    public static final double BUNNY_NORMAL_HOP_DISTANCE = 20.0;
    public static final int BUNNY_HUNGER_THRESHOLD = 250;
    public static final int BUNNY_MAX_HUNGER = 600;
    public static final double BUNNY_SIDE_SPACER = 10.0;
    public static final double WOLF_MAX_STEP = 5.0;
    public static final double WOLF_DOUBLE_BACK_DISTANCE = WOLF_MAX_STEP * 6;
    public static final double WOLF_KILL_DISTANCE = WOLF_MAX_STEP * 2;
    public static final int MAX_POPULATION = 500;
    public static final int WOLF_BASE = 5;
    public static final int BUNNIES_PER_WOLVES = 10;
    public static final double WOLF_SELECTION_BUNNY_OFFSET = 10;
    public static final double WOLF_SELECTION_BUNNY_EXPONENT = 0.4;
    public static final double WOLF_SELECTION_SCALE = 0.25;
    public static final double WOLF_SELECTION_BLEND_SCALE = 0.2;
    public static final double FOOD_SELECTION_BUNNY_OFFSET = -3;
    public static final double FOOD_SELECTION_BUNNY_EXPONENT = 0.5;
    public static final double FOOD_SELECTION_SCALE = 0.25;
    public static final double FOOD_SELECTION_BLEND_SCALE = 0.2;
    public static final int MUTATING_BUNNY_BASE = 1;
    public static final int MUTATING_BUNNY_PER_BUNNIES = 7;
}