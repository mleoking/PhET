/**
 * Class: MicrowaveConfig
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Aug 19, 2003
 */
package edu.colorado.phet.microwaves;

public class MicrowavesConfig {
    
    public static final String PROJECT_NAME = "microwaves";

    public static final int NUM_WATER_MOLECULES_IN_SINGLE_LINE = 4;
    public static final int NUM_WATER_MOLECULES_PLACED_RANDOMLY = 20;
    public static final int NUM_WATER_MOLECULES_IN_COFFEE_CUP = 6;

    public static final double MAX_FREQUENCY = 3E-3;
    public static final double INIT_FREQUENCY = MAX_FREQUENCY / 2;
    public static final double SLIDER_FREQUENCY_SCALE = 100 / MAX_FREQUENCY;

    public static final double MAX_AMPLITUDE = .5;
    public static final double INIT_AMPLITUDE = MAX_AMPLITUDE / 2;
    public static final double SLIDER_AMPLITUDE_SCALE = 100 / MAX_AMPLITUDE;
}
