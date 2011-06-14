// Copyright 2002-2011, University of Colorado

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
    
    private static final String getString( String key ) {
        return ABSResources.getString( key );
    }
    
    // patterns used with MessageFormat
    public static final String PATTERN_VALUE_UNITS = getString( "pattern.0value.1units" );
    public static final String PATTERN_LABEL_VALUE = getString( "pattern.0label.1value" );
    public static final String PATTERN_SOLUTION_SYMBOL = getString( "pattern.0solution.1symbol" );
    
    public static final String PH = getString( "pH" ); 
    public static final String SOLUTION = getString( "solution" ); 
    public static final String SOLUTIONS = getString( "solutions" ); 
    public static final String TESTS = getString( "tests" ); 
    public static final String CONDUCTIVITY = getString( "conductivity" ); 
    public static final String PH_PAPER = getString( "pHPaper" ); 
    public static final String PH_METER = getString( "pHMeter" ); 
    public static final String VIEW_PARTICLES = getString( "viewParticles" ); 
    public static final String INITIAL_CONCENTRATION = getString( "initialConcentration" ); 
    public static final String STRENGTH = getString( "strength" ); 
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
    public static final String MOLES_PER_LITER = getString( "molesPerLiter" );
    public static final String LITERS = getString( "liters" );
    public static final String MOLECULES = getString( "molecules" );
    public static final String EQUILIBRIUM_CONCENTRATION = getString( "equilibriumConcentration" );
    public static final String CONCENTRATION_GRAPH_Y_AXIS = getString( "concentrationGraph.yAxis" );
    public static final String NEGLIGIBLE = getString( "negligible" );
    public static final String PH_COLOR_KEY = getString( "pHColorKey" );
    public static final String LIQUID = getString( "liquid" );
    public static final String VIEWS = getString( "views" );
    public static final String SHOW_WATER = getString( "showWater" );
}
