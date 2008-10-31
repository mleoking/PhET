/**
 * Class: MicrowaveConfig
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Aug 19, 2003
 */
package edu.colorado.phet.microwaves;

public class MicrowavesConfig {
    
    public static final String PROJECT_NAME = "microwaves";

    public static int s_numWaterMoleculesInSingleLine = 4;
    public static int s_numWaterMoleculesPlaceRandomly = 20;
    public static int s_numWaterMoleculesInCoffeeCup = 6;

    public static double s_maxFreq = 3E-3;
    public static double s_initFreq = s_maxFreq / 2;
    public static double s_sliderFreqScale = 100 / s_maxFreq;

    public static double s_maxAmp = .5;
    public static double s_initAmp = s_maxAmp / 2;
    public static double s_sliderAmpScale = 100 / s_maxAmp;
}
