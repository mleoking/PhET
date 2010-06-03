/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

/**
 * Collection of localized strings used by this simulations.
 * We load all strings statically so that we will be warned at startup time of any missing strings.
 * Otherwise we'd have to visit every part of the sim to test properly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSStrings {
    
    /* not intended for instantiation */
    private ABSStrings() {}
    
    public static final String PATTERN_VALUE_UNITS = "{0}{1}";
    
    public static final String SHOW_WATER = "Show Water"; 
    public static final String SOLUTION = "Solution"; 
    public static final String TOOLS = "Tools"; 
    public static final String TEST_CONDUCTIVITY = "Test Conductivity"; 
    public static final String MEASURE_PH = "Measure pH"; 
    public static final String VIEW_PARTICLES = "View Particles"; 
    public static final String STRENGTH = "Strength"; 
    public static final String CONCENTRATION = "Concentration"; 
    public static final String WEAK = "weak"; 
    public static final String STRONG = "strong"; 
    public static final String WEAKER = "weaker"; 
    public static final String STRONGER = "stronger"; 
    public static final String WATER = "Water";
    public static final String STRONG_ACID = "Strong Acid"; 
    public static final String WEAK_ACID = "Weak Acid"; 
    public static final String STRONG_BASE = "Strong Base"; 
    public static final String WEAK_BASE = "Weak Base"; 
    public static final String ACID = "Acid"; 
    public static final String BASE = "Base";
    public static final String TEST_SOLUTION = "Test Solution"; 
    public static final String CUSTOM_SOLUTION = "Custom Solution"; 
    public static final String MOLAR = "M";
    public static final String LITERS = "L";
    
    private static final String getString( String key ) {
        return ABSResources.getString( key );
    }
}
