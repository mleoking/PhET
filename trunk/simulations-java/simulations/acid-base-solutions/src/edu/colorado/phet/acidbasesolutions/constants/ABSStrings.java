/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.constants;

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
    
    public static final String SHOW_WATER = getString( "showWater" ); 
    public static final String SOLUTION = getString( "solution" ); 
    public static final String TOOLS = getString( "tools" ); 
    public static final String TEST_CONDUCTIVITY = getString( "testConductivity" ); 
    public static final String MEASURE_PH = getString( "measurePH" ); 
    public static final String VIEW_PARTICLES = getString( "viewParticles" ); 
    public static final String STRENGTH = getString( "strength" ); 
    public static final String CONCENTRATION = getString( "concentration" ); 
    public static final String WEAK = getString( "weak" ); 
    public static final String STRONG = getString( "strong" ); 
    public static final String WEAKER = getString( "weaker" ); 
    public static final String STRONGER = getString( "stronger" ); 
    public static final String WATER = getString( "water" );
    public static final String STRONG_ACID = getString( "strongAcid" ); 
    public static final String WEAK_ACID = getString( "weakAcid" ); 
    public static final String STRONG_BASE = getString( "strongBase" ); 
    public static final String WEAK_BASE = getString( "weakBase" ); 
    public static final String ACID = getString( "acid" ); 
    public static final String BASE = getString( "base" );
    public static final String TEST_SOLUTION = getString( "testSolution" ); 
    public static final String CUSTOM_SOLUTION = getString( "customSolution" ); 
    public static final String MOLAR = getString( "molar" );
    public static final String LITERS = getString( "liters" );
    
    private static final String getString( String key ) {
        return ABSResources.getString( key );
    }
}
